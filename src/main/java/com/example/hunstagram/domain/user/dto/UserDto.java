package com.example.hunstagram.domain.user.dto;

import com.example.hunstagram.domain.post.dto.PostDto;
import com.example.hunstagram.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-14
 */
public class UserDto {

    @Getter
    @AllArgsConstructor
    public static class SignUpRequest {

        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,15}",
                message = "비밀번호는 영어, 숫자, 특수문자를 포함하여 8~15자로 입력해주세요.")
        private String password;
    }

    @Getter
    @AllArgsConstructor(access = PRIVATE)
    public static class SignUpResponse {
        private String email;
        private String password;

        public static SignUpResponse fromRequestDto(SignUpRequest requestDto) {
            return new SignUpResponse(requestDto.getEmail(), requestDto.getPassword());
        }
    }

    @Builder
    @AllArgsConstructor(access = PRIVATE)
    @Getter
    public static class SignUpInfoRequest {

        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,15}",
                message = "비밀번호는 영어, 숫자, 특수문자를 포함하여 8~15자로 입력해주세요.")
        private String password;

        private String profileImage;

        @NotBlank(message = "이름을 입력해주세요.")
        @Pattern(regexp = "[a-zA-Z가-힣1-9]{2,18}",
                message = "이름은 영어, 한글, 숫자로 구성된 2~18자로 입력해주세요.")
        private String name;

        @NotBlank(message = "닉네임을 입력해주세요.")
        @Pattern(regexp = "[a-z1-9_]{4,12}",
                message = "닉네임은 영어, 숫자, _로 구성된 4~10자로 입력해주세요.")
        private String nickname;

        @Max(value = 30, message = "소개글은 최대 30자까지 입력 가능합니다.")
        private String introText;

        public void encodePassword(String encodedPassword) {
            this.password = encodedPassword;
        }

        public User toEntity(String profileImage) {
            return User.builder()
                    .email(this.email)
                    .password(this.password)
                    .profileImage(profileImage)
                    .name(this.name)
                    .nickname(this.nickname)
                    .introText(this.introText)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = PRIVATE)
    public static class MyProfileResponse {
        private Long userId;
        private String nickname;
        private String name;
        private String profileImage;
        private String postCount;
        private String followerCount;
        private String followingCount;
        private List<PostDto.PostThumbnailResponse> postThumbnails;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = PRIVATE)
    public static class OtherProfileResponse {
        private Long userId;
        private String nickname;
        private String name;
        private String profileImage;
        private String postCount;
        private String followerCount;
        private String followingCount;
        private Boolean isFollow;
        private List<PostDto.PostThumbnailResponse> postThumbnails;
    }
}
