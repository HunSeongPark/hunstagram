package com.example.hunstagram.unit.follow.controller;

import com.example.hunstagram.domain.follow.controller.FollowApiController;
import com.example.hunstagram.domain.follow.dto.FollowDto;
import com.example.hunstagram.domain.follow.dto.FollowDto.FollowListResponse;
import com.example.hunstagram.domain.follow.entity.Follow;
import com.example.hunstagram.domain.follow.entity.FollowRepository;
import com.example.hunstagram.domain.follow.service.FollowService;
import com.example.hunstagram.domain.user.entity.User;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    private User createFromUser() {
        return User.builder()
                .email("test1@test.com")
                .name("test1")
                .nickname("test1")
                .id(1L)
                .build();
    }

    private User createToUser() {
        return User.builder()
                .email("test2@test.com")
                .name("test2")
                .nickname("test2")
                .id(2L)
                .build();
    }

    @DisplayName("follow 추가")
    @WithMockUser
    @Test
    void follow_add() throws Exception {

        // given
        given(followService.follow(any())).willReturn(new FollowDto.FollowResponse(true));

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
        given(followService.follow(any())).willReturn(new FollowDto.FollowResponse(false));

        // when & then
        mvc.perform(post("/v1/follow/2").with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isFollowAdd").value(false))
                .andDo(print());
    }

    @DisplayName("followee 목록 확인")
    @WithMockUser
    @Test
    void followee_list() throws Exception {

        // given
        User toUser = createToUser();
        User fromUser = createFromUser();
        Follow follow = Follow.builder()
                .toUser(toUser)
                .fromUser(fromUser)
                .build();

        List<FollowListResponse> followList = List.of(FollowListResponse.fromEntity(follow.getToUser()));
        given(followService.getFolloweeList(any(), any())).willReturn(new PageImpl<>(followList));

        // when & then
        mvc.perform(get("/v1/follow/followee/2").with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].nickname").value(toUser.getNickname()))
                .andDo(print());
    }

    @DisplayName("following 목록 확인")
    @WithMockUser
    @Test
    void following_list() throws Exception {

        // given
        User toUser = createToUser();
        User fromUser = createFromUser();
        Follow follow = Follow.builder()
                .toUser(toUser)
                .fromUser(fromUser)
                .build();

        List<FollowListResponse> followList = List.of(FollowListResponse.fromEntity(follow.getToUser()));
        given(followService.getFollowingList(any(), any())).willReturn(new PageImpl<>(followList));

        // when & then
        mvc.perform(get("/v1/follow/following/1").with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].nickname").value(toUser.getNickname()))
                .andDo(print());
    }
}
