package com.example.hunstagram.unit.follow.controller;

import com.example.hunstagram.domain.follow.controller.FollowApiController;
import com.example.hunstagram.domain.follow.dto.FollowDto;
import com.example.hunstagram.domain.follow.entity.FollowRepository;
import com.example.hunstagram.domain.follow.service.FollowService;
import com.example.hunstagram.domain.user.entity.UserRepository;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-21
 */
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = FollowApiController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        })
public class FollowApiControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private FollowService followService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private FollowRepository followRepository;

    @MockBean
    private UserRepository userRepository;

    @DisplayName("follow 추가")
    @WithMockUser
    @Test
    void follow_add() throws Exception {

        // given
        given(followService.follow(any())).willReturn(new FollowDto.Response(true));

        // when & then
        mvc.perform(post("/v1/follow/2").with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isFollowAdd").value(true))
                .andDo(print());
    }

    @DisplayName("follow 취소")
    @WithMockUser
    @Test
    void follow_cancel() throws Exception {

        // given
        given(followService.follow(any())).willReturn(new FollowDto.Response(false));

        // when & then
        mvc.perform(post("/v1/follow/2").with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isFollowAdd").value(false))
                .andDo(print());
    }
}
