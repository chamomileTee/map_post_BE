package com.example.pinboard.memo.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LocationDto
 * <p>Location Data Transfer Object</p>
 * @since 2025-01-15
 * @version 1.0
 * @author Jihyeon Park(jihyeon2525)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationDto {
    private Long memoId;
    private Double latitude;
    private Double longitude;
}
