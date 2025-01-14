package com.example.pinboard.security.service;

import com.example.pinboard.security.domain.dto.LoginDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> login(LoginDto dto);
    ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response);
}