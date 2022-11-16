package com.example.hunstagram.domain.user.controller;

import com.example.hunstagram.domain.user.dto.UserDto;
import com.example.hunstagram.domain.user.service.UserService;
import com.example.hunstagram.global.aws.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<Void> signupInfo(
            @RequestPart(value = "data") @Valid UserDto.SignUpInfoRequest requestDto,
            @RequestPart(value = "profileImage", required = false) MultipartFile image
            ) {
        userService.signupInfo(requestDto, image);
        return ResponseEntity.ok().build();
    }
}
