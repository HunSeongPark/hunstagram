package com.example.hunstagram.domain.post.dto;

import com.example.hunstagram.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-23
 */
public class PostDto {

    @Builder
    @AllArgsConstructor(access = PRIVATE)
    @Getter
    public static class Request {
        private String content;
        private List<String> hashtags;
    }

    @Getter
    @AllArgsConstructor(access = PRIVATE)
    public static class PostThumbnailResponse {
        private Long postId;
        private String imagePath;

        public static PostThumbnailResponse fromEntity(Post post) {
            return new PostThumbnailResponse(post.getId(), post.getThumbnailImage());
        }
    }
}