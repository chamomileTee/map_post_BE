package com.example.pinboard.security.domain.model;

import com.example.pinboard.account.domain.model.UserModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * RefreshTokenModel
 * <p>유저 리프레시 토큰 정보 저장</p>
 * @since 2024-01-13
 * @version 1.0
 * @author Jihyeon Park(jihyeon2525)
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "pb_refresh_token")
public class RefreshTokenModel {
    @Id
    @Column(name = "token_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "is_valid")
    private Boolean isValid = true;

    @CreatedDate
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss.SS")
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;
}
