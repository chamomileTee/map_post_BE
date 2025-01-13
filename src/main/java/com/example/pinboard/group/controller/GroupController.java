package com.example.pinboard.group.controller;

import com.example.pinboard.group.domain.dto.GroupNameDto;
import com.example.pinboard.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.pinboard.common.domain.dto.Messenger;
import com.example.pinboard.common.domain.vo.ExceptionStatus;
import com.example.pinboard.common.domain.vo.SuccessStatus;

import java.util.List;

/**
 * GroupController
 * <p>그룹 관련 API 컨트롤러</p>
 *
 * @since 2025-01-13
 * @version 1.0
 * @author JaeSeung Lee
 */
@Slf4j(topic = "GroupController")
@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class GroupController {

    private final GroupService groupService;

    /**
     * 그룹 이름 리스트 조회
     *
     * @return 그룹 이름 리스트와 메시지를 담은 ResponseEntity
     */
    @GetMapping("/names")
    public ResponseEntity<Messenger> getGroupNames() {

        try {
            // groupId를 전달하지 않고 getGroupNames() 호출
            List<GroupNameDto> groupNames = groupService.getGroupNames();

            if (groupNames.isEmpty()) {
                return ResponseEntity.ok(Messenger.builder()
                        .message("Get Group Names: Failed")
                        .data(null)
                        .count(null)
                        .state(null)
                        .build());
            }

            return ResponseEntity.ok(Messenger.builder()
                    .message("Get Group Names: " + SuccessStatus.OK.getMessage())
                    .data(groupNames)
                    .count(null)
                    .state(null)
                    .build());

        } catch (Exception e) {
            return ResponseEntity.status(ExceptionStatus.INTERNAL_SERVER_ERROR.getHttpStatus())
                    .body(Messenger.builder()
                            .message("Error: " + e.getMessage())
                            .data(null)
                            .count(null)
                            .state(null)
                            .build());
        }
    }
}
