package com.example.pinboard.group.service;

import com.example.pinboard.account.domain.dto.AccountDto;
import com.example.pinboard.group.domain.dto.GroupNameDto;
import com.example.pinboard.group.domain.dto.CreateGroupDto;
import com.example.pinboard.group.domain.model.GroupModel;
import com.example.pinboard.memo.domain.dto.CreateMemoDto;

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
}
