package io.github.taichi0373.kumamoto_henno_map.controller.admin;

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
import org.springframework.web.multipart.MultipartFile;

import io.github.taichi0373.kumamoto_henno_map.dto.ApiResponseDto;
import io.github.taichi0373.kumamoto_henno_map.dto.admin.AdminPagedResponseDto;
import io.github.taichi0373.kumamoto_henno_map.dto.admin.CsvImportResultDto;
import io.github.taichi0373.kumamoto_henno_map.repository.entity.BenefitEligibilityEntity;
import io.github.taichi0373.kumamoto_henno_map.service.admin.AdminBenefitEligibilityService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 特典条件管理コントローラー（管理者専用）
 * <p>
 * BENEFIT_ELIGIBILITY テーブルの管理者向けCRUDエンドポイントを提供する。
 * </p>
 */
@Tag(name = "管理者")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin/benefit-eligibilities")
public class AdminBenefitEligibilityController {

    private static final Logger log = LoggerFactory.getLogger(AdminBenefitEligibilityController.class);

    /** 特典条件管理サービス */
    @Autowired
    private AdminBenefitEligibilityService adminBenefitEligibilityService;

    /**
     * 特典条件一覧を取得する
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<AdminPagedResponseDto<BenefitEligibilityEntity>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String benefitId,
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String licenseStatus,
            @RequestParam(required = false) String minAge,
            @RequestParam(required = false) String maxAge,
            @RequestParam(required = false) String municipalityCd,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String order) {
        try {
            var result = adminBenefitEligibilityService.getAll(page, size, benefitId, id, licenseStatus, minAge, maxAge, municipalityCd, keyword, sort, order);
            return ResponseEntity.ok(ApiResponseDto.success(result));
        } catch (Exception e) {
            log.error("特典条件一覧取得エラー", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("特典条件一覧の取得に失敗しました"));
        }
    }

    /**
     * 特典条件を登録する
     */
    @PostMapping
    public ResponseEntity<ApiResponseDto<BenefitEligibilityEntity>> create(@RequestBody BenefitEligibilityEntity entity) {
        try {
            var result = adminBenefitEligibilityService.create(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            log.error("特典条件登録エラー", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("特典条件の登録に失敗しました: " + e.getMessage()));
        }
    }

    /**
     * 特典条件を更新する
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<BenefitEligibilityEntity>> update(
            @PathVariable Long id,
            @RequestBody BenefitEligibilityEntity entity) {
        try {
            var result = adminBenefitEligibilityService.update(id, entity);
            return ResponseEntity.ok(ApiResponseDto.success(result));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            log.error("特典条件更新エラー: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("特典条件の更新に失敗しました: " + e.getMessage()));
        }
    }

    /**
     * CSVファイルから特典条件を一括インポートする（常にINSERT）
     */
    @PostMapping("/import")
    public ResponseEntity<ApiResponseDto<CsvImportResultDto>> importCsv(
            @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponseDto.error("ファイルが空です"));
            }
            var result = adminBenefitEligibilityService.importCsv(file);
            return ResponseEntity.ok(ApiResponseDto.success(result));
        } catch (Exception e) {
            log.error("特典条件CSVインポートエラー", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("CSVインポートに失敗しました: " + e.getMessage()));
        }
    }

    /**
     * 特典条件を削除する
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable Long id) {
        try {
            adminBenefitEligibilityService.delete(id);
            return ResponseEntity.ok(ApiResponseDto.success(null));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            log.error("特典条件削除エラー: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("特典条件の削除に失敗しました: " + e.getMessage()));
        }
    }
}
