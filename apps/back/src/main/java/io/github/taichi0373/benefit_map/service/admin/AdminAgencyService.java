package io.github.taichi0373.benefit_map.service.admin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.taichi0373.benefit_map.dto.admin.AdminPagedResponseDto;
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
     * @param page       ページ番号（0始まり）
     * @param size       ページあたり件数
     * @param agencyName 事業者名称フィルター（null の場合は全件）
     * @return ページングレスポンス
     */
    public AdminPagedResponseDto<AgencyEntity> getAll(int page, int size, String agencyName) {
        int offset = page * size;
        var items = agencyDao.selectForAdmin(offset, size, agencyName);
        long total = agencyDao.countForAdmin(agencyName);
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
