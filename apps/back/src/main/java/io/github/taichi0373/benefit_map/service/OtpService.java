// package com.example.back.service;

// import com.example.back.dto.RouteRequest;
// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import lombok.RequiredArgsConstructor;
// import org.apache.hc.client5.http.classic.methods.HttpGet;
// import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
// import org.apache.hc.client5.http.impl.classic.HttpClients;
// import org.apache.hc.core5.http.ClassicHttpResponse;
// import org.apache.hc.core5.http.ParseException;
// import org.apache.hc.core5.http.io.entity.EntityUtils;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.stereotype.Service;

// import java.io.IOException;
// import java.net.URLEncoder;
// import java.nio.charset.StandardCharsets;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// @Service
// @RequiredArgsConstructor
// public class OtpService {

//     private static final Logger log = LoggerFactory.getLogger(OtpService.class);

//     private static final String OTP_BASE_URL = "http://160.16.92.209:8080/otp/routers/default/plan";
//     private static final int MAX_WALK_DISTANCE = 1000;
//     private static final int NUM_ITINERARIES = 5;
//     private static final String LOCALE = "ja";
//     private static final String OPTIMIZE = "QUICK";
//     private static final double WALK_SPEED = 1.389;

//     private final ObjectMapper objectMapper = new ObjectMapper();

//     public JsonNode searchRoutes(RouteRequest request) throws IOException, ParseException {
//         String otpUrl = buildOtpUrl(request);
//         log.info("OTP Request URL: {}", otpUrl);

//         try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//             HttpGet httpGet = new HttpGet(otpUrl);
//             httpGet.setHeader("Accept", "application/json");

//             ClassicHttpResponse response = httpClient.execute(httpGet);
//             String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

//             if (response.getCode() != 200) {
//                 log.error("OTP API error: {} - {}", response.getCode(), responseBody);
//                 throw new RuntimeException("Failed to fetch route data from OTP");
//             }

//             JsonNode otpResponse = objectMapper.readTree(responseBody);
//             return processOtpResponse(otpResponse, request);
//         }
//     }

//     private String buildOtpUrl(RouteRequest request) {
//         StringBuilder url = new StringBuilder(OTP_BASE_URL);
//         url.append("?fromPlace=").append(encodeParam(request.getStartLocation()));
//         url.append("&toPlace=").append(encodeParam(request.getEndLocation()));

//         if (request.getTime() != null && !request.getTime().isEmpty()) {
//             url.append("&time=").append(encodeParam(request.getTime()));
//         }
//         if (request.getDate() != null && !request.getDate().isEmpty()) {
//             url.append("&date=").append(encodeParam(request.getDate()));
//         }

//         url.append("&mode=").append(encodeParam(request.getTransportMode()));
//         url.append("&arriveBy=").append(request.isArriveBy());
//         url.append("&maxWalkDistance=").append(MAX_WALK_DISTANCE);
//         url.append("&numItineraries=").append(NUM_ITINERARIES);
//         url.append("&locale=").append(LOCALE);
//         url.append("&optimize=").append(OPTIMIZE);
//         url.append("&walkSpeed=").append(WALK_SPEED);
//         url.append("&useRealtime=true");

//         return url.toString();
//     }

//     private String encodeParam(String param) {
//         return URLEncoder.encode(param, StandardCharsets.UTF_8);
//     }

//     private JsonNode processOtpResponse(JsonNode otpResponse, RouteRequest request) {
//         try {
//             JsonNode planNode = otpResponse.get("plan");
//             if (planNode == null || !planNode.has("itineraries")) {
//                 log.warn("No itineraries found in OTP response");
//                 return objectMapper.createArrayNode();
//             }

//             JsonNode itineraries = planNode.get("itineraries");
//             List<Map<String, Object>> processedItineraries = new ArrayList<>();

//             // 最大3つの結果を処理
//             int maxResults = Math.min(3, itineraries.size());
//             for (int i = 0; i < maxResults; i++) {
//                 JsonNode itinerary = itineraries.get(i);
//                 Map<String, Object> processedItinerary = processItinerary(itinerary);
//                 processedItineraries.add(processedItinerary);
//             }

//             return objectMapper.valueToTree(processedItineraries);
//         } catch (Exception e) {
//             log.error("Error processing OTP response", e);
//             throw new RuntimeException("Failed to process route data", e);
//         }
//     }

//     private Map<String, Object> processItinerary(JsonNode itinerary) {
//         Map<String, Object> result = new HashMap<>();
//         List<Map<String, Object>> legs = new ArrayList<>();
//         int totalFare = 0;

//         JsonNode fareNode = itinerary.get("fare");
//         JsonNode legsNode = itinerary.get("legs");

