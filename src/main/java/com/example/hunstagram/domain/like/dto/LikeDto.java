package com.example.hunstagram.domain.like.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : Hunseong-Park
 * @date : 2023-01-01
 */
public class LikeDto {

    @Getter
    @AllArgsConstructor
    public static class Response {
        // true 시 좋아요 추가, false 시 좋아요 취소
        private Boolean isLikeAdd;
    }
}
