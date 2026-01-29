package com.example.demo.security;

import com.example.demo.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 独自JWTの発行・検証を担当するサービス。
 * 署名はアプリの秘密鍵（app.jwt.secret）を使い、アクセストークンとリフレッシュトークンを生成する。
 */
@Service
@Slf4j
public class JwtService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${app.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("name", user.getName());
        claims.put("roles", List.of("USER"));
        // カスタム情報を追加可能
        // claims.put("tenantId", user.getTenantId());
        // claims.put("plan", user.getPlan());

        return buildToken(claims, user.getId().toString(), accessTokenExpiration);
    }

    public String generateRefreshToken(User user) {
        return buildToken(new HashMap<>(), user.getId().toString(), refreshTokenExpiration);
    }

    private String buildToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String getSubjectFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String getEmailFromToken(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    public Date getExpirationFromToken(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token) {
        return getExpirationFromToken(token).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        // Base64エンコードされていない場合はそのまま使用
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
}
