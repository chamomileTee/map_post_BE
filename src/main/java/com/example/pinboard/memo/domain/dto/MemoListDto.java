package com.example.pinboard.memo.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
/**
 * LocationDto
 * <p>메모 리스트 조회</p>
 * @since 2025-01-21
 * @version 1.0
 * @author JaeSeung Lee
 */
@Data
@Builder
@AllArgsConstructor
public class MemoListDto {
    private Long memoId;
    private String title;
    private String content;
    private String author;
    private String createdAt;
    private Boolean isHidden;
}
