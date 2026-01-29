package com.example.demo.security;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * GoogleのJWT（IDトークン）を認証済みPrincipalに変換するクラス。
 * 以前のOAuth2 Resource Server方式で使っていた名残で、現在は未使用。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserRepository userRepository;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // JWTからユーザー情報を抽出
        String googleId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name");
        String picture = jwt.getClaimAsString("picture");

        // ユーザーをDBに保存/更新
        User user = userRepository.findByGoogleId(googleId)
            .map(existingUser -> {
                // 既存ユーザーの情報を更新
                existingUser.setLastLogin(LocalDateTime.now());
                existingUser.setName(name);
                existingUser.setPictureUrl(picture);
                if (email != null) {
                    existingUser.setEmail(email);
                }
                return userRepository.save(existingUser);
            })
            .orElseGet(() -> {
                // 新規ユーザーを作成
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

        log.debug("Authenticated user: id={}, email={}", user.getId(), user.getEmail());

        // 権限を設定（必要に応じてロールベースに拡張可能）
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_USER")
        );

        return new JwtAuthenticationToken(jwt, authorities, email);
    }
}
