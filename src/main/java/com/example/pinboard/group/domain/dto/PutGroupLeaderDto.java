package com.example.pinboard.group.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GroupLeaderChangeDto
 * <p>그룹 방장 변경 요청을 위한 DTO</p>
 *
 * @author JaeSeung Lee(fndl5759)
 * @version 1.0
 * @since 2025-01-09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PutGroupLeaderDto {
    private Long userId;
}
