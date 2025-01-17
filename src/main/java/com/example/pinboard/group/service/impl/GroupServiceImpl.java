package com.example.pinboard.group.service.impl;

import com.example.pinboard.account.domain.dto.AccountDto;
import com.example.pinboard.account.domain.model.UserModel;
import com.example.pinboard.account.repository.AccountRepository;
import com.example.pinboard.common.domain.vo.ExceptionStatus;
import com.example.pinboard.common.exception.GlobalException;
import com.example.pinboard.group.domain.model.GroupModel;
import com.example.pinboard.group.domain.model.GroupMemberModel;
import com.example.pinboard.group.domain.dto.CreateGroupDto;
import com.example.pinboard.group.repository.GroupMemberRepository;
import com.example.pinboard.group.repository.GroupRepository;
import com.example.pinboard.group.service.GroupService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * GroupService
 * <p>그룹 관련 비즈니스 로직을 처리하는 서비스</p>
 *
 * @author JaeSeung Lee
 * @version 1.0
 * @see GroupService
 * @see GroupRepository
 * @since 2025-01-15
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j(topic = "GroupServiceImpl")
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final AccountRepository accountRepository;

    @Override
    public void create(AccountDto accountDto, CreateGroupDto createGroupDto) {
        UserModel user = accountRepository.findById(accountDto.getUserId())
                .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND));

        GroupModel newGroup = GroupModel.builder()
                .build();
        groupRepository.save(newGroup);

        GroupMemberModel leaderMember = GroupMemberModel.builder()
                .user(user)
                .group(newGroup)
                .isLeader(true)
                .groupName(createGroupDto.getGroupName())
                .groupDetail(createGroupDto.getGroupDetail())
                .build();
        groupMemberRepository.save(leaderMember);

        if (createGroupDto.getUserIds() != null && !createGroupDto.getUserIds().isEmpty()) {
            List<GroupMemberModel> additionalMembers = createGroupDto.getUserIds().stream()
                    .map(memberId -> {
                        UserModel member = accountRepository.findById(memberId)
                                .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND));
                        return GroupMemberModel.builder()
                                .user(member)
                                .group(newGroup)
                                .isLeader(false)
                                .groupName(createGroupDto.getGroupName())
                                .groupDetail(createGroupDto.getGroupDetail())
                                .build();
                    })
                    .collect(Collectors.toList());
            groupMemberRepository.saveAll(additionalMembers);
        }
    }
}
