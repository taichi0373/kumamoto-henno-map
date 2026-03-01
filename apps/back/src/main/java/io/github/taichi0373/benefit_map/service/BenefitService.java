package io.github.taichi0373.benefit_map.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.taichi0373.benefit_map.util.AgeUtils;
import io.github.taichi0373.benefit_map.util.ValidateUtils;
import io.github.taichi0373.benefit_map.dto.BenefitEligibilityDto;
import io.github.taichi0373.benefit_map.repository.dao.BenefitDetailDao;
import io.github.taichi0373.benefit_map.repository.dao.UsersDao;
import io.github.taichi0373.benefit_map.repository.entity.BenefitDetailEntity;
import io.github.taichi0373.benefit_map.repository.entity.UsersEntity;

@Service
public class BenefitService {

    /**
     * ユーザー情報取得DAO
     */
    @Autowired
    private UsersDao usersDao;

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
            request.getMunicipalityCd()
        );
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
        Integer age = AgeUtils.calculateAge(user.getBirthDate());

        // 特典適用条件に一致する特典を取得
        BenefitEligibilityDto benefitEligibilityDto = new BenefitEligibilityDto();
        benefitEligibilityDto.setAge(age);
        benefitEligibilityDto.setLicenseStatus(user.getLicenseStatus());
        benefitEligibilityDto.setMunicipalityCd(user.getMunicipalityCd());
        return searchBenefits(benefitEligibilityDto);
    }
}
