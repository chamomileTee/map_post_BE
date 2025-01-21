package com.example.pinboard.memo.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LocationDto
 * <p>메모 댓글 작성</p>
 * @since 2025-01-21
 * @version 1.0
 * @author JaeSeung Lee
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCommentDto {
    private String content;
}
