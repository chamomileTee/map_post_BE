package com.example.pinboard.memo.service;

import com.example.pinboard.account.domain.dto.AccountDto;
import com.example.pinboard.memo.domain.dto.CreateMemoDto;
import com.example.pinboard.memo.domain.dto.LocationDto;
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
    default LocationDto entityToDtoLocation(MemoModel model) {
        return LocationDto.builder()
                .memoId(model.getMemoId())
                .latitude(model.getLatitude())
                .longitude(model.getLongitude())
                .build();
    }

    void create(AccountDto accountDto, CreateMemoDto createMemoDto);
    List<LocationDto> getLocations(AccountDto accountDto);
}
