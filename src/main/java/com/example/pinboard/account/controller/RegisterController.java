package com.example.pinboard.account.controller;

import com.example.pinboard.account.domain.dto.RegisterDto;
import com.example.pinboard.account.service.AccountService;
import com.example.pinboard.common.domain.dto.Messenger;
import com.example.pinboard.common.domain.vo.ExceptionStatus;
import com.example.pinboard.common.exception.GlobalException;
import com.sun.xml.messaging.saaj.packaging.mime.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Register Controller
 * <p>사용자 계정 등록 관련 요청을 처리하는 컨트롤러</p>
 * <p>RestController 어노테이션을 통해 Rest API 요청을 Spring Web MVC 방식으로 처리한다.</p>
 * <p>Endpoint: <b>/api/register</b></p>
 *
 * @author Jihyeon Park(jihyeon2525)
 * @version 1.0
 * @since 2025-01-14
 */
@Slf4j(topic = "RegisterController")
@RestController
@RequestMapping("/api/register")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RegisterController {
    private final AccountService accountService;

    @PostMapping("")
    public ResponseEntity<Messenger> register(@RequestBody RegisterDto dto) {
        try {
            accountService.register(dto);
            return ResponseEntity.ok(Messenger.builder()
                    .message("Register Account: Ok")
                    .build());
        } catch (GlobalException e) {
            log.error("Error registering account: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message("Register Account: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error registering account: {}", e.getMessage(), e);
            return ResponseEntity.status(ExceptionStatus.INTERNAL_SERVER_ERROR.getHttpStatus())
                    .body(Messenger.builder()
                            .message("Register Account: " + e.getMessage())
                            .build());
        }
    }

}
