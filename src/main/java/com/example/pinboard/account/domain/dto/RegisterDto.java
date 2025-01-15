package com.example.pinboard.account.domain.dto;

import lombok.*;

/**
 * RegisterDto
 * <p>Register Data Transfer Object</p>
 * @since 2025-01-14
 * @version 1.0
 * @author Jihyeon Park(jihyeon2525)
 */
@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterDto {
    private String userName;
    private String email;
    private String password;
}
