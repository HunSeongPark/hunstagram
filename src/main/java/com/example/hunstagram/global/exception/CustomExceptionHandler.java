package com.example.hunstagram.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

import java.util.Objects;

import static com.example.hunstagram.global.exception.CustomErrorCode.*;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-12
 */
@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    // CustomException
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e, HttpServletRequest request) {
        log.error("url: {} | errorCode: {} | errorMessage: {} | cause Exception: ",
                request.getRequestURL(), e.getErrorCode(), e.getErrorMessage(), e.getCause());

        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(new ErrorResponse(e));
    }

    // Not Support Http Method Exception
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMethodException(
            HttpRequestMethodNotSupportedException e,
            HttpServletRequest request
    ) {
        log.error("url: {} | errorCode: {} | errorMessage: {} | cause Exception: ",
                request.getRequestURL(), INVALID_HTTP_METHOD, INVALID_HTTP_METHOD.getErrorMessage(), e);

        return ResponseEntity
                .status(INVALID_HTTP_METHOD.getHttpStatus())
                .body(new ErrorResponse(INVALID_HTTP_METHOD));
    }

    // Validation Exception
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) {
        String validationMessage = Objects.requireNonNull(e.getFieldError()).getDefaultMessage();
        log.error("url: {} | errorCode: {} | errorMessage: {} | cause Exception: ",
                request.getRequestURL(), INVALID_VALUE, validationMessage, e);

        CustomException customException = new CustomException(INVALID_VALUE, validationMessage);
        return ResponseEntity
                .status(INVALID_VALUE.getHttpStatus())
                .body(new ErrorResponse(customException));
    }
}
