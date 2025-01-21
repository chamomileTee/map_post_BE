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
import com.example.pinboard.memo.domain.dto.*;
import com.example.pinboard.memo.domain.model.*;
import com.example.pinboard.memo.repository.MemoCommentRepository;
import com.example.pinboard.memo.repository.MemoVisibilityRepository;
import com.example.pinboard.memo.service.MemoService;
import com.example.pinboard.memo.repository.MemoRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private final MemoCommentRepository memoCommentRepository;

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

    @Override
    public Page<MemoListDto> getMemoList(String userEmail, int page, int size, String option) {
        UserModel user = accountRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size);

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
                        throw new GlobalException(ExceptionStatus.NO_PERMISSION);
                    }
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

        Page<MemoModel> memosPage = new PageImpl<>(
                queryFactory
                        .selectFrom(qMemo)
                        .innerJoin(qMemoVisibility).on(qMemo.memoId.eq(qMemoVisibility.memo.memoId))
                        .where(whereClause)
                        .orderBy(qMemo.createdAt.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                queryFactory
                        .selectFrom(qMemo)
                        .innerJoin(qMemoVisibility).on(qMemo.memoId.eq(qMemoVisibility.memo.memoId))
                        .where(whereClause)
                        .fetchCount()
        );

        if (memosPage.isEmpty()) {
            throw new GlobalException(ExceptionStatus.DATA_NOT_FOUND);
        }

        return memosPage.map(memo -> {
            String author;
            Boolean isHidden = null;

            if (memo.getGroup() != null) {
                GroupMemberModel groupMember = groupMemberRepository.findByUserAndGroup(memo.getUser(), memo.getGroup());
                author = groupMember.getGroupName() + " (" + memo.getUser().getUserName() + ")";
            } else {
                author = memo.getUser().getUserName();
            }

            if (!memo.getMemoVisibilities().isEmpty()) {
                isHidden = memo.getMemoVisibilities().get(0).getIsHidden();  // 예시로 첫 번째 요소의 isHidden을 가져옴
            }

            return MemoListDto.builder()
                    .memoId(memo.getMemoId())
                    .title(memo.getMemoTitle())
                    .content(memo.getMemoContent())
                    .author(author)
                    .createdAt(memo.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .isHidden(isHidden)
                    .build();
        });
    }

    public MemoFullDto getMemoFull(Long memoId, String userEmail) {
        MemoModel memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.DATA_NOT_FOUND));

        UserModel user = accountRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND));

        MemoVisibilityModel memoVisibility = memoVisibilityRepository.findByMemoAndUser(memo, user)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.NO_PERMISSION));

        List<MemoCommentModel> comments = memoCommentRepository.findByMemo(memo);

        List<MemoCommentDto> commentDtos = comments.stream()
                .map(comment -> MemoCommentDto.builder()
                        .commentId(comment.getCommentId())
                        .content(comment.getContent())
                        .userName(comment.getUser().getUserName())
                        .createdAt(comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy. MM. dd. HH:mm")))
                        .build())
                .collect(Collectors.toList());

        Boolean isAuthor = memo.getUser().getEmail().equals(userEmail);

        return MemoFullDto.builder()
                .memoId(memo.getMemoId())
                .title(memo.getMemoTitle())
                .content(memo.getMemoContent())
                .author(memo.getUser().getUserName())
                .createdAt(memo.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy. MM. dd. HH:mm")))
                .isHidden(memoVisibility.getIsHidden())
                .isAuthor(isAuthor)
                .comments(commentDtos)
                .build();
    }

    @Override
    public void createComment(Long memoId, String email, CreateCommentDto createCommentDto) {
        UserModel user = accountRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND));

        MemoModel memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.DATA_NOT_FOUND));

        MemoVisibilityModel memoVisibility = memoVisibilityRepository.findByMemoAndUser(memo, user)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.NO_PERMISSION));

        MemoCommentModel newComment = MemoCommentModel.builder()
                .content(createCommentDto.getContent())
                .memo(memo)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        memoCommentRepository.save(newComment);
    }

    @Override
    public void deleteComment(Long commentId, String email) {
        UserModel user = accountRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND));

        MemoCommentModel comment = memoCommentRepository.findById(commentId)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.DATA_NOT_FOUND));

        if (!comment.getUser().getEmail().equals(email)) {
            throw new GlobalException(ExceptionStatus.NO_PERMISSION);
        }
        memoCommentRepository.delete(comment);
    }

    @Override
    public void modifyMemo(Long memoId, String userEmail, MemoModifyDto memoModifyDto) {
        UserModel user = accountRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND));

        MemoModel memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.DATA_NOT_FOUND));

        if (!memo.getUser().getEmail().equals(userEmail)) {
            throw new GlobalException(ExceptionStatus.NO_PERMISSION);
        }
        memo.setMemoTitle(memoModifyDto.getTitle());
        memo.setMemoContent(memoModifyDto.getContent());

        memoRepository.save(memo);
    }

    @Override
    @Transactional
    public void deleteMemo(Long memoId, String userEmail) {
        UserModel user = accountRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND));

        MemoModel memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.DATA_NOT_FOUND));

        if (!memo.getUser().getEmail().equals(userEmail)) {
            throw new GlobalException(ExceptionStatus.NO_PERMISSION);
        }

        memoVisibilityRepository.deleteByMemo(memo);
        memoCommentRepository.deleteByMemo(memo);
        memoRepository.delete(memo);
    }

    public void updateMemoVisibility(Long memoId, boolean isHidden, String email) {
        MemoModel memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.DATA_NOT_FOUND));

        UserModel user = accountRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND));

        MemoVisibilityModel memoVisibility = memoVisibilityRepository.findByMemoAndUser(memo, user)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.NO_PERMISSION));

        memoVisibility.setIsHidden(!memoVisibility.getIsHidden());

        memoVisibilityRepository.save(memoVisibility);
    }
}
