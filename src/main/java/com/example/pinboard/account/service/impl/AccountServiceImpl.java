package com.example.pinboard.account.service.impl;

import com.example.pinboard.account.domain.dto.AccountDto;
import com.example.pinboard.account.domain.model.UserModel;
import com.example.pinboard.account.repository.AccountRepository;
import com.example.pinboard.account.service.AccountService;
import com.example.pinboard.common.domain.vo.ExceptionStatus;
import com.example.pinboard.common.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    @Override
    public AccountDto findByEmail(String email) {
        UserModel account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND, "User not found with email: " + email));
        return new AccountDto(account.getUserId(), account.getEmail(), account.getUserName());
    }
}
