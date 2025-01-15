package com.example.pinboard.memo.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CreateMemoDto
 * <p>Create memo Data Transfer Object</p>
 * @since 2025-01-15
 * @version 1.0
 * @author Jihyeon Park(jihyeon2525)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateMemoDto {
    private Long groupId;
    private String title;
    private String content;
    private Double lat;
    private Double lng;
}
