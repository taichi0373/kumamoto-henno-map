package io.github.taichi0373.kumamoto_henno_map.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

import io.github.taichi0373.kumamoto_henno_map.repository.dao.EmailVerificationTokensDao;
import io.github.taichi0373.kumamoto_henno_map.repository.dao.UsersDao;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.EmailVerificationTokensEntity;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.SystemField;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.UsersEntity;
import io.github.taichi0373.kumamoto_henno_map.service.EmailVerificationService.ConfirmResult;
import io.github.taichi0373.kumamoto_henno_map.util.TokenUtils;

/**
 * EmailVerificationService 単体テスト
 */
@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

    @InjectMocks
    private EmailVerificationService emailVerificationService;

    @Mock
    private UsersDao usersDao;

    @Mock
    private EmailVerificationTokensDao emailVerificationTokensDao;

    @Mock
    private MailService mailService;

    private UsersEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = new UsersEntity();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("old@example.com");
        LocalDateTime createdAt = LocalDateTime.of(2026, 1, 1, 0, 0);
        testUser.setSystemField(new SystemField(createdAt, createdAt));
    }

    /**
     * 新規登録時はハッシュ化したトークンを24時間の期限で保存し、平文トークン入りのURLを登録アドレスへ送信すること
     */
    @Test
    void 新規登録時の確認メールはハッシュ化したトークンを保存して送信する() {
        // Arrange
        when(mailService.getFrontendBaseUrl()).thenReturn("http://localhost:3000");
        LocalDateTime before = LocalDateTime.now();

        // Act
        emailVerificationService.sendSignupVerification(testUser);

        // Assert
        verify(emailVerificationTokensDao).markAllAsUsedByUserId(eq(1L), any(LocalDateTime.class));
        ArgumentCaptor<EmailVerificationTokensEntity> tokenCaptor =
                ArgumentCaptor.forClass(EmailVerificationTokensEntity.class);
        verify(emailVerificationTokensDao).insert(tokenCaptor.capture());
        EmailVerificationTokensEntity saved = tokenCaptor.getValue();
        assertThat(saved.getUserId()).isEqualTo(1L);
        assertThat(saved.getNewEmail()).isNull();
        assertThat(saved.getUsed()).isFalse();
        assertThat(saved.getExpiresAt()).isAfterOrEqualTo(before.plusHours(24));

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(mailService).send(eq("old@example.com"), anyString(), bodyCaptor.capture());
        String plainToken = bodyCaptor.getValue().replaceAll("(?s).*verify-email\\?token=([0-9a-f]{64}).*", "$1");
        assertThat(TokenUtils.hashToken(plainToken)).isEqualTo(saved.getToken());
    }

    /**
     * メールアドレス変更時は変更後アドレスをトークンに保存し、変更後アドレスへ送信すること
     */
    @Test
    void メールアドレス変更時は変更後のアドレスへ送信する() {
        // Arrange
        when(mailService.getFrontendBaseUrl()).thenReturn("http://localhost:3000");

        // Act
        emailVerificationService.sendEmailChangeVerification(1L, "new@example.com");

        // Assert
        ArgumentCaptor<EmailVerificationTokensEntity> tokenCaptor =
                ArgumentCaptor.forClass(EmailVerificationTokensEntity.class);
        verify(emailVerificationTokensDao).insert(tokenCaptor.capture());
        assertThat(tokenCaptor.getValue().getNewEmail()).isEqualTo("new@example.com");
        verify(mailService).send(eq("new@example.com"), anyString(), anyString());
    }

    /**
     * 同じ変更後アドレスへの送信は送信間隔内の場合スキップすること
     */
    @Test
    void 同じ変更後アドレスへの送信間隔内の再送はスキップする() {
        // Arrange
        when(emailVerificationTokensDao.selectLatestByUserId(1L))
                .thenReturn(buildToken("new@example.com", LocalDateTime.now().minusSeconds(10)));

        // Act
        emailVerificationService.sendEmailChangeVerification(1L, "new@example.com");

        // Assert
        verify(emailVerificationTokensDao, never()).insert(any());
        verify(mailService, never()).send(anyString(), anyString(), anyString());
    }

    /**
     * 存在しないメールアドレスへの再送要求は送信しないこと
     */
    @Test
    void 存在しないメールアドレスへの再送は送信しない() {
        // Arrange
        when(usersDao.selectByEmail("none@example.com")).thenReturn(null);

        // Act
        emailVerificationService.resendSignupVerification("none@example.com");

        // Assert
        verify(mailService, never()).send(anyString(), anyString(), anyString());
    }

    /**
     * 確認済みユーザーへの再送要求は送信しないこと
     */
    @Test
    void 確認済みユーザーへの再送は送信しない() {
        // Arrange
        testUser.setEmailVerifiedAt(LocalDateTime.now());
        when(usersDao.selectByEmail("old@example.com")).thenReturn(testUser);

        // Act
        emailVerificationService.resendSignupVerification("old@example.com");

        // Assert
        verify(mailService, never()).send(anyString(), anyString(), anyString());
    }

    /**
     * 前回送信から送信間隔内の再送要求は送信しないこと
     */
    @Test
    void 送信間隔内の再送は送信しない() {
        // Arrange
        when(usersDao.selectByEmail("old@example.com")).thenReturn(testUser);
        when(emailVerificationTokensDao.selectLatestByUserId(1L))
                .thenReturn(buildToken(null, LocalDateTime.now().minusSeconds(10)));

        // Act
        emailVerificationService.resendSignupVerification("old@example.com");

        // Assert
        verify(mailService, never()).send(anyString(), anyString(), anyString());
    }

    /**
     * 前回送信から送信間隔を過ぎた未確認ユーザーへの再送要求は送信すること
     */
    @Test
    void 送信間隔を過ぎた未確認ユーザーへの再送は送信する() {
        // Arrange
        when(mailService.getFrontendBaseUrl()).thenReturn("http://localhost:3000");
        when(usersDao.selectByEmail("old@example.com")).thenReturn(testUser);
        when(emailVerificationTokensDao.selectLatestByUserId(1L))
                .thenReturn(buildToken(null, LocalDateTime.now().minusSeconds(61)));

        // Act
        emailVerificationService.resendSignupVerification("old@example.com");

        // Assert
        verify(mailService).send(eq("old@example.com"), anyString(), anyString());
    }

    /**
     * 存在しないトークンは無効となること
     */
    @Test
    void 存在しないトークンは無効となる() {
        // Arrange
        when(emailVerificationTokensDao.selectByToken(anyString())).thenReturn(null);

        // Act
        ConfirmResult result = emailVerificationService.confirm("unknown");

        // Assert
        assertThat(result).isEqualTo(ConfirmResult.INVALID);
        verify(usersDao, never()).update(any());
    }

    /**
     * 使用済み・期限切れのトークンは無効となること
     */
    @Test
    void 使用済みまたは期限切れのトークンは無効となる() {
        // Arrange
        when(emailVerificationTokensDao.selectByToken(anyString())).thenReturn(buildToken(null, LocalDateTime.now()));
        when(emailVerificationTokensDao.markAsUsedIfValid(anyString(), any(LocalDateTime.class))).thenReturn(0);

        // Act
        ConfirmResult result = emailVerificationService.confirm("used");

        // Assert
        assertThat(result).isEqualTo(ConfirmResult.INVALID);
        verify(usersDao, never()).update(any());
    }

    /**
     * 新規登録時のトークンで確認するとユーザーが確認済みになること
     */
    @Test
    void 新規登録時のトークンで確認すると確認済みになる() {
        // Arrange
        when(emailVerificationTokensDao.selectByToken(TokenUtils.hashToken("valid")))
                .thenReturn(buildToken(null, LocalDateTime.now()));
        when(emailVerificationTokensDao.markAsUsedIfValid(anyString(), any(LocalDateTime.class))).thenReturn(1);
        when(usersDao.selectById(1L)).thenReturn(testUser);

        // Act
        ConfirmResult result = emailVerificationService.confirm("valid");

        // Assert
        assertThat(result).isEqualTo(ConfirmResult.VERIFIED);
        ArgumentCaptor<UsersEntity> userCaptor = ArgumentCaptor.forClass(UsersEntity.class);
        verify(usersDao).update(userCaptor.capture());
        assertThat(userCaptor.getValue().getEmailVerifiedAt()).isNotNull();
        assertThat(userCaptor.getValue().getEmail()).isEqualTo("old@example.com");
    }

    /**
     * メールアドレス変更時のトークンで確認するとメールアドレスが切り替わること
     */
    @Test
    void メールアドレス変更時のトークンで確認するとアドレスが切り替わる() {
        // Arrange
        testUser.setEmailVerifiedAt(LocalDateTime.of(2026, 1, 1, 0, 0));
        when(emailVerificationTokensDao.selectByToken(anyString()))
                .thenReturn(buildToken("new@example.com", LocalDateTime.now()));
        when(emailVerificationTokensDao.markAsUsedIfValid(anyString(), any(LocalDateTime.class))).thenReturn(1);
        when(usersDao.selectById(1L)).thenReturn(testUser);
        when(usersDao.existsByEmailExcluding("new@example.com", 1L)).thenReturn(false);

        // Act
        ConfirmResult result = emailVerificationService.confirm("valid");

        // Assert
        assertThat(result).isEqualTo(ConfirmResult.VERIFIED);
        ArgumentCaptor<UsersEntity> userCaptor = ArgumentCaptor.forClass(UsersEntity.class);
        verify(usersDao).update(userCaptor.capture());
        assertThat(userCaptor.getValue().getEmail()).isEqualTo("new@example.com");
    }

    /**
     * 確認時に変更後のアドレスが他のユーザーに使用されている場合は競合となり、更新しないこと
     */
    @Test
    void 変更後のアドレスが使用済みの場合は競合となる() {
        // Arrange
        when(emailVerificationTokensDao.selectByToken(anyString()))
                .thenReturn(buildToken("taken@example.com", LocalDateTime.now()));
        when(emailVerificationTokensDao.markAsUsedIfValid(anyString(), any(LocalDateTime.class))).thenReturn(1);
        when(usersDao.selectById(1L)).thenReturn(testUser);
        when(usersDao.existsByEmailExcluding("taken@example.com", 1L)).thenReturn(true);

        // Act
        ConfirmResult result = emailVerificationService.confirm("valid");

        // Assert
        assertThat(result).isEqualTo(ConfirmResult.CONFLICT);
        verify(usersDao, never()).update(any());
    }

    /**
     * クリーンアップでは作成から24時間を経過した未確認ユーザーを削除対象とすること
     */
    @Test
    void クリーンアップは24時間経過した未確認ユーザーを削除する() {
        // Arrange
        LocalDateTime before = LocalDateTime.now();

        // Act
        emailVerificationService.cleanupUnverified();

        // Assert
        ArgumentCaptor<LocalDateTime> thresholdCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(usersDao).deleteUnverifiedCreatedBefore(thresholdCaptor.capture());
        assertThat(thresholdCaptor.getValue())
                .isAfterOrEqualTo(before.minusHours(24))
                .isBeforeOrEqualTo(LocalDateTime.now().minusHours(24));
        verify(emailVerificationTokensDao).deleteExpiredOrUsed(any(LocalDateTime.class));
    }

    /**
     * テスト用トークンエンティティを生成する
     * @param newEmail 変更後メールアドレス（新規登録時はnull）
     * @param createdAt 作成日時
     * @return トークンエンティティ
     */
    private EmailVerificationTokensEntity buildToken(String newEmail, LocalDateTime createdAt) {
        EmailVerificationTokensEntity token = new EmailVerificationTokensEntity();
        token.setTokenId(10L);
        token.setUserId(1L);
        token.setNewEmail(newEmail);
        token.setUsed(false);
        token.setExpiresAt(createdAt.plusHours(24));
        token.setSystemField(new SystemField(createdAt, createdAt));
        return token;
    }
}
