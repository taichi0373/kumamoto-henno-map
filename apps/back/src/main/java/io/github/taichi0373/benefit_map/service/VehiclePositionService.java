package io.github.taichi0373.benefit_map.service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * GTFS-RT 車両位置情報サービス
 * <p>
 * OTP の GraphQL API（/otp/gtfs/v1）を通じて全バス事業者のリアルタイム車両位置を取得する。
 * </p>
 */
@Service
public class VehiclePositionService {

    private static final Logger log = LoggerFactory.getLogger(VehiclePositionService.class);

    /** OTP GraphQL クエリ（vehiclePositions フィールド） */
    private static final String GRAPHQL_QUERY =
            "{ vehiclePositions { vehicleId lat lon trip { route { shortName agency { name } } } } }";

    @Value("${otp.api.url}")
    private String otpApiUrl;

    /** OTP GraphQL 接続タイムアウト */
    @Value("${otp.graphql.connect-timeout:3s}")
    private Duration otpGraphqlConnectTimeout;

    /** OTP GraphQL 応答タイムアウト */
    @Value("${otp.graphql.response-timeout:10s}")
    private Duration otpGraphqlResponseTimeout;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * OTP の GraphQL エンドポイント URL を組み立てる。
     * {@code otp.api.url} は plan エンドポイントなので、
     * "/otp/" 以降を "otp/gtfs/v1" に置き換える。
     *
     * @return GraphQL エンドポイント URL
     */
    private String buildGraphqlUrl() {
        int idx = otpApiUrl.indexOf("/otp/");
        if (idx < 0) {
            return otpApiUrl;
        }
        return otpApiUrl.substring(0, idx) + "/otp/gtfs/v1";
    }

    /**
     * 全バス事業者の車両位置情報を取得する
     *
     * @return 車両位置情報リスト。各要素は vehicleId / lat / lon / agencyName / routeId を持つ
     */
    public List<Map<String, Object>> fetchVehiclePositions() {
        String graphqlUrl = buildGraphqlUrl();
        String requestBody = "{\"query\":\"" + GRAPHQL_QUERY + "\"}";

        try (CloseableHttpClient client = createHttpClient()) {
            HttpPost post = new HttpPost(graphqlUrl);
            post.setHeader("Content-Type", "application/json");
            post.setConfig(RequestConfig.custom()
                    .setResponseTimeout(Timeout.ofMilliseconds(otpGraphqlResponseTimeout.toMillis()))
                    .build());
            post.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));

            return client.execute(post, response -> {
                if (response.getCode() != 200) {
                    log.warn("OTP GraphQL 車両位置取得失敗: status={}", response.getCode());
                    return new ArrayList<>();
                }
                String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                return parseResponse(objectMapper.readTree(body));
            });
        } catch (Exception e) {
            log.error("OTP GraphQL 車両位置取得エラー", e);
            return new ArrayList<>();
        }
    }

            /**
             * OTP GraphQL 呼び出し用の HTTP クライアントを生成する
             *
             * @return タイムアウト設定済み HTTP クライアント
             */
            private CloseableHttpClient createHttpClient() {
            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(otpGraphqlConnectTimeout.toMillis()))
                .build();
            RequestConfig requestConfig = RequestConfig.custom()
                .setResponseTimeout(Timeout.ofMilliseconds(otpGraphqlResponseTimeout.toMillis()))
                .build();

            return HttpClients.custom()
                .setDefaultConnectionConfig(connectionConfig)
                .setDefaultRequestConfig(requestConfig)
                .build();
            }

    /**
     * GraphQL レスポンスから車両位置リストを生成する
     *
     * @param root GraphQL レスポンスの JSON ルートノード
     * @return 車両位置情報リスト
     */
    private List<Map<String, Object>> parseResponse(JsonNode root) {
        List<Map<String, Object>> result = new ArrayList<>();
        JsonNode positions = root.path("data").path("vehiclePositions");
        if (!positions.isArray()) {
            return result;
        }

        for (JsonNode vp : positions) {
            double lat = vp.path("lat").asDouble(0);
            double lon = vp.path("lon").asDouble(0);
            String vehicleId = vp.path("vehicleId").asText("");

            if (vehicleId.isBlank()) {
                continue;
            }

            // 座標が取得できない車両はスキップ
            if (lat == 0 && lon == 0) {
                continue;
            }

            Map<String, Object> vehicle = new HashMap<>();
            vehicle.put("vehicleId", vehicleId);
            vehicle.put("lat", lat);
            vehicle.put("lon", lon);

            JsonNode route = vp.path("trip").path("route");
            vehicle.put("routeId", route.path("shortName").asText(""));
            vehicle.put("agencyName", route.path("agency").path("name").asText(""));

            result.add(vehicle);
        }
        return result;
    }
}
