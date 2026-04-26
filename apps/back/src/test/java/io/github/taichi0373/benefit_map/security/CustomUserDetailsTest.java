package io.github.taichi0373.benefit_map.security;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import io.github.taichi0373.benefit_map.constants.CodeConstants;
import io.github.taichi0373.benefit_map.repository.entity.UsersEntity;

/**
 * CustomUserDetails の単体テスト
 */
class CustomUserDetailsTest {

    /**
     * isAdmin が "1" のとき、getAuthorities() が ROLE_ADMIN を返すことを確認する
     */
    @Test
    void getAuthorities_管理者フラグが1のとき_ROLE_ADMINを返す() {
        UsersEntity user = new UsersEntity();
        user.setUsername("admin");
        user.setPasswordHash("hash");
        user.setIsAdmin(CodeConstants.UserType.ADMIN);

        CustomUserDetails details = new CustomUserDetails(user);
        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();

        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    /**
     * isAdmin が "0" のとき、getAuthorities() が ROLE_USER を返すことを確認する
     */
    @Test
    void getAuthorities_管理者フラグが0のとき_ROLE_USERを返す() {
        UsersEntity user = new UsersEntity();
        user.setUsername("user");
        user.setPasswordHash("hash");
        user.setIsAdmin(CodeConstants.UserType.GENERAL);

        CustomUserDetails details = new CustomUserDetails(user);
        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();

        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    /**
     * isAdmin が null のとき、getAuthorities() が ROLE_USER を返すことを確認する
     */
    @Test
    void getAuthorities_管理者フラグがnullのとき_ROLE_USERを返す() {
        UsersEntity user = new UsersEntity();
        user.setUsername("user");
        user.setPasswordHash("hash");
        user.setIsAdmin(null);

        CustomUserDetails details = new CustomUserDetails(user);
        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();

        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }
}
