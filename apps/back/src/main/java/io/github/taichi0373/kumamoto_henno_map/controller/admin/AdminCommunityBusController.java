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
import io.github.taichi0373.kumamoto_henno_map.repository.entity.CommunityBusEntity;
import io.github.taichi0373.kumamoto_henno_map.service.admin.AdminCommunityBusService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * コミュニティバス路線管理コントローラー（管理者専用）
 * <p>
 * COMMUNITY_BUS テーブルの管理者向けCRUDエンドポイントを提供する。
 * </p>
 */
@Tag(name = "管理者")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin/community-buses")
public class AdminCommunityBusController {

    private static final Logger log = LoggerFactory.getLogger(AdminCommunityBusController.class);

    /** コミュニティバス路線管理サービス */
    @Autowired
    private AdminCommunityBusService adminCommunityBusService;

    /**
     * コミュニティバス路線一覧を取得する
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<AdminPagedResponseDto<CommunityBusEntity>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String routeName,
            @RequestParam(required = false) String routeId,
            @RequestParam(required = false) String communityBusId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String order) {
        try {
            var result = adminCommunityBusService.getAll(page, size, routeName, routeId, communityBusId, keyword, sort, order);
            return ResponseEntity.ok(ApiResponseDto.success(result));
        } catch (Exception e) {
            log.error("コミュニティバス路線一覧取得エラー", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("コミュニティバス路線一覧の取得に失敗しました"));
        }
    }

    /**
     * コミュニティバス路線を登録する
     */
    @PostMapping
    public ResponseEntity<ApiResponseDto<CommunityBusEntity>> create(@RequestBody CommunityBusEntity entity) {
        try {
            var result = adminCommunityBusService.create(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            log.error("コミュニティバス路線登録エラー", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("コミュニティバス路線の登録に失敗しました: " + e.getMessage()));
        }
    }

    /**
     * コミュニティバス路線を更新する
     */
    @PutMapping("/{routeId}")
    public ResponseEntity<ApiResponseDto<CommunityBusEntity>> update(
            @PathVariable String routeId,
            @RequestBody CommunityBusEntity entity) {
        try {
            var result = adminCommunityBusService.update(routeId, entity);
            return ResponseEntity.ok(ApiResponseDto.success(result));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            log.error("コミュニティバス路線更新エラー: {}", routeId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("コミュニティバス路線の更新に失敗しました: " + e.getMessage()));
        }
    }

    /**
     * CSVファイルからコミュニティバス路線を一括インポートする
     */
    @PostMapping("/import")
    public ResponseEntity<ApiResponseDto<CsvImportResultDto>> importCsv(
            @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponseDto.error("ファイルが空です"));
            }
            var result = adminCommunityBusService.importCsv(file);
            return ResponseEntity.ok(ApiResponseDto.success(result));
        } catch (Exception e) {
            log.error("コミュニティバスCSVインポートエラー", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("CSVインポートに失敗しました: " + e.getMessage()));
        }
    }

    /**
     * コミュニティバス路線を削除する
     */
    @DeleteMapping("/{routeId}")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable String routeId) {
        try {
            adminCommunityBusService.delete(routeId);
            return ResponseEntity.ok(ApiResponseDto.success(null));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            log.error("コミュニティバス路線削除エラー: {}", routeId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("コミュニティバス路線の削除に失敗しました: " + e.getMessage()));
        }
    }
}
