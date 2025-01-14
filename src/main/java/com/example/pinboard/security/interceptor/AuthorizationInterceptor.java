package com.example.pinboard.security.interceptor;

import com.example.pinboard.account.domain.dto.AccountDto;
import com.example.pinboard.account.service.AccountService;
import com.example.pinboard.security.provider.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j(topic = "AuthorizationInterceptor")
@Component
@RequiredArgsConstructor
public class AuthorizationInterceptor implements HandlerInterceptor {
    private final JwtTokenProvider jwtTokenProvider;
    private final AccountService accountService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String accessToken = jwtTokenProvider.resolveToken(request);

        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof String) {
                String email = (String) authentication.getPrincipal();
                AccountDto accountDto = accountService.findByEmail(email);
                request.setAttribute("accountDto", accountDto);
                log.info("Set AccountDto for user: {}", email);
            }
        }

        return true;
    }
}