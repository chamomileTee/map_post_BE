package com.example.pinboard.memo.domain.model;

import com.example.pinboard.account.domain.model.UserModel;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

/**
 * MemoVisibilityModel
 * <p>memo_visibility 테이블</p>
 *
 * @author Jihyeon Park(jihyeon2525)
 * @version 1.0
 * @since 2025-01-09
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "pb_memo_visibility")
public class MemoVisibilityModel {
    @Id
    @Column(name = "memo_visibility_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memoVisiblityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memo_id", nullable = false)
    private MemoModel memo;

    @Column(name = "is_hidden")
    private Boolean isHidden = false;
}