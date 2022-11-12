package com.example.hunstagram.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-12
 */
@Getter
@RequiredArgsConstructor
public enum CustomErrorCode {
    /* WRITE ErrorCode HERE! */
    TEST_ERROR(HttpStatus.BAD_GATEWAY, "Test Error Message.");
    private final HttpStatus httpStatus;
    private final String errorMessage;
}
