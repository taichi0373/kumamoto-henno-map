package io.github.taichi0373.benefit_map.controller;

import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.taichi0373.benefit_map.dto.RouteRequest;
import io.github.taichi0373.benefit_map.service.RouteService;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RestController
@RequestMapping("/route")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:6006", "http://127.0.0.1:3000", "http://127.0.0.1:6006"}, allowCredentials = "true")
public class RouteController {
    
    private static final Logger log = LoggerFactory.getLogger(RouteController.class);
    
    private final RouteService routeService;
    
    /**
     * 経路探索
     */
    @PostMapping("/search")
    public ResponseEntity<JsonNode> searchRoutes(@RequestBody RouteRequest request) {
        try {
            log.info("経路探索リクエスト: {}", request);
            JsonNode result = routeService.searchRoutes(request);
            return ResponseEntity.ok(result);
        } catch (IOException | ParseException e) {
            log.error("経路探索エラー", e);
            return ResponseEntity.status(500).build();
        } catch (Exception e) {
            log.error("経路探索エラー", e);
            return ResponseEntity.status(500).build();
        }
    }
}
