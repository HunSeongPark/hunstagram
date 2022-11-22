package com.example.hunstagram.domain.follow.dto;

import com.example.hunstagram.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static lombok.AccessLevel.PRIVATE;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-21
 */
public class FollowDto {

    @Getter
    @AllArgsConstructor
    public static class FollowResponse {
        // true 시 팔로우 추가, false 시 팔로우 취소
        private Boolean isFollowAdd;
    }

    @Getter
    @AllArgsConstructor(access = PRIVATE)
    public static class FollowListResponse {
        private String name;
        private String nickname;
        private String profileImage;

        public static FollowListResponse fromEntity(User user) {
            return new FollowListResponse(user.getName(), user.getNickname(), user.getProfileImage());
        }
    }
}
