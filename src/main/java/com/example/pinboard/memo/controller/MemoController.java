package com.example.pinboard.memo.controller;

import com.example.pinboard.account.domain.dto.AccountDto;
import com.example.pinboard.account.domain.dto.RegisterDto;
import com.example.pinboard.account.service.AccountService;
import com.example.pinboard.common.domain.dto.Messenger;
import com.example.pinboard.common.domain.vo.ExceptionStatus;
import com.example.pinboard.common.exception.GlobalException;
import com.example.pinboard.memo.domain.dto.CreateMemoDto;
import com.example.pinboard.memo.domain.dto.LocationDto;
import com.example.pinboard.memo.service.MemoService;
import com.example.pinboard.security.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Provider;
import java.util.List;

/**
 * Memo Controller
 * <p>메모 관련 요청을 처리하는 컨트롤러</p>
 * <p>RestController 어노테이션을 통해 Rest API 요청을 Spring Web MVC 방식으로 처리한다.</p>
 * <p>Endpoint: <b>/api/memos</b></p>
 * @since 2025-01-15
 * @version 1.0
 * @author Jihyeon Park(jihyeon2525)
 */
@Slf4j(topic = "MemoController")
@RestController
@RequestMapping("/api/memos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@PreAuthorize("isAuthenticated()")
public class MemoController {
    private final MemoService memoService;
    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<Messenger> create(
            @RequestBody CreateMemoDto dto,
            HttpServletRequest request) {
        String userEmail = (String) request.getAttribute("userEmail");
        try {
            memoService.create(userEmail, dto);
            return ResponseEntity.ok(Messenger.builder()
                    .message("Create Memo: Ok")
                    .build());
        } catch (GlobalException e) {
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/locations")
    public ResponseEntity<Messenger> getLocations(HttpServletRequest request) {
        String userEmail = (String) request.getAttribute("userEmail");
        try {
            List<LocationDto> locations = memoService.getLocations(userEmail);
            return ResponseEntity.ok(Messenger.builder()
                    .message("Get Locations: Ok")
                    .data(locations)
                    .build());
        } catch (GlobalException e) {
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message(e.getMessage())
                            .build());
        }
    }
}
