package com.example.pinboard.account.domain.dto;

import lombok.*;

/**
 * UserNameDto
 * <p>UserName Data Transfer Object</p>
 * @since 2025-01-17
 * @version 1.0
 * @author Jihyeon Park(jihyeon2525)
 */
@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserNameDto {
    private Long userId;
    private String userName;
}
