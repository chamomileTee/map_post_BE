package com.example.pinboard.group.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GroupMemberDto
 * <p>그룹 멤버 정보 DTO</p>
 *
 * @author JaeSeung Lee(fndl5759)
 * @version 1.0
 * @since 2025-01-09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMembersDto {
    private Long userId;
    private String userName;
    private Boolean isLeader;
}
