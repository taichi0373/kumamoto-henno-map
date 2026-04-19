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
import org.springframework.web.multipart.MultipartFile;

import io.github.taichi0373.benefit_map.dto.ApiResponseDto;
import io.github.taichi0373.benefit_map.dto.admin.AdminPagedResponseDto;
import io.github.taichi0373.benefit_map.dto.admin.CsvImportResultDto;
import io.github.taichi0373.benefit_map.repository.entity.FareDiscountEntity;
import io.github.taichi0373.benefit_map.service.admin.AdminFareDiscountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 運賃割引管理コントローラー（管理者専用）
 * <p>
 * FARE_DISCOUNT テーブルの管理者向けCRUDエンドポイントを提供する。
 * 複合PK（benefitId + agencyId）を使用する。
 * </p>
 */
@Tag(name = "管理者")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin/fare-discounts")
public class AdminFareDiscountController {

    private static final Logger log = LoggerFactory.getLogger(AdminFareDiscountController.class);

    /** 運賃割引管理サービス */
    @Autowired
    private AdminFareDiscountService adminFareDiscountService;

    /**
     * 運賃割引一覧を取得する
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<AdminPagedResponseDto<FareDiscountEntity>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String benefitId,
            @RequestParam(required = false) String agencyId,
            @RequestParam(required = false) String discountType,
            @RequestParam(required = false) String discountValue,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String order) {
        try {
            var result = adminFareDiscountService.getAll(page, size, benefitId, agencyId, discountType, discountValue, sort, order);
            return ResponseEntity.ok(ApiResponseDto.success(result));
        } catch (Exception e) {
            log.error("運賃割引一覧取得エラー", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("運賃割引一覧の取得に失敗しました"));
        }
    }

    /**
     * 運賃割引を登録する
     */
    @PostMapping
    public ResponseEntity<ApiResponseDto<FareDiscountEntity>> create(@RequestBody FareDiscountEntity entity) {
        try {
            var result = adminFareDiscountService.create(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            log.error("運賃割引登録エラー", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("運賃割引の登録に失敗しました: " + e.getMessage()));
        }
    }

    /**
     * 運賃割引を更新する（複合PK）
     */
    @PutMapping("/{benefitId}/{agencyId}")
    public ResponseEntity<ApiResponseDto<FareDiscountEntity>> update(
            @PathVariable String benefitId,
            @PathVariable String agencyId,
            @RequestBody FareDiscountEntity entity) {
        try {
            var result = adminFareDiscountService.update(benefitId, agencyId, entity);
            return ResponseEntity.ok(ApiResponseDto.success(result));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            log.error("運賃割引更新エラー: {}/{}", benefitId, agencyId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("運賃割引の更新に失敗しました: " + e.getMessage()));
        }
    }

    /**
     * CSVファイルから運賃割引を一括インポートする（upsert・複合PK）
     */
    @PostMapping("/import")
    public ResponseEntity<ApiResponseDto<CsvImportResultDto>> importCsv(
            @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponseDto.error("ファイルが空です"));
            }
            var result = adminFareDiscountService.importCsv(file);
            return ResponseEntity.ok(ApiResponseDto.success(result));
        } catch (Exception e) {
            log.error("運賃割引CSVインポートエラー", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("CSVインポートに失敗しました: " + e.getMessage()));
        }
    }

    /**
     * 運賃割引を削除する（複合PK）
     */
    @DeleteMapping("/{benefitId}/{agencyId}")
    public ResponseEntity<ApiResponseDto<Void>> delete(
            @PathVariable String benefitId,
            @PathVariable String agencyId) {
        try {
            adminFareDiscountService.delete(benefitId, agencyId);
            return ResponseEntity.ok(ApiResponseDto.success(null));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            log.error("運賃割引削除エラー: {}/{}", benefitId, agencyId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("運賃割引の削除に失敗しました: " + e.getMessage()));
        }
    }
}
