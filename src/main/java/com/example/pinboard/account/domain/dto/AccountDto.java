package com.example.pinboard.account.domain.dto;

import lombok.*;

/**
 * AccountDto
 * <p>Account Data Transfer Object</p>
 * @since 2025-01-09
 * @version 1.0
 * @author Jihyeon Park(jihyeon2525)
 */
@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDto {
    private Long userId;
    private String email;
    private String userName;
}