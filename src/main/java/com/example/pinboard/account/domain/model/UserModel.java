package com.example.pinboard.account.domain.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * UserModel
 * <p>user 테이블</p>
 *
 * @author Jihyeon Park(jihyeon2525)
 * @version 1.0
 * @since 2025-01-09
 */
@Data
@Entity //JPA 엔티티 클래스임을 나타냄. 데이터베이스 테이블과 매핑
@NoArgsConstructor(access = AccessLevel.PROTECTED) //기본 생성자: 인자 없는 생성자 생성. 무분별한 객체 생성 방지
@AllArgsConstructor //전체 생성자: 모든 필드를 인자로 받는 생성자를 자동 생성
@EntityListeners(AuditingEntityListener.class)
@Table(name = "pb_user")
public class UserModel {
    @Id
    @Column(name = "user_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "password", nullable = false)
    private String password;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
