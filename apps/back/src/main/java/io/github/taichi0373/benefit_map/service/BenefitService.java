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
        try {
            // 特典適用条件に一致する特典を取得
            List<BenefitDetailEntity> benefits = benefitDetailDao.selectEligible(
                request.getAge(),
                request.getLicenseStatus(),
                request.getMunicipalityCd()
                );
            return benefits;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * ユーザーIDからユーザーが受けられる特典を検索
     */
    public List<BenefitDetailEntity> getUsersBenefits(Long userId) {
        try {
            // ユーザー情報を取得
            UsersEntity user = usersDao.selectById(userId);
            if (ValidateUtils.isNullOrEmpty(user)) {
                return null;
            }

            // 年齢を計算
            Integer age = AgeUtils.calculateAge(user.getBirthDate());

            // 特典適用条件に一致する特典を取得
            BenefitEligibilityDto benefitEligibilityDto = new BenefitEligibilityDto();
            benefitEligibilityDto.setAge(age);
            benefitEligibilityDto.setLicenseStatus(user.getLicenseStatus());
            benefitEligibilityDto.setMunicipalityCd(user.getMunicipalityCode());
            List<BenefitDetailEntity> benefits = searchBenefits(benefitEligibilityDto);
            return benefits;
        } catch (Exception e) {
            return null;
        }
    }
}
