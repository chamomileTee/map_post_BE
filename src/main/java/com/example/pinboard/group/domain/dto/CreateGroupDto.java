package com.example.pinboard.group.domain.dto;

import lombok.*;
import java.util.List;

/**
 * GroupCreateDto
 * <p>그룹 생성 요청 DTO</p>
 *
 * @author JaeSeung Lee(fndl5759)
 * @version 1.0
 * @since 2025-01-09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateGroupDto {
    private String groupName;
    private String groupDetail;
    private List<Long> userIds;
}
