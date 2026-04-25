package io.github.taichi0373.benefit_map.service.admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import io.github.taichi0373.benefit_map.dto.admin.AdminPagedResponseDto;
import io.github.taichi0373.benefit_map.dto.admin.CsvImportResultDto;
import io.github.taichi0373.benefit_map.repository.dao.AgencyDao;
import io.github.taichi0373.benefit_map.repository.dao.BenefitDao;
import io.github.taichi0373.benefit_map.repository.dao.FareDiscountDao;
import io.github.taichi0373.benefit_map.repository.entity.FareDiscountEntity;
import io.github.taichi0373.benefit_map.repository.entity.SystemField;

/**
 * 運賃割引管理サービス
 * <p>
 * 管理者向けの運賃割引CRUD操作を提供する。
 * </p>
 */
@Service
public class AdminFareDiscountService {

    /** 運賃割引DAO */
    @Autowired
    private FareDiscountDao fareDiscountDao;

    /** 特典DAO（外部キー存在チェック用） */
    @Autowired
    private BenefitDao benefitDao;

    /** 事業者DAO（外部キー存在チェック用） */
    @Autowired
    private AgencyDao agencyDao;

    /**
     * 運賃割引一覧をページング取得する
     *
     * @param page          ページ番号（0始まり）
     * @param size          ページあたり件数
     * @param benefitId     特典IDフィルター（null の場合は全件）
     * @param agencyId      事業者IDフィルター（null の場合は全件）
     * @param discountType  割引種別フィルター（null の場合は全件）
     * @param discountValue 割引額フィルター（null の場合は全件）
     * @param sort          ソートフィールド名
     * @param order         ソート順（desc で降順）
     * @return ページングレスポンス
     */
    public AdminPagedResponseDto<FareDiscountEntity> getAll(int page, int size, String benefitId,
            String agencyId, String discountType, String discountValue, String keyword, String sort, String order) {
        int offset = page * size;
        var items = fareDiscountDao.selectForAdmin(offset, size, benefitId, agencyId, discountType, discountValue, keyword, sort, order);
        long total = fareDiscountDao.countForAdmin(benefitId, agencyId, discountType, discountValue, keyword);
        return AdminPagedResponseDto.of(items, total, page, size);
    }

    /**
     * 運賃割引を新規登録する
     *
     * @param entity 登録する運賃割引エンティティ
     * @return 登録した運賃割引エンティティ
     * @throws IllegalArgumentException BENEFIT_ID または AGENCY_ID が存在しない場合
     */
    public FareDiscountEntity create(FareDiscountEntity entity) {
        validateForeignKeys(entity.getBenefitId(), entity.getAgencyId());
        LocalDateTime now = LocalDateTime.now();
        entity.setSystemField(new SystemField(now, now));
        fareDiscountDao.insert(entity);
        return entity;
    }

    /**
     * 運賃割引を更新する
     *
     * @param benefitId 特典ID（複合PK）
     * @param agencyId  事業者ID（複合PK）
     * @param entity    更新する運賃割引エンティティ
     * @return 更新した運賃割引エンティティ
     * @throws NoSuchElementException   運賃割引が存在しない場合
     * @throws IllegalArgumentException BENEFIT_ID または AGENCY_ID が存在しない場合
     */
    public FareDiscountEntity update(String benefitId, String agencyId, FareDiscountEntity entity) {
        FareDiscountEntity existing = fareDiscountDao.selectById(benefitId, agencyId);
        if (existing == null) {
            throw new NoSuchElementException("運賃割引が見つかりません: " + benefitId + "/" + agencyId);
        }
        validateForeignKeys(benefitId, agencyId);
        entity.setBenefitId(benefitId);
        entity.setAgencyId(agencyId);
        LocalDateTime createdAt = existing.getSystemField() != null ? existing.getSystemField().getSysCreatedAt() : null;
        entity.setSystemField(new SystemField(createdAt, LocalDateTime.now()));
        fareDiscountDao.update(entity);
        return entity;
    }

    /**
     * 運賃割引を削除する
     *
     * @param benefitId 特典ID（複合PK）
     * @param agencyId  事業者ID（複合PK）
     * @throws NoSuchElementException 運賃割引が存在しない場合
     */
    public void delete(String benefitId, String agencyId) {
        FareDiscountEntity existing = fareDiscountDao.selectById(benefitId, agencyId);
        if (existing == null) {
            throw new NoSuchElementException("運賃割引が見つかりません: " + benefitId + "/" + agencyId);
        }
        fareDiscountDao.delete(existing);
    }

    /**
     * CSVファイルから運賃割引を一括インポートする（upsert・複合PK）
     * <p>
     * CSVヘッダー: benefitId, agencyId, discountType, discountValue
     * </p>
     *
     * @param file インポートするCSVファイル（UTF-8、1行目はヘッダー）
     * @return インポート結果
     * @throws IOException ファイル読み込みエラー
     */
    @Transactional
    public CsvImportResultDto importCsv(MultipartFile file) throws IOException {
        List<FareDiscountEntity> toInsert = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setIgnoreEmptyLines(true)
                     .setTrim(true)
                     .build()
                     .parse(skipBom(reader))) {

            LocalDateTime now = LocalDateTime.now();
            for (CSVRecord record : parser) {
                try {
                    String benefitId = csvVal(record, "benefitId");
                    String agencyId = csvVal(record, "agencyId");
                    if (benefitId == null || agencyId == null) {
                        errors.add("行 " + record.getRecordNumber() + ": 特典IDまたは事業者IDが空です");
                        continue;
                    }
                    validateForeignKeys(benefitId, agencyId);

                    FareDiscountEntity entity = new FareDiscountEntity();
                    entity.setBenefitId(benefitId);
                    entity.setAgencyId(agencyId);
                    entity.setDiscountType(csvVal(record, "discountType"));
                    entity.setDiscountValue(csvInt(record, "discountValue"));

                    entity.setSystemField(new SystemField(now, now));
                    toInsert.add(entity);
                } catch (Exception e) {
                    errors.add("行 " + record.getRecordNumber() + ": " + e.getMessage());
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errors));
        }
        toInsert.forEach(fareDiscountDao::insert);
        return new CsvImportResultDto(toInsert.size(), 0, 0, List.of());
    }

    private String csvVal(CSVRecord record, String column) {
        try {
            String val = record.get(column);
            return (val == null || val.isBlank()) ? null : val.trim();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Integer csvInt(CSVRecord record, String column) {
        String val = csvVal(record, column);
        if (val == null) return null;
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + column + "' の値が数値ではありません: " + val);
        }
    }

    private BufferedReader skipBom(BufferedReader reader) throws IOException {
        reader.mark(1);
        if (reader.read() != '\uFEFF') {
            reader.reset();
        }
        return reader;
    }

    /**
     * 外部キー存在チェック
     *
     * @param benefitId 特典ID
     * @param agencyId  事業者ID
     * @throws IllegalArgumentException BENEFIT_ID または AGENCY_ID が存在しない場合
     */
    private void validateForeignKeys(String benefitId, String agencyId) {
        if (benefitDao.selectById(benefitId) == null) {
            throw new IllegalArgumentException("指定された特典IDが存在しません: " + benefitId);
        }
        if (agencyDao.selectById(agencyId) == null) {
            throw new IllegalArgumentException("指定された事業者IDが存在しません: " + agencyId);
        }
    }
}
