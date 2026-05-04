package com.example.back.controller;

import com.example.back.dto.RouteRequest;
import com.example.back.service.OtpService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/route")
@RequiredArgsConstructor
@CrossOrigin
public class RouteController {

    private static final Logger log = LoggerFactory.getLogger(RouteController.class);

    private final OtpService otpService;

    @PostMapping("/search")
    public ResponseEntity<JsonNode> searchRoute(@RequestBody RouteRequest request) {
        try {
            log.info("Route search request: {}", request);
            JsonNode routes = otpService.searchRoutes(request);
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            log.error("Error searching routes", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<JsonNode> searchRouteGet(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(required = false) String time,
            @RequestParam(required = false) String date,
            @RequestParam(defaultValue = "WALK") String mode,
            @RequestParam(defaultValue = "false") String arriveBy) {
        
        try {
            RouteRequest request = new RouteRequest(from, to, mode, null, date, time, "true".equals(arriveBy));
            
            log.info("Route search request (GET): {}", request);
            JsonNode routes = otpService.searchRoutes(request);
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            log.error("Error searching routes", e);
            return ResponseEntity.status(500).build();
        }
    }
}
