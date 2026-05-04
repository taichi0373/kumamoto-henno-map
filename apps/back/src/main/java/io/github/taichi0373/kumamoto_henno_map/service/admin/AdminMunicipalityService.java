package io.github.taichi0373.kumamoto_henno_map.service.admin;

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

import io.github.taichi0373.kumamoto_henno_map.dto.admin.AdminPagedResponseDto;
import io.github.taichi0373.kumamoto_henno_map.dto.admin.CsvImportResultDto;
import io.github.taichi0373.kumamoto_henno_map.repository.dao.BenefitDao;
import io.github.taichi0373.kumamoto_henno_map.repository.dao.MunicipalityDao;
import io.github.taichi0373.kumamoto_henno_map.repository.dao.UsersDao;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.MunicipalityEntity;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.SystemField;

/**
 * 自治体管理サービス
 * <p>
 * 管理者向けの自治体CRUD操作を提供する。
 * </p>
 */
@Service
public class AdminMunicipalityService {

    /** 自治体DAO */
    @Autowired
    private MunicipalityDao municipalityDao;

    /** 特典DAO（削除前依存チェック用） */
    @Autowired
    private BenefitDao benefitDao;

    /** ユーザーDAO（削除前依存チェック用） */
    @Autowired
    private UsersDao usersDao;

    /**
     * 自治体一覧をページング取得する
     *
     * @param page             ページ番号（0始まり）
     * @param size             ページあたり件数
     * @param municipalityName 自治体名称フィルター（null の場合は全件）
     * @param municipalityCd   自治体コードフィルター（null の場合は全件）
     * @param municipalityKana 自治体カナフィルター（null の場合は全件）
     * @param municipalityType 自治体区分フィルター（null の場合は全件）
     * @param sort             ソートフィールド名
     * @param order            ソート順（desc で降順）
     * @return ページングレスポンス
     */
    public AdminPagedResponseDto<MunicipalityEntity> getAll(int page, int size, String municipalityName,
            String municipalityCd, String municipalityKana, String municipalityType,
            String keyword, String sort, String order) {
        int offset = page * size;
        var items = municipalityDao.selectForAdmin(offset, size, municipalityName, municipalityCd, municipalityKana, municipalityType, keyword, sort, order);
        long total = municipalityDao.countForAdmin(municipalityName, municipalityCd, municipalityKana, municipalityType, keyword);
        return AdminPagedResponseDto.of(items, total, page, size);
    }

    /**
     * 自治体を新規登録する
     *
     * @param entity 登録する自治体エンティティ
     * @return 登録した自治体エンティティ
     */
    public MunicipalityEntity create(MunicipalityEntity entity) {
        LocalDateTime now = LocalDateTime.now();
        entity.setSystemField(new SystemField(now, now));
        municipalityDao.insert(entity);
        return entity;
    }

    /**
     * 自治体を更新する
     *
     * @param municipalityCd 自治体コード
     * @param entity         更新する自治体エンティティ
     * @return 更新した自治体エンティティ
     * @throws NoSuchElementException 自治体が存在しない場合
     */
    public MunicipalityEntity update(String municipalityCd, MunicipalityEntity entity) {
        MunicipalityEntity existing = municipalityDao.selectById(municipalityCd);
        if (existing == null) {
            throw new NoSuchElementException("自治体が見つかりません: " + municipalityCd);
        }
        entity.setMunicipalityCd(municipalityCd);
        LocalDateTime createdAt = existing.getSystemField() != null ? existing.getSystemField().getSysCreatedAt() : null;
        entity.setSystemField(new SystemField(createdAt, LocalDateTime.now()));
        municipalityDao.update(entity);
        return entity;
    }

    /**
     * CSVファイルから自治体を一括インポートする
     * <p>
     * CSVヘッダー: municipalityCd, municipalityName, municipalityKana, municipalityType
     * </p>
     *
     * @param file インポートするCSVファイル（UTF-8、1行目はヘッダー）
     * @return インポート結果
     * @throws IOException ファイル読み込みエラー
     */
    @Transactional
    public CsvImportResultDto importCsv(MultipartFile file) throws IOException {
        List<MunicipalityEntity> toInsert = new ArrayList<>();
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
                    String municipalityCd = csvVal(record, "municipalityCd");
                    if (municipalityCd == null) {
                        errors.add("行 " + record.getRecordNumber() + ": 自治体コードが空です");
                        continue;
                    }
                    MunicipalityEntity entity = new MunicipalityEntity();
                    entity.setMunicipalityCd(municipalityCd);
                    entity.setMunicipalityName(csvVal(record, "municipalityName"));
                    entity.setMunicipalityKana(csvVal(record, "municipalityKana"));
                    entity.setMunicipalityType(csvVal(record, "municipalityType"));

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
        toInsert.forEach(municipalityDao::insert);
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

    private BufferedReader skipBom(BufferedReader reader) throws IOException {
        reader.mark(1);
        if (reader.read() != '\uFEFF') {
            reader.reset();
        }
        return reader;
    }

    /**
     * 自治体を削除する
     * <p>
     * 紐付く BENEFIT または USERS が存在する場合は IllegalStateException をスローする。
     * </p>
     *
     * @param municipalityCd 自治体コード
     * @throws NoSuchElementException 自治体が存在しない場合
     * @throws IllegalStateException  依存するレコードが存在する場合
     */
    public void delete(String municipalityCd) {
        MunicipalityEntity existing = municipalityDao.selectById(municipalityCd);
        if (existing == null) {
            throw new NoSuchElementException("自治体が見つかりません: " + municipalityCd);
        }
        if (!benefitDao.selectByMunicipalityCd(municipalityCd).isEmpty()) {
            throw new IllegalStateException("この自治体に紐付く特典が存在するため削除できません");
        }
        if (!usersDao.selectByMunicipalityCd(municipalityCd).isEmpty()) {
            throw new IllegalStateException("この自治体に居住するユーザーが存在するため削除できません");
        }
        municipalityDao.delete(existing);
    }
}
