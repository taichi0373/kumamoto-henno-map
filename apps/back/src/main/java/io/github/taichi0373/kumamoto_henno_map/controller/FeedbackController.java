package io.github.taichi0373.kumamoto_henno_map.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.taichi0373.kumamoto_henno_map.dto.ApiResponseDto;
import io.github.taichi0373.kumamoto_henno_map.dto.FeedbackRequestDto;
import io.github.taichi0373.kumamoto_henno_map.security.CustomUserDetails;
import io.github.taichi0373.kumamoto_henno_map.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * フィードバックコントローラー
 * <p>
 * ユーザーからのご意見・ご要望を受け付け、Slackへ通知するエンドポイントを提供する。
 * 認証不要（未ログインユーザーも送信可能）。
 * </p>
 */
@Tag(name = "フィードバック", description = "ご意見・ご要望の送信")
@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    /**
     * フィードバックサービス
     */
    @Autowired
    private FeedbackService feedbackService;

    /**
     * フィードバックを送信する
     * <p>
     * ログイン済みの場合はユーザー名をSlackメッセージに含める。
     * 認証不要のエンドポイント（anyRequest().permitAll() でカバー）。
     * </p>
     *
     * @param request フィードバックリクエスト
     * @param auth    認証情報（未ログイン時はnull）
     * @return 送信結果
     */
    @Operation(summary = "フィードバック送信", description = "ご意見・ご要望をSlackへ送信する。認証不要。")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "送信成功",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "リクエスト不正",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "サーバー内部エラー",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @PostMapping
    public ResponseEntity<ApiResponseDto<Void>> sendFeedback(
            @RequestBody FeedbackRequestDto request,
            Authentication auth) {
        try {
            if (request.getContent() == null || request.getContent().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDto.error("内容は必須です"));
            }

            String username = (auth != null && auth.getPrincipal() instanceof CustomUserDetails principal)
                    ? principal.getUsername() : null;

            feedbackService.sendFeedback(request, username);
            return ResponseEntity.ok(ApiResponseDto.success(null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("フィードバックの送信に失敗しました"));
        }
    }
}
