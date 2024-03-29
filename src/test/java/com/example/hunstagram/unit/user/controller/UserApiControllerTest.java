package com.example.hunstagram.unit.user.controller;

import com.example.hunstagram.domain.user.controller.UserApiController;
import com.example.hunstagram.domain.user.dto.UserDto;
import com.example.hunstagram.domain.user.entity.UserRepository;
import com.example.hunstagram.domain.user.service.UserService;
import com.example.hunstagram.global.aws.service.AwsS3Service;
import com.example.hunstagram.global.security.SecurityConfig;
import com.example.hunstagram.global.security.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-14
 */
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = UserApiController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        })
class UserApiControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AwsS3Service awsS3Service;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserRepository userRepository;

    @DisplayName("회원가입을 위한 email, pw 입력")
    @WithMockUser
    @Test
    void signup() throws Exception {

        // given
        String email = "gnstjd0831@naver.com";
        String password = "test123456!";
        UserDto.SignUpRequest requestDto = new UserDto.SignUpRequest(email, password);
        String body = mapper.writeValueAsString(requestDto);

        given(userService.signup(any()))
                .willReturn(UserDto.SignUpResponse.fromRequestDto(requestDto));

        // when & then
        mvc.perform(post("/v1/users/signup")
                        .content(body)
                        .contentType(APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.password").value(password));
    }

    @DisplayName("회원가입 정보 입력 및 회원 생성")
    @WithMockUser
    @Test
    void signup_info() throws Exception {
        // given
        String email = "gnstjd0831@naver.com";
        String password = "test123456!";
        String name = "hunseong";
        String nickname = "bba_koon";

        UserDto.SignUpInfoRequest requestDto = UserDto.SignUpInfoRequest.builder()
                .email(email)
                .password(password)
                .name(name)
                .nickname(nickname)
                .build();
        String body = mapper.writeValueAsString(requestDto);
        MockMultipartFile bodyFile
                = new MockMultipartFile("data", "data", "application/json", body.getBytes(StandardCharsets.UTF_8));

        // when & then
        mvc.perform(multipart("/v1/users/signup/info")
                        .file(bodyFile)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("refresh token을 통해 access(refresh) token 재발급에 성공한다")
    @WithMockUser
    @Test
    void refresh() throws Exception {
        // given
        String token = "Bearer test123";
        given(userService.refresh(any())).willReturn(new HashMap<>());

        // when & then
        mvc.perform(get("/v1/users/refresh")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("token 재발급 시 token이 존재하지 않으면 실패한다")
    @WithMockUser
    @Test
    void refresh_token_not_exist_fail() throws Exception {
        // given
        given(userService.refresh(any())).willReturn(new HashMap<>());

        // when & then
        mvc.perform(get("/v1/users/refresh"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @DisplayName("token 재발급 시 token prefix가 Bearer가 아니면 실패한다")
    @WithMockUser
    @Test
    void refresh_token_prefix_fail() throws Exception {
        // given
        String token = "test123";
        given(userService.refresh(any())).willReturn(new HashMap<>());

        // when & then
        mvc.perform(get("/v1/users/refresh")
                        .header("Authorization", token))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @DisplayName("logout에 성공한다")
    @WithMockUser
    @Test
    void logout() throws Exception {
        // given & when & then
        mvc.perform(post("/v1/users/logout")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    @DisplayName("내 프로필 조회에 성공한다")
    @WithMockUser
    @Test
    void get_my_profile() throws Exception {

        // given
        UserDto.MyProfileResponse response = UserDto.MyProfileResponse.builder()
                .userId(1L)
                .name("test")
                .nickname("test")
                .build();
        given(userService.getMyProfile()).willReturn(response);

        // when & then
        mvc.perform(get("/v1/users/profile/my")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.nickname").value("test"));
    }

    @DisplayName("유저 프로필 조회에 성공한다 - accessToken X")
    @WithMockUser
    @Test
    void get_user_profile() throws Exception {

        // given
        UserDto.OtherProfileResponse response = UserDto.OtherProfileResponse.builder()
                .userId(2L)
                .name("test")
                .isFollow(false)
                .nickname("test")
                .build();
        given(userService.getProfile(any(), any())).willReturn(response);

        // when & then
        mvc.perform(get("/v1/users/profile/2")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(2L))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.nickname").value("test"));
    }

    @DisplayName("유저 프로필 조회에 성공한다 - bad token header prefix")
    @WithMockUser
    @Test
    void get_user_profile_2_bad_token_header() throws Exception {

        // given
        UserDto.OtherProfileResponse response = UserDto.OtherProfileResponse.builder()
                .userId(2L)
                .name("test")
                .isFollow(false)
                .nickname("test")
                .build();
        given(userService.getProfile(any(), any())).willReturn(response);

        // when & then
        mvc.perform(get("/v1/users/profile/2")
                        .header(AUTHORIZATION, "testAccesstoken123")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(2L))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.nickname").value("test"));
    }

    @DisplayName("유저 프로필 조회에 성공한다 - accessToken O")
    @WithMockUser
    @Test
    void get_user_profile_with_access_token() throws Exception {

        // given
        UserDto.OtherProfileResponse response = UserDto.OtherProfileResponse.builder()
                .userId(2L)
                .name("test")
                .isFollow(false)
                .nickname("test")
                .build();
        given(userService.getProfile(any(), any())).willReturn(response);

        // when & then
        mvc.perform(get("/v1/users/profile/2")
                        .header(AUTHORIZATION, "Bearer testAccesstoken123")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(2L))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.nickname").value("test"));
    }
}