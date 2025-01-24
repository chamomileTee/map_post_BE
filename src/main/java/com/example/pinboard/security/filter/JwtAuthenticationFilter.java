package com.example.pinboard.security.filter;

import com.example.pinboard.common.domain.vo.ExceptionStatus;
import com.example.pinboard.common.exception.GlobalException;
import com.example.pinboard.security.provider.JwtTokenProvider;
import com.example.pinboard.security.service.RefreshTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.ErrorResponse;
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
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final List<String> AUTH_BLACKLIST = Arrays.asList(
            "/api/memos/**", "/api/groups/**", "/api/account/**", "/api/auth/logout"
    ); //인증필요

    private static final List<String> AUTH_WHITELIST = Arrays.asList(
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-ui/index.html",
            "/v3/api-docs/**",
            "/api/auth/login",
            "/api/register/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestUrl = request.getRequestURI();

        if (AUTH_BLACKLIST.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestUrl))) {
            log.info("Request to blacklisted URL: {}(Authentication required for this path)", requestUrl);
        } else if (AUTH_WHITELIST.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestUrl))) {
            log.info("Request to whitelisted URL: {}", requestUrl);
            filterChain.doFilter(request,response);
            return;
        }

        try {
            String accessToken = jwtTokenProvider.resolveToken(request);
            String refreshToken = jwtTokenProvider.extractRefreshTokenFromCookie(request);

            if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
                Authentication authentication = getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String userEmail = jwtTokenProvider.getUserEmailFromToken(accessToken);
                request.setAttribute("userEmail", userEmail);
            } else if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken) && refreshTokenService.validateRefreshToken(refreshToken)) {
                log.info("Access token is missing or invalid, but refresh token is valid. Generating new access token.");
                handleTokenRefresh(request, response, filterChain, refreshToken);
                return;
            } else {
                log.warn("Both access and refresh tokens are invalid or missing for request: {}", requestUrl);
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED, "Authentication failed. Please login.");
            }

            filterChain.doFilter(request, response);
        } catch (GlobalException e) {
            log.error("Authentication error for request {}: {}", requestUrl, e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(new SimpleErrorResponse(e.getMessage())));
        }
    }

    private Authentication getAuthentication(String token) {
        String userEmail = jwtTokenProvider.getUserEmailFromToken(token);
        return new UsernamePasswordAuthenticationToken(userEmail, "", new ArrayList<>());
    }

    private void handleTokenRefresh(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String refreshToken) throws IOException, ServletException {
        String userEmail = jwtTokenProvider.getUserEmailFromToken(refreshToken);
        String newAccessToken = jwtTokenProvider.generateAccessToken(userEmail);
        log.info("AccessToken for user {}: {}", userEmail, newAccessToken);

        response.setHeader("Authorization", "Bearer " + newAccessToken);
        response.setHeader("X-Token-Status", "RefreshedAccessToken");
        response.setStatus(HttpServletResponse.SC_OK);
        request.setAttribute("userEmail", userEmail);

        Authentication authentication = getAuthentication(newAccessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response); //원래 요청 다시 실행
    }
}

class SimpleErrorResponse {
    private final String error;

    public SimpleErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}