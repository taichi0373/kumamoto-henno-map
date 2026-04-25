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
import io.github.taichi0373.benefit_map.repository.entity.BenefitCategoryEntity;
import io.github.taichi0373.benefit_map.service.admin.AdminBenefitCategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 特典カテゴリ管理コントローラー（管理者専用）
 * <p>
 * BENEFIT_CATEGORY テーブルの管理者向けCRUDエンドポイントを提供する。
 * </p>
 */
@Tag(name = "管理者")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin/benefit-categories")
public class AdminBenefitCategoryController {

    private static final Logger log = LoggerFactory.getLogger(AdminBenefitCategoryController.class);

    /** 特典カテゴリ管理サービス */
    @Autowired
    private AdminBenefitCategoryService adminBenefitCategoryService;

    /**
     * 特典カテゴリ一覧を取得する（ページング）
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<AdminPagedResponseDto<BenefitCategoryEntity>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String categoryCd,
            @RequestParam(required = false) String displayOrder,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String order) {
        try {
            var result = adminBenefitCategoryService.getAll(page, size, categoryName, categoryCd, displayOrder, keyword, sort, order);
            return ResponseEntity.ok(ApiResponseDto.success(result));
        } catch (Exception e) {
            log.error("特典カテゴリ一覧取得エラー", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("特典カテゴリ一覧の取得に失敗しました"));
        }
    }

    /**
     * 特典カテゴリを登録する
     */
    @PostMapping
    public ResponseEntity<ApiResponseDto<BenefitCategoryEntity>> create(@RequestBody BenefitCategoryEntity entity) {
        try {
            var result = adminBenefitCategoryService.create(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(result));
        } catch (Exception e) {
            log.error("特典カテゴリ登録エラー", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("特典カテゴリの登録に失敗しました: " + e.getMessage()));
        }
    }

    /**
     * 特典カテゴリを更新する
     */
    @PutMapping("/{categoryCd}")
    public ResponseEntity<ApiResponseDto<BenefitCategoryEntity>> update(
            @PathVariable String categoryCd,
            @RequestBody BenefitCategoryEntity entity) {
        try {
            var result = adminBenefitCategoryService.update(categoryCd, entity);
            return ResponseEntity.ok(ApiResponseDto.success(result));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            log.error("特典カテゴリ更新エラー: {}", categoryCd, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("特典カテゴリの更新に失敗しました: " + e.getMessage()));
        }
    }

    /**
     * CSVファイルから特典カテゴリを一括インポートする
     */
    @PostMapping("/import")
    public ResponseEntity<ApiResponseDto<CsvImportResultDto>> importCsv(
            @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponseDto.error("ファイルが空です"));
            }
            var result = adminBenefitCategoryService.importCsv(file);
            return ResponseEntity.ok(ApiResponseDto.success(result));
        } catch (Exception e) {
            log.error("特典カテゴリCSVインポートエラー", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("CSVインポートに失敗しました: " + e.getMessage()));
        }
    }

    /**
     * 特典カテゴリを削除する
     */
    @DeleteMapping("/{categoryCd}")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable String categoryCd) {
        try {
            adminBenefitCategoryService.delete(categoryCd);
            return ResponseEntity.ok(ApiResponseDto.success(null));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            log.error("特典カテゴリ削除エラー: {}", categoryCd, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("特典カテゴリの削除に失敗しました: " + e.getMessage()));
        }
    }
}
