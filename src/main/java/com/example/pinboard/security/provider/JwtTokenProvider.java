package com.example.pinboard.security.provider;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.Authentication;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Date;

/**
 * JwtTokenProvider
 * <p>Handles JWT token generation, validation, and management.</p>
 *
 * @author Jihyeon Park(jihyeon2525)
 * @version 1.0
 * @since 2024-01-13
 */
@Slf4j(topic = "JwtTokenProvider")
@Service
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.token.access.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.token.refresh.expiration}")
    private long refreshTokenExpiration;

    @Value("${jwt.token.access.prefix}")
    private String tokenPrefix;

    private SecretKey key;

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String email) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .subject(email) //로그인ID
                .claim("tokenType", "ACCESS")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(String email) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .subject(email)
                .claim("tokenType", "REFRESH")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public String getUserEmailFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public ResponseCookie generateRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true) //client에서의 쿠키 탈취 예방
                .secure(true)
                .path("/") //이 하위 path에서만 cookie 전송
                .maxAge(refreshTokenExpiration / 1000)
                .sameSite("none") //모든 요청에 서드파티 쿠키와, 퍼스트파티 쿠키가 전송
                .build();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty");
        }
        return false;
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh_token")) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    public long getRefreshTokenExpirationInSeconds() {
        return refreshTokenExpiration / 1000;
    }
}
