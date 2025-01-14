package com.example.pinboard.security.service.impl;

import com.example.pinboard.account.domain.model.UserModel;
import com.example.pinboard.account.repository.AccountRepository;
import com.example.pinboard.common.domain.dto.Messenger;
import com.example.pinboard.common.domain.vo.ExceptionStatus;
import com.example.pinboard.common.domain.vo.SuccessStatus;
import com.example.pinboard.common.exception.GlobalException;
import com.example.pinboard.security.domain.dto.LoginDto;
import com.example.pinboard.security.provider.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.pinboard.security.service.AuthService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AuthServiceImpl")
public class AuthServiceImpl implements AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public ResponseEntity<Messenger> login(LoginDto dto) {
        String encoder = passwordEncoder.encode(dto.getPassword());

        String email = dto.getEmail();
        String password = dto.getPassword();

        log.info("encoder: {}", encoder);
        log.info("encoder: {}", passwordEncoder.matches(password, encoder));

        Optional<UserModel> user = accountRepository.findByEmail(email);

        String accessToken = null;
        String refreshToken = null;

        if (user.isEmpty()) {
            throw new GlobalException(ExceptionStatus.USER_NOT_FOUND);
        } else if (user.isPresent()) {
            if (!passwordEncoder.matches(password, user.get().getPassword())) {
                throw new GlobalException(ExceptionStatus.INVALID_PASSWORD);
            }

            accessToken = jwtTokenProvider.generateAccessToken(email);
            refreshToken = jwtTokenProvider.generateRefreshToken(email);

        }

        try {
            ResponseCookie refreshTokenCookie = jwtTokenProvider.generateRefreshTokenCookie(refreshToken);
            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + accessToken)
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Messenger.builder()
                            .message("Login: " + SuccessStatus.OK.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Messenger.builder()
                            .message("Login: " + e.getMessage())
                            .build());
        }
    }

    @Override
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        // AccessToken은 클라이언트에서 제거)

        // RefreshToken 쿠키 삭제
        ResponseCookie deletedCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deletedCookie.toString())
                .body(Messenger.builder()
                        .message("Logout: " + SuccessStatus.OK.getMessage())
                        .build());
    }
}
