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
import io.github.taichi0373.benefit_map.repository.entity.BenefitEntity;
import io.github.taichi0373.benefit_map.service.admin.AdminBenefitService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 特典管理コントローラー（管理者専用）
 * <p>
 * BENEFIT テーブルの管理者向けCRUDエンドポイントを提供する。
 * </p>
 */
@Tag(name = "管理者")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin/benefits")
public class AdminBenefitController {

    private static final Logger log = LoggerFactory.getLogger(AdminBenefitController.class);

    /** 特典管理サービス */
    @Autowired
    private AdminBenefitService adminBenefitService;

    /**
     * 特典一覧を取得する
     *
     * @param page           ページ番号（0始まり、デフォルト0）
     * @param size           ページあたり件数（デフォルト20）
     * @param municipalityCd 自治体コードフィルター（任意）
     * @param categoryCd     カテゴリコードフィルター（任意）
     * @return ページング付き特典一覧
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<AdminPagedResponseDto<BenefitEntity>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String municipalityCd,
            @RequestParam(required = false) String categoryCd) {
        try {
            var result = adminBenefitService.getAll(page, size, municipalityCd, categoryCd);
            return ResponseEntity.ok(ApiResponseDto.success(result));
        } catch (Exception e) {
            log.error("特典一覧取得エラー", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("特典一覧の取得に失敗しました"));
        }
    }

    /**
     * 特典を登録する
     *
     * @param entity 登録する特典エンティティ
     * @return 登録した特典エンティティ
     */
    @PostMapping
    public ResponseEntity<ApiResponseDto<BenefitEntity>> create(@RequestBody BenefitEntity entity) {
        try {
            var result = adminBenefitService.create(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(result));
        } catch (Exception e) {
            log.error("特典登録エラー", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("特典の登録に失敗しました: " + e.getMessage()));
        }
    }

    /**
     * 特典を更新する
     *
     * @param benefitId 特典ID
     * @param entity    更新する特典エンティティ
     * @return 更新した特典エンティティ
     */
    @PutMapping("/{benefitId}")
    public ResponseEntity<ApiResponseDto<BenefitEntity>> update(
            @PathVariable String benefitId,
            @RequestBody BenefitEntity entity) {
        try {
            var result = adminBenefitService.update(benefitId, entity);
            return ResponseEntity.ok(ApiResponseDto.success(result));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            log.error("特典更新エラー: {}", benefitId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("特典の更新に失敗しました: " + e.getMessage()));
        }
    }

    /**
     * 特典を削除する
     *
     * @param benefitId 特典ID
     * @return 削除結果
     */
    @DeleteMapping("/{benefitId}")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable String benefitId) {
        try {
            adminBenefitService.delete(benefitId);
            return ResponseEntity.ok(ApiResponseDto.success(null));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            log.error("特典削除エラー: {}", benefitId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("特典の削除に失敗しました: " + e.getMessage()));
        }
    }
}
