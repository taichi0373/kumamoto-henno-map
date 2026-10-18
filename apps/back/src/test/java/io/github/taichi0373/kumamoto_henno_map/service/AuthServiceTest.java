package io.github.taichi0373.kumamoto_henno_map.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
    }

    /**
     * ログイン成功時にlastLoginAtとlastLoginIpがエンティティに設定されてDAOのupdateが呼ばれること
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

        ArgumentCaptor<UsersEntity> captor = ArgumentCaptor.forClass(UsersEntity.class);
        verify(usersDao).update(captor.capture());
        UsersEntity updated = captor.getValue();

        assertThat(updated.getLastLoginIp()).isEqualTo("192.168.1.1");
        assertThat(updated.getLastLoginAt()).isNotNull();
        assertThat(updated.getLastLoginAt()).isAfterOrEqualTo(beforeLogin);
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
        ArgumentCaptor<UsersEntity> captor = ArgumentCaptor.forClass(UsersEntity.class);
        verify(usersDao).update(captor.capture());
        assertThat(captor.getValue().getLastLoginIp()).isEqualTo("::1");
    }

    /**
     * ログイン失敗時（パスワード不一致）はupdateが呼ばれないこと
     */
    @Test
    void ログイン失敗時はupdateが呼ばれない() {
        // Arrange
        when(usersDao.selectByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        // Act
        var result = authService.login("testuser", "wrong", "192.168.1.1");

        // Assert
        assertThat(result).isNull();
        verify(usersDao, org.mockito.Mockito.never()).update(any());
    }
}
