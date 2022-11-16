package com.example.hunstagram.integration.user;

import com.example.hunstagram.config.AwsS3MockConfig;
import com.example.hunstagram.domain.user.dto.UserDto;
import com.example.hunstagram.domain.user.entity.User;
import com.example.hunstagram.domain.user.entity.UserRepository;
import com.example.hunstagram.domain.user.service.UserService;
import com.example.hunstagram.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.example.hunstagram.global.exception.CustomErrorCode.EMAIL_ALREADY_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-14
 */
@Import(AwsS3MockConfig.class)
@ActiveProfiles("test")
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

    @DisplayName("회원가입을 위한 정보 입력 및 회원 생성에 성공한다 (프로필이미지 X)")
    @Test
    void signup_info_without_image_success() {
        // given
        String email = "gnstjd0831@naver.com";
        String password = "test123456!";
        String name = "hunseong";
        String nickname = "bba_koon";

        UserDto.SignUpInfoRequest requestDto = UserDto.SignUpInfoRequest.builder()
                .email(email)
                .password(password)
                .name(name)
                .nickname(nickname)
                .build();

        // when
        userService.signupInfo(requestDto, null);

        // then
        User user = userRepository.findAll().get(0);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getProfileImage()).isNull();
    }

    @DisplayName("회원가입을 위한 정보 입력 및 회원 생성에 성공한다 (프로필이미지 O)")
    @Test
    void signup_info_with_image_success() throws IOException {
        // given
        String email = "gnstjd0831@naver.com";
        String password = "test123456!";
        String name = "hunseong";
        String nickname = "bba_koon";

        String fileName = "tet";
        String contentType = "image";
        String filePath = "src/test/resources/img/tet.png";
        MockMultipartFile image
                = new MockMultipartFile(fileName, fileName, contentType, new FileInputStream(filePath));
        UserDto.SignUpInfoRequest requestDto = UserDto.SignUpInfoRequest.builder()
                .email(email)
                .password(password)
                .name(name)
                .nickname(nickname)
                .build();

        // when
        userService.signupInfo(requestDto, image);

        // then
        User user = userRepository.findAll().get(0);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getProfileImage()).isNotNull();
        System.out.println(user.getProfileImage());
    }
}
