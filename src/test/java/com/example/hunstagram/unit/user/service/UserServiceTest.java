package com.example.hunstagram.unit.user.service;

import com.example.hunstagram.domain.user.dto.UserDto;
import com.example.hunstagram.domain.user.entity.UserRepository;
import com.example.hunstagram.domain.user.service.UserService;
import com.example.hunstagram.global.exception.CustomErrorCode;
import com.example.hunstagram.global.exception.CustomException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.example.hunstagram.global.exception.CustomErrorCode.EMAIL_ALREADY_EXISTS;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-14
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @DisplayName("회원가입을 위한 email, pw 입력에 성공한다")
    @Test
    void signup_email_pw_success() {
        
        // given
        String email = "gnstjd0831@naver.com";
        String password = "test123456!";
        UserDto.SignUpRequest requestDto = new UserDto.SignUpRequest(email, password);
        given(userRepository.existsByEmail(any()))
                .willReturn(false);
        
        // when
        UserDto.SignUpResponse responseDto = userService.signup(requestDto);

        // then
        assertThat(responseDto.getEmail()).isEqualTo(email);
        assertThat(responseDto.getPassword()).isEqualTo(password);
    }

    @DisplayName("회원가입을 위한 email, pw 입력 시 이메일 중복일 경우 실패한다")
    @Test
    void signup_email_pw_duplicate_email_fail() {

        // given
        String email = "gnstjd0831@naver.com";
        String password = "test123456!";
        UserDto.SignUpRequest requestDto = new UserDto.SignUpRequest(email, password);
        given(userRepository.existsByEmail(any()))
                .willReturn(true);

        // when & then
        CustomException e = assertThrows(CustomException.class, () -> userService.signup(requestDto));
        assertThat(e.getErrorCode()).isEqualTo(EMAIL_ALREADY_EXISTS);
    }

}