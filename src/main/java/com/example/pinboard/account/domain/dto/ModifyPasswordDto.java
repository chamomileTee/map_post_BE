package com.example.pinboard.account.domain.dto;

import lombok.*;

/**
 * ModifyPasswordDto
 * <p>Modify Password Transfer Object</p>
 * @since 2025-01-19
 * @version 1.0
 * @author Jihyeon Park(jihyeon2525)
 */
@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModifyPasswordDto {
    private String Password;
    private String NewPassword;
}