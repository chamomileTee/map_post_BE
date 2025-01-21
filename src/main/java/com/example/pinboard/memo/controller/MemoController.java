package com.example.pinboard.memo.controller;

import com.example.pinboard.account.domain.dto.AccountDto;
import com.example.pinboard.account.domain.dto.RegisterDto;
import com.example.pinboard.account.service.AccountService;
import com.example.pinboard.common.domain.dto.Messenger;
import com.example.pinboard.common.domain.vo.ExceptionStatus;
import com.example.pinboard.common.exception.GlobalException;
import com.example.pinboard.memo.domain.dto.*;
import com.example.pinboard.memo.service.MemoService;
import com.example.pinboard.security.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Provider;
import java.util.List;
import java.util.Map;

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
            log.error("Error creating memo: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message("Create Memo: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/locations")
    public ResponseEntity<Messenger> getLocations(
            HttpServletRequest request,
            @RequestParam(required = false) String option) {
        String userEmail = (String) request.getAttribute("userEmail");
        try {
            List<LocationDto> locations = memoService.getLocations(userEmail, option);
            return ResponseEntity.ok(Messenger.builder()
                    .message("Get Locations: Ok")
                    .data(locations)
                    .build());
        } catch (GlobalException e) {
            log.error("Error fetching locations: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message("Get Locations: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/{memoId}")
    public ResponseEntity<Messenger> getMemo(
            HttpServletRequest request,
            @PathVariable("memoId") Long memoId) {
        String userEmail = (String) request.getAttribute("userEmail");
        try {
            MemoDto memo = memoService.getMemo(userEmail, memoId);
            return ResponseEntity.ok(Messenger.builder()
                    .message("Get Memo: Ok")
                    .data(memo)
                    .build());
        } catch (GlobalException e) {
            log.error("Error fetching memo: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message("Get Memo: " + e.getMessage())
                            .build());
        }

    }

    @GetMapping("/")
    public ResponseEntity<Messenger> getMemoList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size,
            @RequestParam(required = false) String option) {
        String userEmail = (String) request.getAttribute("userEmail");

        try {
            Page<MemoListDto> memoPage = memoService.getMemoList(userEmail, page, size, option);

            return ResponseEntity.ok(Messenger.builder()
                    .message("Get Memo List: Ok")
                    .data(memoPage.getContent())
                    .build());
        } catch (GlobalException e) {
            log.error("Error fetching memo list: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message("Get Memo List: Failed")
                            .data(null)
                            .build());
        }
    }

    @GetMapping("/{memoId}/full")
    public ResponseEntity<?> getMemoFull(
            HttpServletRequest request,
            @PathVariable Long memoId) {
        String userEmail = (String) request.getAttribute("userEmail");

        try {
            MemoFullDto memoFullDto = memoService.getMemoFull(memoId, userEmail);

            return ResponseEntity.ok(Messenger.builder()
                    .message("Get Memo Full: Ok")
                    .data(List.of(memoFullDto))
                    .build());
        } catch (GlobalException e) {
            log.error("Error fetching full memo details for memoId {}: {}", memoId, e.getMessage(), e);
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message("Get Memo Full: Failed")
                            .data(null)
                            .build());
        }
    }

    @PostMapping("/view-post/{memoId}/comments")
    public ResponseEntity<Messenger> createComment(
            HttpServletRequest request,
            @PathVariable Long memoId,
            @RequestBody CreateCommentDto createCommentDto) {
        String userEmail = (String) request.getAttribute("userEmail");

        try {
            memoService.createComment(memoId, userEmail, createCommentDto);

            return ResponseEntity.ok(Messenger.builder()
                    .message("Create Comment: Ok")
                    .data(null)
                    .build());

        } catch (GlobalException e) {
            log.error("Error creating comment for memoId {}: {}", memoId, e.getMessage(), e);
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message("Create Comment: Failed")
                            .data(null)
                            .build());
        }
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Messenger> deleteComment(
            HttpServletRequest request,
            @PathVariable Long commentId) {
        String userEmail = (String) request.getAttribute("userEmail");

        try {
            memoService.deleteComment(commentId, userEmail);

            return ResponseEntity.ok(Messenger.builder()
                    .message("Delete Comment: Ok")
                    .data(null)
                    .build());

        } catch (GlobalException e) {
            log.error("Error deleting comment with commentId {}: {}", commentId, e.getMessage(), e);
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message("Delete Comment: Failed")
                            .data(null)
                            .build());
        }
    }

    @PatchMapping("/{memoId}")
    public ResponseEntity<Messenger> modifyMemo(
            HttpServletRequest request,
            @PathVariable Long memoId,
            @RequestBody MemoModifyDto memoModifyDto) {
        String userEmail = (String) request.getAttribute("userEmail");

        try {
            memoService.modifyMemo(memoId, userEmail, memoModifyDto);

            return ResponseEntity.ok(Messenger.builder()
                    .message("Modify Memo: Ok")
                    .data(null)
                    .build());

        } catch (GlobalException e) {
            log.error("Error modifying memo with memoId {}: {}", memoId, e.getMessage(), e);
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message("Modify Memo: Failed")
                            .data(null)
                            .build());
        }
    }

    @DeleteMapping("/{memoId}")
    public ResponseEntity<Messenger> deleteMemo(
            HttpServletRequest request,
            @PathVariable Long memoId) {
        String userEmail = (String) request.getAttribute("userEmail");

        try {
            memoService.deleteMemo(memoId, userEmail);

            return ResponseEntity.ok(Messenger.builder()
                    .message("Delete Memo: Ok")
                    .data(null)
                    .build());

        } catch (GlobalException e) {
            log.error("Error deleting memo with memoId {}: {}", memoId, e.getMessage(), e);
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message("Delete Memo: Failed")
                            .data(null)
                            .build());
        }
    }

    @PutMapping("/{memoId}/visibility")
    public ResponseEntity<Messenger> updateMemoVisibility(
            HttpServletRequest request,
            @PathVariable Long memoId,
            @RequestBody(required = false) Map<String, String> requestBody) {

        String userEmail = (String) request.getAttribute("userEmail");
        boolean isHidden = false; // 기본값은 false

        // 요청 본문에 isHidden이 있다면 해당 값을 확인하여 true/false로 설정
        if (requestBody != null && requestBody.containsKey("isHidden")) {
            String isHiddenValue = requestBody.get("isHidden");
            isHidden = "1".equals(isHiddenValue);
        }

        try {
            memoService.updateMemoVisibility(memoId, isHidden, userEmail);

            return ResponseEntity.ok(Messenger.builder()
                    .message("Modify Memo Visibility: Ok")
                    .data(null)
                    .build());

        } catch (GlobalException e) {
            log.error("Error updating memo visibility for memoId {}: {}", memoId, e.getMessage(), e);
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message("Modify Memo Visibility: Failed")
                            .data(null)
                            .build());
        }
    }
}

