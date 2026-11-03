package io.github.taichi0373.kumamoto_henno_map.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.taichi0373.kumamoto_henno_map.dto.BenefitEligibilityDto;

/** ValidateUtils の特典検索リクエスト検証のテスト */
class ValidateUtilsBenefitSearchRequestTest {

    private BenefitEligibilityDto request;

    @BeforeEach
    void setUp() {
        request = new BenefitEligibilityDto();
    }

    @Test
    void 位置情報を指定しない検索は検証を通過する() {
        assertThat(ValidateUtils.validateBenefitSearchRequest(request)).isEmpty();
    }

    @Test
    void フロントが送る現在地周辺検索は検証を通過する() {
        request.setLatitude(32.79);
        request.setLongitude(130.74);
        request.setRadiusKm(10.0);

        assertThat(ValidateUtils.validateBenefitSearchRequest(request)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(doubles = {-90.1, 90.1, 1e308, Double.NaN})
    void 範囲外の緯度はエラーになる(double latitude) {
        request.setLatitude(latitude);
        request.setLongitude(130.74);

        assertThat(ValidateUtils.validateBenefitSearchRequest(request)).isPresent();
    }

    @ParameterizedTest
    @ValueSource(doubles = {-180.1, 180.1, 1e308, Double.NaN})
    void 範囲外の経度はエラーになる(double longitude) {
        request.setLatitude(32.79);
        request.setLongitude(longitude);

        assertThat(ValidateUtils.validateBenefitSearchRequest(request)).isPresent();
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.0, 10.1, 1e308, Double.NaN})
    void 範囲外の検索半径はエラーになる(double radiusKm) {
        request.setLatitude(32.79);
        request.setLongitude(130.74);
        request.setRadiusKm(radiusKm);

        assertThat(ValidateUtils.validateBenefitSearchRequest(request)).isPresent();
    }
}
