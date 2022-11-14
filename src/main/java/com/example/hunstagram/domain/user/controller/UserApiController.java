package com.example.hunstagram.domain.user.controller;

import com.example.hunstagram.domain.user.dto.UserDto;
import com.example.hunstagram.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-08
 */
@RequiredArgsConstructor
@RequestMapping("/v1/users")
@RestController
public class UserApiController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto.SignUpResponse> signup(@RequestBody @Valid UserDto.SignUpRequest requestDto) {
        return ResponseEntity.ok(userService.signup(requestDto));
    }

    @PostMapping("/signup/info")
    public ResponseEntity<Void> signupInfo(@RequestBody @Valid UserDto.SignUpInfoRequest requestDto) {
        userService.signupInfo(requestDto);
        return ResponseEntity.ok().build();
    }
}
