package io.github.taichi0373.benefit_map.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
     * バスのリアルタイム車両位置を取得する
     *
     * @return 車両位置情報リスト（vehicleId / lat / lon / agencyName / routeId）
     */
    @Operation(summary = "車両位置取得", description = "GTFS-RT フィードから全事業者のバスリアルタイム位置を取得する。")
    @GetMapping("/vehicles")
    public ResponseEntity<ApiResponseDto<List<Map<String, Object>>>> getVehiclePositions() {
        try {
            List<Map<String, Object>> vehicles = vehiclePositionService.fetchVehiclePositions();
            return ResponseEntity.ok(ApiResponseDto.success(vehicles));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("車両位置情報の取得に失敗しました"));
        }
    }
}
