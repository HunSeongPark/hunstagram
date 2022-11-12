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

}
