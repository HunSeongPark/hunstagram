package com.example.hunstagram.domain.hashtag.dto;

import lombok.Getter;

/**
 * @author : Hunseong-Park
 * @date : 2023-01-01
 */
public class HashtagDto {

    @Getter
    public static class Response {
        private Long id;
        private String hashtag;
    }
}
