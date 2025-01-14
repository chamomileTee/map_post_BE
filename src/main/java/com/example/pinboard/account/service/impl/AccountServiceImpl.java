package com.example.pinboard.account.service.impl;

import com.example.pinboard.account.domain.dto.AccountDto;
import com.example.pinboard.account.domain.dto.RegisterDto;
import com.example.pinboard.account.domain.model.UserModel;
import com.example.pinboard.account.repository.AccountRepository;
import com.example.pinboard.account.service.AccountService;
import com.example.pinboard.common.domain.dto.Messenger;
import com.example.pinboard.common.domain.vo.ExceptionStatus;
import com.example.pinboard.common.exception.GlobalException;
import com.example.pinboard.log.domain.vo.ActivityType;
import com.example.pinboard.log.service.UserActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final UserActivityLogService userActivityLogService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public AccountDto findByEmail(String email) {
        UserModel account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND, "User not found with email: " + email));
        return new AccountDto(account.getUserId(), account.getEmail(), account.getUserName());
    }

    @Override
    @Transactional
    public void register(RegisterDto registerDto) {
        if (accountRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new GlobalException(ExceptionStatus.BAD_REQUEST, "Create Profile: Email already exists");
        }

        if (accountRepository.findByName(registerDto.getUserName()).isPresent()) {
            throw new GlobalException(ExceptionStatus.BAD_REQUEST, "Create Profile: Username already exists");
        }

        UserModel newUser = UserModel.builder()
                .userName(registerDto.getUserName())
                .email(registerDto.getEmail())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .build();

        accountRepository.save(newUser);

        userActivityLogService.logUserActivity(newUser, ActivityType.REGISTER);
    }
}
