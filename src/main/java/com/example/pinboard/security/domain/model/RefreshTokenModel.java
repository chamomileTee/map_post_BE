package com.example.pinboard.security.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import lombok.*;

/**
 * RefreshTokenModel
 * <p>유저 리프레시 토큰 정보 redis 저장</p>
 * @since 2024-01-13
 * @version 2.0
 * @author Jihyeon Park(jihyeon2525)
 */
@RedisHash(value = "refreshToken", timeToLive = 1209600)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefreshTokenModel {
    @Id
    private String id;

    private String userEmail;

    @Indexed
    private String token;

    @Builder.Default
    private Boolean isValid = true;
}