//         if (legsNode != null && legsNode.isArray()) {
//             for (int i = 0; i < legsNode.size(); i++) {
//                 JsonNode leg = legsNode.get(i);
//                 Map<String, Object> legData = processLeg(leg, fareNode, i);
//                 totalFare += (Integer) legData.getOrDefault("fare", 0);
                
//                 // 最初と最後の要素の名前を設定
//                 if (i == 0) {
//                     legData.put("from", "Origin");
//                 }
//                 if (i == legsNode.size() - 1) {
//                     legData.put("to", "Destination");
//                 }
                
//                 legs.add(legData);
//             }
//         }

//         result.put("legs", legs);
//         result.put("startTime", itinerary.has("startTime") ? itinerary.get("startTime").asText() : "");
//         result.put("endTime", itinerary.has("endTime") ? itinerary.get("endTime").asText() : "");
//         result.put("duration", itinerary.has("duration") ? itinerary.get("duration").asInt() : 0);
//         result.put("totalFare", totalFare);
//         result.put("totalDiscountFare", null);
//         result.put("transfers", itinerary.has("transfers") ? itinerary.get("transfers").asInt() : 0);

//         return result;
//     }

//     private Map<String, Object> processLeg(JsonNode leg, JsonNode fareNode, int legIndex) {
//         Map<String, Object> legData = new HashMap<>();
        
//         // アイコンのマッピング
//         Map<String, String> iconTranslations = Map.of(
//             "WALK", "🚶",
//             "BICYCLE", "🚲",
//             "RAIL", "🚆",
//             "BUS", "🚌",
//             "TRAM", "🚋"
//         );

//         String mode = leg.has("mode") ? leg.get("mode").asText() : "";
//         String icon = iconTranslations.getOrDefault(mode, mode);
        
//         // 運賃情報の取得
//         int legFare = 0;
//         if (fareNode != null && fareNode.has("legProducts")) {
//             JsonNode legProducts = fareNode.get("legProducts");
//             for (JsonNode fareData : legProducts) {
//                 if (fareData.has("legIndices") && 
//                     fareData.get("legIndices").get(0).asInt() == legIndex) {
//                     if (fareData.has("products") && fareData.get("products").size() > 0) {
//                         JsonNode product = fareData.get("products").get(0);
//                         if (product.has("amount") && product.get("amount").has("cents")) {
//                             legFare = product.get("amount").get("cents").asInt();
//                         }
//                     }
//                 }
//             }
//         }

//         // エージェンシーIDの処理
//         String agencyId = "";
//         if (leg.has("agencyId")) {
//             String fullAgencyId = leg.get("agencyId").asText();
//             String[] parts = fullAgencyId.split(":");
//             if (parts.length > 1) {
//                 agencyId = parts[1];
//             }
//         }

//         legData.put("mode", mode);
//         legData.put("icon", icon);
//         legData.put("startTime", leg.has("startTime") ? leg.get("startTime").asText() : "");
//         legData.put("endTime", leg.has("endTime") ? leg.get("endTime").asText() : "");
//         legData.put("duration", leg.has("duration") ? leg.get("duration").asInt() : 0);
        
//         JsonNode fromNode = leg.get("from");
//         if (fromNode != null) {
//             legData.put("from", fromNode.has("name") ? fromNode.get("name").asText() : "");
//             legData.put("fromLat", fromNode.has("lat") ? fromNode.get("lat").asDouble() : 0.0);
//             legData.put("fromLon", fromNode.has("lon") ? fromNode.get("lon").asDouble() : 0.0);
//         }
        
//         JsonNode toNode = leg.get("to");
//         if (toNode != null) {
//             legData.put("to", toNode.has("name") ? toNode.get("name").asText() : "");
//             legData.put("toLat", toNode.has("lat") ? toNode.get("lat").asDouble() : 0.0);
//             legData.put("toLon", toNode.has("lon") ? toNode.get("lon").asDouble() : 0.0);
//         }
        
//         legData.put("fare", legFare);
//         legData.put("discountFare", null);
//         legData.put("agencyName", leg.has("agencyName") ? leg.get("agencyName").asText() : "");
//         legData.put("agencyID", agencyId);
//         legData.put("communitybusID", "");
//         legData.put("agencyUrl", leg.has("agencyUrl") ? leg.get("agencyUrl").asText() : "");
//         legData.put("routeId", leg.has("routeId") ? leg.get("routeId").asText() : "");
//         legData.put("legGeometry", leg.get("legGeometry"));
//         legData.put("transitLeg", leg.has("transitLeg") ? leg.get("transitLeg").asBoolean() : false);
//         legData.put("benefitURL", "");
//         legData.put("benefitID", "");
//         legData.put("free_pass", "");

//         return legData;
//     }
// }
