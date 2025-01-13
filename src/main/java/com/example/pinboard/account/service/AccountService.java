package com.example.pinboard.account.service;

import com.example.pinboard.account.domain.dto.AccountDto;

public interface AccountService {
    AccountDto findByEmail(String email);
}
