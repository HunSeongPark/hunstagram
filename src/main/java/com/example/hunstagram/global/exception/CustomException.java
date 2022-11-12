package com.example.hunstagram.global.exception;

import lombok.Getter;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-12
 */
@Getter
public class CustomException extends RuntimeException {

    private final CustomErrorCode errorCode;
    private final String errorMessage;

    // Without Cause Exception
    public CustomException(CustomErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getErrorMessage();
    }

    public CustomException(CustomErrorCode errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    // With Cause Exception
    public CustomException(CustomErrorCode errorCode, Exception cause) {
        super(errorCode.getErrorMessage(), cause);
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getErrorMessage();
    }

    public CustomException(CustomErrorCode errorCode, String errorMessage, Exception cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
