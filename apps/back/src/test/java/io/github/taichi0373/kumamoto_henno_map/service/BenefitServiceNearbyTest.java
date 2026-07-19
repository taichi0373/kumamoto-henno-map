package io.github.taichi0373.kumamoto_henno_map.service;

import io.github.taichi0373.kumamoto_henno_map.dto.BenefitEligibilityDto;
import io.github.taichi0373.kumamoto_henno_map.repository.dao.BenefitCategoryDao;
import io.github.taichi0373.kumamoto_henno_map.repository.dao.BenefitDetailDao;
import io.github.taichi0373.kumamoto_henno_map.repository.dao.UsersDao;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.BenefitDetailEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/** BenefitService の現在地周辺フィルタリングのテスト */
@ExtendWith(MockitoExtension.class)
class BenefitServiceNearbyTest {

    @InjectMocks
    private BenefitService benefitService;

    @Mock
    private BenefitDetailDao benefitDetailDao;

    @Mock
    private BenefitCategoryDao benefitCategoryDao;

    @Mock
    private UsersDao usersDao;

    /** 熊本市役所の座標（テスト基準点） */
    private static final double KUMAMOTO_LAT = 32.7897;
    private static final double KUMAMOTO_LNG = 130.7416;

    private BenefitDetailEntity createBenefitWithCoords(String id, double lat, double lng) {
        BenefitDetailEntity b = new BenefitDetailEntity();
        b.setBenefitId(id);
        b.setLatitude(BigDecimal.valueOf(lat));
        b.setLongitude(BigDecimal.valueOf(lng));
        return b;
    }

    @Test
    void 現在地指定なしのとき全件返す() {
        BenefitDetailEntity b1 = createBenefitWithCoords("B001", KUMAMOTO_LAT, KUMAMOTO_LNG);
        BenefitDetailEntity b2 = createBenefitWithCoords("B002", 33.0, 131.0); // 遠い
        when(benefitDetailDao.selectEligible(any(), any(), any(), any(), any()))
            .thenReturn(List.of(b1, b2));

        BenefitEligibilityDto request = new BenefitEligibilityDto();
        // latitude, longitude を設定しない

        List<BenefitDetailEntity> result = benefitService.searchBenefits(request);

        assertThat(result).hasSize(2);
    }

    @Test
    void 現在地指定ありのとき半径2km以内のみ返す() {
        // 熊本市役所から約0.5km
        BenefitDetailEntity nearby = createBenefitWithCoords("B001", 32.7930, 130.7416);
        // 熊本市役所から約30km
        BenefitDetailEntity far = createBenefitWithCoords("B002", 33.0, 131.0);
        when(benefitDetailDao.selectEligible(any(), any(), any(), any(), any()))
            .thenReturn(List.of(nearby, far));

        BenefitEligibilityDto request = new BenefitEligibilityDto();
        request.setLatitude(KUMAMOTO_LAT);
        request.setLongitude(KUMAMOTO_LNG);
        request.setRadiusKm(2.0);

        List<BenefitDetailEntity> result = benefitService.searchBenefits(request);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBenefitId()).isEqualTo("B001");
    }

    @Test
    void 座標がnullの特典は現在地フィルタリング時に除外される() {
        BenefitDetailEntity noCoords = new BenefitDetailEntity();
        noCoords.setBenefitId("B003");
        // latitude, longitude = null
        when(benefitDetailDao.selectEligible(any(), any(), any(), any(), any()))
            .thenReturn(List.of(noCoords));

        BenefitEligibilityDto request = new BenefitEligibilityDto();
        request.setLatitude(KUMAMOTO_LAT);
        request.setLongitude(KUMAMOTO_LNG);

        List<BenefitDetailEntity> result = benefitService.searchBenefits(request);

        assertThat(result).isEmpty();
    }
}
