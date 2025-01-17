package com.example.pinboard.user.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserExistsDto
 * <p>유저 검색 결과를 위한 DTO</p>
 *
 * @author JaeSeung Lee(fndl5759)
 * @version 1.0
 * @since 2025-01-09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserExistsDto {
    private Long userId;
    private String userName;
}
