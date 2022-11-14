package com.example.hunstagram.domain.user.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static lombok.AccessLevel.*;

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
                message = "영어, 숫자, 특수문자를 포함하여 8~15자로 입력해주세요.")
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
}
