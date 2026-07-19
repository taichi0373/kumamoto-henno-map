package io.github.taichi0373.kumamoto_henno_map.service.admin;

import io.github.taichi0373.kumamoto_henno_map.repository.dao.BenefitDao;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.BenefitEntity;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.SystemField;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/** AdminBenefitService の確認済みマーク機能のテスト */
@ExtendWith(MockitoExtension.class)
class AdminBenefitConfirmTest {

    @InjectMocks
    private AdminBenefitService adminBenefitService;

    @Mock
    private BenefitDao benefitDao;

    @Test
    void 特典が存在するとき最終確認日が今日の日付で更新される() {
        // Arrange
        BenefitEntity entity = new BenefitEntity();
        entity.setBenefitId("BENEFIT_001");
        entity.setSystemField(new SystemField(null, null));
        when(benefitDao.selectById("BENEFIT_001")).thenReturn(entity);

        // Act
        adminBenefitService.confirmBenefit("BENEFIT_001");

        // Assert
        ArgumentCaptor<BenefitEntity> captor = ArgumentCaptor.forClass(BenefitEntity.class);
        verify(benefitDao).update(captor.capture());
        assertThat(captor.getValue().getLastConfirmedDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void 特典が存在しないとき例外がスローされる() {
        when(benefitDao.selectById("UNKNOWN")).thenReturn(null);

        assertThatThrownBy(() -> adminBenefitService.confirmBenefit("UNKNOWN"))
            .isInstanceOf(NoSuchElementException.class);
    }
}
