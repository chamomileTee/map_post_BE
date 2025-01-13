package com.example.pinboard.group.service;

import com.example.pinboard.group.domain.dto.GroupNameDto;
import com.example.pinboard.group.domain.model.GroupMemberModel;
import com.example.pinboard.group.repository.GroupRepository;
import com.example.pinboard.group.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * GroupService
 * <p>그룹 관련 비즈니스 로직을 처리하는 서비스</p>
 *
 * @author JaeSeung Lee
 * @version 1.0
 * @since 2025-01-13
 */
@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public List<GroupNameDto> getGroupNames() {
        // 모든 그룹 멤버 정보를 가져오는 로직
        List<GroupMemberModel> groupMembers = groupMemberRepository.findAll();

        // 그룹 정보에서 GroupNameDto로 변환하여 반환
        return groupMembers.stream()
                .map(groupMember -> new GroupNameDto(groupMember.getGroup().getGroupId(), groupMember.getGroupName()))
                .collect(Collectors.toList());
    }
}
