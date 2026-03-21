package io.github.taichi0373.benefit_map.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.taichi0373.benefit_map.constants.CodeConstants;
import io.github.taichi0373.benefit_map.dto.RouteRequestDto;
import io.github.taichi0373.benefit_map.repository.dao.BenefitDetailDao;
import io.github.taichi0373.benefit_map.repository.dao.UsersDao;
import io.github.taichi0373.benefit_map.repository.entity.BenefitDetailEntity;
import io.github.taichi0373.benefit_map.repository.entity.UsersEntity;
import io.github.taichi0373.benefit_map.util.AgeUtils;
import io.github.taichi0373.benefit_map.util.ValidateUtils;

@Service
public class RouteService {

    @Value("${otp.api.url}")
    private String otpApiUrl;

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

    // 日本標準時タイムゾーン
    private static final ZoneId JST = ZoneId.of("Asia/Tokyo");
    // 時刻フォーマット
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm").withZone(JST);
    // 最大徒歩距離
    private static final int MAX_WALK_DISTANCE = 1000;
    // 取得経路数
    private static final int NUM_ITINERARIES = 5;
    // ロケール
    private static final String LOCALE = "ja";
    // 最適化設定
    private static final String OPTIMIZE = "QUICK";
    // 歩行速度（m/s）
    private static final double WALK_SPEED = 1.389;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 経路探索を実行
     */
    public JsonNode searchRoutes(RouteRequestDto request) throws IOException, ParseException {
        String otpUrl = buildOtpUrl(request);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(otpUrl);
            httpGet.setHeader("Accept", "application/json");

            ClassicHttpResponse response = httpClient.execute(httpGet);
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            if (response.getCode() != 200) {
                throw new RuntimeException("経路探索に失敗しました");
            }

            JsonNode otpResponse = objectMapper.readTree(responseBody);

            // ユーザーの割引情報を取得
            Map<String, BenefitDetailEntity> discountMap = buildDiscountMap(request.getUserId());

            return processOtpResponse(otpResponse, discountMap);
        } catch (IOException | ParseException e) {
            throw new RuntimeException("経路探索のリクエストに失敗しました", e);
        }
    }

    /**
     * OTP URLを構築
     */
    private String buildOtpUrl(RouteRequestDto request) {
        StringBuilder url = new StringBuilder(otpApiUrl);
        String fromPlace = buildPlaceParam(request.getStartLocation(), request.getStartLat(), request.getStartLon());
        String toPlace = buildPlaceParam(request.getEndLocation(), request.getEndLat(), request.getEndLon());
        url.append("?fromPlace=").append(encodeParam(fromPlace));
        url.append("&toPlace=").append(encodeParam(toPlace));

        if (!ValidateUtils.isNullOrEmpty(request.getTime())) {
            url.append("&time=").append(encodeParam(request.getTime()));
        }
        if (!ValidateUtils.isNullOrEmpty(request.getDate())) {
            url.append("&date=").append(encodeParam(request.getDate()));
        }

        url.append("&mode=").append(encodeParam(request.getTransportMode()));
        url.append("&arriveBy=").append(request.isArriveBy());
        url.append("&maxWalkDistance=").append(MAX_WALK_DISTANCE);
        url.append("&numItineraries=").append(NUM_ITINERARIES);
        url.append("&locale=").append(LOCALE);
        url.append("&optimize=").append(OPTIMIZE);
        url.append("&walkSpeed=").append(WALK_SPEED);
        url.append("&useRealtime=true");

        return url.toString();
    }

    /**
     * OTP用の地点パラメータを構築
     */
    private String buildPlaceParam(String name, Double lat, Double lon) {
        if (!ValidateUtils.isNullOrEmpty(lat) && !ValidateUtils.isNullOrEmpty(lon)) {
            return (!ValidateUtils.isNullOrEmpty(name) ? name : "") + "::" + lat + "," + lon;
        }
        return !ValidateUtils.isNullOrEmpty(name) ? name : "";
    }

    /**
     * パラメータをエンコード
     */
    private String encodeParam(String param) {
        return URLEncoder.encode(param, StandardCharsets.UTF_8);
    }

    /**
     * OTPレスポンスを処理
     *
     * @param otpResponse OTPレスポンス
     * @param discountMap ユーザーの割引情報マップ {事業者ID, 割引情報}
      * @return 処理済み経路情報
     */
    private JsonNode processOtpResponse(JsonNode otpResponse, Map<String, BenefitDetailEntity> discountMap) {
        try {
            JsonNode planNode = otpResponse.get("plan");
            if (ValidateUtils.isNullOrEmpty(planNode) || !planNode.has("itineraries")) {
                return objectMapper.createArrayNode();
            }

            JsonNode itineraries = planNode.get("itineraries");
            List<Map<String, Object>> processedItineraries = new ArrayList<>();

            // 到着時刻の昇順にソートして最大3つを処理
            List<JsonNode> sortedItineraries = new ArrayList<>();
            itineraries.forEach(sortedItineraries::add);
            sortedItineraries.sort((a, b) -> {
                long endTimeA = a.has("endTime") ? a.get("endTime").asLong() : Long.MAX_VALUE;
                long endTimeB = b.has("endTime") ? b.get("endTime").asLong() : Long.MAX_VALUE;
                return Long.compare(endTimeA, endTimeB);
            });

            int maxResults = Math.min(3, sortedItineraries.size());
            for (int i = 0; i < maxResults; i++) {
                JsonNode itinerary = sortedItineraries.get(i);
                Map<String, Object> processedItinerary = processItinerary(itinerary, discountMap);
                processedItineraries.add(processedItinerary);
            }

            return objectMapper.valueToTree(processedItineraries);
        } catch (Exception e) {
            throw new RuntimeException("経路データの処理に失敗しました", e);
        }
    }

    /**
     * 経路情報を処理
     * @param itinerary   OTPの経路情報
     * @param discountMap ユーザーの割引情報マップ {事業者ID, 割引情報}
     * @return 処理済み経路情報
     */
    private Map<String, Object> processItinerary(JsonNode itinerary, Map<String, BenefitDetailEntity> discountMap) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> legs = new ArrayList<>();
        // 合計料金
        int totalFare = 0;
        // 割引適用後の合計料金
        int totalDiscountFare = 0;
        // 割引適用フラグ（1区間でも割引が適用されていればtrue）
        boolean isDiscount = false;

        JsonNode fareNode = itinerary.get("fare");
        JsonNode legsNode = itinerary.get("legs");

        if (!ValidateUtils.isNullOrEmpty(legsNode) && legsNode.isArray()) {
            for (int i = 0; i < legsNode.size(); i++) {
                JsonNode leg = legsNode.get(i);
                Map<String, Object> legData = processLeg(leg, fareNode, i, discountMap);
                int legFare = (Integer) legData.getOrDefault("fare", 0);
                totalFare += legFare;

                // 割引運賃が設定されている場合は合計に加算、なければ通常運賃を加算
                Integer legDiscountFare = (Integer) legData.get("discountFare");
                if (!ValidateUtils.isNullOrEmpty(legDiscountFare)) {
                    totalDiscountFare += legDiscountFare;
                    isDiscount = true;
                } else {
                    totalDiscountFare += legFare;
                }

                // 最初と最後の要素の名前を設定
                if (i == 0) {
                    legData.put("from", "出発地");
                }
                if (i == legsNode.size() - 1) {
                    legData.put("to", "目的地");
                }

                legs.add(legData);
            }
        }

        result.put("legs", legs);
        result.put("startTime", itinerary.has("startTime") ? formatTime(itinerary.get("startTime").asLong()) : "");
        result.put("endTime", itinerary.has("endTime") ? formatTime(itinerary.get("endTime").asLong()) : "");
        result.put("duration", itinerary.has("duration") ? toMinutes(itinerary.get("duration").asInt()) : 0);
        result.put("totalFare", totalFare);
        result.put("totalDiscountFare", isDiscount ? totalDiscountFare : null);
        result.put("transfers", itinerary.has("transfers") ? itinerary.get("transfers").asInt() : 0);

        return result;
    }

    /**
     * 区間情報を処理
     * @param leg         OTPの区間情報
     * @param fareNode    OTPの運賃情報
     * @param legIndex    区間インデックス
     * @param discountMap ユーザーの割引情報マップ {事業者ID, 割引情報}
      * @return 処理済み区間情報
     */
    private Map<String, Object> processLeg(JsonNode leg, JsonNode fareNode, int legIndex,
            Map<String, BenefitDetailEntity> discountMap) {
        Map<String, Object> legData = new HashMap<>();

        String mode = leg.has("mode") ? leg.get("mode").asText() : "";

        // 運賃情報の取得
        int legFare = 0;
        if (!ValidateUtils.isNullOrEmpty(fareNode) && fareNode.has("legProducts")) {
            JsonNode legProducts = fareNode.get("legProducts");
            for (JsonNode fareData : legProducts) {
                if (fareData.has("legIndices") &&
                        fareData.get("legIndices").get(0).asInt() == legIndex) {
                    if (fareData.has("products") && fareData.get("products").size() > 0) {
                        JsonNode product = fareData.get("products").get(0);
                        if (product.has("amount") && product.get("amount").has("cents")) {
                            legFare = product.get("amount").get("cents").asInt();
                        }
                    }
                }
            }
        }

        // エージェンシーIDの処理
        String agencyId = "";
        if (leg.has("agencyId")) {
            String fullAgencyId = leg.get("agencyId").asText();
            String[] parts = fullAgencyId.split(":");
            if (parts.length > 1) {
                agencyId = parts[1];
            }
        }

        // 運賃 > 0 かつ事業者IDが存在する場合、割引運賃を計算
        Integer discountFare = null;
        if (legFare > 0 && !agencyId.isEmpty()) {
            BenefitDetailEntity discount = discountMap.get(agencyId);
            if (!ValidateUtils.isNullOrEmpty(discount)) {
                discountFare = applyDiscount(legFare, discount);
            }
        }

        legData.put("mode", mode);
        legData.put("startTime", leg.has("startTime") ? formatTime(leg.get("startTime").asLong()) : "");
        legData.put("endTime", leg.has("endTime") ? formatTime(leg.get("endTime").asLong()) : "");
        legData.put("duration", leg.has("duration") ? toMinutes(leg.get("duration").asInt()) : 0);

        JsonNode fromNode = leg.get("from");
        if (!ValidateUtils.isNullOrEmpty(fromNode)) {
            legData.put("from", fromNode.has("name") ? fromNode.get("name").asText() : "");
            legData.put("fromLat", fromNode.has("lat") ? fromNode.get("lat").asDouble() : 0.0);
            legData.put("fromLon", fromNode.has("lon") ? fromNode.get("lon").asDouble() : 0.0);
        }

        JsonNode toNode = leg.get("to");
        if (!ValidateUtils.isNullOrEmpty(toNode)) {
            legData.put("to", toNode.has("name") ? toNode.get("name").asText() : "");
            legData.put("toLat", toNode.has("lat") ? toNode.get("lat").asDouble() : 0.0);
            legData.put("toLon", toNode.has("lon") ? toNode.get("lon").asDouble() : 0.0);
        }

        legData.put("fare", legFare);
        legData.put("discountFare", discountFare);
        legData.put("agencyName", leg.has("agencyName") ? leg.get("agencyName").asText() : "");
        legData.put("agencyId", agencyId);
        legData.put("communityBusId", "");
        legData.put("agencyUrl", leg.has("agencyUrl") ? leg.get("agencyUrl").asText() : "");
        legData.put("routeId", leg.has("routeId") ? leg.get("routeId").asText() : "");
        legData.put("legGeometry", leg.has("legGeometry") ? leg.get("legGeometry") : null);
        legData.put("transitLeg", leg.has("transitLeg") ? leg.get("transitLeg").asBoolean() : false);
        legData.put("benefitUrl", "");
        legData.put("benefitId", "");
        legData.put("freePass", "");

        return legData;
    }

    /**
     * ユーザーIDからユーザーの割引情報マップを構築
     * @param userId ユーザーID
     * @return ユーザーの割引情報マップ {事業者ID, 割引情報}
     */
    private Map<String, BenefitDetailEntity> buildDiscountMap(Long userId) {
        if (ValidateUtils.isNullOrEmpty(userId)) {
            return Map.of();
        }

        UsersEntity user = usersDao.selectById(userId);
        if (ValidateUtils.isNullOrEmpty(user)) {
            return Map.of();
        }

        // 生年月日から年齢を計算
        Integer age = null;
        if (!ValidateUtils.isNullOrEmpty(user.getBirthDate())) {
            age = AgeUtils.calculateAge(user.getBirthDate());
        }

        // ユーザーの利用資格条件に一致する運賃割引特典を取得
        List<BenefitDetailEntity> discounts = benefitDetailDao.selectFareDiscountsEligibleForUser(
                age, user.getLicenseStatus(), user.getMunicipalityCd());

        // 事業者IDごとに最大割引（割引後運賃が最安値）となるものをセット
        Map<String, BenefitDetailEntity> discountMap = new HashMap<>();
        for (BenefitDetailEntity discount : discounts) {
            String agencyId = discount.getAgencyId();
            if (ValidateUtils.isNullOrEmpty(agencyId)) {
                continue;
            }
            BenefitDetailEntity existing = discountMap.get(agencyId);
            if (ValidateUtils.isNullOrEmpty(existing) || getEffectiveDiscountRate(discount) > getEffectiveDiscountRate(existing)) {
                discountMap.put(agencyId, discount);
            }
        }
        return discountMap;
    }

    /**
     * 割引の実質的な割引率を返す（比較用）。<br>
     * 無料の場合は100、パーセンテージ割引の場合はその値。
     *
     * @param discount 割引情報エンティティ
     * @return 割引率（0〜100）
     */
    private int getEffectiveDiscountRate(BenefitDetailEntity discount) {
        if (CodeConstants.DiscountType.FREE.equals(discount.getDiscountType())) {
            return 100;
        }
        if (CodeConstants.DiscountType.PERCENTAGE.equals(discount.getDiscountType())
                && !ValidateUtils.isNullOrEmpty(discount.getDiscountValue())) {
            return discount.getDiscountValue().intValue();
        }
        return 0;
    }

    /**
     * 割引情報をもとに割引後運賃を計算する。
     * @param fare     通常運賃（円）
     * @param discount 割引情報
     * @return 割引後の運賃（円）
     */
    private Integer applyDiscount(int fare, BenefitDetailEntity discount) {
        if (CodeConstants.DiscountType.FREE.equals(discount.getDiscountType())) {
            return 0;
        }
        if (CodeConstants.DiscountType.PERCENTAGE.equals(discount.getDiscountType())
                && !ValidateUtils.isNullOrEmpty(discount.getDiscountValue())) {
            // 通常運賃 * (100 - 割引率) / 100 を計算し、10円未満切り上げ
            return BigDecimal.valueOf(fare)
                    .multiply(BigDecimal.valueOf(100).subtract(discount.getDiscountValue()))
                    .divide(BigDecimal.valueOf(1000), 0, RoundingMode.CEILING)
                    .multiply(BigDecimal.TEN)
                    .intValue();
        }
        return null;
    }

    /**
     * Unixミリ秒をJST "HH:mm" 形式に変換
     */
    private String formatTime(long epochMillis) {
        return TIME_FORMATTER.format(Instant.ofEpochMilli(epochMillis));
    }

    /**
     * 秒を分（切り上げ）に変換
     */
    private int toMinutes(int seconds) {
        return (int) Math.ceil(seconds / 60.0);
    }
}
