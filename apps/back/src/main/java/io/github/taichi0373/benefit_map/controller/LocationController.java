// package com.example.back.controller;

// import com.example.back.service.YolpService;
// import com.fasterxml.jackson.databind.JsonNode;
// import lombok.RequiredArgsConstructor;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.Map;

// @RestController
// @RequestMapping("/location")
// @RequiredArgsConstructor
// @CrossOrigin
// public class LocationController {

//     private static final Logger log = LoggerFactory.getLogger(LocationController.class);

//     private final YolpService yolpService;

//     @PostMapping("/search")
//     public ResponseEntity<Map<String, Object>> searchLocation(@RequestBody Map<String, String> request) {
//         try {
//             String query = request.get("query");
//             if (query == null || query.trim().isEmpty()) {
//                 return ResponseEntity.badRequest().build();
//             }

//             log.info("Location search request: {}", query);
//             Map<String, Object> result = yolpService.searchLocation(query);
//             return ResponseEntity.ok(result);
//         } catch (Exception e) {
//             log.error("Error searching location", e);
//             return ResponseEntity.status(500).build();
//         }
//     }

//     @GetMapping("/stores")
//     public ResponseEntity<JsonNode> getStores() {
//         try {
//             log.info("Getting stores with coordinates");
//             JsonNode stores = yolpService.getStoresWithCoordinates();
//             return ResponseEntity.ok(stores);
//         } catch (Exception e) {
//             log.error("Error getting stores", e);
//             return ResponseEntity.status(500).build();
//         }
//     }
// }
