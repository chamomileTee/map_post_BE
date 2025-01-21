package com.example.pinboard.memo.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * LocationDto
 * <p>메모 전체 내용</p>
 * @since 2025-01-21
 * @version 1.0
 * @author JaeSeung Lee
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemoFullDto {
    private Long memoId;
    private String title;
    private String content;
    private String author;
    private String createdAt;
    private Boolean isHidden;
    private Boolean isAuthor;
    private List<MemoCommentDto> comments;
}
