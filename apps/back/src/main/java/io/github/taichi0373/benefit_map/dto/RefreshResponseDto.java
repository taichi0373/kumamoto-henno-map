package io.github.taichi0373.benefit_map.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * トークンリフレッシュレスポンスDTO
 * <p>
 * リフレッシュトークンを用いたアクセストークン再発行時に返す、新しいJWTトークンとユーザー情報を保持する。
 * </p>
 */
@Schema(description = "トークンリフレッシュレスポンス")
@Data
@AllArgsConstructor
public class RefreshResponseDto {

    /** 新しいJWTアクセストークン */
    @Schema(description = "新しいJWTアクセストークン（Authorization: Bearer <token> で使用）")
    private String token;

    /** ユーザー情報（フロントエンドのセッション復元用） */
    @Schema(description = "ユーザー情報（フロントエンドの isAdmin 判定に使用）")
    private UserInfo user;

    /**
     * リフレッシュレスポンスに含めるユーザー情報
     * <p>
     * フロントエンドの User 型（id, username, isAdmin）に対応する。
     * </p>
     */
    @Schema(description = "ユーザー情報")
    @Data
    @AllArgsConstructor
    public static class UserInfo {

        /** ユーザーID（文字列） */
        @Schema(description = "ユーザーID", example = "1")
        private String id;

        /** ユーザー名 */
        @Schema(description = "ユーザー名", example = "taro")
        private String username;

        /** 管理者フラグ */
        @JsonProperty("isAdmin")
        @Schema(description = "管理者フラグ（trueの場合は管理者ユーザー）", example = "false")
        private boolean isAdmin;
    }
}
