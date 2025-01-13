package com.example.pinboard.group.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * GroupListDto
 * <p>그룹 리스트 조회 DTO</p>
 *
 * @author JaeSeung Lee(fndl5759)
 * @version 1.0
 * @since 2025-01-09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupListDto {
    private Long groupId;
    private String groupName;
    private String groupDetail;
    private List<GroupMembersDto> groupMembers;
}
