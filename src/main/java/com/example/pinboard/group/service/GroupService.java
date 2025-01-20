package com.example.pinboard.group.service;

import com.example.pinboard.account.domain.dto.AccountDto;
import com.example.pinboard.group.domain.dto.*;

import java.util.List;

/**
 * GroupService
 * <p>그룹 관련 비즈니스 로직을 처리하는 서비스</p>
 *
 * @author JaeSeung Lee
 * @version 1.0
 * @since 2025-01-13
 */

public interface GroupService {
    void create(AccountDto accountDto, CreateGroupDto createGroupDto);
    void updateGroup(Long groupId, String userEmail, GroupModifyDto groupModifyDto);
    void changeGroupLeader(Long groupId, PutGroupLeaderDto requestDto, String userEmail);
    void addMembers(Long groupId, MembersDto membersDto, String userEmail);
    void deleteMembers(Long groupId, MembersDto membersDto, String userEmail);
    void leaveGroup(Long groupId, String userEmail);

    List<GroupNameDto> getGroupNames(AccountDto accountDto);
    List<GroupListDto> getGroupList(String userEmail);
}
