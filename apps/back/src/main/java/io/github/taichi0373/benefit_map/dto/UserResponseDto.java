package io.github.taichi0373.benefit_map.dto;

import java.time.LocalDate;

import lombok.Data;

/**
 * 認証APIレスポンスDTO
 * <p>
 * passwordHash 等の機微情報を含まない、クライアント返却用のユーザー情報DTO。
 * login / signup / getUsersInfo のレスポンスで使用する。
 * </p>
 */
@Data
public class UserResponseDto {

    /** ユーザーID */
    private Long userId;

    /** ユーザー名 */
    private String username;

    /** メールアドレス */
    private String email;

    /** 生年月日 */
    private LocalDate birthDate;

    /** 自治体コード */
    private String municipalityCode;

    /** 運転免許所持状況 */
    private String licenseStatus;

    /** 運転免許返納日 */
    private LocalDate licenseSurrenderedAt;

}
