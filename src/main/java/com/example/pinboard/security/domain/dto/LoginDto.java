package com.example.pinboard.security.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LoginDto
 * <p>Login Data Transfer Object</p>
 * @since 2024-01-13
 * @version 1.0
 * @author Jihyeon Park(jihyeon2525)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginDto {
    @NotNull(message = "이메일 입력은 필수입니다.")
    private String email;

    @NotNull(message = "패스워드 입력은 필수입니다.")
    private String password;
}
