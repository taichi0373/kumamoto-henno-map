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
import io.github.taichi0373.benefit_map.repository.dao.CommunityBusDao;
import io.github.taichi0373.benefit_map.repository.dao.FareDiscountDao;
import io.github.taichi0373.benefit_map.repository.entity.AgencyEntity;
import io.github.taichi0373.benefit_map.repository.entity.SystemField;

/**
 * 事業者管理サービス
 * <p>
 * 管理者向けの事業者CRUD操作を提供する。
 * </p>
 */
@Service
public class AdminAgencyService {

    /** 事業者DAO */
    @Autowired
    private AgencyDao agencyDao;

    /** 運賃割引DAO（削除前依存チェック用） */
    @Autowired
    private FareDiscountDao fareDiscountDao;

    /** コミュニティバスDAO（削除前依存チェック用） */
    @Autowired
    private CommunityBusDao communityBusDao;

    /**
     * 事業者一覧をページング取得する
     *
     * @param page        ページ番号（0始まり）
     * @param size        ページあたり件数
     * @param agencyName  事業者名称フィルター（null の場合は全件）
     * @param agencyId    事業者IDフィルター（null の場合は全件）
     * @param agencyKana  事業者カナフィルター（null の場合は全件）
     * @param phoneNumber 電話番号フィルター（null の場合は全件）
     * @param operatorId  オペレーターIDフィルター（null の場合は全件）
     * @param sort        ソートフィールド名
     * @param order       ソート順（desc で降順）
     * @return ページングレスポンス
     */
    public AdminPagedResponseDto<AgencyEntity> getAll(int page, int size, String agencyName,
            String agencyId, String agencyKana, String phoneNumber, String operatorId,
            String sort, String order) {
        int offset = page * size;
        var items = agencyDao.selectForAdmin(offset, size, agencyName, agencyId, agencyKana, phoneNumber, operatorId, sort, order);
        long total = agencyDao.countForAdmin(agencyName, agencyId, agencyKana, phoneNumber, operatorId);
        return AdminPagedResponseDto.of(items, total, page, size);
    }

    /**
     * 事業者全件を取得する（コミュニティバス画面のセレクト用）
     *
     * @return 事業者エンティティリスト
     */
    public List<AgencyEntity> getAllForSelect() {
        return agencyDao.selectAll();
    }

    /**
     * 事業者を新規登録する
     *
     * @param entity 登録する事業者エンティティ
     * @return 登録した事業者エンティティ
     */
    public AgencyEntity create(AgencyEntity entity) {
        LocalDateTime now = LocalDateTime.now();
        entity.setSystemField(new SystemField(now, now));
        agencyDao.insert(entity);
        return entity;
    }

    /**
     * 事業者を更新する
     *
     * @param agencyId 事業者ID
     * @param entity   更新する事業者エンティティ
     * @return 更新した事業者エンティティ
     * @throws NoSuchElementException 事業者が存在しない場合
     */
    public AgencyEntity update(String agencyId, AgencyEntity entity) {
        AgencyEntity existing = agencyDao.selectById(agencyId);
        if (existing == null) {
            throw new NoSuchElementException("事業者が見つかりません: " + agencyId);
        }
        entity.setAgencyId(agencyId);
        LocalDateTime createdAt = existing.getSystemField() != null ? existing.getSystemField().getSysCreatedAt() : null;
        entity.setSystemField(new SystemField(createdAt, LocalDateTime.now()));
        agencyDao.update(entity);
        return entity;
    }

    /**
     * 事業者を削除する
     * <p>
     * 紐付く FARE_DISCOUNT または COMMUNITY_BUS が存在する場合は IllegalStateException をスローする。
     * </p>
     *
     * @param agencyId 事業者ID
     * @throws NoSuchElementException 事業者が存在しない場合
     * @throws IllegalStateException  依存するレコードが存在する場合
     */
    /**
     * CSVファイルから事業者を一括インポートする（upsert）
     * <p>
     * CSVヘッダー: agencyId, agencyName, agencyKana, phoneNumber, agencyUrl, operatorId
     * </p>
     *
     * @param file インポートするCSVファイル（UTF-8、1行目はヘッダー）
     * @return インポート結果
     * @throws IOException ファイル読み込みエラー
     */
    @Transactional
    public CsvImportResultDto importCsv(MultipartFile file) throws IOException {
        List<AgencyEntity> toInsert = new ArrayList<>();
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
                    String agencyId = csvVal(record, "agencyId");
                    if (agencyId == null) {
                        errors.add("行 " + record.getRecordNumber() + ": 事業者IDが空です");
                        continue;
                    }
                    AgencyEntity entity = new AgencyEntity();
                    entity.setAgencyId(agencyId);
                    entity.setAgencyName(csvVal(record, "agencyName"));
                    entity.setAgencyKana(csvVal(record, "agencyKana"));
                    entity.setPhoneNumber(csvVal(record, "phoneNumber"));
                    entity.setAgencyUrl(csvVal(record, "agencyUrl"));
                    entity.setOperatorId(csvVal(record, "operatorId"));

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
        toInsert.forEach(agencyDao::insert);
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

    public void delete(String agencyId) {
        AgencyEntity existing = agencyDao.selectById(agencyId);
        if (existing == null) {
            throw new NoSuchElementException("事業者が見つかりません: " + agencyId);
        }
        if (!fareDiscountDao.selectByAgencyId(agencyId).isEmpty()) {
            throw new IllegalStateException("この事業者に紐付く運賃割引が存在するため削除できません");
        }
        if (!communityBusDao.selectByCommunityBusId(agencyId).isEmpty()) {
            throw new IllegalStateException("この事業者に紐付くコミュニティバス路線が存在するため削除できません");
        }
        agencyDao.delete(existing);
    }
}
