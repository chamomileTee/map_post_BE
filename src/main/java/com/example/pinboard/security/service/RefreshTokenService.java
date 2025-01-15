package com.example.pinboard.security.service;

import com.example.pinboard.account.domain.model.UserModel;
import com.example.pinboard.security.domain.model.RefreshTokenModel;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshTokenModel createRefreshToken(UserModel user, String token);
    void invalidateRefreshToken(String token);
    void invalidateExpiredTokens();
    boolean validateRefreshToken(String token);
}