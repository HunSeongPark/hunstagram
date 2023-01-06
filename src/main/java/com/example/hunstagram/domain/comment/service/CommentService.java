package com.example.hunstagram.domain.comment.service;

import com.example.hunstagram.domain.comment.dto.CommentDto;
import com.example.hunstagram.domain.comment.entity.Comment;
import com.example.hunstagram.domain.comment.entity.CommentRepository;
import com.example.hunstagram.domain.like.dto.LikeDto;
import com.example.hunstagram.domain.like.entity.Like;
import com.example.hunstagram.domain.like.entity.LikeRepository;
import com.example.hunstagram.domain.post.entity.Post;
import com.example.hunstagram.domain.post.entity.PostRepository;
import com.example.hunstagram.domain.user.entity.User;
import com.example.hunstagram.domain.user.entity.UserRepository;
import com.example.hunstagram.global.exception.CustomException;
import com.example.hunstagram.global.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.hunstagram.global.exception.CustomErrorCode.*;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-08
 */
@RequiredArgsConstructor
@Transactional
@Service
public class CommentService {

    private final JwtService jwtService;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    public LikeDto.Response like(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
        User user = userRepository.findById(jwtService.getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        Optional<Like> like = likeRepository.findByCommentAndUserId(comment.getId(), user.getId());

        // 좋아요 취소
        if (like.isPresent()) {
            likeRepository.delete(like.get());
            return new LikeDto.Response(false);
        } else {
            // 좋아요 추가
            Like newLike = Like.builder()
                    .user(user)
                    .comment(comment)
                    .build();
            likeRepository.save(newLike);
            return new LikeDto.Response(true);
        }
    }

    public void createComment(CommentDto.Request requestDto) {
        Post post = postRepository.findById(requestDto.getPostId())
                .orElseThrow(() -> new CustomException(POST_NOT_FOUND));
        User user = userRepository.findById(jwtService.getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .content(requestDto.getContent())
                .build();
        commentRepository.save(comment);
    }
}
