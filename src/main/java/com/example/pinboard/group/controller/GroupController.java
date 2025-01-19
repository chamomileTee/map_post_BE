package com.example.pinboard.group.controller;

import com.example.pinboard.account.domain.dto.AccountDto;
import com.example.pinboard.account.service.AccountService;
import com.example.pinboard.common.exception.GlobalException;
import com.example.pinboard.group.domain.dto.*;
import com.example.pinboard.security.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import com.example.pinboard.group.domain.dto.GroupNameDto;
import com.example.pinboard.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping("/names")
    public ResponseEntity<Messenger> getGroupNames(HttpServletRequest request) {
        String userEmail = (String) request.getAttribute("userEmail");

        try {
            AccountDto accountDto = accountService.findByEmail(userEmail);
            List<GroupNameDto> groupNames = groupService.getGroupNames(accountDto);

            return ResponseEntity.ok(Messenger.builder()
                    .message("Get Group Names: Ok")
                    .data(groupNames)
                    .build());
        } catch (GlobalException e) {
            log.error("Failed to retrieve group names for user {}: {}", userEmail, e.getMessage());
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message("Get Group Names: Failed")
                            .build());
        }
    }

    @GetMapping
    public ResponseEntity<Messenger> getGroupList(HttpServletRequest request) {
        String userEmail = (String) request.getAttribute("userEmail");

        try {
            List<GroupListDto> groupList = groupService.getGroupList(userEmail);

            return ResponseEntity.ok(Messenger.builder()
                    .message("Get Group List: Ok")
                    .data(groupList)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Messenger.builder()
                    .message("Get Group List: Failed")
                    .data(null)
                    .build());
        }
    }

    @PatchMapping("/{groupId}")
    public ResponseEntity<Messenger> updateGroup(
            @PathVariable Long groupId,
            @RequestBody GroupModifyDto groupModifyDto,
            HttpServletRequest request) {
        String userEmail = (String) request.getAttribute("userEmail");

        try {
            groupService.updateGroup(groupId, userEmail, groupModifyDto);
            return ResponseEntity.ok(Messenger.builder()
                    .message("Group update: Ok")
                    .build());
        } catch (GlobalException e) {
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message(e.getMessage())
                            .build());
        }
    }

    @PutMapping("/{group-id}/manager")
    public ResponseEntity<String> changeGroupLeader(
            @PathVariable("group-id") Long groupId,
            @RequestBody PutGroupLeaderDto requestDto,
            @AuthenticationPrincipal String userEmail) {

        Long newLeaderUserId = requestDto.getUserId();

        try {
            String responseMessage = groupService.changeGroupLeader(groupId, requestDto, userEmail);
            return ResponseEntity.ok(responseMessage);
        } catch (GlobalException e) {
            log.error("Error changing group leader", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"Set Leader: Failed\"}");
        }
    }

}
