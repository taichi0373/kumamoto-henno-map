package io.github.taichi0373.benefit_map.dto;

import java.time.LocalDate;

import io.github.taichi0373.benefit_map.repository.entity.SystemField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * ユーザー情報DTO
 * <p>
 * ユーザーの詳細情報を保持するデータ転送オブジェクト。
 * </p>
 */
@Schema(description = "ユーザー情報")
@Data
public class UsersDto {

    /** ユーザーID */
    @Schema(description = "ユーザーID（更新時に指定）", example = "1")
    private Long userId;

    /** ユーザー名 */
    @Schema(description = "ユーザー名（一意）", example = "taro", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    /** パスワード */
    @Schema(description = "パスワード（平文。サーバー側でハッシュ化）", example = "password123")
    private String password;

    /** メールアドレス */
    @Schema(description = "メールアドレス", example = "taro@example.com")
    private String email;

    /** 生年月日 */
    @Schema(description = "生年月日 (ISO 8601)", example = "2000-01-01", type = "string", format = "date")
    private LocalDate birthDate;

    /** 居住地域 */
    @Schema(description = "居住地域", example = "熊本市中央区")
    private String address;

    /** 運転免許の所持状況 */
    @Schema(description = "運転免許所持状況コード（例: SURRENDERED）", example = "SURRENDERED")
    private String licenseStatus;

    /** 運転免許返納日 */
    @Schema(description = "運転免許返納日 (ISO 8601)", example = "2024-04-01", type = "string", format = "date")
    private LocalDate licenseSurrenderedAt;

    /** システム共通フィールド */
    @Schema(hidden = true)
    private SystemField systemField;
}
