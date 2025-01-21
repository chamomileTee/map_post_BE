package com.example.pinboard.memo.service;

import com.example.pinboard.account.domain.dto.AccountDto;
import com.example.pinboard.memo.domain.dto.*;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * MemoService
 * <p>Memo Service Interface</p>
 *
 * @version 1.0
 * @since 2025-01-15
 */
public interface MemoService {
    void create(String email, CreateMemoDto createMemoDto);
    List<LocationDto> getLocations(String email, String option);
    MemoDto getMemo(String email, Long memoId);
    Page<MemoListDto> getMemoList(String userEmail, int page, int size, String option);
    MemoFullDto getMemoFull(Long memoId, String userEmail);

    void createComment(Long memoId, String email, CreateCommentDto createCommentDto);
    void deleteComment(Long commentId, String email);
    void modifyMemo(Long memoId, String userEmail, MemoModifyDto memoModifyDto);
    void deleteMemo(Long memoId, String userEmail);
    void updateMemoVisibility(Long memoId, boolean isHidden, String email);
}
