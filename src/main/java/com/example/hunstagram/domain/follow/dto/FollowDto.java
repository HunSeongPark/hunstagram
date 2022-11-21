package com.example.hunstagram.domain.follow.dto;

import lombok.AllArgsConstructor;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-21
 */
public class FollowDto {

    @AllArgsConstructor
    public static class Response {
        // true 시 팔로우 추가, false 시 팔로우 취소
        private Boolean isFollowAdd;
    }
}
