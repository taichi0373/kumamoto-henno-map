package io.github.taichi0373.kumamoto_henno_map.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import io.github.taichi0373.kumamoto_henno_map.constants.CodeConstants;
import io.github.taichi0373.kumamoto_henno_map.exception.AdminAccountDeletionException;
import io.github.taichi0373.kumamoto_henno_map.repository.dao.UsersDao;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.UsersEntity;

/**
 * UsersService#deleteOwnAccount の単体テスト
 */
@ExtendWith(MockitoExtension.class)
class UsersServiceDeleteTest {

    @InjectMocks
    private UsersService usersService;

    @Mock
    private UsersDao usersDao;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    /** パスワード一致・一般ユーザーの場合、trueを返してDAOのdeleteを呼ぶ */
    @Test
    void deleteOwnAccount_正常削除() throws Exception {
        UsersEntity entity = new UsersEntity();
        entity.setUserId(1L);
        entity.setIsAdmin(CodeConstants.UserType.GENERAL);
        entity.setPasswordHash("hashedPass");

        when(usersDao.selectById(1L)).thenReturn(entity);
        when(passwordEncoder.matches("pass", "hashedPass")).thenReturn(true);
        when(usersDao.delete(entity)).thenReturn(1);

        Boolean result = usersService.deleteOwnAccount(1L, "pass");

        assertThat(result).isTrue();
        verify(usersDao).delete(entity);
    }

    /** パスワード不一致の場合、falseを返してDAOのdeleteを呼ばない */
    @Test
    void deleteOwnAccount_パスワード不一致() throws Exception {
        UsersEntity entity = new UsersEntity();
        entity.setUserId(1L);
        entity.setIsAdmin(CodeConstants.UserType.GENERAL);
        entity.setPasswordHash("hashedPass");

        when(usersDao.selectById(1L)).thenReturn(entity);
        when(passwordEncoder.matches("wrong", "hashedPass")).thenReturn(false);

        Boolean result = usersService.deleteOwnAccount(1L, "wrong");

        assertThat(result).isFalse();
        verify(usersDao, never()).delete(any());
    }

    /** ユーザーが存在しない場合、nullを返す */
    @Test
    void deleteOwnAccount_ユーザー不存在() throws Exception {
        when(usersDao.selectById(99L)).thenReturn(null);

        Boolean result = usersService.deleteOwnAccount(99L, "pass");

        assertThat(result).isNull();
        verify(usersDao, never()).delete(any());
    }

    /** 管理者アカウントの場合、AdminAccountDeletionExceptionをスローする */
    @Test
    void deleteOwnAccount_管理者は削除不可() {
        UsersEntity entity = new UsersEntity();
        entity.setUserId(1L);
        entity.setIsAdmin(CodeConstants.UserType.ADMIN);
        entity.setPasswordHash("hashedPass");

        when(usersDao.selectById(1L)).thenReturn(entity);

        assertThatThrownBy(() -> usersService.deleteOwnAccount(1L, "pass"))
                .isInstanceOf(AdminAccountDeletionException.class)
                .hasMessage("管理者アカウントは退会できません");
        verify(usersDao, never()).delete(any());
    }
}
