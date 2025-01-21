package com.example.pinboard.group.controller;

import com.example.pinboard.account.domain.dto.AccountDto;
import com.example.pinboard.account.service.AccountService;
import com.example.pinboard.common.exception.GlobalException;
import com.example.pinboard.group.domain.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import com.example.pinboard.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.pinboard.common.domain.dto.Messenger;

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
                    .message("Create Group: Ok")
                    .build());
        } catch (GlobalException e) {
            log.error("Error creating group", e);
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message(e.getMessage())
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
            log.error("Error updating group", e);
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message(e.getMessage())
                            .build());
        }
    }

    @PutMapping("/{group-id}/manager")
    public ResponseEntity<Messenger> changeGroupLeader(
            @PathVariable("group-id") Long groupId,
            @RequestBody PutGroupLeaderDto requestDto,
            HttpServletRequest request) {
        String userEmail = (String) request.getAttribute("userEmail");

        try {
            groupService.changeGroupLeader(groupId, requestDto, userEmail);
            return ResponseEntity.ok(Messenger.builder()
                    .message("Set Leader: Ok")
                    .build());
        } catch (GlobalException e) {
            log.error("Error changing group leader", e);
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/{group-id}/members")
    public ResponseEntity<Messenger> addMembers(
            @PathVariable("group-id") Long groupId,
            @RequestBody MembersDto membersDto,
            HttpServletRequest request) {

        String userEmail = (String) request.getAttribute("userEmail");

        try {
            groupService.addMembers(groupId, membersDto, userEmail);
            return ResponseEntity.ok(Messenger.builder()
                    .message("Add Members: Ok")
                    .build());
        } catch (GlobalException e) {
            log.error("Error adding members to group", e);
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message(e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/{group-id}/members")
    public ResponseEntity<Messenger> deleteMembers(@PathVariable("group-id") Long groupId,
                                                   @RequestBody MembersDto membersDto,
                                                   @AuthenticationPrincipal String userEmail) {
        try {
            groupService.deleteMembers(groupId, membersDto, userEmail);

            return ResponseEntity.ok(Messenger.builder()
                    .message("Delete Members: Ok")
                    .build());
        } catch (GlobalException e) {
            log.error("Error deleting members", e);
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message(e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/{group-id}/leave")
    public ResponseEntity<Messenger> leaveGroup(
            @PathVariable("group-id") Long groupId,
            @AuthenticationPrincipal String userEmail) {

        try {
            groupService.leaveGroup(groupId, userEmail);
            return ResponseEntity.ok(Messenger.builder()
                    .message("Leave Group: Ok")
                    .build());
        } catch (GlobalException e) {
            log.error("Error leaving group", e);
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
                            .message(e.getMessage())
                            .build());
        }
    }

    @GetMapping
    public ResponseEntity<Messenger> getGroupList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size) {
        String userEmail = (String) request.getAttribute("userEmail");

        try {
            Page<GroupListDto> groupListPage = groupService.getGroupList(userEmail, page, size);

            return ResponseEntity.ok(Messenger.builder()
                    .message("Get Group List: Ok")
                    .data(groupListPage)
                    .build());
        } catch (Exception e) {
            log.error("Failed to retrieve group list", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Messenger.builder()
                            .message(e.getMessage())
                            .build());
        }
    }
}
