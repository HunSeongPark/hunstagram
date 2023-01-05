package com.example.hunstagram.domain.comment.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * @author : Hunseong-Park
 * @date : 2023-01-01
 */
public class CommentDto {

    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class Request {
        private Long postId;
        private String content;
    }

    @Getter
    public static class Response {
        private Long commentId;
        private Long userId;
        private Long writerNickname;
        private String content;
        private Long likeCount;
        private LocalDateTime createdAt;
    }
}
