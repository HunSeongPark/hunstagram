package com.example.hunstagram.domain.post.dto;

import lombok.Getter;

import java.util.List;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-23
 */
public class PostDto {

    @Getter
    public static class PostRequest {
        private String content;
        private List<String> hashtags;
    }
}