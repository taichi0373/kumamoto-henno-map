package io.github.taichi0373.benefit_map.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.taichi0373.benefit_map.dto.ApiResponseDto;
import io.github.taichi0373.benefit_map.service.VehiclePositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 車両位置情報コントローラー
 * <p>
 * GTFS-RT フィードから取得した各バス事業者のリアルタイム車両位置を返す。
 * </p>
 */
@Tag(name = "経路探索", description = "OpenTripPlanner を使用した公共交通経路探索")
@RestController
@RequestMapping("/route")
public class VehiclePositionController {

    /**
     * 車両位置情報サービス
     */
    @Autowired
    private VehiclePositionService vehiclePositionService;

    /**
     * 指定した routeId のバスリアルタイム車両位置を取得する
     *
     * @param routeIds カンマ区切りの routeId 文字列（例: "5_5232_1_20260502,722_722120_1_20260501"）
     * @return 車両位置情報リスト（vehicleId / lat / lon / agencyName / routeId）
     */
    @Operation(summary = "車両位置取得", description = "GTFS-RT フィードから指定 routeId のバスリアルタイム位置を取得する。")
    @GetMapping("/vehicles")
    public ResponseEntity<ApiResponseDto<List<Map<String, Object>>>> getVehiclePositions(
            @RequestParam(required = false) String routeIds) {
        try {
            Set<String> routeIdSet = (routeIds != null && !routeIds.isBlank())
                    ? Arrays.stream(routeIds.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toSet())
                    : Set.of();
            List<Map<String, Object>> vehicles = vehiclePositionService.fetchVehiclePositions(routeIdSet);
            return ResponseEntity.ok(ApiResponseDto.success(vehicles));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("車両位置情報の取得に失敗しました"));
        }
    }
}
