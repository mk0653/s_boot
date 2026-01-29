package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * リフレッシュトークンをDBに保持するエンティティ。
 * ユーザーに紐づき、期限（expiryDate）切れかどうかを判定する。
 */
@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant expiryDate;

    public boolean isExpired() {
        return Instant.now().isAfter(expiryDate);
    }
}
