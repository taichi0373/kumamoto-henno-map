package io.github.taichi0373.benefit_map.controller.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.taichi0373.benefit_map.dto.ApiResponseDto;
import io.github.taichi0373.benefit_map.dto.admin.AdminPagedResponseDto;
import io.github.taichi0373.benefit_map.dto.admin.AdminUserResponseDto;
import io.github.taichi0373.benefit_map.repository.entity.UsersEntity;
import io.github.taichi0373.benefit_map.service.admin.AdminUsersService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * ユーザー管理コントローラー（管理者専用）
 * <p>
 * USERS テーブルの管理者向け参照・更新・削除エンドポイントを提供する。
 * POST（新規作成）は提供しない（ユーザーはセルフ登録のみ）。
 * パスワードハッシュはレスポンスに含めない。
 * </p>
 */
@Tag(name = "管理者")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin/users")
public class AdminUsersController {

    private static final Logger log = LoggerFactory.getLogger(AdminUsersController.class);

    /** ユーザー管理サービス */
    @Autowired
    private AdminUsersService adminUsersService;

    /**
     * ユーザー一覧を取得する
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<AdminPagedResponseDto<AdminUserResponseDto>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String birthDate,
            @RequestParam(required = false) String municipalityCd,
            @RequestParam(required = false) String licenseStatus,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String order) {
        try {
            var result = adminUsersService.getAll(page, size, username, email, userId, birthDate, municipalityCd, licenseStatus, keyword, sort, order);
            return ResponseEntity.ok(ApiResponseDto.success(result));
        } catch (Exception e) {
            log.error("ユーザー一覧取得エラー", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("ユーザー一覧の取得に失敗しました"));
        }
    }

    /**
     * ユーザーを取得する
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponseDto<AdminUserResponseDto>> getById(@PathVariable Long userId) {
        try {
            var result = adminUsersService.getById(userId);
            return ResponseEntity.ok(ApiResponseDto.success(result));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            log.error("ユーザー取得エラー: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("ユーザーの取得に失敗しました"));
        }
    }

    /**
     * ユーザー情報を更新する
     * <p>
     * パスワードハッシュ・IS_ADMIN は更新対象外。
     * </p>
     */
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponseDto<AdminUserResponseDto>> update(
            @PathVariable Long userId,
            @RequestBody UsersEntity entity) {
        try {
            var result = adminUsersService.update(userId, entity);
            return ResponseEntity.ok(ApiResponseDto.success(result));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            log.error("ユーザー更新エラー: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("ユーザーの更新に失敗しました: " + e.getMessage()));
        }
    }

    /**
     * ユーザーを削除する
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable Long userId) {
        try {
            adminUsersService.delete(userId);
            return ResponseEntity.ok(ApiResponseDto.success(null));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            log.error("ユーザー削除エラー: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("ユーザーの削除に失敗しました: " + e.getMessage()));
        }
    }
}
