// package com.example.back.controller;

// import com.example.back.service.StoreService;
// import com.example.back.dto.StoreResponse;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/store")
// @CrossOrigin(origins = "*")
// public class StoreController {

//     @Autowired
//     private StoreService storeService;

//     @GetMapping("/facilities")
//     public ResponseEntity<StoreResponse> getFacilities() {
//         try {
//             StoreResponse response = storeService.getFacilityInfo();
//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             e.printStackTrace();
//             return ResponseEntity.status(500)
//                 .body(new StoreResponse(false, "施設情報の取得に失敗しました: " + e.getMessage(), null));
//         }
//     }
// }
