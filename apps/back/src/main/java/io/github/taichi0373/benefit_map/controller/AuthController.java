package io.github.taichi0373.benefit_map.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.taichi0373.benefit_map.dto.ApiResponseDto;
import io.github.taichi0373.benefit_map.dto.LoginRequestDto;
import io.github.taichi0373.benefit_map.dto.LoginResponseDto;
import io.github.taichi0373.benefit_map.service.AuthService;

/**
 * 認証コントローラー
 * <p>
 * JWT認証のエンドポイントを提供する。
 * </p>
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    /** 認証サービス */
    @Autowired
    private AuthService authService;

    /**
     * ログイン
     * <p>
     * 認証成功時に JWT トークンを返す。
     * </p>
     * @param request ログインリクエスト
     * @return JWTトークンとユーザー情報
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<?>> login(@RequestBody LoginRequestDto request) {
        try {
            LoginResponseDto response = authService.login(request.getUsername(), request.getPassword());
            if (response == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseDto.error("ユーザー名またはパスワードが正しくありません"));
            }
            return ResponseEntity.ok(ApiResponseDto.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("ログイン中にエラーが発生しました"));
        }
    }
}
