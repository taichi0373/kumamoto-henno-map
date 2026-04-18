package io.github.taichi0373.benefit_map.controller.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.taichi0373.benefit_map.dto.ApiResponseDto;
import io.github.taichi0373.benefit_map.dto.admin.AdminPagedResponseDto;
import io.github.taichi0373.benefit_map.repository.entity.MunicipalityEntity;
import io.github.taichi0373.benefit_map.service.admin.AdminMunicipalityService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 自治体管理コントローラー（管理者専用）
 * <p>
 * MUNICIPALITY テーブルの管理者向けCRUDエンドポイントを提供する。
 * </p>
 */
@Tag(name = "管理者")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin/municipalities")
public class AdminMunicipalityController {

    private static final Logger log = LoggerFactory.getLogger(AdminMunicipalityController.class);

    /** 自治体管理サービス */
    @Autowired
    private AdminMunicipalityService adminMunicipalityService;

    /**
     * 自治体一覧を取得する
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<AdminPagedResponseDto<MunicipalityEntity>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String municipalityName) {
        try {
            var result = adminMunicipalityService.getAll(page, size, municipalityName);
            return ResponseEntity.ok(ApiResponseDto.success(result));
        } catch (Exception e) {
            log.error("自治体一覧取得エラー", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("自治体一覧の取得に失敗しました"));
        }
    }

    /**
     * 自治体を登録する
     */
    @PostMapping
    public ResponseEntity<ApiResponseDto<MunicipalityEntity>> create(@RequestBody MunicipalityEntity entity) {
        try {
            var result = adminMunicipalityService.create(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(result));
        } catch (Exception e) {
            log.error("自治体登録エラー", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("自治体の登録に失敗しました: " + e.getMessage()));
        }
    }

    /**
     * 自治体を更新する
     */
    @PutMapping("/{municipalityCd}")
    public ResponseEntity<ApiResponseDto<MunicipalityEntity>> update(
            @PathVariable String municipalityCd,
            @RequestBody MunicipalityEntity entity) {
        try {
            var result = adminMunicipalityService.update(municipalityCd, entity);
            return ResponseEntity.ok(ApiResponseDto.success(result));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            log.error("自治体更新エラー: {}", municipalityCd, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("自治体の更新に失敗しました: " + e.getMessage()));
        }
    }

    /**
     * 自治体を削除する
     */
    @DeleteMapping("/{municipalityCd}")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable String municipalityCd) {
        try {
            adminMunicipalityService.delete(municipalityCd);
            return ResponseEntity.ok(ApiResponseDto.success(null));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            log.error("自治体削除エラー: {}", municipalityCd, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("自治体の削除に失敗しました: " + e.getMessage()));
        }
    }
}
