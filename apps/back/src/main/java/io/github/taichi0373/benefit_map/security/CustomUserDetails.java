package io.github.taichi0373.benefit_map.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import io.github.taichi0373.benefit_map.repository.entity.UsersEntity;
import io.github.taichi0373.benefit_map.constants.CodeConstants;

/**
 * Spring Security 用のカスタムユーザー詳細クラス
 * <p>
 * UsersEntity をラップして UserDetails インターフェースを実装する。
 * userId への追加アクセスを提供する。
 * </p>
 */
public class CustomUserDetails implements UserDetails {

    /** ユーザーエンティティ */
    private final UsersEntity user;

    /**
     * コンストラクタ
     * @param user ユーザーエンティティ
     */
    public CustomUserDetails(UsersEntity user) {
        this.user = user;
    }

    /**
     * ユーザーIDを取得する
     * @return ユーザーID
     */
    public Long getUserId() {
        return user.getUserId();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = CodeConstants.UserType.ADMIN.equals(user.getIsAdmin()) ? "ROLE_ADMIN" : "ROLE_USER";
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
