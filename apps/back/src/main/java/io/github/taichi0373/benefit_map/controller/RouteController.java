package io.github.taichi0373.benefit_map.controller;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.taichi0373.benefit_map.dto.ApiResponseDto;
import io.github.taichi0373.benefit_map.dto.RouteRequestDto;
import io.github.taichi0373.benefit_map.security.CustomUserDetails;
import io.github.taichi0373.benefit_map.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;

/**
 * 経路探索コントローラー
 * <p>
 * OTP（OpenTripPlanner）を使用した経路探索に関するエンドポイントを提供する。
 * </p>
 */
@Tag(name = "経路探索", description = "OpenTripPlanner を使用した公共交通経路探索")
@RestController
@RequestMapping("/route")
public class RouteController {

    /**
     * 経路情報サービス
     */
    @Autowired
    private RouteService routeService;

    /**
     * 経路探索
     */
    @Operation(summary = "経路探索", description = "出発地・目的地・日時を指定し OTP 経由で公共交通経路を探索する。未ログインでも利用可（ログイン時はユーザーIDがログに記録される）。X-Service-Name ヘッダー必須。")
    @SecurityRequirement(name = "serviceHeader")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "探索成功（OTP レスポンスをそのまま返却）"),
            @ApiResponse(responseCode = "500", description = "OTP 接続エラーまたは探索失敗",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @PostMapping("/search")
    public ResponseEntity<ApiResponseDto<JsonNode>> searchRoutes(@RequestBody RouteRequestDto request, Authentication auth) {
        try {
            // JWT認証済みの場合はユーザーIDを取得（未ログインは null）
            Long userId = (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails)
                    ? ((CustomUserDetails) auth.getPrincipal()).getUserId()
                    : null;

            JsonNode result = routeService.searchRoutes(request, userId);
            return ResponseEntity.ok(ApiResponseDto.success(result));
        } catch (IOException | ParseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("経路探索中にエラーが発生しました"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("経路探索中にエラーが発生しました"));
        }
    }
}
