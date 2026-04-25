package com.example.back.controller;

import com.example.back.dto.LoginRequest;
import com.example.back.dto.RegisterRequest;
import com.example.back.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:6006", "http://127.0.0.1:3000", "http://127.0.0.1:6006"}, allowCredentials = "true")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request, HttpSession session) {
        try {
            log.info("Login attempt for username: {}", request.getUsername());
            Map<String, Object> result = userService.authenticateUser(request.getUsername(), request.getPassword());
            
            if ((Boolean) result.get("success")) {
                Map<String, Object> user = (Map<String, Object>) result.get("user");
                session.setAttribute("user_id", user.get("id"));
                session.setAttribute("username", user.get("username"));
                log.info("User {} logged in successfully", user.get("username"));
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Login error", e);
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        try {
            session.invalidate();
            log.info("User logged out");
            return ResponseEntity.ok(Map.of("success", true, "message", "ログアウトしました"));
        } catch (Exception e) {
            log.error("Logout error", e);
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        try {
            log.info("Registration attempt for username: {}", request.getUsername());
            Map<String, Object> result = userService.registerUser(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Registration error", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/session")
    public ResponseEntity<Map<String, Object>> getSession(HttpSession session) {
        try {
            Object userId = session.getAttribute("user_id");
            Object username = session.getAttribute("username");
            
            if (userId != null) {
                return ResponseEntity.ok(Map.of(
                    "authenticated", true,
                    "user_id", userId,
                    "username", username
                ));
            } else {
                return ResponseEntity.ok(Map.of("authenticated", false));
            }
        } catch (Exception e) {
            log.error("Session check error", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "OK",
            "message", "Backend server is running",
            "timestamp", System.currentTimeMillis()
        ));
    }
}
