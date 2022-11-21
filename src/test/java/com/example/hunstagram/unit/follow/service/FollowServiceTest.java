package com.example.hunstagram.unit.follow.service;

import com.example.hunstagram.domain.follow.dto.FollowDto;
import com.example.hunstagram.domain.follow.entity.Follow;
import com.example.hunstagram.domain.follow.entity.FollowRepository;
import com.example.hunstagram.domain.follow.service.FollowService;
import com.example.hunstagram.domain.user.entity.User;
import com.example.hunstagram.domain.user.entity.UserRepository;
import com.example.hunstagram.global.exception.CustomException;
import com.example.hunstagram.global.security.service.JwtService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.example.hunstagram.global.exception.CustomErrorCode.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-21
 */
@ExtendWith(MockitoExtension.class)
public class FollowServiceTest {

    @InjectMocks
    FollowService followService;

    @Mock
    JwtService jwtService;

    @Mock
    FollowRepository followRepository;

    @Mock
    UserRepository userRepository;

    @DisplayName("follow 추가에 성공한다")
    @Test
    void follow_add_success() {

        // given
        User fromUser = User.builder()
                .email("test1@test.com")
                .name("test1")
                .nickname("test1")
                .id(1L)
                .build();

        User toUser = User.builder()
                .email("test2@test.com")
                .name("test2")
                .nickname("test2")
                .id(2L)
                .build();

        given(jwtService.getId()).willReturn(1L);
        given(followRepository.findByFromAndToUserId(any(), any())).willReturn(Optional.empty());
        given(userRepository.findById(1L)).willReturn(Optional.of(fromUser));
        given(userRepository.findById(2L)).willReturn(Optional.of(toUser));

        // when
        FollowDto.Response response = followService.follow(2L);

        // then
        assertThat(response.getIsFollowAdd()).isTrue();
    }

    @DisplayName("follow 추가시 fromUser가 없으면 실패한다")
    @Test
    void follow_add_from_user_not_found_fail() {

        // given
        User fromUser = User.builder()
                .email("test1@test.com")
                .name("test1")
                .nickname("test1")
                .id(1L)
                .build();

        User toUser = User.builder()
                .email("test2@test.com")
                .name("test2")
                .nickname("test2")
                .id(2L)
                .build();

        given(jwtService.getId()).willReturn(1L);
        given(followRepository.findByFromAndToUserId(any(), any())).willReturn(Optional.empty());
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        CustomException e = assertThrows(CustomException.class, () -> followService.follow(2L));
        assertThat(e.getErrorCode()).isEqualTo(USER_NOT_FOUND);
    }

    @DisplayName("follow 추가시 toUser가 없으면 실패한다")
    @Test
    void follow_add_to_user_not_found_fail() {

        // given
        User fromUser = User.builder()
                .email("test1@test.com")
                .name("test1")
                .nickname("test1")
                .id(1L)
                .build();

        User toUser = User.builder()
                .email("test2@test.com")
                .name("test2")
                .nickname("test2")
                .id(2L)
                .build();

        given(jwtService.getId()).willReturn(1L);
        given(followRepository.findByFromAndToUserId(any(), any())).willReturn(Optional.empty());
        given(userRepository.findById(1L)).willReturn(Optional.of(fromUser));
        given(userRepository.findById(2L)).willReturn(Optional.empty());

        // when & then
        CustomException e = assertThrows(CustomException.class, () -> followService.follow(2L));
        assertThat(e.getErrorCode()).isEqualTo(USER_NOT_FOUND);
    }

    @DisplayName("follow 취소에 성공한다")
    @Test
    void follow_cancel_success() {

        // given
        User fromUser = User.builder()
                .email("test1@test.com")
                .name("test1")
                .nickname("test1")
                .id(1L)
                .build();

        User toUser = User.builder()
                .email("test2@test.com")
                .name("test2")
                .nickname("test2")
                .id(2L)
                .build();

        Follow follow = Follow.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .build();

        given(jwtService.getId()).willReturn(1L);
        given(followRepository.findByFromAndToUserId(any(), any())).willReturn(Optional.of(follow));

        // when
        FollowDto.Response response = followService.follow(2L);

        // then
        assertThat(response.getIsFollowAdd()).isFalse();
    }
}
