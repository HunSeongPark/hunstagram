package com.example.hunstagram.integration.user;

import com.example.hunstagram.domain.user.dto.UserDto;
import com.example.hunstagram.domain.user.entity.User;
import com.example.hunstagram.domain.user.entity.UserRepository;
import com.example.hunstagram.domain.user.service.UserService;
import com.example.hunstagram.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.example.hunstagram.global.exception.CustomErrorCode.EMAIL_ALREADY_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-14
 */
@Transactional
@SpringBootTest
public class UserServiceIntegrationTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @DisplayName("회원가입을 위한 email, pw 입력에 성공한다")
    @Test
    void signup_email_pw_success() {

        // given
        String email = "gnstjd0831@naver.com";
        String password = "test123456!";
        UserDto.SignUpRequest requestDto = new UserDto.SignUpRequest(email, password);

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
        User user = User.builder()
                .email(email)
                .password(password)
                .nickname("nick")
                .name("name")
                .build();
        userRepository.save(user);

        // when & then
        CustomException e = assertThrows(CustomException.class, () -> userService.signup(requestDto));
        assertThat(e.getErrorCode()).isEqualTo(EMAIL_ALREADY_EXISTS);
    }
}
