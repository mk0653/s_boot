package com.example.demo.controller;

import com.example.demo.model.RefreshToken;
import com.example.demo.model.User;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 認証系APIをまとめたコントローラ。
 * /auth/login でGoogle IDトークンを検証し、OKなら独自JWT（access/refresh）を発行する。
 * /auth/refresh でアクセストークン更新、/auth/logout でリフレッシュトークン破棄。
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final JwtDecoder jwtDecoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String googleIdToken = request.get("googleIdToken");

        if (googleIdToken == null || googleIdToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "googleIdToken is required"));
        }

        try {
            // Google IDトークンを検証
            Jwt jwt = jwtDecoder.decode(googleIdToken);

            String googleId = jwt.getSubject();
            String email = jwt.getClaimAsString("email");
            String name = jwt.getClaimAsString("name");
            String picture = jwt.getClaimAsString("picture");

            // ユーザーを取得または作成
            User user = userRepository.findByGoogleId(googleId)
                    .map(existingUser -> {
                        existingUser.setLastLogin(LocalDateTime.now());
                        existingUser.setName(name);
                        existingUser.setPictureUrl(picture);
                        if (email != null) {
                            existingUser.setEmail(email);
                        }
                        return userRepository.save(existingUser);
                    })
                    .orElseGet(() -> {
                        log.info("Creating new user: email={}, googleId={}", email, googleId);
                        User newUser = User.builder()
                                .email(email)
                                .name(name)
                                .googleId(googleId)
                                .pictureUrl(picture)
                                .lastLogin(LocalDateTime.now())
                                .build();
                        return userRepository.save(newUser);
                    });

            // 既存のリフレッシュトークンを削除
            refreshTokenRepository.deleteByUser(user);

            // 独自JWTを生成
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = createRefreshToken(user);

            log.info("User logged in: {}", user.getEmail());

            return ResponseEntity.ok(Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken,
                    "expiresIn", jwtService.getAccessTokenExpiration() / 1000,
                    "user", Map.of(
                            "id", user.getId(),
                            "email", user.getEmail(),
                            "name", user.getName(),
                            "picture", user.getPictureUrl()
                    )
            ));

        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(401).body(Map.of("error", "Invalid Google ID token"));
        }
    }

    @PostMapping("/refresh")
    @Transactional
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        String refreshTokenStr = request.get("refreshToken");

        if (refreshTokenStr == null || refreshTokenStr.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "refreshToken is required"));
        }

        return refreshTokenRepository.findByToken(refreshTokenStr)
                .map(refreshToken -> {
                    if (refreshToken.isExpired()) {
                        refreshTokenRepository.delete(refreshToken);
                        return ResponseEntity.status(401).body(Map.of("error", "Refresh token expired"));
                    }

                    User user = refreshToken.getUser();
                    String newAccessToken = jwtService.generateAccessToken(user);

                    log.info("Token refreshed for user: {}", user.getEmail());

                    return ResponseEntity.ok(Map.of(
                            "accessToken", newAccessToken,
                            "expiresIn", jwtService.getAccessTokenExpiration() / 1000
                    ));
                })
                .orElse(ResponseEntity.status(401).body(Map.of("error", "Invalid refresh token")));
    }

    @PostMapping("/logout")
    @Transactional
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
        String refreshTokenStr = request.get("refreshToken");

        if (refreshTokenStr != null && !refreshTokenStr.isEmpty()) {
            refreshTokenRepository.deleteByToken(refreshTokenStr);
        }

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    private String createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(Instant.now().plusMillis(jwtService.getRefreshTokenExpiration()))
                .build();

        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }
}
