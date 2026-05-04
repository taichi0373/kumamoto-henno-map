package io.github.taichi0373.benefit_map.dto.admin;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * 管理者向けユーザーレスポンスDTO
 * <p>
 * パスワードハッシュを除いた、管理者API用のユーザー情報DTO。
 * IS_ADMIN の変更は受け付けない（読み取り専用）。
 * </p>
 */
@Data
public class AdminUserResponseDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** ユーザーID */
    private Long userId;

    /** ユーザー名 */
    private String username;

    /** メールアドレス */
    private String email;

    /** 生年月日 */
    private LocalDate birthDate;

    /** 自治体コード */
    private String municipalityCd;

    /** 運転免許所持状況 */
    private String licenseStatus;

    /** 運転免許返納日 */
    private LocalDate licenseSurrenderedAt;

    /** 管理者フラグ（読み取り専用） */
    private String isAdmin;

    /** 作成日時 */
    private LocalDateTime sysCreatedAt;

    /** 更新日時 */
    private LocalDateTime sysUpdatedAt;
}
