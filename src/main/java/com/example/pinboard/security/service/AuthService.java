package com.example.pinboard.security.service;

import com.example.pinboard.security.domain.dto.LoginDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> login(LoginDto dto);
}