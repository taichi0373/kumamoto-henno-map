package io.github.taichi0373.kumamoto_henno_map.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.taichi0373.kumamoto_henno_map.dto.RouteRequestDto;

/** ValidateUtils の経路探索リクエスト検証のテスト */
class ValidateUtilsRouteRequestTest {

    private RouteRequestDto request;

    @BeforeEach
    void setUp() {
        request = new RouteRequestDto();
        request.setTransportMode("TRANSIT,WALK");
        request.setStartLat(32.79);
        request.setStartLon(130.74);
        request.setEndLat(32.80);
        request.setEndLon(130.71);
        request.setDate("2026-11-02");
        request.setTime("10:00");
    }

    @ParameterizedTest
    @ValueSource(strings = {"TRANSIT,WALK", "RAIL,TRAM,WALK", "WALK", "BICYCLE"})
    void フロントが送る交通手段は検証を通過する(String transportMode) {
        request.setTransportMode(transportMode);

        assertThat(ValidateUtils.validateRouteRequest(request)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "transportMode", "WALK,", ",WALK", "WALK;DROP"})
    void 不正な交通手段はエラーになる(String transportMode) {
        request.setTransportMode(transportMode);

        assertThat(ValidateUtils.validateRouteRequest(request)).isPresent();
    }

    @Test
    void 長すぎる交通手段はエラーになる() {
        request.setTransportMode("A".repeat(51));

        assertThat(ValidateUtils.validateRouteRequest(request)).isPresent();
    }

    @Test
    void 交通手段がnullならエラーになる() {
        request.setTransportMode(null);

        assertThat(ValidateUtils.validateRouteRequest(request)).isPresent();
    }

    @Test
    void 座標がnullならエラーになる() {
        request.setStartLat(null);

        assertThat(ValidateUtils.validateRouteRequest(request)).isPresent();
    }

    @Test
    void 範囲外の緯度はエラーになる() {
        request.setEndLat(90.1);

        assertThat(ValidateUtils.validateRouteRequest(request)).isPresent();
    }

    @Test
    void 範囲外の経度はエラーになる() {
        request.setEndLon(-180.1);

        assertThat(ValidateUtils.validateRouteRequest(request)).isPresent();
    }

    @Test
    void NaNの座標はエラーになる() {
        request.setStartLon(Double.NaN);

        assertThat(ValidateUtils.validateRouteRequest(request)).isPresent();
    }

    @Test
    void 日付と時刻が空なら検証を通過する() {
        request.setDate("");
        request.setTime("");

        assertThat(ValidateUtils.validateRouteRequest(request)).isEmpty();
    }

    @Test
    void 秒付きの時刻は検証を通過する() {
        request.setTime("23:59:59");

        assertThat(ValidateUtils.validateRouteRequest(request)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"date", "2026-02-30", "2026/11/02"})
    void 不正な日付はエラーになる(String date) {
        request.setDate(date);

        assertThat(ValidateUtils.validateRouteRequest(request)).isPresent();
    }

    @ParameterizedTest
    @ValueSource(strings = {"time", "24:00", "10:60", "1:00"})
    void 不正な時刻はエラーになる(String time) {
        request.setTime(time);

        assertThat(ValidateUtils.validateRouteRequest(request)).isPresent();
    }

    @Test
    void 長すぎる地点名称はエラーになる() {
        request.setStartLocation("あ".repeat(201));

        assertThat(ValidateUtils.validateRouteRequest(request)).isPresent();
    }
}
