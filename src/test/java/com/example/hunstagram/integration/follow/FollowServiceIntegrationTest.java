package com.example.hunstagram.integration.follow;

import com.example.hunstagram.domain.follow.dto.FollowDto;
import com.example.hunstagram.domain.follow.entity.Follow;
import com.example.hunstagram.domain.follow.entity.FollowRepository;
import com.example.hunstagram.domain.follow.service.FollowService;
import com.example.hunstagram.domain.user.entity.User;
import com.example.hunstagram.domain.user.entity.UserRepository;
import com.example.hunstagram.global.exception.CustomException;
import com.example.hunstagram.global.security.service.JwtService;
import com.example.hunstagram.global.type.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static com.example.hunstagram.global.exception.CustomErrorCode.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-21
 */
@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class FollowServiceIntegrationTest {

    @Autowired
    FollowService followService;

    @Autowired
    JwtService jwtService;

    @Autowired
    FollowRepository followRepository;

    @Autowired
    UserRepository userRepository;

    private User createFromUser() {
        return User.builder()
                .email("test1@test.com")
                .password("test1111!")
                .name("test1")
                .nickname("test1")
                .build();
    }

    private User createToUser() {
        return User.builder()
                .email("test2@test.com")
                .password("test2222!")
                .name("test2")
                .nickname("test2")
                .build();
    }

    @DisplayName("follow 추가에 성공한다")
    @Test
    void follow_add_success() {

        // given
        User fromUser = createFromUser();
        User toUser = createToUser();
        userRepository.save(fromUser);
        userRepository.save(toUser);

        // SecurityContextHolder에 accessToken 포함하여 저장
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        String accessToken = jwtService.createAccessToken(fromUser.getEmail(), RoleType.USER, fromUser.getId());
        Authentication authToken = new UsernamePasswordAuthenticationToken(fromUser.getEmail(), accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // when
        FollowDto.Response response = followService.follow(toUser.getId());
        Follow follow = followRepository.findByFromAndToUserId(fromUser.getId(), toUser.getId()).orElse(null);

        // then
        assertThat(response.getIsFollowAdd()).isTrue();
        assertThat(follow).isNotNull();
        assertThat(follow.getFromUser().getId()).isEqualTo(fromUser.getId());
        assertThat(follow.getToUser().getId()).isEqualTo(toUser.getId());
    }

    @DisplayName("follow 추가시 fromUser가 없으면 실패한다")
    @Test
    void follow_add_from_user_not_found_fail() {

        // given
        User fromUser = createFromUser();
        User toUser = createToUser();

        // ! fromUser 테이블에 저장 X
        userRepository.save(toUser);

        // SecurityContextHolder에 accessToken 포함하여 저장
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        String accessToken = jwtService.createAccessToken(fromUser.getEmail(), RoleType.USER, 1L);
        Authentication authToken = new UsernamePasswordAuthenticationToken(fromUser.getEmail(), accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // when & then
        CustomException e = assertThrows(CustomException.class, () -> followService.follow(toUser.getId()));
        assertThat(e.getErrorCode()).isEqualTo(USER_NOT_FOUND);
    }

    @DisplayName("follow 추가시 toUser가 없으면 실패한다")
    @Test
    void follow_add_to_user_not_found_fail() {

        // given
        User fromUser = createFromUser();
        User toUser = createToUser();

        userRepository.save(fromUser);
        // ! toUser 테이블에 저장 X

        // SecurityContextHolder에 accessToken 포함하여 저장
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        String accessToken = jwtService.createAccessToken(fromUser.getEmail(), RoleType.USER, fromUser.getId());
        Authentication authToken = new UsernamePasswordAuthenticationToken(fromUser.getEmail(), accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // when & then
        CustomException e = assertThrows(CustomException.class, () -> followService.follow(2L));
        assertThat(e.getErrorCode()).isEqualTo(USER_NOT_FOUND);
    }

    @DisplayName("follow 취소에 성공한다")
    @Test
    void follow_cancel_success() {

        // given
        User fromUser = createFromUser();
        User toUser = createToUser();
        userRepository.save(fromUser);
        userRepository.save(toUser);

        Follow saveFollow = Follow.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .build();
        followRepository.save(saveFollow);

        // SecurityContextHolder에 accessToken 포함하여 저장
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        String accessToken = jwtService.createAccessToken(fromUser.getEmail(), RoleType.USER, fromUser.getId());
        Authentication authToken = new UsernamePasswordAuthenticationToken(fromUser.getEmail(), accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // when
        FollowDto.Response response = followService.follow(toUser.getId());
        Follow follow = followRepository.findByFromAndToUserId(fromUser.getId(), toUser.getId()).orElse(null);

        // then
        assertThat(response.getIsFollowAdd()).isFalse();
        assertThat(follow).isNull();
    }
}
