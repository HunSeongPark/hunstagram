package com.example.hunstagram.unit.comment.service;

import com.example.hunstagram.domain.comment.entity.Comment;
import com.example.hunstagram.domain.comment.entity.CommentRepository;
import com.example.hunstagram.domain.comment.service.CommentService;
import com.example.hunstagram.domain.like.dto.LikeDto;
import com.example.hunstagram.domain.like.entity.Like;
import com.example.hunstagram.domain.like.entity.LikeRepository;
import com.example.hunstagram.domain.user.entity.User;
import com.example.hunstagram.domain.user.entity.UserRepository;
import com.example.hunstagram.global.exception.CustomException;
import com.example.hunstagram.global.security.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.example.hunstagram.global.exception.CustomErrorCode.COMMENT_NOT_FOUND;
import static com.example.hunstagram.global.exception.CustomErrorCode.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * @author : Hunseong-Park
 * @date : 2023-01-04
 */
@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    CommentService commentService;

    @Mock
    JwtService jwtService;

    @Mock
    CommentRepository commentRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    LikeRepository likeRepository;

    @DisplayName("comment 좋아요에 성공한다 - 추가")
    @Test
    void add_like_comment_success() {

        // given
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("test12345!")
                .name("test")
                .nickname("test")
                .build();
        Comment comment = Comment.builder()
                .id(1L)
                .user(user)
                .content("test")
                .build();
        given(commentRepository.findById(any())).willReturn(Optional.of(comment));
        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(likeRepository.findByCommentAndUserId(any(), any())).willReturn(Optional.empty());

        // when
        LikeDto.Response response = commentService.like(comment.getId());

        // then
        assertThat(response.getIsLikeAdd()).isTrue();
    }

    @DisplayName("comment 좋아요에 성공한다 - 취소")
    @Test
    void cancel_like_comment_success() {

        // given
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("test12345!")
                .name("test")
                .nickname("test")
                .build();
        Comment comment = Comment.builder()
                .id(1L)
                .user(user)
                .content("test")
                .build();
        given(commentRepository.findById(any())).willReturn(Optional.of(comment));
        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(likeRepository.findByCommentAndUserId(any(), any())).willReturn(Optional.of(Like.builder().build()));

        // when
        LikeDto.Response response = commentService.like(comment.getId());

        // then
        assertThat(response.getIsLikeAdd()).isFalse();
    }

    @DisplayName("comment 좋아요 시 댓글이 존재하지 않으면 실패한다")
    @Test
    void like_comment_not_found_fail() {

        // given
        given(commentRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        CustomException e = assertThrows(CustomException.class, () -> commentService.like(1L));
        assertThat(e.getErrorCode()).isEqualTo(COMMENT_NOT_FOUND);
    }

    @DisplayName("comment 좋아요 시 사용자가 존재하지 않으면 실패한다")
    @Test
    void like_comment_user_not_found_fail() {

        // given
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("test12345!")
                .name("test")
                .nickname("test")
                .build();
        Comment comment = Comment.builder()
                .id(1L)
                .user(user)
                .content("test")
                .build();
        given(commentRepository.findById(any())).willReturn(Optional.of(comment));
        given(userRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        CustomException e = assertThrows(CustomException.class, () -> commentService.like(1L));
        assertThat(e.getErrorCode()).isEqualTo(USER_NOT_FOUND);
    }
}
