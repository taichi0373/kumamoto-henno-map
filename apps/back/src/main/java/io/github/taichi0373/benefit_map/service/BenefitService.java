package io.github.taichi0373.benefit_map.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.github.taichi0373.benefit_map.dto.BenefitSearchRequest;
import io.github.taichi0373.benefit_map.repository.dao.BenefitDao;
import io.github.taichi0373.benefit_map.repository.dao.BenefitEligibilityDao;
import io.github.taichi0373.benefit_map.repository.dao.UsersDao;
import io.github.taichi0373.benefit_map.repository.entity.BenefitEntity;
import io.github.taichi0373.benefit_map.repository.entity.UsersEntity;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BenefitService {
    
    private static final Logger log = LoggerFactory.getLogger(BenefitService.class);
    
    private final BenefitDao benefitDao;
    private final BenefitEligibilityDao benefitEligibilityDao;
    private final UsersDao usersDao;
    private final YolpService yolpService;
    
    @Value("${yahoo.api.key}")
    private String yahooApiKey;
    
    @Value("${yahoo.api.url}")
    private String yahooApiUrl;
    
    /**
     * ユーザー条件に一致する特典を検索
     */
    public Map<String, Object> searchBenefits(BenefitSearchRequest request) {
        try {
            log.info("特典検索リクエスト: {}", request);
            
            // 年齢、運転免許所持状況、自治体コードから条件に一致する特典IDを取得
            List<String> eligibleBenefitIds = benefitEligibilityDao.selectEligibleBenefitIds(
                request.getAge(),
                request.getLicenseStatus(),
                request.getAddress()
            );
            
            if (eligibleBenefitIds.isEmpty()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "条件に一致する特典が見つかりませんでした");
                result.put("benefits", new ArrayList<>());
                return result;
            }
            
            // 特典IDリストから特典情報を取得
            List<BenefitEntity> benefits = benefitDao.selectByIds(eligibleBenefitIds);
            
            // レスポンス用のリストを作成
            List<Map<String, Object>> benefitList = new ArrayList<>();
            
            for (BenefitEntity benefit : benefits) {
                Map<String, Object> benefitMap = convertToMap(benefit);
                
                // カテゴリコードがBS**で始まる場合は、Yahoo APIから位置情報を取得
                if (benefit.getCategoryCd() != null && benefit.getCategoryCd().startsWith("BS")) {
                    try {
                        Map<String, Object> locationInfo = yolpService.searchLocation(benefit.getBenefitName());
                        if (locationInfo != null && locationInfo.containsKey("suggestions")) {
                            @SuppressWarnings("unchecked")
                            List<Map<String, Object>> suggestions = (List<Map<String, Object>>) locationInfo.get("suggestions");
                            if (!suggestions.isEmpty()) {
                                Map<String, Object> firstSuggestion = suggestions.get(0);
                                benefitMap.put("latitude", firstSuggestion.get("latitude"));
                                benefitMap.put("longitude", firstSuggestion.get("longitude"));
                                benefitMap.put("address", firstSuggestion.get("address"));
                            }
                        }
                    } catch (Exception e) {
                        log.warn("Yahoo APIから位置情報を取得できませんでした: {}", benefit.getBenefitName(), e);
                    }
                }
                
                benefitList.add(benefitMap);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("benefits", benefitList);
            result.put("count", benefitList.size());
            
            return result;
            
        } catch (Exception e) {
            log.error("特典検索エラー", e);
            throw new RuntimeException("特典検索に失敗しました", e);
        }
    }
    
    /**
     * ユーザーIDから特典を検索
     */
    public Map<String, Object> getUserBenefits(String userId) {
        try {
            log.info("ユーザー特典取得: userId={}", userId);
            
            // ユーザー情報を取得
            UsersEntity user = usersDao.selectById(userId);
            if (user == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "ユーザーが見つかりませんでした");
                result.put("benefits", new ArrayList<>());
                return result;
            }
            
            // 年齢を計算
            Integer age = null;
            if (user.getBirthDate() != null) {
                age = Period.between(user.getBirthDate(), LocalDate.now()).getYears();
            }
            
            // 検索リクエストを作成
            BenefitSearchRequest request = new BenefitSearchRequest(
                age,
                user.getLicenseStatus(),
                user.getMunicipalityCode()
            );
            
            return searchBenefits(request);
            
        } catch (Exception e) {
            log.error("ユーザー特典取得エラー: userId={}", userId, e);
            throw new RuntimeException("ユーザー特典取得に失敗しました", e);
        }
    }
    
    /**
     * BenefitEntityをMapに変換
     */
    private Map<String, Object> convertToMap(BenefitEntity benefit) {
        Map<String, Object> map = new HashMap<>();
        map.put("benefitId", benefit.getBenefitId());
        map.put("municipalityCd", benefit.getMunicipalityCd());
        map.put("categoryCd", benefit.getCategoryCd());
        map.put("benefitName", benefit.getBenefitName());
        map.put("benefitShortName", benefit.getBenefitShortName());
        map.put("benefitDetail", benefit.getBenefitDetail());
        map.put("expDetail", benefit.getExpDetail());
        map.put("phoneNumber", benefit.getPhoneNumber());
        map.put("benefitUrl", benefit.getBenefitUrl());
        
        if (benefit.getSystemField() != null) {
            map.put("sysCreatedAt", benefit.getSystemField().getSysCreatedAt());
            map.put("sysUpdatedAt", benefit.getSystemField().getSysUpdatedAt());
        }
        
        return map;
    }
}
