package com.example.pinboard.group.service.impl;

import com.example.pinboard.account.domain.dto.AccountDto;
import com.example.pinboard.group.domain.dto.*;
import com.example.pinboard.account.domain.model.UserModel;
import com.example.pinboard.account.repository.AccountRepository;
import com.example.pinboard.common.domain.vo.ExceptionStatus;
import com.example.pinboard.common.exception.GlobalException;
import com.example.pinboard.group.domain.model.GroupModel;
import com.example.pinboard.group.domain.model.GroupMemberModel;
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

    @Override
    public void updateGroup(Long groupId, String userEmail, GroupModifyDto groupModifyDto) {
        UserModel user = accountRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND));

        List<GroupMemberModel> groupMembers = groupMemberRepository.findByGroup_GroupId(groupId);

        GroupMemberModel groupMember = groupMembers.stream()
                .filter(member -> member.getUser().getUserId().equals(user.getUserId()))
                .findFirst()
                .orElseThrow(() -> new GlobalException(ExceptionStatus.UNAUTHORIZED));

        groupMember.setGroupName(groupModifyDto.getGroupName());
        groupMember.setGroupDetail(groupModifyDto.getDetail());

        groupMemberRepository.save(groupMember);

        log.info("Group with ID {} has been updated by user with email {}", groupId, userEmail);
    }

    @Override
    public void changeGroupLeader(Long groupId, PutGroupLeaderDto requestDto, String userEmail) {
        Long newLeaderUserId = requestDto.getUserId();

        UserModel user = accountRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND));

        GroupMemberModel currentLeader = groupMemberRepository.findByGroup_GroupIdAndUser_UserId(groupId, user.getUserId())
                .filter(GroupMemberModel::getIsLeader)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.UNAUTHORIZED));
        GroupMemberModel newLeader = groupMemberRepository.findByGroup_GroupIdAndUser_UserId(groupId, newLeaderUserId)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.DATA_NOT_FOUND));

        currentLeader.setIsLeader(false);
        newLeader.setIsLeader(true);

        groupMemberRepository.save(currentLeader);
        groupMemberRepository.save(newLeader);

        log.info("Group leader for group {} has been changed to user with ID {}", groupId, newLeaderUserId);
    }

    @Override
    public void addMembers(Long groupId, MembersDto membersDto, String userEmail) {
        UserModel user = accountRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND));

        GroupModel group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.DATA_NOT_FOUND));

        GroupMemberModel currentLeader = groupMemberRepository.findByGroup_GroupIdAndUser_UserId(groupId, user.getUserId())
                .filter(GroupMemberModel::getIsLeader)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.UNAUTHORIZED));

        List<Long> existingUserIds = groupMemberRepository.findByGroup_GroupId(groupId).stream()
                .map(groupMember -> groupMember.getUser().getUserId())
                .toList();

        List<GroupMemberModel> newMembers = membersDto.getUserIds().stream()
                .filter(userId -> !existingUserIds.contains(userId))
                .map(userId -> {
                    UserModel newUser = accountRepository.findById(userId)
                            .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND));

                    return GroupMemberModel.builder()
                            .user(newUser)
                            .group(group)
                            .isLeader(false)
                            .groupName(currentLeader.getGroupName())
                            .groupDetail(currentLeader.getGroupDetail())
                            .build();
                })
                .collect(Collectors.toList());

        if (!newMembers.isEmpty()) {
            groupMemberRepository.saveAll(newMembers);
        }
    }

    @Override
    public void deleteMembers(Long groupId, MembersDto membersDto, String userEmail) {
        UserModel user = accountRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND));
        GroupModel group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.DATA_NOT_FOUND));
        GroupMemberModel currentLeader = groupMemberRepository.findByGroup_GroupIdAndUser_UserId(groupId, user.getUserId())
                .filter(GroupMemberModel::getIsLeader)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.UNAUTHORIZED));
        List<GroupMemberModel> membersToRemove = groupMemberRepository.findByGroup_GroupId(groupId).stream()
                .filter(groupMember -> membersDto.getUserIds().contains(groupMember.getUser().getUserId()))
                .collect(Collectors.toList());
        if (membersToRemove.isEmpty()) {
            throw new GlobalException(ExceptionStatus.DATA_NOT_FOUND); // 삭제할 멤버가 없으면 에러
        }

        groupMemberRepository.deleteAll(membersToRemove);
    }

    @Override
    public void leaveGroup(Long groupId, String userEmail) {
        UserModel user = accountRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND));

        GroupModel group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.DATA_NOT_FOUND));

        GroupMemberModel currentMember = groupMemberRepository.findByGroup_GroupIdAndUser_UserId(groupId, user.getUserId())
                .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND));
        if (currentMember.getIsLeader()) {
            throw new GlobalException(ExceptionStatus.UNAUTHORIZED);
        }

        groupMemberRepository.delete(currentMember);
    }

    @Override
    public List<GroupNameDto> getGroupNames(AccountDto accountDto) {
        UserModel user = accountRepository.findById(accountDto.getUserId())
                .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND));

        List<GroupMemberModel> groupMembers = groupMemberRepository.findByUser(user);

        if (groupMembers.isEmpty()) {
            throw new GlobalException(ExceptionStatus.DATA_NOT_FOUND);
        }

        return groupMembers.stream()
                .map(member -> new GroupNameDto(
                        member.getGroup().getGroupId(),
                        member.getGroupName()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<GroupListDto> getGroupList(String userEmail) {
        UserModel user = accountRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND));

        List<GroupMemberModel> groupMembers = groupMemberRepository.findByUser(user);

        if (groupMembers.isEmpty()) {
            throw new GlobalException(ExceptionStatus.DATA_NOT_FOUND);
        }

        List<Long> groupIds = groupMembers.stream()
                .map(groupMember -> groupMember.getGroup().getGroupId())
                .toList();
        return groupIds.stream()
                .map(groupId -> {
                    List<GroupMemberModel> membersInGroup = groupMemberRepository.findByGroup_GroupId(groupId);

                    List<GroupMembersDto> members = membersInGroup.stream()
                            .map(member -> new GroupMembersDto(
                                    member.getUser().getUserId(),
                                    member.getUser().getUserName(),
                                    member.getIsLeader()
                            ))
                            .collect(Collectors.toList());
                    GroupMemberModel representative = membersInGroup.get(0);

                    return new GroupListDto(
                            representative.getGroup().getGroupId(),
                            representative.getGroupName(),
                            representative.getGroupDetail(),
                            members
                    );
                })
                .collect(Collectors.toList());
    }
}
