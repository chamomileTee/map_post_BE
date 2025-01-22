package com.example.pinboard.security.service.impl;

import com.example.pinboard.account.domain.model.UserModel;
import com.example.pinboard.account.repository.AccountRepository;
import com.example.pinboard.common.domain.dto.Messenger;
import com.example.pinboard.common.domain.vo.ExceptionStatus;
import com.example.pinboard.common.domain.vo.SuccessStatus;
import com.example.pinboard.common.exception.GlobalException;
import com.example.pinboard.log.domain.vo.ActivityType;
import com.example.pinboard.log.service.UserActivityLogService;
import com.example.pinboard.memo.repository.MemoRepository;
import com.example.pinboard.memo.service.MemoService;
import com.example.pinboard.security.domain.dto.LoginDto;
import com.example.pinboard.security.provider.JwtTokenProvider;
import com.example.pinboard.security.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.pinboard.security.service.AuthService;

/**
 * Auth Service Implementation
 * <p>Auth Service Implementation</p>
 *
 * @author Jihyeon Park(jihyeon2525)
 * @version 1.0
 * @see AuthService
 * @since 2025-01-14
 */
@Service
@RequiredArgsConstructor
@Slf4j(topic = "AuthServiceImpl")
public class AuthServiceImpl implements AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    private final RefreshTokenService refreshTokenService;
    private final UserActivityLogService userActivityLogService;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public ResponseEntity<Messenger> login(LoginDto dto) {
        String email = dto.getEmail();
        String password = dto.getPassword();

        try {
            return accountRepository.findByEmail(email)
                    .map(user -> {
                        if (!passwordEncoder.matches(password, user.getPassword())) {
                            throw new GlobalException(ExceptionStatus.INVALID_PASSWORD);
                        }

                        String accessToken = jwtTokenProvider.generateAccessToken(email);
                        String refreshToken = jwtTokenProvider.generateRefreshToken(email);

                        log.info("AccessToken for user {}: {}", email, accessToken);
                        log.info("RefreshToken for user {}: {}", email, refreshToken);

                        refreshTokenService.createRefreshToken(user, refreshToken); //DB에 refreshToken 저장

                        ResponseCookie refreshTokenCookie = jwtTokenProvider.generateRefreshTokenCookie(refreshToken);

                        userActivityLogService.logUserActivity(user, ActivityType.LOGIN);

                        return ResponseEntity.ok()
                                .header("Authorization", "Bearer " + accessToken)
                                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(Messenger.builder()
                                        .message("Login: Ok")
                                        .build());
                    })
                    .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND));
        } catch (GlobalException e) {
            log.error("Error logging in: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message("Login: " + e.getMessage())
                            .build());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> logout(String userEmail, HttpServletRequest request) {
        try {
            UserModel user = accountRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND));

            //DB에서 RefreshToken 삭제
            String refreshToken = jwtTokenProvider.extractRefreshTokenFromCookie(request);
            if (refreshToken != null) {
                refreshTokenService.invalidateRefreshToken(refreshToken);
            }
            //log.info("클라이언트로부터 받은 Refresh Token: "+refreshToken);

            //RefreshToken 쿠키 삭제
            ResponseCookie deletedCookie = ResponseCookie.from("refresh_token", "")
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(0)
                    .build();

            userActivityLogService.logUserActivity(user, ActivityType.LOGOUT);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, deletedCookie.toString())
                    .body(Messenger.builder()
                            .message("Logout: Ok")
                            .build());
        } catch (GlobalException e) {
            log.error("Error logging out: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message("Logout: " +  e.getMessage())
                            .build());
        }
    }
}
