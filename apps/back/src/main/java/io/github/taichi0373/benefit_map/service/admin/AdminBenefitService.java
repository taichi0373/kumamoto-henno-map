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
import org.springframework.web.multipart.MultipartFile;

import io.github.taichi0373.benefit_map.dto.admin.AdminPagedResponseDto;
import io.github.taichi0373.benefit_map.dto.admin.CsvImportResultDto;
import io.github.taichi0373.benefit_map.repository.dao.BenefitDao;
import io.github.taichi0373.benefit_map.repository.dao.BenefitEligibilityDao;
import io.github.taichi0373.benefit_map.repository.dao.FareDiscountDao;
import io.github.taichi0373.benefit_map.repository.entity.BenefitEntity;
import io.github.taichi0373.benefit_map.repository.entity.SystemField;

/**
 * 特典管理サービス
 * <p>
 * 管理者向けの特典CRUD操作とビジネスロジックを提供する。
 * </p>
 */
@Service
public class AdminBenefitService {

    /** 特典DAO */
    @Autowired
    private BenefitDao benefitDao;

    /** 特典適用条件DAO（削除前依存チェック用） */
    @Autowired
    private BenefitEligibilityDao benefitEligibilityDao;

    /** 運賃割引DAO（削除前依存チェック用） */
    @Autowired
    private FareDiscountDao fareDiscountDao;

    /**
     * 特典一覧をページング取得する
     *
     * @param page           ページ番号（0始まり）
     * @param size           ページあたり件数
     * @param municipalityCd 自治体コードフィルター（null可・部分一致）
     * @param categoryCd     カテゴリコードフィルター（null可・部分一致）
     * @param benefitId      特典IDフィルター（null可・部分一致）
     * @param benefitName    特典名称フィルター（null可・部分一致）
     * @param expDetail      説明詳細フィルター（null可・部分一致）
     * @param sort           ソートフィールド名
     * @param order          ソート順（desc で降順）
     * @return ページングレスポンス
     */
    public AdminPagedResponseDto<BenefitEntity> getAll(int page, int size, String municipalityCd, String categoryCd,
            String benefitId, String benefitName, String expDetail, String sort, String order) {
        int offset = page * size;
        var items = benefitDao.selectForAdmin(offset, size, municipalityCd, categoryCd, benefitId, benefitName, expDetail, sort, order);
        long total = benefitDao.countForAdmin(municipalityCd, categoryCd, benefitId, benefitName, expDetail);
        return AdminPagedResponseDto.of(items, total, page, size);
    }

    /**
     * 特典を新規登録する
     *
     * @param entity 登録する特典エンティティ
     * @return 登録した特典エンティティ
     */
    public BenefitEntity create(BenefitEntity entity) {
        LocalDateTime now = LocalDateTime.now();
        entity.setSystemField(new SystemField(now, now));
        benefitDao.insert(entity);
        return entity;
    }

    /**
     * 特典を更新する
     *
     * @param benefitId 特典ID
     * @param entity    更新する特典エンティティ
     * @return 更新した特典エンティティ
     * @throws NoSuchElementException 特典が存在しない場合
     */
    public BenefitEntity update(String benefitId, BenefitEntity entity) {
        BenefitEntity existing = benefitDao.selectById(benefitId);
        if (existing == null) {
            throw new NoSuchElementException("特典が見つかりません: " + benefitId);
        }
        entity.setBenefitId(benefitId);
        LocalDateTime createdAt = existing.getSystemField() != null ? existing.getSystemField().getSysCreatedAt() : null;
        entity.setSystemField(new SystemField(createdAt, LocalDateTime.now()));
        benefitDao.update(entity);
        return entity;
    }

    /**
     * 特典を削除する
     * <p>
     * BENEFIT_ELIGIBILITY または FARE_DISCOUNT に依存するレコードが存在する場合は
     * IllegalStateException をスローする。
     * </p>
     *
     * @param benefitId 特典ID
     * @throws NoSuchElementException  特典が存在しない場合
     * @throws IllegalStateException   依存レコードが存在する場合
     */
    /**
     * CSVファイルから特典を一括インポートする（upsert）
     * <p>
     * CSVヘッダー: benefitId, municipalityCd, categoryCd, benefitName,
     * benefitShortName, benefitDetail, expDetail, phoneNumber, benefitUrl
     * </p>
     *
     * @param file インポートするCSVファイル（UTF-8、1行目はヘッダー）
     * @return インポート結果（登録・更新・失敗件数）
     * @throws IOException ファイル読み込みエラー
     */
    public CsvImportResultDto importCsv(MultipartFile file) throws IOException {
        int inserted = 0, updated = 0, failed = 0;
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

            for (CSVRecord record : parser) {
                try {
                    String benefitId = record.get("benefitId");
                    if (benefitId == null || benefitId.isBlank()) {
                        errors.add("行 " + record.getRecordNumber() + ": 特典IDが空です");
                        failed++;
                        continue;
                    }
                    BenefitEntity entity = new BenefitEntity();
                    entity.setBenefitId(benefitId.trim());
                    entity.setMunicipalityCd(csvVal(record, "municipalityCd"));
                    entity.setCategoryCd(csvVal(record, "categoryCd"));
                    entity.setBenefitName(csvVal(record, "benefitName"));
                    entity.setBenefitShortName(csvVal(record, "benefitShortName"));
                    entity.setBenefitDetail(csvVal(record, "benefitDetail"));
                    entity.setExpDetail(csvVal(record, "expDetail"));
                    entity.setPhoneNumber(csvVal(record, "phoneNumber"));
                    entity.setBenefitUrl(csvVal(record, "benefitUrl"));

                    LocalDateTime now = LocalDateTime.now();
                    BenefitEntity existing = benefitDao.selectById(benefitId);
                    if (existing != null) {
                        LocalDateTime createdAt = existing.getSystemField() != null
                                ? existing.getSystemField().getSysCreatedAt() : now;
                        entity.setSystemField(new SystemField(createdAt, now));
                        benefitDao.update(entity);
                        updated++;
                    } else {
                        entity.setSystemField(new SystemField(now, now));
                        benefitDao.insert(entity);
                        inserted++;
                    }
                } catch (Exception e) {
                    failed++;
                    errors.add("行 " + record.getRecordNumber() + ": " + e.getMessage());
                }
            }
        }
        return new CsvImportResultDto(inserted, updated, failed, errors);
    }

    /** CSV値取得（列が存在しない場合・空の場合はnullを返す） */
    private String csvVal(CSVRecord record, String column) {
        try {
            String val = record.get(column);
            return (val == null || val.isBlank()) ? null : val.trim();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /** UTF-8 BOMをスキップする */
    private BufferedReader skipBom(BufferedReader reader) throws IOException {
        reader.mark(1);
        if (reader.read() != '\uFEFF') {
            reader.reset();
        }
        return reader;
    }

    public void delete(String benefitId) {
        BenefitEntity existing = benefitDao.selectById(benefitId);
        if (existing == null) {
            throw new NoSuchElementException("特典が見つかりません: " + benefitId);
        }
        if (!benefitEligibilityDao.selectByBenefitId(benefitId).isEmpty()) {
            throw new IllegalStateException("この特典に紐付く特典条件が存在するため削除できません");
        }
        if (!fareDiscountDao.selectByBenefitId(benefitId).isEmpty()) {
            throw new IllegalStateException("この特典に紐付く運賃割引が存在するため削除できません");
        }
        benefitDao.delete(existing);
    }
}
