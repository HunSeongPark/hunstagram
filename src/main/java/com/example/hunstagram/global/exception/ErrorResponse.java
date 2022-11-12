package com.example.hunstagram.global.exception;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-12
 */
public class ErrorResponse {

    private final CustomErrorCode errorCode;
    private final String errorMessage;

    public ErrorResponse(CustomErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getErrorMessage();
    }
}
