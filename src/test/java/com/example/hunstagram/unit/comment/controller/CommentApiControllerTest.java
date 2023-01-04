package com.example.hunstagram.unit.comment.controller;

import com.example.hunstagram.domain.comment.controller.CommentApiController;
import com.example.hunstagram.domain.comment.service.CommentService;
import com.example.hunstagram.domain.post.service.PostService;
import com.example.hunstagram.domain.user.entity.UserRepository;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author : Hunseong-Park
 * @date : 2023-01-04
 */
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = CommentApiController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        })
public class CommentApiControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private CommentService commentService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserRepository userRepository;

    @DisplayName("comment 좋아요에 성공한다")
    @WithMockUser
    @Test
    void like_comment_success() throws Exception {

        // given & when & then
        mvc.perform(post("/v1/comments/1/like")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().isOk())
                .andDo(print());
    }
}
