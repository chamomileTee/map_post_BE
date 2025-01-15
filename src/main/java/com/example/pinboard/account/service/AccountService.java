package com.example.pinboard.account.service;

import com.example.pinboard.account.domain.dto.AccountDto;
import com.example.pinboard.account.domain.dto.RegisterDto;
import com.example.pinboard.common.domain.dto.Messenger;

public interface AccountService {
    AccountDto findByEmail(String email);

    void register(RegisterDto registerDto);
}
