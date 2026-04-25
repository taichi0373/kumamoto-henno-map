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
import io.github.taichi0373.benefit_map.repository.entity.CommunityBusEntity;
import io.github.taichi0373.benefit_map.repository.entity.SystemField;

/**
 * コミュニティバス路線管理サービス
 * <p>
 * 管理者向けのコミュニティバス路線CRUD操作を提供する。
 * </p>
 */
@Service
public class AdminCommunityBusService {

    /** コミュニティバスDAO */
    @Autowired
    private CommunityBusDao communityBusDao;

    /** 事業者DAO（外部キー存在チェック用） */
    @Autowired
    private AgencyDao agencyDao;

    /**
     * コミュニティバス路線一覧をページング取得する
     *
     * @param page           ページ番号（0始まり）
     * @param size           ページあたり件数
     * @param routeName      行先名称フィルター（null の場合は全件）
     * @param routeId        路線IDフィルター（null の場合は全件）
     * @param communityBusId コミュニティバスIDフィルター（null の場合は全件）
     * @param sort           ソートフィールド名
     * @param order          ソート順（desc で降順）
     * @return ページングレスポンス
     */
    public AdminPagedResponseDto<CommunityBusEntity> getAll(int page, int size, String routeName,
            String routeId, String communityBusId, String keyword, String sort, String order) {
        int offset = page * size;
        var items = communityBusDao.selectForAdmin(offset, size, routeName, routeId, communityBusId, keyword, sort, order);
        long total = communityBusDao.countForAdmin(routeName, routeId, communityBusId, keyword);
        return AdminPagedResponseDto.of(items, total, page, size);
    }

    /**
     * コミュニティバス路線を新規登録する
     *
     * @param entity 登録するコミュニティバスエンティティ
     * @return 登録したコミュニティバスエンティティ
     * @throws IllegalArgumentException COMMUNITY_BUS_ID（AGENCY_ID）が存在しない場合
     */
    public CommunityBusEntity create(CommunityBusEntity entity) {
        validateCommunityBusId(entity.getCommunityBusId());
        LocalDateTime now = LocalDateTime.now();
        entity.setSystemField(new SystemField(now, now));
        communityBusDao.insert(entity);
        return entity;
    }

    /**
     * コミュニティバス路線を更新する
     *
     * @param routeId 路線ID
     * @param entity  更新するコミュニティバスエンティティ
     * @return 更新したコミュニティバスエンティティ
     * @throws NoSuchElementException   路線が存在しない場合
     * @throws IllegalArgumentException COMMUNITY_BUS_ID（AGENCY_ID）が存在しない場合
     */
    public CommunityBusEntity update(String routeId, CommunityBusEntity entity) {
        CommunityBusEntity existing = communityBusDao.selectById(routeId);
        if (existing == null) {
            throw new NoSuchElementException("コミュニティバス路線が見つかりません: " + routeId);
        }
        validateCommunityBusId(entity.getCommunityBusId());
        entity.setRouteId(routeId);
        LocalDateTime createdAt = existing.getSystemField() != null ? existing.getSystemField().getSysCreatedAt() : null;
        entity.setSystemField(new SystemField(createdAt, LocalDateTime.now()));
        communityBusDao.update(entity);
        return entity;
    }

    /**
     * コミュニティバス路線を削除する
     *
     * @param routeId 路線ID
     * @throws NoSuchElementException 路線が存在しない場合
     */
    /**
     * CSVファイルからコミュニティバス路線を一括インポートする（upsert）
     * <p>
     * CSVヘッダー: routeId, communityBusId, routeName
     * </p>
     *
     * @param file インポートするCSVファイル（UTF-8、1行目はヘッダー）
     * @return インポート結果
     * @throws IOException ファイル読み込みエラー
     */
    @Transactional
    public CsvImportResultDto importCsv(MultipartFile file) throws IOException {
        List<CommunityBusEntity> toInsert = new ArrayList<>();
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
                    String routeId = csvVal(record, "routeId");
                    if (routeId == null) {
                        errors.add("行 " + record.getRecordNumber() + ": 路線IDが空です");
                        continue;
                    }
                    String communityBusId = csvVal(record, "communityBusId");
                    validateCommunityBusId(communityBusId);

                    CommunityBusEntity entity = new CommunityBusEntity();
                    entity.setRouteId(routeId);
                    entity.setCommunityBusId(communityBusId);
                    entity.setRouteName(csvVal(record, "routeName"));

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
        toInsert.forEach(communityBusDao::insert);
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

    public void delete(String routeId) {
        CommunityBusEntity existing = communityBusDao.selectById(routeId);
        if (existing == null) {
            throw new NoSuchElementException("コミュニティバス路線が見つかりません: " + routeId);
        }
        communityBusDao.delete(existing);
    }

    /**
     * コミュニティバスID（事業者ID）の存在チェック
     *
     * @param communityBusId コミュニティバスID
     * @throws IllegalArgumentException AGENCY_ID が存在しない場合
     */
    private void validateCommunityBusId(String communityBusId) {
        if (communityBusId != null && agencyDao.selectById(communityBusId) == null) {
            throw new IllegalArgumentException("指定された事業者ID（コミュニティバスID）が存在しません: " + communityBusId);
        }
    }
}
