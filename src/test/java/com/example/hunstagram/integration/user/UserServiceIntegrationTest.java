package com.example.hunstagram.integration.user;

import com.auth0.jwt.JWT;
import com.example.hunstagram.config.AwsS3MockConfig;
import com.example.hunstagram.domain.follow.entity.Follow;
import com.example.hunstagram.domain.follow.entity.FollowRepository;
import com.example.hunstagram.domain.post.entity.Post;
import com.example.hunstagram.domain.post.entity.PostRepository;
import com.example.hunstagram.domain.postimage.entity.PostImage;
import com.example.hunstagram.domain.postimage.entity.PostImageRepository;
import com.example.hunstagram.domain.user.dto.UserDto;
import com.example.hunstagram.domain.user.entity.User;
import com.example.hunstagram.domain.user.entity.UserRepository;
import com.example.hunstagram.domain.user.service.UserService;
import com.example.hunstagram.global.exception.CustomException;
import com.example.hunstagram.global.security.service.JwtService;
import com.example.hunstagram.global.type.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.auth0.jwt.algorithms.Algorithm.HMAC256;
import static com.example.hunstagram.global.exception.CustomErrorCode.EMAIL_ALREADY_EXIST;
import static com.example.hunstagram.global.exception.CustomErrorCode.INVALID_TOKEN;
import static com.example.hunstagram.global.security.service.JwtService.*;
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
    JwtService jwtService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostImageRepository postImageRepository;

    @Autowired
    FollowRepository followRepository;

    @Autowired
    EntityManager em;

    private User createUser(Long id) {
        return User.builder()
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
        assertThat(e.getErrorCode()).isEqualTo(EMAIL_ALREADY_EXIST);
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
        String contentType = "image/png";
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
    }

    @DisplayName("refresh token을 통해 access(refresh) token 재발급에 성공한다")
    @Test
    void refresh_success() {

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
        userService.signupInfo(requestDto, null);

        String refreshToken = jwtService.createRefreshToken(email);
        User user = userRepository.findByEmail(email).orElseThrow(RuntimeException::new);
        user.updateRefreshToken(refreshToken);
        em.flush();
        em.clear();

        // when
        Map<String, String> result = userService.refresh(refreshToken);

        // then
        assertThat(result.get("accessToken")).isNotNull().isNotEmpty();
    }

    @DisplayName("token 재발급 시 db 내 refresh token과 일치하지 않으면 실패한다")
    @Test
    void refresh_db_not_matched_fail() {

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

        userService.signupInfo(requestDto, null);

        String refreshToken = jwtService.createRefreshToken(email);
        User user = userRepository.findByEmail(email).orElseThrow(RuntimeException::new);
        user.updateRefreshToken(refreshToken + "dummy");
        em.flush();
        em.clear();

        // when & then
        CustomException e = assertThrows(CustomException.class,
                () -> userService.refresh(refreshToken));
        assertThat(e.getErrorCode()).isEqualTo(INVALID_TOKEN);
    }

    @DisplayName("token 재발급 시 refresh token 만료기간이 1개월 미만이면 refresh token도 재발급한다")
    @Test
    void refresh_token_issue_success() {

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
        userService.signupInfo(requestDto, null);

        // ** 만료일을 25일 (< 1개월) 로 설정
        String refreshToken = JWT.create()
                .withSubject(email)
                .withExpiresAt(new Date(System.currentTimeMillis() + (DAY * 25)))
                .sign(HMAC256(JWT_SECRET));

        User user = userRepository.findByEmail(email).orElseThrow(RuntimeException::new);
        user.updateRefreshToken(refreshToken);
        em.flush();
        em.clear();

        // when
        Map<String, String> result = userService.refresh(refreshToken);

        // then
        assertThat(result.get("accessToken")).isNotNull().isNotEmpty();
        assertThat(result.get("refreshToken")).isNotNull().isNotEmpty();
    }

    @DisplayName("token 재발급 시 refresh token에 해당하는 user가 없으면 실패한다")
    @Test
    void refresh_token_user_not_found_fail() {

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
        userService.signupInfo(requestDto, null);

        // ** refresh Token에 해당하는 이메일을 유효하지 않도록 설정
        String refreshToken = JWT.create()
                .withSubject(email + "dummy")
                .withExpiresAt(new Date(System.currentTimeMillis() + RT_EXP_TIME))
                .sign(HMAC256(JWT_SECRET));

        User user = userRepository.findByEmail(email).orElseThrow(RuntimeException::new);
        user.updateRefreshToken(refreshToken);
        em.flush();
        em.clear();

        // when & then
        CustomException e = assertThrows(CustomException.class, () -> userService.refresh(refreshToken));
        assertThat(e.getErrorCode()).isEqualTo(INVALID_TOKEN);
    }

    @DisplayName("token 재발급 시 user table 내 refresh token이 없으면 실패한다")
    @Test
    void refresh_user_token_not_exist_fail() {

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
        userService.signupInfo(requestDto, null);

        // User Table에 Refresh Token 저장 X

        String refreshToken = JWT.create()
                .withSubject(email)
                .withExpiresAt(new Date(System.currentTimeMillis() + RT_EXP_TIME))
                .sign(HMAC256(JWT_SECRET));

        User user = userRepository.findByEmail(email).orElseThrow(RuntimeException::new);
        em.flush();
        em.clear();

        // when & then
        CustomException e = assertThrows(CustomException.class, () -> userService.refresh(refreshToken));
        assertThat(e.getErrorCode()).isEqualTo(INVALID_TOKEN);
    }

    @DisplayName("logout에 성공한다")
    @Test
    void logout_success() {

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
        userService.signupInfo(requestDto, null);
        User user = userRepository.findByEmail(email).orElseThrow(RuntimeException::new);
        String accessToken = jwtService.createAccessToken(email, RoleType.USER, user.getId());
        String refreshToken = jwtService.createRefreshToken(email);
        user.updateRefreshToken(refreshToken);
        em.flush();
        em.clear();

        // SecurityContextHolder에 accessToken 포함하여 저장
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        Authentication authToken = new UsernamePasswordAuthenticationToken(email, accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // when
        userService.logout();

        // then
        User findUser = userRepository.findByEmail(email).get();
        assertThat(findUser.getRefreshToken()).isNull();
    }

    @DisplayName("logout 시 access token 내 email 정보가 올바르지 않을 시 실패한다")
    @Test
    void logout_invalid_email_fail() {

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
        userService.signupInfo(requestDto, null);
        User user = userRepository.findByEmail(email).orElseThrow(RuntimeException::new);
        // ** 잘못된 email이 들어간 access Token
        String accessToken = jwtService.createAccessToken(email + "dummy", RoleType.USER, user.getId());
        String refreshToken = jwtService.createRefreshToken(email);
        user.updateRefreshToken(refreshToken);
        em.flush();
        em.clear();

        // SecurityContextHolder에 accessToken 포함하여 저장
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        Authentication authToken = new UsernamePasswordAuthenticationToken(email, accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // when & then
        CustomException e = assertThrows(CustomException.class, () -> userService.logout());
        assertThat(e.getErrorCode()).isEqualTo(INVALID_TOKEN);
    }

    @DisplayName("내 프로필 조회에 성공한다")
    @Test
    void get_my_profile_success() {

        // given
        User me = createUser(1L);
        User other = createUser(2L);
        userRepository.save(me);
        userRepository.save(other);

        Follow follow1 = Follow.builder()
                .fromUser(me)
                .toUser(other)
                .build();

        Follow follow2 = Follow.builder()
                .fromUser(other)
                .toUser(me)
                .build();
        followRepository.save(follow1);
        followRepository.save(follow2);

        Post post = Post.builder()
                .content("content")
                .user(me)
                .thumbnailImage("test")
                .build();
        postRepository.save(post);

        PostImage postImage = new PostImage("test", post);
        postImageRepository.save(postImage);

        em.flush();
        em.clear();

        // SecurityContextHolder에 accessToken 포함하여 저장
        String accessToken = jwtService.createAccessToken(me.getEmail(), RoleType.USER, me.getId());
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        Authentication authToken = new UsernamePasswordAuthenticationToken(me.getEmail(), accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // when
        UserDto.MyProfileResponse response = userService.getMyProfile();

        // then
        Post findPost = postRepository.findAll().get(0);
        assertThat(response.getUserId()).isEqualTo(me.getId());
        assertThat(response.getNickname()).isEqualTo(me.getNickname());
        assertThat(response.getName()).isEqualTo(me.getName());
        assertThat(response.getPostThumbnails().get(0).getPostId()).isEqualTo(findPost.getId());
        assertThat(response.getPostThumbnails().get(0).getImagePath()).isEqualTo(findPost.getThumbnailImage());
        assertThat(response.getFollowerCount()).isEqualTo("1");
        assertThat(response.getFollowingCount()).isEqualTo("1");
    }

    @DisplayName("내 프로필 조회 시, 로그인하지 않으면 실패한다")
    @Test
    void get_my_profile_guest_fail() {

        // given
        User me = createUser(1L);
        User other = createUser(2L);
        userRepository.save(me);
        userRepository.save(other);

        Follow follow1 = Follow.builder()
                .fromUser(me)
                .toUser(other)
                .build();

        Follow follow2 = Follow.builder()
                .fromUser(other)
                .toUser(me)
                .build();
        followRepository.save(follow1);
        followRepository.save(follow2);

        Post post = Post.builder()
                .content("content")
                .user(me)
                .thumbnailImage("test")
                .build();
        postRepository.save(post);

        PostImage postImage = new PostImage("test", post);
        postImageRepository.save(postImage);

        em.flush();
        em.clear();

        // when & then
        assertThrows(Exception.class, () -> userService.getMyProfile());
    }
}
