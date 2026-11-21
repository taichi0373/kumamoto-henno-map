package io.github.taichi0373.kumamoto_henno_map.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import io.github.taichi0373.kumamoto_henno_map.exception.EmailNotVerifiedException;
import io.github.taichi0373.kumamoto_henno_map.repository.dao.UsersDao;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.UsersEntity;
import io.github.taichi0373.kumamoto_henno_map.util.JwtUtil;

/**
 * AuthService 単体テスト
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UsersDao usersDao;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    private UsersEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = new UsersEntity();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setPasswordHash("hashed");
        testUser.setIsAdmin("0");
        testUser.setEmailVerifiedAt(LocalDateTime.of(2026, 1, 1, 0, 0));
    }

    /**
     * ログイン成功時に最終ログイン日時とIPのみを更新するDAOメソッドが呼ばれ、エンティティ全体の更新は行われないこと
     */
    @Test
    void ログイン成功時に最終ログイン日時とIPが保存される() {
        // Arrange
        when(usersDao.selectByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("password", "hashed")).thenReturn(true);
        when(jwtUtil.generateToken(any())).thenReturn("dummy-token");

        LocalDateTime beforeLogin = LocalDateTime.now();

        // Act
        var result = authService.login("testuser", "password", "192.168.1.1");

        // Assert
        assertThat(result).isNotNull();

        ArgumentCaptor<LocalDateTime> atCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(usersDao).updateLastLogin(eq(1L), atCaptor.capture(), eq("192.168.1.1"));
        assertThat(atCaptor.getValue()).isAfterOrEqualTo(beforeLogin);
        verify(usersDao, never()).update(any());
    }

    /**
     * IPv6アドレスでのログイン成功時にIPが保存されること
     */
    @Test
    void IPv6アドレスでログイン成功時にIPが保存される() {
        // Arrange
        when(usersDao.selectByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("password", "hashed")).thenReturn(true);
        when(jwtUtil.generateToken(any())).thenReturn("dummy-token");

        // Act
        var result = authService.login("testuser", "password", "::1");

        // Assert
        assertThat(result).isNotNull();
        verify(usersDao).updateLastLogin(eq(1L), any(LocalDateTime.class), eq("::1"));
    }

    /**
     * カラム長（45文字）を超えるIPアドレスは切り詰めて保存されること
     */
    @Test
    void カラム長を超えるIPアドレスは切り詰めて保存される() {
        // Arrange
        when(usersDao.selectByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("password", "hashed")).thenReturn(true);
        when(jwtUtil.generateToken(any())).thenReturn("dummy-token");
        String longIp = "fe80:0000:0000:0000:0000:0000:0000:0001%enp0s31f6";

        // Act
        authService.login("testuser", "password", longIp);

        // Assert
        verify(usersDao).updateLastLogin(eq(1L), any(LocalDateTime.class), eq(longIp.substring(0, 45)));
    }

    /**
     * 最終ログイン情報の保存に失敗してもログインは成功すること
     */
    @Test
    void 最終ログイン情報の保存失敗時もログインは成功する() {
        // Arrange
        when(usersDao.selectByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("password", "hashed")).thenReturn(true);
        when(jwtUtil.generateToken(any())).thenReturn("dummy-token");
        when(usersDao.updateLastLogin(anyLong(), any(LocalDateTime.class), anyString()))
                .thenThrow(new RuntimeException("DB error"));

        // Act
        var result = authService.login("testuser", "password", "192.168.1.1");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("dummy-token");
    }

    /**
     * ログイン失敗時（パスワード不一致）は最終ログイン情報が更新されないこと
     */
    @Test
    void ログイン失敗時は最終ログイン情報が更新されない() {
        // Arrange
        when(usersDao.selectByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        // Act
        var result = authService.login("testuser", "wrong", "192.168.1.1");

        // Assert
        assertThat(result).isNull();
        verify(usersDao, never()).updateLastLogin(any(), any(), any());
    }

    /**
     * パスワードが正しくメールアドレスが未確認の場合は未確認例外となり、ログイン情報は更新されないこと
     */
    @Test
    void メールアドレス未確認の場合は未確認例外となる() {
        // Arrange
        testUser.setEmailVerifiedAt(null);
        when(usersDao.selectByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("password", "hashed")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.login("testuser", "password", "192.168.1.1"))
                .isInstanceOf(EmailNotVerifiedException.class);
        verify(usersDao, never()).updateLastLogin(any(), any(), any());
        verify(jwtUtil, never()).generateToken(any());
    }

    /**
     * メールアドレス未確認でもパスワードが誤っている場合は通常の認証失敗（null）となること
     */
    @Test
    void メールアドレス未確認でもパスワード誤りは認証失敗となる() {
        // Arrange
        testUser.setEmailVerifiedAt(null);
        when(usersDao.selectByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        // Act
        var result = authService.login("testuser", "wrong", "192.168.1.1");

        // Assert
        assertThat(result).isNull();
    }
}
