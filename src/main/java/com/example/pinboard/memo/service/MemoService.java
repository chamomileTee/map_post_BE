package com.example.pinboard.memo.service;

import com.example.pinboard.account.domain.dto.AccountDto;
import com.example.pinboard.memo.domain.dto.CreateMemoDto;
import com.example.pinboard.memo.domain.dto.LocationDto;
import com.example.pinboard.memo.domain.dto.MemoDto;
import com.example.pinboard.memo.domain.model.MemoModel;

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
}
