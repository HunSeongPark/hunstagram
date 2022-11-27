package com.example.hunstagram.unit.user.service;

import com.example.hunstagram.domain.follow.entity.FollowRepository;
import com.example.hunstagram.domain.post.entity.Post;
import com.example.hunstagram.domain.post.entity.PostRepository;
import com.example.hunstagram.domain.user.dto.UserDto;
import com.example.hunstagram.domain.user.entity.User;
import com.example.hunstagram.domain.user.entity.UserRepository;
import com.example.hunstagram.domain.user.service.UserService;
import com.example.hunstagram.global.aws.service.AwsS3Service;
import com.example.hunstagram.global.exception.CustomException;
import com.example.hunstagram.global.security.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static com.example.hunstagram.global.exception.CustomErrorCode.EMAIL_ALREADY_EXIST;
import static com.example.hunstagram.global.exception.CustomErrorCode.NICKNAME_ALREADY_EXIST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Mock
    PostRepository postRepository;

    @Mock
    FollowRepository followRepository;

    @Mock
    JwtService jwtService;

    @Mock
    AwsS3Service awsS3Service;

    @Mock
    PasswordEncoder passwordEncoder;

    private User createUser(Long id) {
        return User.builder()
                .id(id)
                .email("test" + id + "@test.com")
                .password("test123!" + id)
                .name("test" + id)
                .nickname("test" + id)
                .build();
    }

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
        assertThat(e.getErrorCode()).isEqualTo(EMAIL_ALREADY_EXIST);
    }

    @DisplayName("회원가입을 위한 정보 입력에 성공한다")
    @Test
    void signup_info_success() {

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
        given(userRepository.existsByNickname(any()))
                .willReturn(false);

        // when & then
        userService.signupInfo(requestDto, null);
    }

    @DisplayName("회원가입을 위한 정보 입력 시 닉네임 중복일 경우 실패한다")
    @Test
    void signup_email_pw_duplicate_nickname_fail() {

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
        given(userRepository.existsByNickname(any()))
                .willReturn(true);

        // when & then
        CustomException e = assertThrows(CustomException.class, () -> userService.signupInfo(requestDto, null));
        assertThat(e.getErrorCode()).isEqualTo(NICKNAME_ALREADY_EXIST);
    }

    @DisplayName("내 프로필 조회에 성공한다")
    @Test
    void get_my_profile_success() {

        // given
        User me = createUser(1L);

        given(userRepository.findByEmail(any())).willReturn(Optional.of(me));

        Post post = Post.builder()
                .id(1L)
                .thumbnailImage("test")
                .build();

        given(postRepository.findAllByUserId(any())).willReturn(List.of(post));
        Integer followerCount = 918;
        Integer followingCount = 1920;
        given(followRepository.countFolloweeByUserId(any())).willReturn(followerCount);
        given(followRepository.countFollowingByUserId(any())).willReturn(followingCount);

        // when
        UserDto.MyProfileResponse response = userService.getMyProfile();

        // then
        assertThat(response.getUserId()).isEqualTo(me.getId());
        assertThat(response.getNickname()).isEqualTo(me.getNickname());
        assertThat(response.getName()).isEqualTo(me.getName());
        assertThat(response.getPostThumbnails().get(0).getPostId()).isEqualTo(post.getId());
        assertThat(response.getPostThumbnails().get(0).getImagePath()).isEqualTo(post.getThumbnailImage());
        assertThat(response.getFollowerCount()).isEqualTo(followerCount.toString());
        assertThat(response.getFollowingCount()).isEqualTo("1.92K");
    }

    @DisplayName("내 프로필 조회에 성공한다 2")
    @Test
    void get_my_profile_success2() {

        // given
        User me = createUser(1L);

        given(userRepository.findByEmail(any())).willReturn(Optional.of(me));

        Post post = Post.builder()
                .id(1L)
                .thumbnailImage("test")
                .build();

        given(postRepository.findAllByUserId(any())).willReturn(List.of(post));
        Integer followerCount = 9957;
        Integer followingCount = 58201;
        given(followRepository.countFolloweeByUserId(any())).willReturn(followerCount);
        given(followRepository.countFollowingByUserId(any())).willReturn(followingCount);

        // when
        UserDto.MyProfileResponse response = userService.getMyProfile();

        // then
        assertThat(response.getUserId()).isEqualTo(me.getId());
        assertThat(response.getNickname()).isEqualTo(me.getNickname());
        assertThat(response.getName()).isEqualTo(me.getName());
        assertThat(response.getPostThumbnails().get(0).getPostId()).isEqualTo(post.getId());
        assertThat(response.getPostThumbnails().get(0).getImagePath()).isEqualTo(post.getThumbnailImage());
        assertThat(response.getFollowerCount()).isEqualTo("9.96K");
        assertThat(response.getFollowingCount()).isEqualTo("5.82M");
    }
}