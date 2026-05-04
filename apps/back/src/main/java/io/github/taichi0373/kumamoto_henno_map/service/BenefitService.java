package io.github.taichi0373.kumamoto_henno_map.service;

import java.util.List;

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
     * 検索条件に一致する特典を検索
     */
    public List<BenefitDetailEntity> searchBenefits(BenefitEligibilityDto request) {
        // 特典適用条件に一致する特典を取得
        return benefitDetailDao.selectEligible(
            request.getAge(),
            request.getLicenseStatus(),
            request.getMunicipalityCd(),
            request.getKeyword(),
            request.getCategoryCd()
        );
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
