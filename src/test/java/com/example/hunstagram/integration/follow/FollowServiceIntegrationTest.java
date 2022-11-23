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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    private User createUser(Long id) {
        return User.builder()
                .email("test" + id + "@test.com")
                .password("test123!" + id)
                .name("test" + id)
                .nickname("test" + id)
                .build();
    }

    @DisplayName("follow 추가에 성공한다")
    @Test
    void follow_add_success() {

        // given
        User fromUser = createUser(1L);
        User toUser = createUser(2L);
        userRepository.save(fromUser);
        userRepository.save(toUser);

        // SecurityContextHolder에 accessToken 포함하여 저장
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        String accessToken = jwtService.createAccessToken(fromUser.getEmail(), RoleType.USER, fromUser.getId());
        Authentication authToken = new UsernamePasswordAuthenticationToken(fromUser.getEmail(), accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // when
        FollowDto.FollowResponse followResponse = followService.follow(toUser.getId());
        Follow follow = followRepository.findByFromAndToUserId(fromUser.getId(), toUser.getId()).orElse(null);

        // then
        assertThat(followResponse.getIsFollowAdd()).isTrue();
        assertThat(follow).isNotNull();
        assertThat(follow.getFromUser().getId()).isEqualTo(fromUser.getId());
        assertThat(follow.getToUser().getId()).isEqualTo(toUser.getId());
    }

    @DisplayName("follow 추가시 fromUser가 없으면 실패한다")
    @Test
    void follow_add_from_user_not_found_fail() {

        // given
        User fromUser = createUser(1L);
        User toUser = createUser(2L);

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
        User fromUser = createUser(1L);
        User toUser = createUser(2L);

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
        User fromUser = createUser(1L);
        User toUser = createUser(2L);
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
        FollowDto.FollowResponse followResponse = followService.follow(toUser.getId());
        Follow follow = followRepository.findByFromAndToUserId(fromUser.getId(), toUser.getId()).orElse(null);

        // then
        assertThat(followResponse.getIsFollowAdd()).isFalse();
        assertThat(follow).isNull();
    }

    @DisplayName("followee 목록 조회에 성공한다")
    @Test
    void followee_list_success() {

        // given
        User fromUser1 = createUser(1L);
        User fromUser2 = createUser(2L);
        User fromUser3 = createUser(3L);
        User toUser = createUser(4L);
        userRepository.save(fromUser1);
        userRepository.save(fromUser2);
        userRepository.save(fromUser3);
        userRepository.save(toUser);

        Follow follow1 = Follow.builder()
                .fromUser(fromUser1)
                .toUser(toUser)
                .build();
        Follow follow2 = Follow.builder()
                .fromUser(fromUser2)
                .toUser(toUser)
                .build();
        Follow follow3 = Follow.builder()
                .fromUser(fromUser3)
                .toUser(toUser)
                .build();
        followRepository.save(follow1);
        followRepository.save(follow2);
        followRepository.save(follow3);

        // when
        Page<FollowDto.FollowListResponse> result =
                followService.getFolloweeList(PageRequest.of(0, 10), toUser.getId());

        // then
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent().get(0).getNickname()).isEqualTo(fromUser1.getNickname());
        assertThat(result.getContent().get(1).getNickname()).isEqualTo(fromUser2.getNickname());
        assertThat(result.getContent().get(2).getNickname()).isEqualTo(fromUser3.getNickname());
    }

    @DisplayName("followee 목록 조회 시, 사용자가 존재하지 않으면 실패한다")
    @Test
    void followee_list_user_not_found_fail() {

        // given
        User fromUser = createUser(1L);
        User toUser = createUser(2L);
        userRepository.save(fromUser);
        userRepository.save(toUser);

        Follow follow = Follow.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .build();
        followRepository.save(follow);

        // when & then
        // * 유효하지 않은 userId로 followee 조회 시 에러
        CustomException e = assertThrows(CustomException.class,
                () -> followService.getFolloweeList(PageRequest.of(0, 10), 3L));
        assertThat(e.getErrorCode()).isEqualTo(USER_NOT_FOUND);
    }

    @DisplayName("following 목록 조회에 성공한다")
    @Test
    void following_list_success() {

        // given
        User fromUser = createUser(1L);
        User toUser1 = createUser(2L);
        User toUser2 = createUser(3L);
        User toUser3 = createUser(4L);
        userRepository.save(fromUser);
        userRepository.save(toUser1);
        userRepository.save(toUser2);
        userRepository.save(toUser3);

        Follow follow1 = Follow.builder()
                .fromUser(fromUser)
                .toUser(toUser1)
                .build();
        Follow follow2 = Follow.builder()
                .fromUser(fromUser)
                .toUser(toUser2)
                .build();
        Follow follow3 = Follow.builder()
                .fromUser(fromUser)
                .toUser(toUser3)
                .build();
        followRepository.save(follow1);
        followRepository.save(follow2);
        followRepository.save(follow3);

        // when
        Page<FollowDto.FollowListResponse> result =
                followService.getFollowingList(PageRequest.of(0, 10), fromUser.getId());

        // then
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent().get(0).getNickname()).isEqualTo(toUser1.getNickname());
        assertThat(result.getContent().get(1).getNickname()).isEqualTo(toUser2.getNickname());
        assertThat(result.getContent().get(2).getNickname()).isEqualTo(toUser3.getNickname());
    }

    @DisplayName("following 목록 조회 시, 사용자가 존재하지 않으면 실패한다")
    @Test
    void following_list_user_not_found_fail() {

        // given
        User fromUser = createUser(1L);
        User toUser = createUser(2L);
        userRepository.save(fromUser);
        userRepository.save(toUser);

        Follow follow = Follow.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .build();
        followRepository.save(follow);

        // when & then
        // * 유효하지 않은 userId로 following 조회 시 에러
        CustomException e = assertThrows(CustomException.class,
                () -> followService.getFollowingList(PageRequest.of(0, 10), 3L));
        assertThat(e.getErrorCode()).isEqualTo(USER_NOT_FOUND);
    }
}
