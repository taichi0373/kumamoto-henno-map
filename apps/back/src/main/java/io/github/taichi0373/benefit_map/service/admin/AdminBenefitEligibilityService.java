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
import io.github.taichi0373.benefit_map.repository.dao.BenefitEligibilityDao;
import io.github.taichi0373.benefit_map.repository.entity.BenefitEligibilityEntity;
import io.github.taichi0373.benefit_map.repository.entity.SystemField;

/**
 * 特典条件管理サービス
 * <p>
 * 管理者向けの特典適用条件CRUD操作を提供する。
 * </p>
 */
@Service
public class AdminBenefitEligibilityService {

    /** 特典適用条件DAO */
    @Autowired
    private BenefitEligibilityDao benefitEligibilityDao;

    /**
     * 特典条件一覧をページング取得する
     *
     * @param page           ページ番号（0始まり）
     * @param size           ページあたり件数
     * @param benefitId      特典IDフィルター（null可）
     * @param id             IDフィルター（null可）
     * @param licenseStatus  免許状況フィルター（null可）
     * @param minAge         最低年齢フィルター（null可）
     * @param maxAge         最高年齢フィルター（null可）
     * @param municipalityCd 自治体コードフィルター（null可）
     * @param sort           ソートフィールド名
     * @param order          ソート順（desc で降順）
     * @return ページングレスポンス
     */
    public AdminPagedResponseDto<BenefitEligibilityEntity> getAll(int page, int size, String benefitId,
            String id, String licenseStatus, String minAge, String maxAge, String municipalityCd,
            String sort, String order) {
        int offset = page * size;
        var items = benefitEligibilityDao.selectForAdmin(offset, size, benefitId, id, licenseStatus, minAge, maxAge, municipalityCd, sort, order);
        long total = benefitEligibilityDao.countForAdmin(benefitId, id, licenseStatus, minAge, maxAge, municipalityCd);
        return AdminPagedResponseDto.of(items, total, page, size);
    }

    /**
     * 特典条件を新規登録する
     * <p>
     * MIN_AGE が MAX_AGE より大きい場合は IllegalArgumentException をスローする。
     * </p>
     *
     * @param entity 登録する特典条件エンティティ
     * @return 登録した特典条件エンティティ
     * @throws IllegalArgumentException MIN_AGE > MAX_AGE の場合
     */
    public BenefitEligibilityEntity create(BenefitEligibilityEntity entity) {
        validateAgeRange(entity);
        LocalDateTime now = LocalDateTime.now();
        entity.setSystemField(new SystemField(now, now));
        benefitEligibilityDao.insert(entity);
        return entity;
    }

    /**
     * 特典条件を更新する
     *
     * @param id     特典条件ID
     * @param entity 更新する特典条件エンティティ
     * @return 更新した特典条件エンティティ
     * @throws NoSuchElementException   特典条件が存在しない場合
     * @throws IllegalArgumentException MIN_AGE > MAX_AGE の場合
     */
    public BenefitEligibilityEntity update(Long id, BenefitEligibilityEntity entity) {
        BenefitEligibilityEntity existing = benefitEligibilityDao.selectById(id);
        if (existing == null) {
            throw new NoSuchElementException("特典条件が見つかりません: " + id);
        }
        validateAgeRange(entity);
        entity.setId(id);
        LocalDateTime createdAt = existing.getSystemField() != null ? existing.getSystemField().getSysCreatedAt() : null;
        entity.setSystemField(new SystemField(createdAt, LocalDateTime.now()));
        benefitEligibilityDao.update(entity);
        return entity;
    }

    /**
     * 特典条件を削除する
     *
     * @param id 特典条件ID
     * @throws NoSuchElementException 特典条件が存在しない場合
     */
    /**
     * CSVファイルから特典条件を一括インポートする（常にINSERT・IDは自動採番）
     * <p>
     * CSVヘッダー: benefitId, licenseStatus, minAge, maxAge, municipalityCd, note
     * </p>
     *
     * @param file インポートするCSVファイル（UTF-8、1行目はヘッダー）
     * @return インポート結果
     * @throws IOException ファイル読み込みエラー
     */
    public CsvImportResultDto importCsv(MultipartFile file) throws IOException {
        int inserted = 0, failed = 0;
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
                    BenefitEligibilityEntity entity = new BenefitEligibilityEntity();
                    entity.setBenefitId(csvVal(record, "benefitId"));
                    entity.setLicenseStatus(csvVal(record, "licenseStatus"));
                    entity.setMinAge(csvInt(record, "minAge"));
                    entity.setMaxAge(csvInt(record, "maxAge"));
                    entity.setMunicipalityCd(csvVal(record, "municipalityCd"));
                    entity.setNote(csvVal(record, "note"));

                    validateAgeRange(entity);
                    LocalDateTime now = LocalDateTime.now();
                    entity.setSystemField(new SystemField(now, now));
                    benefitEligibilityDao.insert(entity);
                    inserted++;
                } catch (Exception e) {
                    failed++;
                    errors.add("行 " + record.getRecordNumber() + ": " + e.getMessage());
                }
            }
        }
        return new CsvImportResultDto(inserted, 0, failed, errors);
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

    public void delete(Long id) {
        BenefitEligibilityEntity existing = benefitEligibilityDao.selectById(id);
        if (existing == null) {
            throw new NoSuchElementException("特典条件が見つかりません: " + id);
        }
        benefitEligibilityDao.delete(existing);
    }

    /**
     * 年齢範囲のバリデーション
     *
     * @param entity バリデーション対象エンティティ
     * @throws IllegalArgumentException MIN_AGE > MAX_AGE の場合
     */
    private void validateAgeRange(BenefitEligibilityEntity entity) {
        if (entity.getMinAge() != null && entity.getMaxAge() != null
                && entity.getMinAge() > entity.getMaxAge()) {
            throw new IllegalArgumentException("最低年齢が最高年齢より大きい値です");
        }
    }
}
