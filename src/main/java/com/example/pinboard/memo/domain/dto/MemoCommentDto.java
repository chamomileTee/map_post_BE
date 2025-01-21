package com.example.pinboard.memo.domain.dto;

import lombok.*;

/**
 * LocationDto
 * <p>메모 댓글</p>
 * @since 2025-01-21
 * @version 1.0
 * @author JaeSeung Lee
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemoCommentDto {
    private Long commentId;
    private String content;
    private String userName;
    private String createdAt;
}
