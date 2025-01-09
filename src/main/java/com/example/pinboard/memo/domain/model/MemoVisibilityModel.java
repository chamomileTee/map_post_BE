package com.example.pinboard.memo.domain.model;

import com.example.pinboard.account.domain.model.UserModel;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

import java.io.Serializable;

/**
 * MemoVisibilityModel
 * <p>memo_visibility 테이블</p>
 *
 * @author Jihyeon Park(jihyeon2525)
 * @version 1.0
 * @since 2025-01-09
 */
@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "pb_memo_visibility")
public class MemoVisibilityModel {
    @EmbeddedId
    private MemoVisibilityId memoVisiblityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserModel user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memoId")
    @JoinColumn(name = "memo_id")
    private MemoModel memo;

    @Column(name = "is_hidden")
    private Boolean isHidden = false;
}

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
class MemoVisibilityId implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "memo_id")
    private Long memoId;
}
