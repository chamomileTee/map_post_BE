package com.example.pinboard.security.repository;

import com.example.pinboard.account.domain.model.UserModel;
import com.example.pinboard.security.domain.model.RefreshTokenModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenModel, Long> {
    Optional<RefreshTokenModel> findByToken(String token);
    Optional<RefreshTokenModel> findByUserAndIsValidTrue(UserModel user);

    @Modifying
    @Query("UPDATE RefreshTokenModel r SET r.isValid = false WHERE r.expirationTime < :now")
    void invalidateExpiredTokens(@Param("now") LocalDateTime now);
}