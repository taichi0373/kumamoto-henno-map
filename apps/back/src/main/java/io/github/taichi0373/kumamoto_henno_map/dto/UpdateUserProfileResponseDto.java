package io.github.taichi0373.kumamoto_henno_map.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ユーザー情報更新レスポンスDTO
 */
@Schema(description = "ユーザー情報更新レスポンス")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserProfileResponseDto {

    /** メールアドレス確認待ちフラグ（true の場合、確認完了までメールアドレスは変更前のまま） */
    @Schema(description = "メールアドレス変更の確認待ちの場合 true。確認が完了するまでメールアドレスは変更前のまま", example = "false")
    private boolean emailVerificationPending;

}
