package com.example.hunstagram.domain.user.controller;

import com.example.hunstagram.domain.user.dto.UserDto;
import com.example.hunstagram.domain.user.service.UserService;
import com.example.hunstagram.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

import static com.example.hunstagram.global.exception.CustomErrorCode.TOKEN_NOT_EXIST;
import static com.example.hunstagram.global.security.service.JwtService.TOKEN_HEADER_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

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

    @GetMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_HEADER_PREFIX)) {
            throw new CustomException(TOKEN_NOT_EXIST);
        }
        String refreshToken = authorizationHeader.substring(TOKEN_HEADER_PREFIX.length());
        return ResponseEntity.ok(userService.refresh(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        userService.logout();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile/my")
    public ResponseEntity<UserDto.MyProfileResponse> getMyProfile() {
        return ResponseEntity.ok(userService.getMyProfile());
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserDto.OtherProfileResponse> getProfile(
            HttpServletRequest request,
            @PathVariable Long userId
    ) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String accessToken;
        if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_HEADER_PREFIX)) {
            accessToken = null;
        } else {
            accessToken = authorizationHeader.substring(TOKEN_HEADER_PREFIX.length());
        }
        return ResponseEntity.ok(userService.getProfile(accessToken, userId));
    }
}
