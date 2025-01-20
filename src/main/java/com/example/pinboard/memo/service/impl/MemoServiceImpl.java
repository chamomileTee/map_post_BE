package com.example.pinboard.memo.service.impl;

import com.example.pinboard.account.domain.dto.AccountDto;
import com.example.pinboard.account.domain.model.UserModel;
import com.example.pinboard.account.repository.AccountRepository;
import com.example.pinboard.common.domain.vo.ExceptionStatus;
import com.example.pinboard.common.exception.GlobalException;
import com.example.pinboard.group.domain.model.GroupMemberModel;
import com.example.pinboard.group.domain.model.GroupModel;
import com.example.pinboard.group.domain.model.QGroupMemberModel;
import com.example.pinboard.group.repository.GroupMemberRepository;
import com.example.pinboard.group.repository.GroupRepository;
import com.example.pinboard.memo.domain.dto.CreateMemoDto;
import com.example.pinboard.memo.domain.dto.LocationDto;
import com.example.pinboard.memo.domain.dto.MemoDto;
import com.example.pinboard.memo.domain.model.MemoModel;
import com.example.pinboard.memo.domain.model.MemoVisibilityModel;
import com.example.pinboard.memo.domain.model.QMemoModel;
import com.example.pinboard.memo.domain.model.QMemoVisibilityModel;
import com.example.pinboard.memo.repository.MemoVisibilityRepository;
import com.example.pinboard.memo.service.MemoService;
import com.example.pinboard.memo.repository.MemoRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Memo Service Implementation
 * <p>Memo Service Implementation</p>
 *
 * @author Jihyeon Park(jihyeon2525), JaeSeung Lee(fndl5759)
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
    private final MemoVisibilityRepository memoVisibilityRepository;

    private final JPAQueryFactory queryFactory;
    private final QMemoModel qMemo = QMemoModel.memoModel;
    private final QGroupMemberModel qGroupMember = QGroupMemberModel.groupMemberModel;
    private final QMemoVisibilityModel qMemoVisibility = QMemoVisibilityModel.memoVisibilityModel;

    @Override
    public void create(String email, CreateMemoDto createMemoDto) {
        UserModel user = accountRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND));

        GroupModel group = null;
        List<GroupMemberModel> groupMembers = new ArrayList<>();
        if (createMemoDto.getGroupId() != null) {
            group = groupRepository.findById(createMemoDto.getGroupId())
                    .orElse(null);
            if (!groupMemberRepository.existsByUserAndGroup(user, group)) {
                throw new GlobalException(ExceptionStatus.NO_PERMISSION);
            }
            groupMembers = groupMemberRepository.findAllByGroup(group);
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

        if (group != null) {
            List<MemoVisibilityModel> memoVisibilities = groupMembers.stream()
                    .map(member -> MemoVisibilityModel.builder()
                            .user(member.getUser())
                            .memo(newMemo)
                            .isHidden(false)
                            .build())
                    .collect(Collectors.toList());
            memoVisibilityRepository.saveAll(memoVisibilities);
        } else {
            MemoVisibilityModel memoVisibility = MemoVisibilityModel.builder()
                    .user(user)
                    .memo(newMemo)
                    .isHidden(false)
                    .build();
            memoVisibilityRepository.save(memoVisibility);
        }
    }

    @Override
    public List<LocationDto> getLocations(String email, String option) {
        UserModel user = accountRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND));

        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(qMemoVisibility.user.eq(user))
                .and(qMemoVisibility.isHidden.eq(false));

        if (option != null) {
            if (option.equals("PRIVATE")) {
                whereClause.and(qMemo.group.isNull());
            } else {
                try {
                    Long groupId = Long.parseLong(option);
                    if (!groupMemberRepository.existsByUserAndGroupGroupId(user, groupId)) {
                        throw new GlobalException(ExceptionStatus.NO_PERMISSION); }
                    whereClause.and(qMemo.group.groupId.eq(groupId));
                } catch (NumberFormatException e) {
                    throw new GlobalException(ExceptionStatus.BAD_REQUEST);
                }
            }
        } else {
                whereClause.and(
                        qMemo.group.isNull().or(
                                qMemo.group.in(
                                        JPAExpressions.select(qGroupMember.group)
                                                .from(qGroupMember)
                                                .where(qGroupMember.user.eq(user))
                                )
                        )
                );
            }

        List<MemoModel> memos = queryFactory
                .selectFrom(qMemo)
                .innerJoin(qMemoVisibility).on(qMemo.memoId.eq(qMemoVisibility.memo.memoId))
                .where(whereClause)
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

    @Override
    public MemoDto getMemo(String email, Long memoId) {
        UserModel user = accountRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND));

        MemoModel memo = queryFactory
                .selectFrom(qMemo)
                .leftJoin(qMemoVisibility).on(qMemo.memoId.eq(qMemoVisibility.memo.memoId))
                .where(qMemo.memoId.eq(memoId)
                        .and(qMemoVisibility.user.eq(user))
                        .and(qMemoVisibility.isHidden.eq(false)))
                .fetchOne();

        if (memo == null) {
            throw new GlobalException(ExceptionStatus.NO_PERMISSION);
        }

        String author;
        if (memo.getGroup() != null) {
            GroupMemberModel groupMember = groupMemberRepository.findByUserAndGroup(memo.getUser(), memo.getGroup());
            author = groupMember.getGroupName() + " (" + memo.getUser().getUserName() + ")";
        } else {
            author = memo.getUser().getUserName();
        }

        return MemoDto.builder()
                .memoId(memo.getMemoId())
                .title(memo.getMemoTitle())
                .content(memo.getMemoContent())
                .author(author)
                .date(memo.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}
