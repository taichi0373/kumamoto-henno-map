package io.github.taichi0373.benefit_map.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class YolpService {
    
    private static final Logger log = LoggerFactory.getLogger(YolpService.class);
    
    @Value("${yahoo.api.key}")
    private String apiKey;
    
    @Value("${yahoo.api.url}")
    private String apiUrl;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 位置情報を検索（Nominatim APIを使用）
     */
    public Map<String, Object> searchLocation(String query) throws IOException, ParseException {
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
                throw new RuntimeException("位置情報の検索に失敗しました");
            }
            
            JsonNode nominatimResponse = objectMapper.readTree(responseBody);
            return processNominatimResponse(nominatimResponse);
        }
    }
    
    /**
     * Nominatim検索URLを構築
     */
    private String buildNominatimSearchUrl(String query) {
        String encodedQuery = URLEncoder.encode("日本, 熊本県, " + query, StandardCharsets.UTF_8);
        return String.format("https://nominatim.openstreetmap.org/search?q=%s&format=json&addressdetails=1&countrycodes=JP&limit=5",
                encodedQuery);
    }
    
    /**
     * Nominatimレスポンスを処理
     */
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
}
