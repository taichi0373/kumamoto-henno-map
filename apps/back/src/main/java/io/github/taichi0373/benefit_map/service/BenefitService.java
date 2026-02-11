package io.github.taichi0373.benefit_map.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.taichi0373.benefit_map.util.AgeUtils;
import io.github.taichi0373.benefit_map.util.ValidateUtils;
import io.github.taichi0373.benefit_map.dto.BenefitEligibilityDto;
import io.github.taichi0373.benefit_map.repository.dao.BenefitDao;
import io.github.taichi0373.benefit_map.repository.dao.BenefitEligibilityDao;
import io.github.taichi0373.benefit_map.repository.dao.UsersDao;
import io.github.taichi0373.benefit_map.repository.entity.BenefitEligibilityEntity;
import io.github.taichi0373.benefit_map.repository.entity.BenefitEntity;
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
    private BenefitDao benefitDao;

    /**
     * 特典適用条件取得DAO
     */
    @Autowired
    private BenefitEligibilityDao benefitEligibilityDao;
    
    /**
     * 検索条件に一致する特典を検索
     */
    public List<BenefitEntity> searchBenefits(BenefitEligibilityDto request) {
        try {
            // 特典適用条件に一致する特典IDを取得
             List<BenefitEligibilityEntity> benefitEligibilityList = benefitEligibilityDao.selectEligibleBenefitIds(
                request.getMinAge(),
                request.getMaxAge(),
                request.getLicenseStatus(),
                request.getMunicipalityCd()
            );

            if (ValidateUtils.isNullOrEmpty(benefitEligibilityList)) {
                return new ArrayList<>();
            }

            // 取得した特典IDに基づき、特典情報を取得
            List<String> benefitIds = new ArrayList<>();
            for (BenefitEligibilityEntity benefitEligibilityEntity : benefitEligibilityList) {
                benefitIds.add(benefitEligibilityEntity.getBenefitId());
            }

            List<BenefitEntity> benefits = benefitDao.selectByIds(benefitIds);
            return benefits;

        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * ユーザーIDからユーザーが受けられる特典を検索
     */
    public List<BenefitEntity> getUsersBenefits(Integer userId) {
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
            benefitEligibilityDto.setMinAge(age);
            benefitEligibilityDto.setMaxAge(age);
            benefitEligibilityDto.setLicenseStatus(user.getLicenseStatus());
            benefitEligibilityDto.setMunicipalityCd(user.getMunicipalityCode());
            List<BenefitEntity> benefits = searchBenefits(benefitEligibilityDto);
            return benefits;
        } catch (Exception e) {
            return null;
        }
    }
}
