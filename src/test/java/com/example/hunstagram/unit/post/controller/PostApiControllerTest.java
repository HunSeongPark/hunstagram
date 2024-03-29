package com.example.hunstagram.unit.post.controller;

import com.example.hunstagram.domain.post.controller.PostApiController;
import com.example.hunstagram.domain.post.dto.PostDto;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-23
 */
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = PostApiController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        })
public class PostApiControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private PostService postService;

    @MockBean
    private AwsS3Service awsS3Service;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserRepository userRepository;

    @DisplayName("post 등록에 성공한다")
    @WithMockUser
    @Test
    void create_post_success() throws Exception {

        // given
        PostDto.Request requestDto = PostDto.Request.builder()
                .content("content")
                .hashtags(List.of("hash1", "hash2"))
                .build();
        String body = mapper.writeValueAsString(requestDto);
        MockMultipartFile bodyFile
                = new MockMultipartFile("data", "data", "application/json", body.getBytes(StandardCharsets.UTF_8));

        String fileName = "tet";
        String contentType = "image";
        String filePath = "src/test/resources/img/tet.png";
        MockMultipartFile image
                = new MockMultipartFile("images", fileName, contentType, new FileInputStream(filePath));

        // when & then
        mvc.perform(multipart("/v1/posts")
                        .file(bodyFile)
                        .file(image)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("post 수정에 성공한다")
    @WithMockUser
    @Test
    void update_post_success() throws Exception {

        // given
        PostDto.Request requestDto = PostDto.Request.builder()
                .content("content")
                .hashtags(List.of("hash1", "hash2"))
                .build();
        String body = mapper.writeValueAsString(requestDto);

        // when & then
        mvc.perform(patch("/v1/posts/1")
                        .content(body)
                        .contentType(APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("post 삭제에 성공한다")
    @WithMockUser
    @Test
    void delete_post_success() throws Exception {

        // given & when & then
        mvc.perform(delete("/v1/posts/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("post 좋아요에 성공한다")
    @WithMockUser
    @Test
    void like_post_success() throws Exception {

        // given & when & then
        mvc.perform(post("/v1/posts/1/like")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().isOk())
                .andDo(print());
    }
}
