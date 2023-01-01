package com.example.hunstagram.domain.comment.dto;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * @author : Hunseong-Park
 * @date : 2023-01-01
 */
public class CommentDto {

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
