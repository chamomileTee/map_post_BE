package com.example.pinboard.group.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AddMembersDto
 * <p>그룹에 멤버를 추가/삭제 하기 위한 DTO</p>
 *
 * @author JaeSeung Lee(fndl5759)
 * @version 1.0
 * @since 2025-01-09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MembersDto {
    private List<Long> userIds; // 여러 명의 유저를 받을 수 있는 리스트
}
