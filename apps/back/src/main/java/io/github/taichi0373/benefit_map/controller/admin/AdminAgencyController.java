package io.github.taichi0373.benefit_map.controller.admin;

import java.util.List;

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
import io.github.taichi0373.benefit_map.repository.entity.AgencyEntity;
import io.github.taichi0373.benefit_map.service.admin.AdminAgencyService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 事業者管理コントローラー（管理者専用）
 * <p>
 * AGENCY テーブルの管理者向けCRUDエンドポイントを提供する。
 * </p>
 */
@Tag(name = "管理者")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin/agencies")
public class AdminAgencyController {

    private static final Logger log = LoggerFactory.getLogger(AdminAgencyController.class);

    /** 事業者管理サービス */
    @Autowired
    private AdminAgencyService adminAgencyService;

    /**
     * 事業者一覧を取得する（ページング）
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<AdminPagedResponseDto<AgencyEntity>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String agencyName) {
        try {
            var result = adminAgencyService.getAll(page, size, agencyName);
            return ResponseEntity.ok(ApiResponseDto.success(result));
        } catch (Exception e) {
            log.error("事業者一覧取得エラー", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("事業者一覧の取得に失敗しました"));
        }
    }

    /**
     * 事業者全件を取得する（セレクトボックス用）
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponseDto<List<AgencyEntity>>> getAllForSelect() {
        try {
            var result = adminAgencyService.getAllForSelect();
            return ResponseEntity.ok(ApiResponseDto.success(result));
        } catch (Exception e) {
            log.error("事業者全件取得エラー", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("事業者一覧の取得に失敗しました"));
        }
    }

    /**
     * 事業者を登録する
     */
    @PostMapping
    public ResponseEntity<ApiResponseDto<AgencyEntity>> create(@RequestBody AgencyEntity entity) {
        try {
            var result = adminAgencyService.create(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(result));
        } catch (Exception e) {
            log.error("事業者登録エラー", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("事業者の登録に失敗しました: " + e.getMessage()));
        }
    }

    /**
     * 事業者を更新する
     */
    @PutMapping("/{agencyId}")
    public ResponseEntity<ApiResponseDto<AgencyEntity>> update(
            @PathVariable String agencyId,
            @RequestBody AgencyEntity entity) {
        try {
            var result = adminAgencyService.update(agencyId, entity);
            return ResponseEntity.ok(ApiResponseDto.success(result));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            log.error("事業者更新エラー: {}", agencyId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("事業者の更新に失敗しました: " + e.getMessage()));
        }
    }

    /**
     * 事業者を削除する
     */
    @DeleteMapping("/{agencyId}")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable String agencyId) {
        try {
            adminAgencyService.delete(agencyId);
            return ResponseEntity.ok(ApiResponseDto.success(null));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            log.error("事業者削除エラー: {}", agencyId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("事業者の削除に失敗しました: " + e.getMessage()));
        }
    }
}
