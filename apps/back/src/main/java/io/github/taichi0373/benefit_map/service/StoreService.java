package com.example.back.service;

import com.example.back.dto.StoreInfo;
import com.example.back.dto.StoreResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class StoreService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${yahoo.api.key}")
    private String yahooApiKey;

    @Value("${yahoo.api.url}")
    private String yahooApiBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public StoreResponse getFacilityInfo() {
        try {
            String sql = """
                SELECT 
                    store_benefit.id, 
                    store_benefit.name,
                    store_benefit.detail,
                    store_benefit.tel_number,
                    store_condition.license_status,
                    store_condition.min_age,
                    store_condition.max_age,
                    store_condition.note,
                    municipality.name AS municipality_name
                FROM 
                    store_condition
                JOIN 
                    store_benefit ON store_condition.store_benefit_id = store_benefit.id
                JOIN 
                    municipality ON store_condition.address = municipality.id
                WHERE 
                    location_fetched IS NULL OR location_fetched = TRUE
                """;

            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            
            if (rows.isEmpty()) {
                return new StoreResponse(false, "施設が見つかりません", new ArrayList<>());
            }

            List<StoreInfo> stores = fetchCoordinatesParallel(rows);
            
            return new StoreResponse(true, "施設情報を取得しました", stores);

        } catch (Exception e) {
            e.printStackTrace();
            return new StoreResponse(false, "データベースエラー: " + e.getMessage(), new ArrayList<>());
        }
    }

    private List<StoreInfo> fetchCoordinatesParallel(List<Map<String, Object>> rows) {
        List<CompletableFuture<StoreInfo>> futures = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            CompletableFuture<StoreInfo> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return processStore(row);
                } catch (Exception e) {
                    e.printStackTrace();
                    return createStoreInfoFromRow(row, null, null, null);
                }
            }, executorService);
            futures.add(future);
        }

        List<StoreInfo> stores = new ArrayList<>();
        for (CompletableFuture<StoreInfo> future : futures) {
            try {
                StoreInfo store = future.get();
                if (store != null) {
                    stores.add(store);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return stores;
    }

    private StoreInfo processStore(Map<String, Object> row) throws Exception {
        Long id = ((Number) row.get("id")).longValue();
        String name = (String) row.get("name");
        
        // Yahoo APIで座標を取得
        String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);
        String apiUrl = String.format(
            "%s/search/local/V1/localSearch?appid=%s&query=%s&ac=43&output=json&results=1",
            yahooApiBaseUrl, yahooApiKey, encodedName
        );

        try {
            String response = restTemplate.getForObject(apiUrl, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);
            
            Double longitude = null;
            Double latitude = null;
            String address = null;
            boolean locationFetched = false;

            if (jsonNode.has("Feature") && jsonNode.get("Feature").size() > 0) {
                JsonNode feature = jsonNode.get("Feature").get(0);
                if (feature.has("Geometry") && feature.get("Geometry").has("Coordinates")) {
                    String coordinates = feature.get("Geometry").get("Coordinates").asText();
                    String[] coords = coordinates.split(",");
                    longitude = Double.parseDouble(coords[0]);
                    latitude = Double.parseDouble(coords[1]);
                    locationFetched = true;
                    
                    if (feature.has("Property") && feature.get("Property").has("Address")) {
                        address = feature.get("Property").get("Address").asText();
                    }
                }
            }

            // データベースを更新
            updateLocationFetched(id, locationFetched);

            return createStoreInfoFromRow(row, longitude, latitude, address);

        } catch (Exception e) {
            // APIエラーの場合はlocation_fetched = falseで更新
            updateLocationFetched(id, false);
            throw e;
        }
    }

    private void updateLocationFetched(Long id, boolean locationFetched) {
        String updateSql = "UPDATE store_benefit SET location_fetched = ? WHERE id = ?";
        jdbcTemplate.update(updateSql, locationFetched, id);
    }

    private StoreInfo createStoreInfoFromRow(Map<String, Object> row, Double longitude, Double latitude, String address) {
        StoreInfo store = new StoreInfo();
        store.setId(((Number) row.get("id")).longValue());
        store.setName((String) row.get("name"));
        store.setDetail((String) row.get("detail"));
        store.setTelNumber((String) row.get("tel_number"));
        store.setLicenseStatus((String) row.get("license_status"));
        store.setMinAge(row.get("min_age") != null ? ((Number) row.get("min_age")).intValue() : null);
        store.setMaxAge(row.get("max_age") != null ? ((Number) row.get("max_age")).intValue() : null);
        store.setNote((String) row.get("note"));
        store.setMunicipalityName((String) row.get("municipality_name"));
        store.setLongitude(longitude);
        store.setLatitude(latitude);
        store.setAddress(address);
        return store;
    }
}
