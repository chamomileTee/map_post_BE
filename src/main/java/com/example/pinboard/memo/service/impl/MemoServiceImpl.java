package com.example.pinboard.memo.service.impl;

import com.example.pinboard.account.domain.dto.AccountDto;
import com.example.pinboard.account.domain.model.UserModel;
import com.example.pinboard.account.repository.AccountRepository;
import com.example.pinboard.common.domain.vo.ExceptionStatus;
import com.example.pinboard.common.exception.GlobalException;
import com.example.pinboard.group.domain.model.GroupModel;
import com.example.pinboard.group.domain.model.QGroupMemberModel;
import com.example.pinboard.group.repository.GroupMemberRepository;
import com.example.pinboard.group.repository.GroupRepository;
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
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    private final JPAQueryFactory queryFactory;
    private final QMemoModel qMemo = QMemoModel.memoModel;
    private final QGroupMemberModel qGroupMember = QGroupMemberModel.groupMemberModel;
    private final QMemoVisibilityModel qMemoVisibility = QMemoVisibilityModel.memoVisibilityModel;

    @Override
    public void create(String email, CreateMemoDto createMemoDto) {
        UserModel user = accountRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND));

        GroupModel group = null;
        if (createMemoDto.getGroupId() != null) {
            group = groupRepository.findById(createMemoDto.getGroupId())
                    .orElse(null);
            if (!groupMemberRepository.existsByUserAndGroup(user, group)) {
                throw new GlobalException(ExceptionStatus.NO_PERMISSION);
            }
        }

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
    public List<LocationDto> getLocations(String email) {
        UserModel user = accountRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND));

        List<MemoModel> memos = queryFactory
                .selectFrom(qMemo)
                .leftJoin(qGroupMember).on(qMemo.group.eq(qGroupMember.group))
                .leftJoin(qMemoVisibility).on(qMemo.memoId.eq(qMemoVisibility.memo.memoId))
                .where(qGroupMember.user.eq(user)
                        .and(qMemoVisibility.user.eq(user))
                        .and(qMemoVisibility.isHidden.eq(false)))
                .orderBy(qMemo.createdAt.desc())
                .fetch();

        return memos.stream()
                .map(memo -> LocationDto.builder()
                        .memoId(memo.getMemoId())
                        .latitude(memo.getLatitude())
                        .longitude(memo.getLongitude())
                        .build())
                .collect(Collectors.toList());
    }
}
