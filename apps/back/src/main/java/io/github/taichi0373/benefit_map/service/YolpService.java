package com.example.back.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
public class YolpService {

    private static final Logger log = LoggerFactory.getLogger(YolpService.class);

    @Value("${yahoo.api.key}")
    private String apiKey;

    @Value("${yahoo.api.url}")
    private String apiUrl;

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> searchLocation(String query) throws IOException, ParseException {
        // Nominatim APIを使用してジオコーディングを実行
        String searchUrl = buildNominatimSearchUrl(query);
        log.info("Nominatim Search URL: {}", searchUrl);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(searchUrl);
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("User-Agent", "Benefit Map");

            ClassicHttpResponse response = httpClient.execute(httpGet);
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            if (response.getCode() != 200) {
                log.error("Nominatim API error: {} - {}", response.getCode(), responseBody);
                throw new RuntimeException("Failed to search location");
            }

            JsonNode nominatimResponse = objectMapper.readTree(responseBody);
            return processNominatimResponse(nominatimResponse);
        }
    }

    public JsonNode getStoresWithCoordinates() {
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
                WHERE 
                    (location_fetched IS NULL OR location_fetched = TRUE)
                    AND latitude IS NOT NULL 
                    AND longitude IS NOT NULL
                """;

            List<Map<String, Object>> stores = jdbcTemplate.queryForList(sql);
            return objectMapper.valueToTree(stores);

        } catch (Exception e) {
            log.error("Error getting stores with coordinates", e);
            throw new RuntimeException("Failed to get stores", e);
        }
    }

    private String buildNominatimSearchUrl(String query) {
        String encodedQuery = URLEncoder.encode("日本, 熊本県, " + query, StandardCharsets.UTF_8);
        return String.format("https://nominatim.openstreetmap.org/search?q=%s&format=json&addressdetails=1&countrycodes=JP&limit=5",
                encodedQuery);
    }

    private String buildSearchUrl(String query) {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        return String.format("%s?appid=%s&query=%s&ac=43&output=json&results=5",
                apiUrl, apiKey, encodedQuery);
    }

    private Map<String, Object> processNominatimResponse(JsonNode response) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> suggestions = new ArrayList<>();

        if (response.isArray() && response.size() > 0) {
            for (JsonNode item : response) {
                Map<String, Object> suggestion = new HashMap<>();
                
                suggestion.put("id", UUID.randomUUID().toString());
                suggestion.put("name", item.has("name") ? item.get("name").asText() : item.get("display_name").asText());
                suggestion.put("address", item.has("display_name") ? item.get("display_name").asText() : "");
                
                if (item.has("lat") && item.has("lon")) {
                    suggestion.put("latitude", Double.parseDouble(item.get("lat").asText()));
                    suggestion.put("longitude", Double.parseDouble(item.get("lon").asText()));
                }

                suggestions.add(suggestion);
            }
        }

        result.put("suggestions", suggestions);
        return result;
    }

    private Map<String, Object> processSearchResponse(JsonNode response) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> suggestions = new ArrayList<>();

        JsonNode features = response.get("Feature");
        if (features != null && features.isArray()) {
            for (JsonNode feature : features) {
                Map<String, Object> suggestion = new HashMap<>();
                
                JsonNode property = feature.get("Property");
                if (property != null) {
                    suggestion.put("id", UUID.randomUUID().toString());
                    suggestion.put("name", property.has("Name") ? property.get("Name").asText() : "");
                    suggestion.put("address", property.has("Address") ? property.get("Address").asText() : "");
                }

                JsonNode geometry = feature.get("Geometry");
                if (geometry != null && geometry.has("Coordinates")) {
                    String coordinates = geometry.get("Coordinates").asText();
                    String[] coords = coordinates.split(",");
                    if (coords.length == 2) {
                        suggestion.put("longitude", Double.parseDouble(coords[0]));
                        suggestion.put("latitude", Double.parseDouble(coords[1]));
                    }
                }

                suggestions.add(suggestion);
            }
        }

        result.put("suggestions", suggestions);
        return result;
    }

    /**
     * 店舗情報の座標を取得して更新する
     * PHPのgetFacilityInfo関数に相当
     */
    public void updateStoreCoordinates() {
        try {
            String sql = """
                SELECT DISTINCT
                    store_condition.id,
                    store_condition.name,
                    store_condition.address,
                    municipality.name as municipality_name
                FROM 
                    store_condition 
                JOIN 
                    municipality ON store_condition.address = municipality.id
                WHERE 
                    location_fetched IS NULL OR location_fetched = TRUE
                """;

            List<Map<String, Object>> stores = jdbcTemplate.queryForList(sql);
            
            for (Map<String, Object> store : stores) {
                try {
                    updateStoreCoordinate(store);
                    Thread.sleep(100); // API制限を考慮した遅延
                } catch (Exception e) {
                    log.error("Error updating coordinate for store {}: {}", store.get("id"), e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("Error updating store coordinates", e);
            throw new RuntimeException("Failed to update store coordinates", e);
        }
    }

    private void updateStoreCoordinate(Map<String, Object> store) throws IOException, ParseException {
        String storeName = (String) store.get("name");
        String searchUrl = buildSearchUrl(storeName);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(searchUrl);
            ClassicHttpResponse response = httpClient.execute(httpGet);
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            if (response.getCode() == 200) {
                JsonNode yolpResponse = objectMapper.readTree(responseBody);
                JsonNode features = yolpResponse.get("Feature");

                if (features != null && features.isArray() && features.size() > 0) {
                    JsonNode feature = features.get(0);
                    JsonNode geometry = feature.get("Geometry");
                    
                    if (geometry != null && geometry.has("Coordinates")) {
                        String coordinates = geometry.get("Coordinates").asText();
                        String[] coords = coordinates.split(",");
                        
                        if (coords.length == 2) {
                            double longitude = Double.parseDouble(coords[0]);
                            double latitude = Double.parseDouble(coords[1]);

                            String updateSql = """
                                UPDATE store_condition 
                                SET latitude = ?, longitude = ?, location_fetched = TRUE 
                                WHERE id = ?
                                """;
                            
                            jdbcTemplate.update(updateSql, latitude, longitude, store.get("id"));
                            log.info("Updated coordinates for store {}: {}, {}", 
                                   store.get("name"), latitude, longitude);
                        }
                    }
                } else {
                    // 座標が見つからない場合
                    String updateSql = "UPDATE store_condition SET location_fetched = FALSE WHERE id = ?";
                    jdbcTemplate.update(updateSql, store.get("id"));
                    log.warn("No coordinates found for store: {}", store.get("name"));
                }
            }
        }
    }
}
