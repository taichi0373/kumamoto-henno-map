package io.github.taichi0373.kumamoto_henno_map.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.taichi0373.kumamoto_henno_map.dto.ApiResponseDto;
import io.github.taichi0373.kumamoto_henno_map.dto.MunicipalityListResponse;
import io.github.taichi0373.kumamoto_henno_map.service.MunicipalityService;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.MunicipalityEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 市区町村情報コントローラー
 * <p>
 * 市区町村情報の取得に関するエンドポイントを提供する。
 * </p>
 */
@Tag(name = "市区町村", description = "熊本県内の市区町村情報取得")
@RestController
@RequestMapping("/municipality")
public class MunicipalityController {

    /**
     * 市区町村情報サービス
     */
    @Autowired
    private MunicipalityService municipalityService;

    /**
     * 全ての市区町村情報を取得
     */
    @Operation(summary = "全市区町村取得", description = "熊本県内の全市区町村情報を取得する。認証・CSRF 保護は不要。")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "取得成功",
                    content = @Content(schema = @Schema(implementation = MunicipalityListResponse.class))),
            @ApiResponse(responseCode = "500", description = "サーバー内部エラー",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @GetMapping("/all")
    public ResponseEntity<ApiResponseDto<List<MunicipalityEntity>>> getAllMunicipality() {
        try {
            List<MunicipalityEntity> municipalities = municipalityService.getMunicipality();
            return ResponseEntity.ok(ApiResponseDto.success(municipalities));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("市区町村情報の取得に失敗しました"));
        }
    }
}
