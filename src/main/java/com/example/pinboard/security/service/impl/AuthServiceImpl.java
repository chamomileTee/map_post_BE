package com.example.pinboard.security.service.impl;

import com.example.pinboard.common.domain.dto.Messenger;
import com.example.pinboard.security.domain.dto.LoginDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.pinboard.security.service.AuthService;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AuthServiceImpl")
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ResponseEntity<Messenger> login(LoginDto dto) {
        String encoder = passwordEncoder.encode(dto.getPassword());

        String email = dto.getEmail();
        String password = dto.getPassword();

        log.info("encoder: {}", encoder);
        log.info("encoder: {}", passwordEncoder.matches(password, encoder));

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                        Messenger.builder()
                                .message("Invalid User")
                                .build());

    }
}
