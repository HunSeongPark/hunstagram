package com.example.hunstagram.domain.user.controller;

import com.example.hunstagram.domain.user.service.UserService;
import com.example.hunstagram.global.exception.CustomErrorCode;
import com.example.hunstagram.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-08
 */
@RequiredArgsConstructor
@RequestMapping("/v1/users")
@RestController
public class UserApiController {

    private final UserService userService;

    @GetMapping("/err")
    public ResponseEntity<String> err() {
        throw new CustomException(CustomErrorCode.TEST_ERROR);
    }

    @GetMapping("/err2")
    public ResponseEntity<String> err2() {
        try {
            throw new SQLException("SQL EXCEPTION OCCUR");
    } catch (SQLException e) {
            throw new CustomException(CustomErrorCode.TEST_ERROR, e);
        }
    }

    @GetMapping("/err3")
    public ResponseEntity<String> err3() {
        throw new CustomException(CustomErrorCode.TEST_ERROR, "테스트 에러 코드");
    }
}
