package com.example.pinboard.common.config;

import com.example.pinboard.security.interceptor.AuthorizationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebConfig
 * <p>AuthorizationInterceptor 등록을 위한 config 클래스</p>
 * @author Jihyeon Park(jihyeon2525)
 * @version 1.0
 * @see AuthorizationInterceptor
 * @since 2025-01-14
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final AuthorizationInterceptor authorizationInterceptor;

    @Autowired
    public WebConfig(AuthorizationInterceptor authorizationInterceptor) {
        this.authorizationInterceptor = authorizationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorizationInterceptor)
                .addPathPatterns("api/auth/**")
                .addPathPatterns("api/register/**")
                .addPathPatterns("api/account/**")
                .addPathPatterns("api/memos/**")
                .addPathPatterns("api/groups/**");
    }
}
