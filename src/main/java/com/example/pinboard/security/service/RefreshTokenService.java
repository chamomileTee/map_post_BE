package com.example.pinboard.security.service;

public interface RefreshTokenService {
    void createRefreshToken(String userEmail, String token);
    void invalidateRefreshToken(String token);
    boolean validateRefreshToken(String token);
}