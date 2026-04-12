package io.github.taichi0373.benefit_map.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 認証APIレスポンスDTO
 * <p>
 * passwordHash 等の機微情報を含まない、クライアント返却用のユーザー情報DTO。
 * login / signup / getUsersInfo のレスポンスで使用する。
 * </p>
 */
@Schema(description = "ユーザー情報レスポンス（機微情報を除く）")
@Data
public class UserResponseDto {

    /** ユーザーID */
    @Schema(description = "ユーザーID", example = "1")
    private Long userId;

    /** ユーザー名 */
    @Schema(description = "ユーザー名", example = "taro")
    private String username;

    /** メールアドレス */
    @Schema(description = "メールアドレス", example = "taro@example.com")
    private String email;

    /** 生年月日 */
    @Schema(description = "生年月日 (ISO 8601)", example = "2000-01-01", type = "string", format = "date")
    private LocalDate birthDate;

    /** 居住地域 */
    @Schema(description = "居住地域", example = "熊本市中央区")
    private String address;

    /** 運転免許所持状況 */
    @Schema(description = "運転免許所持状況コード（0: 未所持, 1: 所持, 2: 返納, 3: 失効）", example = "2")
    private String licenseStatus;

    /** 運転免許返納日 */
    @Schema(description = "運転免許返納日 (ISO 8601)。未返納の場合は null", example = "2024-04-01", type = "string", format = "date", nullable = true)
    private LocalDate licenseSurrenderedAt;

}
