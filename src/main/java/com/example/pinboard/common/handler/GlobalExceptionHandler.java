package com.example.pinboard.common.handler;

import com.example.pinboard.account.controller.AccountController;
import com.example.pinboard.security.controller.AuthController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.example.pinboard.common.domain.dto.Messenger;
import com.example.pinboard.common.domain.vo.ExceptionStatus;
import com.example.pinboard.common.exception.GlobalException;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Messenger> handleAccessDeniedException(AccessDeniedException ex){
        return ResponseEntity.status(ExceptionStatus.NO_PERMISSION.getHttpStatus())
                .body(Messenger.builder()
                        .message(ExceptionStatus.NO_PERMISSION.getMessage()).build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Messenger> handleGlobalException(GlobalException ex){
        return ResponseEntity.status(ex.getStatus().getHttpStatus())
                .body(Messenger.builder()
                        .message(ex.getMessage()).build());
    }
}
