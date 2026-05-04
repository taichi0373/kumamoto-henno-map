package io.github.taichi0373.benefit_map.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.taichi0373.benefit_map.dto.ApiResponseDto;
import io.github.taichi0373.benefit_map.service.MunicipalityService;
import io.github.taichi0373.benefit_map.repository.entity.MunicipalityEntity;

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
    @GetMapping("/all")
    public ResponseEntity<ApiResponseDto<?>> getAllMunicipality() {
        try {
            List<MunicipalityEntity> municipalities = municipalityService.getMunicipality();
            return ResponseEntity.ok(ApiResponseDto.success(municipalities));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("市区町村情報の取得に失敗しました"));
        }
    }
}
