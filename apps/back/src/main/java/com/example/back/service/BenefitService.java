package com.example.back.service;

import com.example.back.dto.BenefitSearchRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BenefitService {

    private static final Logger log = LoggerFactory.getLogger(BenefitService.class);

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> searchBenefits(BenefitSearchRequest request) {
        try {
            String sql = """
                SELECT b.*, c.*, m.name as municipality_name
                FROM benefits b
                LEFT JOIN conditions c ON b.id = c.benefit_id
                LEFT JOIN municipality m ON c.address = m.id
                WHERE 
                    (c.min_age IS NULL OR c.min_age = '' OR 
                     (c.min_age ~ '^[0-9]+$' AND CAST(c.min_age AS INTEGER) <= ?)) 
                    AND (c.max_age IS NULL OR c.max_age = '' OR 
                         (c.max_age ~ '^[0-9]+$' AND CAST(c.max_age AS INTEGER) >= ?))
                    AND (c.license_status IS NULL OR c.license_status = '' OR c.license_status = 'any' OR c.license_status = ?)
                    AND (c.address IS NULL OR c.address = '' OR c.address = ?)
                ORDER BY b.municipality = ? DESC
                """;

            List<Map<String, Object>> benefits = jdbcTemplate.queryForList(sql,
                    request.getAge(), request.getAge(), request.getLicenseStatus(),
                    request.getAddress(), request.getAddress());

            Map<String, Object> result = new HashMap<>();
            if (!benefits.isEmpty()) {
                result.put("success", true);
                result.put("benefits", benefits);
            } else {
                result.put("success", false);
                result.put("message", "特典が見つかりません");
            }

            return result;

        } catch (Exception e) {
            log.error("Error searching benefits", e);
            throw new RuntimeException("Failed to search benefits", e);
        }
    }

    public JsonNode getUserBenefits(Long userId) {
        try {
            // まずユーザー情報を取得
            String userSql = "SELECT age, license_status, address FROM users WHERE id = ?";
            Map<String, Object> user = jdbcTemplate.queryForMap(userSql, userId);

            // ユーザー情報に基づいて特典を検索
            BenefitSearchRequest request = new BenefitSearchRequest(
                    (Integer) user.get("age"),
                    (String) user.get("license_status"),
                    (String) user.get("address")
            );

            Map<String, Object> result = searchBenefits(request);
            return objectMapper.valueToTree(result.get("benefits"));

        } catch (Exception e) {
            log.error("Error getting user benefits", e);
            throw new RuntimeException("Failed to get user benefits", e);
        }
    }

    public JsonNode getTransitBenefits() {
        try {
            String sql = """
                SELECT 
                    b.*,
                    c.*,
                    m.name as municipality_name
                FROM benefits b
                LEFT JOIN conditions c ON b.id = c.benefit_id
                LEFT JOIN municipality m ON c.address = m.id
                WHERE b.category LIKE '%交通%' OR b.category LIKE '%バス%' OR b.category LIKE '%電車%'
                ORDER BY b.municipality
                """;

            List<Map<String, Object>> benefits = jdbcTemplate.queryForList(sql);
            return objectMapper.valueToTree(benefits);

        } catch (Exception e) {
            log.error("Error getting transit benefits", e);
            throw new RuntimeException("Failed to get transit benefits", e);
        }
    }

    public JsonNode getStores() {
        try {
            String sql = """
                SELECT DISTINCT
                    store_condition.id,
                    store_condition.name,
                    store_condition.category,
                    store_condition.description,
                    store_condition.address,
                    store_condition.latitude,
                    store_condition.longitude,
                    municipality.name as municipality_name
                FROM 
                    store_condition 
                JOIN 
                    municipality ON store_condition.address = municipality.id
                ORDER BY store_condition.name
                """;

            List<Map<String, Object>> stores = jdbcTemplate.queryForList(sql);
            return objectMapper.valueToTree(stores);

        } catch (Exception e) {
            log.error("Error getting stores", e);
            throw new RuntimeException("Failed to get stores", e);
        }
    }

    public JsonNode getMunicipalities() {
        try {
            String sql = "SELECT id, name FROM municipality ORDER BY name";
            List<Map<String, Object>> municipalities = jdbcTemplate.queryForList(sql);
            return objectMapper.valueToTree(municipalities);

        } catch (Exception e) {
            log.error("Error getting municipalities", e);
            throw new RuntimeException("Failed to get municipalities", e);
        }
    }
}
