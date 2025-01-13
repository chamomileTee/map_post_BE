package com.example.pinboard.group.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GroupNameDto
 * <p>그룹 이름 조회를 위한 DTO</p>
 *
 * @author JaeSeung Lee(fndl5759)
 * @version 1.0
 * @since 2025-01-09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupNameDto {
    private Long groupId;
    private String groupName;
}
