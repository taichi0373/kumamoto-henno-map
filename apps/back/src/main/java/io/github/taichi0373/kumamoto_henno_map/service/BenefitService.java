package io.github.taichi0373.kumamoto_henno_map.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.taichi0373.kumamoto_henno_map.util.AgeUtils;
import io.github.taichi0373.kumamoto_henno_map.util.ValidateUtils;
import io.github.taichi0373.kumamoto_henno_map.dto.BenefitCategoryDto;
import io.github.taichi0373.kumamoto_henno_map.dto.BenefitEligibilityDto;
import io.github.taichi0373.kumamoto_henno_map.repository.dao.BenefitCategoryDao;
import io.github.taichi0373.kumamoto_henno_map.repository.dao.BenefitDetailDao;
import io.github.taichi0373.kumamoto_henno_map.repository.dao.UsersDao;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.BenefitDetailEntity;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.UsersEntity;

/**
 * 特典サービス
 * <p>
 * 特典の検索・ユーザー向け特典絞り込みなどのビジネスロジックを提供する。
 * </p>
 */
@Service
public class BenefitService {

    /**
     * ユーザー情報取得DAO
     */
    @Autowired
    private UsersDao usersDao;

    /**
     * 特典カテゴリ取得DAO
     */
    @Autowired
    private BenefitCategoryDao benefitCategoryDao;

    /**
     * 特典情報取得DAO
     */
    @Autowired
    private BenefitDetailDao benefitDetailDao;

    /**
     * 座標データを持つ特典を全件取得（マーカー表示用）
     * @return 座標付き特典一覧
     */
    public List<BenefitDetailEntity> getBenefitsWithCoordinates() {
        return benefitDetailDao.selectWithCoordinates();
    }

    /**
     * 検索条件に一致する特典を検索
     */
    public List<BenefitDetailEntity> searchBenefits(BenefitEligibilityDto request) {
        // 現在地周辺フィルタリング時はDB側でバウンディングボックスによる事前絞り込みを行う
        BigDecimal minLat = null, maxLat = null, minLng = null, maxLng = null;
        double radiusKm = 2.0;
        if (request.getLatitude() != null && request.getLongitude() != null) {
            radiusKm = (request.getRadiusKm() != null) ? request.getRadiusKm() : 2.0;
            double lat = request.getLatitude();
            double lng = request.getLongitude();
            // 緯度方向: 1度 ≈ 111km
            double latDelta = radiusKm / 111.0;
            // 経度方向: 1度 ≈ 111km * cos(緯度)
            double lngDelta = radiusKm / (111.0 * Math.cos(Math.toRadians(lat)));
            minLat = BigDecimal.valueOf(lat - latDelta);
            maxLat = BigDecimal.valueOf(lat + latDelta);
            minLng = BigDecimal.valueOf(lng - lngDelta);
            maxLng = BigDecimal.valueOf(lng + lngDelta);
        }

        // 特典適用条件に一致する特典を取得（現在地指定時はバウンディングボックスでDB側絞り込み）
        List<BenefitDetailEntity> results = benefitDetailDao.selectEligible(
            request.getAge(),
            request.getLicenseStatus(),
            request.getMunicipalityCd(),
            request.getKeyword(),
            request.getCategoryCd(),
            minLat, maxLat, minLng, maxLng
        );

        // 現在地周辺フィルタリング（Haversine公式で正確な距離判定）
        if (request.getLatitude() != null && request.getLongitude() != null) {
            final double finalRadiusKm = radiusKm;
            results = results.stream()
                .filter(b -> b.getLatitude() != null && b.getLongitude() != null)
                .filter(b -> isWithinRadius(
                    b.getLatitude().doubleValue(), b.getLongitude().doubleValue(),
                    request.getLatitude(), request.getLongitude(), finalRadiusKm))
                .collect(java.util.stream.Collectors.toList());
        }

        return results;
    }

    /**
     * 有効なカテゴリ一覧を表示順で取得
     * @return カテゴリDTO一覧
     */
    public List<BenefitCategoryDto> getCategories() {
        return benefitCategoryDao.selectAllOrdered().stream()
                .map(entity -> {
                    BenefitCategoryDto dto = new BenefitCategoryDto();
                    dto.setCategoryCd(entity.getCategoryCd());
                    dto.setCategoryName(entity.getCategoryName());
                    dto.setDisplayOrder(entity.getDisplayOrder());
                    dto.setIsActive(entity.getIsActive());
                    return dto;
                })
                .toList();
    }

    /**
     * 特典IDで特典詳細を取得する
     *
     * @param benefitId 特典ID
     * @return 特典詳細（存在しない場合は空のOptional）
     */
    public Optional<BenefitDetailEntity> getBenefitById(String benefitId) {
        return benefitDetailDao.selectByBenefitId(benefitId).stream().findFirst();
    }

    /**
     * 2点間の距離がradiusKm以内かどうかをHaversine公式で判定する
     *
     * @param lat1      地点1の緯度
     * @param lng1      地点1の経度
     * @param lat2      地点2の緯度
     * @param lng2      地点2の経度
     * @param radiusKm  判定半径（km）
     * @return radiusKm以内であればtrue
     */
    private boolean isWithinRadius(double lat1, double lng1,
                                    double lat2, double lng2,
                                    double radiusKm) {
        final double R = 6371.0; // 地球の半径（km）
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c <= radiusKm;
    }

    /**
     * ユーザーIDからユーザーが受けられる特典を検索
     */
    public List<BenefitDetailEntity> getUsersBenefits(Long userId) {
        // ユーザー情報を取得
        UsersEntity user = usersDao.selectById(userId);
        if (ValidateUtils.isNullOrEmpty(user)) {
            // ユーザーが存在しない場合は空リストを返す
            return List.of();
        }

        // 年齢を計算
        Integer age = null;
        if (!ValidateUtils.isNullOrEmpty(user.getBirthDate())) {
            try {
                age = AgeUtils.calculateAge(user.getBirthDate());
            } catch (IllegalArgumentException e) {
                // 未来日等、年齢計算不可の場合は年齢フィルターをスキップ
            }
        }

        // 特典適用条件に一致する特典を取得
        BenefitEligibilityDto benefitEligibilityDto = new BenefitEligibilityDto();
        benefitEligibilityDto.setAge(age);
        benefitEligibilityDto.setLicenseStatus(user.getLicenseStatus());
        benefitEligibilityDto.setMunicipalityCd(user.getMunicipalityCd());
        return searchBenefits(benefitEligibilityDto);
    }
}
