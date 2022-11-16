package com.example.hunstagram.unit.user.controller;

import com.example.hunstagram.domain.user.controller.UserApiController;
import com.example.hunstagram.domain.user.dto.UserDto;
import com.example.hunstagram.domain.user.entity.UserRepository;
import com.example.hunstagram.domain.user.service.UserService;
import com.example.hunstagram.global.aws.service.AwsS3Service;
import com.example.hunstagram.global.security.SecurityConfig;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
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
}