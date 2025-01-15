package com.example.pinboard.memo.service.impl;

import com.example.pinboard.account.domain.dto.AccountDto;
import com.example.pinboard.account.domain.model.UserModel;
import com.example.pinboard.account.repository.AccountRepository;
import com.example.pinboard.common.domain.vo.ExceptionStatus;
import com.example.pinboard.common.exception.GlobalException;
import com.example.pinboard.group.domain.model.GroupModel;
import com.example.pinboard.group.domain.model.QGroupMemberModel;
import com.example.pinboard.memo.domain.dto.CreateMemoDto;
import com.example.pinboard.memo.domain.dto.LocationDto;
import com.example.pinboard.memo.domain.model.MemoModel;
import com.example.pinboard.memo.domain.model.QMemoModel;
import com.example.pinboard.memo.domain.model.QMemoVisibilityModel;
import com.example.pinboard.memo.service.MemoService;
import com.example.pinboard.memo.repository.MemoRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Memo Service Implementation
 * <p>Memo Service Implementation</p>
 *
 * @author Jihyeon Park(jihyeon2525)
 * @version 1.0
 * @see MemoService
 * @see MemoRepository
 * @since 2025-01-15
 */
@Service
@RequiredArgsConstructor
@Slf4j(topic = "MemoServiceImpl")
public class MemoServiceImpl implements MemoService {
    private final MemoRepository memoRepository;
    private final AccountRepository accountRepository;
    //private final GroupRepository groupRepository;
    //private final GroupMemberRepository groupMemberRepository;

    private final JPAQueryFactory queryFactory;
    private final QMemoModel qMemo = QMemoModel.memoModel;
    private final QGroupMemberModel qGroupMemberModel = QGroupMemberModel.groupMemberModel;
    private final QMemoVisibilityModel qMemoVisibilityModel = QMemoVisibilityModel.memoVisibilityModel;

    @Override
    public void create(AccountDto accountDto, CreateMemoDto createMemoDto) {
        UserModel user = accountRepository.findById(accountDto.getUserId())
                .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND));

        GroupModel group = null;
        /**if (createMemoDto.getGroupId() != null) {
            group = groupRepository.findById(createMemoDto.getGroupId())
                    .orElse(null);
            if (!groupMemberRepository.existsByUserAndGroup(user, group)) {
                throw new GlobalException(ExceptionStatus.NO_PERMISSION);
            }
        }**/

        MemoModel newMemo = MemoModel.builder()
                .user(user)
                .group(group)
                .memoTitle(createMemoDto.getTitle())
                .memoContent(createMemoDto.getContent())
                .latitude(createMemoDto.getLat())
                .longitude(createMemoDto.getLng())
                .build();

        memoRepository.save(newMemo);
    }

    @Override
    public List<LocationDto> getLocations(AccountDto accountDto) {
        Long userId = accountDto.getUserId();

        List<MemoModel> memos = queryFactory
                .selectFrom(qMemo)
                .leftJoin(qGroupMemberModel).on(qMemo.group.eq(qGroupMemberModel.group))
                .leftJoin(qMemoVisibilityModel).on(qMemo.memoId.eq(qMemoVisibilityModel.memo.memoId))
                .where(qGroupMemberModel.user.userId.eq(userId)
                        .and(qMemoVisibilityModel.user.userId.eq(userId))
                        .and(qMemoVisibilityModel.isHidden.eq(false)))
                .orderBy(qMemo.createdAt.desc())
                .fetch();

        return memos.stream()
                .map(this::entityToDtoLocation)
                .collect(Collectors.toList());
    }
}
