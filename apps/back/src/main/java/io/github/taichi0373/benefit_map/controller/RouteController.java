package io.github.taichi0373.benefit_map.controller;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.taichi0373.benefit_map.dto.ApiResponseDto;
import io.github.taichi0373.benefit_map.dto.RouteRequestDto;
import io.github.taichi0373.benefit_map.service.RouteService;

import java.io.IOException;

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
    @PostMapping("/search")
    public ResponseEntity<ApiResponseDto<?>> searchRoutes(@RequestBody RouteRequestDto request) {
        try {
            JsonNode result = routeService.searchRoutes(request);
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
