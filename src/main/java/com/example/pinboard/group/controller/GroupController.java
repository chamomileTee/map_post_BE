package com.example.pinboard.group.controller;

import com.example.pinboard.account.domain.dto.AccountDto;
import com.example.pinboard.account.service.AccountService;
import com.example.pinboard.common.exception.GlobalException;
import com.example.pinboard.group.domain.dto.CreateGroupDto;
import com.example.pinboard.group.domain.dto.GroupNameDto;
import com.example.pinboard.security.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import com.example.pinboard.group.domain.dto.GroupNameDto;
import com.example.pinboard.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.pinboard.common.domain.dto.Messenger;
import com.example.pinboard.common.domain.vo.ExceptionStatus;
import com.example.pinboard.common.domain.vo.SuccessStatus;

import java.util.List;

/**
 * GroupController
 * <p>그룹 관련 API 컨트롤러</p>
 * <p>Endpoint: <b>/api/groups</b></p>
 * @since 2025-01-13
 * @version 1.0
 * @author JaeSeung Lee
 */
@Slf4j(topic = "GroupController")
@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@PreAuthorize("isAuthenticated()")
public class GroupController {
    private final GroupService groupService;
    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<Messenger> create(
            @RequestBody CreateGroupDto dto,
            HttpServletRequest request) {
        AccountDto accountDto = accountService.findByEmail((String) request.getAttribute("userEmail"));
        try {
            groupService.create(accountDto, dto);
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
}
