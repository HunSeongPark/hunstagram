package com.example.hunstagram.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-12
 */
@Getter
@RequiredArgsConstructor
public enum CustomErrorCode {
    EMAIL_ALREADY_EXISTS(BAD_REQUEST, "이미 존재하는 이메일입니다.");
    private final HttpStatus httpStatus;
    private final String errorMessage;
}
