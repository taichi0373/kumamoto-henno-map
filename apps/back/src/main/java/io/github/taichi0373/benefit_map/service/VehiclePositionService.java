package io.github.taichi0373.benefit_map.service;

import com.google.transit.realtime.GtfsRealtime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GTFS-RT 車両位置情報サービス
 * <p>
 * bus-vision.jp の GTFS-RT プロトバッファフィードを直接取得・解析し、
 * 全バス事業者のリアルタイム車両位置を返す。
 * </p>
 */
@Service
public class VehiclePositionService {

    private static final Logger log = LoggerFactory.getLogger(VehiclePositionService.class);

    private static final int CONNECT_TIMEOUT_MS = 5_000;
    private static final int READ_TIMEOUT_MS = 10_000;

    /** feedId → GTFS-RT 車両位置フィード URL */
    private static final Map<String, String> FEED_URLS = Map.of(
        "sankobus",    "https://km.bus-vision.jp/realtime/sankobus_vpos_update.bin",
        "dentetsubus", "https://km.bus-vision.jp/realtime/dentetsu_vpos_update.bin",
        "kumabus",     "https://km.bus-vision.jp/realtime/kumabus_vpos_update.bin",
        "toshibus",    "https://km.bus-vision.jp/realtime/toshibus_vpos_update.bin"
    );

    /** feedId → 事業者名（表示用） */
    private static final Map<String, String> AGENCY_NAMES = Map.of(
        "sankobus",    "産交バス",
        "dentetsubus", "熊本電鉄バス",
        "kumabus",     "熊本バス",
        "toshibus",    "熊本都市バス"
    );

    /**
     * 全バス事業者の車両位置情報を取得する
     *
     * @return 車両位置情報リスト。各要素は vehicleId / lat / lon / agencyName / routeId を持つ
     */
    public List<Map<String, Object>> fetchVehiclePositions() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : FEED_URLS.entrySet()) {
            String feedId = entry.getKey();
            String feedUrl = entry.getValue();
            try {
                result.addAll(fetchFeed(feedId, feedUrl));
            } catch (Exception e) {
                log.warn("GTFS-RT 車両位置取得失敗: feedId={}", feedId, e);
            }
        }
        return result;
    }

    /**
     * 指定フィードから GTFS-RT プロトバッファを取得・解析して車両位置リストを返す
     *
     * @param feedId  フィードID（事業者識別子）
     * @param feedUrl フィードURL
     * @return 車両位置情報リスト
     */
    private List<Map<String, Object>> fetchFeed(String feedId, String feedUrl) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(feedUrl).openConnection();
        conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
        conn.setReadTimeout(READ_TIMEOUT_MS);

        List<Map<String, Object>> result = new ArrayList<>();
        try (InputStream is = conn.getInputStream()) {
            GtfsRealtime.FeedMessage feed = GtfsRealtime.FeedMessage.parseFrom(is);
            String agencyName = AGENCY_NAMES.getOrDefault(feedId, feedId);

            for (GtfsRealtime.FeedEntity entity : feed.getEntityList()) {
                if (!entity.hasVehicle()) {
                    continue;
                }
                GtfsRealtime.VehiclePosition vp = entity.getVehicle();
                if (!vp.hasPosition()) {
                    continue;
                }

                float lat = vp.getPosition().getLatitude();
                float lon = vp.getPosition().getLongitude();
                if (lat == 0 && lon == 0) {
                    continue;
                }

                String vehicleId = vp.hasVehicle() ? vp.getVehicle().getId() : entity.getId();
                if (vehicleId == null || vehicleId.isBlank()) {
                    continue;
                }

                Map<String, Object> vehicle = new HashMap<>();
                vehicle.put("vehicleId", vehicleId);
                vehicle.put("lat", lat);
                vehicle.put("lon", lon);
                vehicle.put("agencyName", agencyName);
                vehicle.put("routeId", vp.hasTrip() ? vp.getTrip().getRouteId() : "");
                result.add(vehicle);
            }
        }
        return result;
    }
}
