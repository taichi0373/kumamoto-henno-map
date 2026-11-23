package io.github.taichi0373.kumamoto_henno_map.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.taichi0373.kumamoto_henno_map.dto.UpdateUserProfileResponseDto;
import io.github.taichi0373.kumamoto_henno_map.dto.UsersDto;
import io.github.taichi0373.kumamoto_henno_map.repository.dao.UsersDao;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.SystemField;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.UsersEntity;

/**
 * UsersService ユーザー情報更新の単体テスト
 */
@ExtendWith(MockitoExtension.class)
class UsersServiceUpdateTest {

    @InjectMocks
    private UsersService usersService;

    @Mock
    private UsersDao usersDao;

    @Mock
    private EmailVerificationService emailVerificationService;

    private UsersEntity existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new UsersEntity();
        existingUser.setUserId(1L);
        existingUser.setUsername("taro");
        existingUser.setEmail("old@example.com");
        LocalDateTime createdAt = LocalDateTime.of(2026, 1, 1, 0, 0);
        existingUser.setSystemField(new SystemField(createdAt, createdAt));
        existingUser.setEmailVerifiedAt(createdAt);
    }

    /**
     * メールアドレスを変更した場合は即時に反映せず、変更後のアドレスへ確認メールを送信すること
     */
    @Test
    void メールアドレス変更時は旧アドレスのまま確認メールを送信する() {
        // Arrange
        when(usersDao.selectById(1L)).thenReturn(existingUser);

        // Act
        UpdateUserProfileResponseDto result = usersService.updateUsersInfo(buildDto("new@example.com"));

        // Assert
        assertThat(result.isEmailVerificationPending()).isTrue();
        ArgumentCaptor<UsersEntity> userCaptor = ArgumentCaptor.forClass(UsersEntity.class);
        verify(usersDao).update(userCaptor.capture());
        assertThat(userCaptor.getValue().getEmail()).isEqualTo("old@example.com");
        assertThat(userCaptor.getValue().getUsername()).isEqualTo("jiro");
        verify(emailVerificationService).sendEmailChangeVerification(1L, "new@example.com");
    }

    /**
     * メールアドレスを変更していない場合は確認メールを送信しないこと
     */
    @Test
    void メールアドレス未変更時は確認メールを送信しない() {
        // Arrange
        when(usersDao.selectById(1L)).thenReturn(existingUser);

        // Act
        UpdateUserProfileResponseDto result = usersService.updateUsersInfo(buildDto("old@example.com"));

        // Assert
        assertThat(result.isEmailVerificationPending()).isFalse();
        verify(usersDao).update(any());
        verify(emailVerificationService, never()).sendEmailChangeVerification(anyLong(), anyString());
    }

    /**
     * テスト用の更新リクエストDTOを生成する
     * @param email メールアドレス
     * @return 更新リクエストDTO
     */
    private UsersDto buildDto(String email) {
        UsersDto dto = new UsersDto();
        dto.setUserId(1L);
        dto.setUsername("jiro");
        dto.setEmail(email);
        dto.setBirthDate(LocalDate.of(1950, 1, 1));
        return dto;
    }
}
