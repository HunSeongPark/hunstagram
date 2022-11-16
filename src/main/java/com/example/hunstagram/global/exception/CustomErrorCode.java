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
    // User
    EMAIL_ALREADY_EXISTS(BAD_REQUEST, "이미 존재하는 이메일입니다."),
    NICKNAME_ALREADY_EXISTS(BAD_REQUEST, "이미 존재하는 닉네임입니다."),
    LOGIN_FAILED(UNAUTHORIZED, "아이디 또는 비밀번호가 일치하지 않습니다."),

    // JWT
    TOKEN_NOT_FOUND(BAD_REQUEST, "JWT Token이 존재하지 않습니다."),

    // AWS
    IMAGE_UPLOAD_FAILED(INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다."),

    // General
    INVALID_HTTP_METHOD(METHOD_NOT_ALLOWED, "잘못된 Http Method 요청입니다."),
    INVALID_VALUE(BAD_REQUEST, "잘못된 입력값입니다.");

    private final HttpStatus httpStatus;
    private final String errorMessage;
}
