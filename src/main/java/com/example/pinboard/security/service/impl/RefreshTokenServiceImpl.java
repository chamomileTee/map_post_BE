package com.example.pinboard.security.service.impl;

import com.example.pinboard.security.domain.model.RefreshTokenModel;
import com.example.pinboard.security.repository.RefreshTokenRepository;
import com.example.pinboard.security.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


/**
 * Refresh Token Service Implementation
 * <p>Refresh Token Service Implementation</p>
 *
 * @author Jihyeon Park(jihyeon2525)
 * @version 2.0
 * @see RefreshTokenService
 * @see RefreshTokenRepository
 * @since 2025-01-14
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void createRefreshToken(String userEmail, String token) {
        RefreshTokenModel refreshToken = RefreshTokenModel.builder()
                .userEmail(userEmail)
                .token(token)
                .isValid(true)
                .build();
        RefreshTokenModel savedToken = refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void invalidateRefreshToken(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshToken -> {
                    refreshToken.setIsValid(false);
                    refreshTokenRepository.save(refreshToken);

                    redisTemplate.opsForSet().remove("refreshToken:userEmail:" + refreshToken.getUserEmail(), refreshToken.getId());
                });
    }

    @Override
    public boolean validateRefreshToken(String token) {
        log.info("Attempting to validate refresh token: {}", token);

        return refreshTokenRepository.findByToken(token)
                .map(refreshToken -> {
                    return refreshToken.getIsValid();
                })
                .orElseGet(() -> {
                    return false;
                });
    }

}
