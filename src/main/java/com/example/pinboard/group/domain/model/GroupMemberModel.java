package com.example.pinboard.group.domain.model;

import com.example.pinboard.account.domain.model.UserModel;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * GroupMemberModel
 * <p>group_members 테이블</p>
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
@Table(name = "pb_group_member")
public class GroupMemberModel {
    @Id
    @Column(name = "group_member_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupModel group;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "group_detail")
    private String groupDetail;

    @Column(name = "is_leader")
    @Builder.Default
    private Boolean isLeader = false;

    @CreatedDate
    @Column(name = "joined_at", updatable = false)
    private LocalDateTime joinedAt;
}
