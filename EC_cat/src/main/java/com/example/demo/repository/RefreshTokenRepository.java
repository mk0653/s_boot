package com.example.demo.repository;

import com.example.demo.model.RefreshToken;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * リフレッシュトークンの永続化・削除用リポジトリ。
 * ログイン時の再発行やログアウト時の削除に使う。
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.user = :user")
    void deleteByUser(User user);

    void deleteByToken(String token);
}
