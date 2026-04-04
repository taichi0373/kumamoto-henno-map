package io.github.taichi0373.benefit_map.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import io.github.taichi0373.benefit_map.dto.ApiResponseDto;
import io.github.taichi0373.benefit_map.dto.BenefitEligibilityDto;
import io.github.taichi0373.benefit_map.dto.BenefitListResponse;
import io.github.taichi0373.benefit_map.security.CustomUserDetails;
import io.github.taichi0373.benefit_map.service.BenefitService;
import io.github.taichi0373.benefit_map.repository.entity.BenefitDetailEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 特典情報コントローラー
 * <p>
 * 特典の検索・取得に関するエンドポイントを提供する。
 * </p>
 */
@Tag(name = "特典", description = "特典情報の検索・取得")
@RestController
@RequestMapping("/benefit")
public class BenefitController {

    /**
     * 特典情報サービス
     */
    @Autowired
    private BenefitService benefitService;

    /**
     * 検索条件（年齢・運転免許所持状況・自治体コード）から特典を検索
     */
    @Operation(summary = "特典検索", description = "年齢・免許状態・自治体コードを指定して特典一覧を取得する。認証不要。X-Service-Name ヘッダー必須。")
    @SecurityRequirement(name = "serviceHeader")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "検索成功",
                    content = @Content(schema = @Schema(implementation = BenefitListResponse.class))),
            @ApiResponse(responseCode = "500", description = "サーバー内部エラー",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @PostMapping("/search")
    public ResponseEntity<ApiResponseDto<List<BenefitDetailEntity>>> searchBenefits(@RequestBody BenefitEligibilityDto request) {
        try {
            List<BenefitDetailEntity> benefits = benefitService.searchBenefits(request);
            return ResponseEntity.ok(ApiResponseDto.success(benefits));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("特典検索中にエラーが発生しました"));
        }
    }

    /**
     * ユーザーIDからユーザーが受けられる特典を検索
     */
    @Operation(summary = "ユーザー特典取得", description = "ユーザーのプロフィール情報（年齢・免許状態・居住自治体）を元に受けられる特典一覧を取得する。JWT 認証必須。")
    @SecurityRequirement(name = "cookieAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "取得成功",
                    content = @Content(schema = @Schema(implementation = BenefitListResponse.class))),
            @ApiResponse(responseCode = "401", description = "未認証",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "他ユーザーへのアクセス",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponseDto<List<BenefitDetailEntity>>> getUsersBenefits(@PathVariable Long userId, Authentication auth) {
        try {
            // JWT認証チェック
            if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof CustomUserDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseDto.error("認証が必要です"));
            }

            // ユーザーIDの一致確認
            CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
            if (!userId.equals(principal.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponseDto.error("アクセス権限がありません"));
            }

            List<BenefitDetailEntity> benefits = benefitService.getUsersBenefits(userId);
            return ResponseEntity.ok(ApiResponseDto.success(benefits));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("ユーザー特典情報の取得に失敗しました"));
        }
    }
}
