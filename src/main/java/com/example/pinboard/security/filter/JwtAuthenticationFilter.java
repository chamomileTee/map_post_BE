package com.example.pinboard.security.filter;

import com.example.pinboard.account.domain.dto.AccountDto;
import com.example.pinboard.common.domain.vo.ExceptionStatus;
import com.example.pinboard.common.exception.GlobalException;
import com.example.pinboard.security.provider.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/**
 * JwtAuthenticationFilter
 * <p>Checks user authentication with access and refresh tokens.</p>
 * <p>If the access token is expired but refresh token is valid, prompts the client to refresh the token.</p>
 * @see JwtTokenProvider
 * @author Jihyeon Park(jihyeon2525)
 * @version 1.0
 * @since 2025-01-13
 */
@Slf4j(topic = "JwtAuthenticationFilter")
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final List<String> AUTH_BLACKLIST = Arrays.asList(
            "/api/memos/**", "/api/groups/**", "/api/account/**"
    );

    private static final List<String> AUTH_WHITELIST = Arrays.asList(
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-ui/index.html",
            "/v3/api-docs/**",
            "/api/auth/**",
            "/api/register/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestUrl = request.getRequestURI();

        if (AUTH_WHITELIST.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestUrl))) {
            log.debug("Request to whitelisted URL: {}", requestUrl);
            filterChain.doFilter(request, response);
            return;
        }

        if (AUTH_BLACKLIST.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestUrl))) {
            log.debug("Request to blacklisted URL: {}", requestUrl);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Access to this resource is forbidden");
            return;
        }

        try {
            String accessToken = jwtTokenProvider.resolveToken(request);
            String refreshToken = jwtTokenProvider.extractRefreshTokenFromCookie(request);

            log.debug("Attempting to authenticate request. Access Token: {}, Refresh Token: {}",
                    accessToken != null ? "Present" : "Absent",
                    refreshToken != null ? "Present" : "Absent");

            if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
                log.warn("Refresh token is expired or invalid for request: {}", requestUrl);
                throw new GlobalException(ExceptionStatus.EXPIRED_TOKEN, "Refresh token is expired or invalid. Please log in again.");
            }

            if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
                Authentication authentication = getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("Successfully authenticated user for request: {}", requestUrl);
            } else {
                log.info("Access token is expired or invalid. Attempting to renew using refresh token for request: {}", requestUrl);
                String newAccessToken = renewAccessToken(refreshToken);
                response.setHeader("Authorization", "Bearer " + newAccessToken);
                SecurityContextHolder.getContext().setAuthentication(getAuthentication(newAccessToken));
                log.info("Successfully renewed access token for request: {}", requestUrl);
            }

            filterChain.doFilter(request, response);
        } catch (GlobalException e) {
            log.error("Authentication error for request {}: {}", requestUrl, e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage());
        }
    }

    private Authentication getAuthentication(String token) {
        String userEmail = jwtTokenProvider.getUserEmailFromToken(token);
        return new UsernamePasswordAuthenticationToken(userEmail, "", new ArrayList<>());
    }


    private String renewAccessToken(String refreshToken) throws GlobalException {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            log.warn("Attempt to renew access token with invalid refresh token");
            throw new GlobalException(ExceptionStatus.EXPIRED_TOKEN, "Expired refresh token");
        }

        String userEmail = jwtTokenProvider.getUserEmailFromToken(refreshToken);
        String newAccessToken = jwtTokenProvider.generateAccessToken(userEmail);

        log.info("Access token renewed for user: {}", userEmail);
        return newAccessToken;
    }
}