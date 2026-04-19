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
import io.github.taichi0373.benefit_map.repository.dao.BenefitCategoryDao;
import io.github.taichi0373.benefit_map.repository.dao.BenefitDao;
import io.github.taichi0373.benefit_map.repository.entity.BenefitCategoryEntity;
import io.github.taichi0373.benefit_map.repository.entity.SystemField;

/**
 * 特典カテゴリ管理サービス
 * <p>
 * 管理者向けの特典カテゴリCRUD操作を提供する。
 * </p>
 */
@Service
public class AdminBenefitCategoryService {

    /** 特典カテゴリDAO */
    @Autowired
    private BenefitCategoryDao benefitCategoryDao;

    /** 特典DAO（削除前依存チェック用） */
    @Autowired
    private BenefitDao benefitDao;

    /**
     * 特典カテゴリ一覧をページング取得する（有効・無効を含む）
     *
     * @param page         ページ番号（0始まり）
     * @param size         ページあたり件数
     * @param categoryName カテゴリ名称フィルター（null の場合は全件）
     * @param categoryCd   カテゴリコードフィルター（null の場合は全件）
     * @param displayOrder 表示順フィルター（null の場合は全件）
     * @param sort         ソートフィールド名
     * @param order        ソート順（desc で降順）
     * @return ページングレスポンス
     */
    public AdminPagedResponseDto<BenefitCategoryEntity> getAll(int page, int size, String categoryName,
            String categoryCd, String displayOrder, String sort, String order) {
        int offset = page * size;
        var items = benefitCategoryDao.selectForAdmin(offset, size, categoryName, categoryCd, displayOrder, sort, order);
        long total = benefitCategoryDao.countForAdmin(categoryName, categoryCd, displayOrder);
        return AdminPagedResponseDto.of(items, total, page, size);
    }

    /**
     * 特典カテゴリを新規登録する
     *
     * @param entity 登録する特典カテゴリエンティティ
     * @return 登録した特典カテゴリエンティティ
     */
    public BenefitCategoryEntity create(BenefitCategoryEntity entity) {
        LocalDateTime now = LocalDateTime.now();
        entity.setSystemField(new SystemField(now, now));
        benefitCategoryDao.insert(entity);
        return entity;
    }

    /**
     * 特典カテゴリを更新する
     *
     * @param categoryCd カテゴリコード
     * @param entity     更新する特典カテゴリエンティティ
     * @return 更新した特典カテゴリエンティティ
     * @throws NoSuchElementException カテゴリが存在しない場合
     */
    public BenefitCategoryEntity update(String categoryCd, BenefitCategoryEntity entity) {
        BenefitCategoryEntity existing = benefitCategoryDao.selectById(categoryCd);
        if (existing == null) {
            throw new NoSuchElementException("特典カテゴリが見つかりません: " + categoryCd);
        }
        entity.setCategoryCd(categoryCd);
        LocalDateTime createdAt = existing.getSystemField() != null ? existing.getSystemField().getSysCreatedAt() : null;
        entity.setSystemField(new SystemField(createdAt, LocalDateTime.now()));
        benefitCategoryDao.update(entity);
        return entity;
    }

    /**
     * 特典カテゴリを削除する
     * <p>
     * 紐付く BENEFIT レコードが存在する場合は IllegalStateException をスローする。
     * </p>
     *
     * @param categoryCd カテゴリコード
     * @throws NoSuchElementException カテゴリが存在しない場合
     * @throws IllegalStateException  依存する特典が存在する場合
     */
    /**
     * CSVファイルから特典カテゴリを一括インポートする（upsert）
     * <p>
     * CSVヘッダー: categoryCd, categoryName, displayOrder, isActive
     * </p>
     *
     * @param file インポートするCSVファイル（UTF-8、1行目はヘッダー）
     * @return インポート結果
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
                    String categoryCd = csvVal(record, "categoryCd");
                    if (categoryCd == null) {
                        errors.add("行 " + record.getRecordNumber() + ": カテゴリコードが空です");
                        failed++;
                        continue;
                    }
                    BenefitCategoryEntity entity = new BenefitCategoryEntity();
                    entity.setCategoryCd(categoryCd);
                    entity.setCategoryName(csvVal(record, "categoryName"));
                    entity.setDisplayOrder(csvInt(record, "displayOrder"));
                    entity.setIsActive(csvVal(record, "isActive"));

                    LocalDateTime now = LocalDateTime.now();
                    BenefitCategoryEntity existing = benefitCategoryDao.selectById(categoryCd);
                    if (existing != null) {
                        LocalDateTime createdAt = existing.getSystemField() != null
                                ? existing.getSystemField().getSysCreatedAt() : now;
                        entity.setSystemField(new SystemField(createdAt, now));
                        benefitCategoryDao.update(entity);
                        updated++;
                    } else {
                        entity.setSystemField(new SystemField(now, now));
                        benefitCategoryDao.insert(entity);
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

    public void delete(String categoryCd) {
        BenefitCategoryEntity existing = benefitCategoryDao.selectById(categoryCd);
        if (existing == null) {
            throw new NoSuchElementException("特典カテゴリが見つかりません: " + categoryCd);
        }
        if (!benefitDao.selectByCategoryCd(categoryCd).isEmpty()) {
            throw new IllegalStateException("このカテゴリに紐付く特典が存在するため削除できません");
        }
        benefitCategoryDao.delete(existing);
    }
}
