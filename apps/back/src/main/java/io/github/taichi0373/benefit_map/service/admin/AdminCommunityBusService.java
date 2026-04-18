package io.github.taichi0373.benefit_map.service.admin;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.taichi0373.benefit_map.dto.admin.AdminPagedResponseDto;
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
     * @param page ページ番号（0始まり）
     * @param size ページあたり件数
     * @return ページングレスポンス
     */
    public AdminPagedResponseDto<CommunityBusEntity> getAll(int page, int size) {
        int offset = page * size;
        var items = communityBusDao.selectForAdmin(offset, size);
        long total = communityBusDao.countAll();
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
