package com.example.pinboard.security.service.impl;

import com.example.pinboard.account.domain.model.UserModel;
import com.example.pinboard.memo.repository.MemoRepository;
import com.example.pinboard.memo.service.MemoService;
import com.example.pinboard.security.domain.model.RefreshTokenModel;
import com.example.pinboard.security.provider.JwtTokenProvider;
import com.example.pinboard.security.repository.RefreshTokenRepository;
import com.example.pinboard.security.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Refresh Token Service Implementation
 * <p>Refresh Token Service Implementation</p>
 *
 * @author Jihyeon Park(jihyeon2525)
 * @version 1.0
 * @see RefreshTokenService
 * @see RefreshTokenRepository
 * @since 2025-01-14
 */
@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public RefreshTokenModel createRefreshToken(UserModel user, String token) {
        refreshTokenRepository.findByUserAndIsValidTrue(user)
                .ifPresent(oldToken -> oldToken.setIsValid(false));

        RefreshTokenModel refreshToken = RefreshTokenModel.builder()
                .user(user)
                .token(token)
                .isValid(true)
                .expirationTime(LocalDateTime.now().plusSeconds(jwtTokenProvider.getRefreshTokenExpirationInSeconds()))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void invalidateRefreshToken(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshToken -> {
                    refreshToken.setIsValid(false);
                    refreshTokenRepository.save(refreshToken);
                });
    }

    @Override

    public void invalidateExpiredTokens() {
        refreshTokenRepository.invalidateExpiredTokens(LocalDateTime.now());
    }

    @Scheduled(cron = "0 0 2 * * ?") // 매일 새벽 2시
    public void scheduleTokenCleanup() {
        invalidateExpiredTokens();
    }

    @Override
    public boolean validateRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(RefreshTokenModel::getIsValid)
                .orElse(false);
    }
}