package com.example.pinboard.group.domain.model;

import com.example.pinboard.account.domain.model.UserModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

import java.io.Serializable;
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
    @EmbeddedId
    private GroupMemberId groupMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserModel user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("groupId")
    @JoinColumn(name = "group_id")
    private GroupModel group;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "group_detail")
    private String groupDetail;

    @Column(name = "is_leader")
    private Boolean isLeader = false;

    @CreatedDate
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "joined_at", updatable = false)
    private LocalDateTime joinedAt;
}

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
class GroupMemberId implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "group_id")
    private Long groupId;
}
