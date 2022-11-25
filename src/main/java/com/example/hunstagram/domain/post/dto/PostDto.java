package com.example.hunstagram.domain.post.dto;

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
    public static class CreateRequest {
        private String content;
        private List<String> hashtags;
    }
}