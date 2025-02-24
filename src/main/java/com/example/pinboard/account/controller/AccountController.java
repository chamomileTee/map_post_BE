package com.example.pinboard.account.controller;

import com.example.pinboard.account.domain.dto.AccountDto;
import com.example.pinboard.account.domain.dto.ModifyPasswordDto;
import com.example.pinboard.account.domain.dto.UserNameDto;
import com.example.pinboard.account.service.AccountService;
import com.example.pinboard.common.domain.dto.Messenger;
import com.example.pinboard.common.exception.GlobalException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Account Controller
 * <p>사용자 계정 관련 요청을 처리하는 컨트롤러</p>
 * <p>RestController 어노테이션을 통해 Rest API 요청을 Spring Web MVC 방식으로 처리한다.</p>
 * <p>Endpoint: <b>/api/account</b></p>
 *
 * @author Jihyeon Park(jihyeon2525)
 * @version 1.0
 * @since 2025-01-14
 */
@Slf4j(topic = "AccountController")
@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@PreAuthorize("isAuthenticated()")
public class AccountController {
    private final AccountService accountService;

    @GetMapping("")
    public ResponseEntity<Messenger> getProfile(HttpServletRequest request) {
        AccountDto accountDto = accountService.findByEmail((String) request.getAttribute("userEmail"));
        try {
            return ResponseEntity.ok(Messenger.builder()
                    .message("Get Profile: Ok")
                    .data(accountDto)
                    .build());
        } catch (GlobalException e) {
            log.error("Error fetching profile: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message("Get Profile: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/{userName}")
    public ResponseEntity<Messenger> searchName(@PathVariable String userName) {
        UserNameDto userNameDto = accountService.searchName(userName);
        try {
            return ResponseEntity.ok(Messenger.builder()
                    .message("Search Name: Ok")
                    .data(userNameDto)
                    .build());
        } catch (GlobalException e) {
            log.error("Error searching by username: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message("Search Name: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/name")
    public ResponseEntity<Messenger> modifyName(
            @RequestBody String name,
            HttpServletRequest request) {
        String userEmail = (String) request.getAttribute("userEmail");
        try {
            accountService.modifyName(userEmail, name);
            return ResponseEntity.ok(Messenger.builder()
                    .message("Modify Name: Ok")
                    .build());
        } catch (GlobalException e) {
            log.error("Error modifying username: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message("Modify Name: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/password")
    public ResponseEntity<Messenger> modifyPassword(
            @RequestBody ModifyPasswordDto dto,
            HttpServletRequest request) {
        String userEmail = (String) request.getAttribute("userEmail");
        try {
            accountService.modifyPassword(userEmail, dto);
            return ResponseEntity.ok(Messenger.builder()
                    .message("Modify Password: Ok")
                    .build());
        } catch (GlobalException e) {
            log.error("Error modifying password: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message("Modify Password: " + e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("")
    public ResponseEntity<Messenger> deleteAccount(HttpServletRequest request) {
        String userEmail = (String) request.getAttribute("userEmail");
        try {
            accountService.deleteAccount(userEmail);
            return ResponseEntity.ok(Messenger.builder()
                    .message("Delete Account: Ok")
                    .build());
        } catch (GlobalException e) {
            log.error("Error deleting account: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message("Delete Account: " + e.getMessage())
                            .build());
        }
    }
}