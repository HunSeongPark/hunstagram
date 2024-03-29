package com.example.hunstagram.unit.post.service;

import com.example.hunstagram.domain.hashtag.entity.HashtagRepository;
import com.example.hunstagram.domain.like.dto.LikeDto;
import com.example.hunstagram.domain.like.entity.Like;
import com.example.hunstagram.domain.like.entity.LikeRepository;
import com.example.hunstagram.domain.post.dto.PostDto;
import com.example.hunstagram.domain.post.entity.Post;
import com.example.hunstagram.domain.post.entity.PostRepository;
import com.example.hunstagram.domain.post.service.PostService;
import com.example.hunstagram.domain.postimage.entity.PostImage;
import com.example.hunstagram.domain.postimage.entity.PostImageRepository;
import com.example.hunstagram.domain.user.entity.User;
import com.example.hunstagram.domain.user.entity.UserRepository;
import com.example.hunstagram.global.aws.service.AwsS3Service;
import com.example.hunstagram.global.exception.CustomException;
import com.example.hunstagram.global.security.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.hunstagram.global.exception.CustomErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-23
 */
@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @InjectMocks
    PostService postService;

    @Mock
    JwtService jwtService;

    @Mock
    AwsS3Service awsS3Service;

    @Mock
    PostRepository postRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    HashtagRepository hashtagRepository;

    @Mock
    PostImageRepository postImageRepository;

    @Mock
    LikeRepository likeRepository;

    @DisplayName("post 등록에 성공한다 (content, hashtag 존재)")
    @Test
    void create_post_success_with_content_hashtag() throws IOException {

        // given
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("test12345!")
                .name("test")
                .nickname("test")
                .build();
        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(awsS3Service.uploadImage(any())).willReturn("http://test.image.path.com");
        String content = "content";
        ArrayList<String> hashtags = new ArrayList<>();
        hashtags.add("hash1");
        hashtags.add("hash2");
        PostDto.Request requestDto = PostDto.Request.builder()
                .content("content")
                .hashtags(hashtags)
                .build();

        String fileName = "tet";
        String contentType = "image";
        String filePath = "src/test/resources/img/tet.png";
        MockMultipartFile image
                = new MockMultipartFile("images", fileName, contentType, new FileInputStream(filePath));

        // when & then
        postService.createPost(requestDto, List.of(image));
    }

    @DisplayName("post 등록에 성공한다 (content 존재)")
    @Test
    void create_post_success_with_content() throws IOException {

        // given
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("test12345!")
                .name("test")
                .nickname("test")
                .build();
        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(awsS3Service.uploadImage(any())).willReturn("http://test.image.path.com");
        String content = "content";
        PostDto.Request requestDto = PostDto.Request.builder()
                .content("content")
                .build();

        String fileName = "tet";
        String contentType = "image";
        String filePath = "src/test/resources/img/tet.png";
        MockMultipartFile image
                = new MockMultipartFile("images", fileName, contentType, new FileInputStream(filePath));

        // when & then
        postService.createPost(requestDto, List.of(image));
    }

    @DisplayName("post 등록에 성공한다 (hashtag 존재)")
    @Test
    void create_post_success_with_hashtag() throws IOException {

        // given
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("test12345!")
                .name("test")
                .nickname("test")
                .build();
        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(awsS3Service.uploadImage(any())).willReturn("http://test.image.path.com");
        ArrayList<String> hashtags = new ArrayList<>();
        hashtags.add("hash1");
        hashtags.add("hash2");
        PostDto.Request requestDto = PostDto.Request.builder()
                .hashtags(hashtags)
                .build();

        String fileName = "tet";
        String contentType = "image";
        String filePath = "src/test/resources/img/tet.png";
        MockMultipartFile image
                = new MockMultipartFile("images", fileName, contentType, new FileInputStream(filePath));

        // when & then
        postService.createPost(requestDto, List.of(image));
    }

    @DisplayName("post 등록에 성공한다 (requestDto 존재 X)")
    @Test
    void create_post_success_without_request_dto() throws IOException {

        // given
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("test12345!")
                .name("test")
                .nickname("test")
                .build();
        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(awsS3Service.uploadImage(any())).willReturn("http://test.image.path.com");

        String fileName = "tet";
        String contentType = "image";
        String filePath = "src/test/resources/img/tet.png";
        MockMultipartFile image
                = new MockMultipartFile("images", fileName, contentType, new FileInputStream(filePath));

        // when & then
        postService.createPost(null, List.of(image));
    }

    @DisplayName("post 등록 시 사용자가 존재하지 않으면 실패한다")
    @Test
    void create_post_user_not_found_fail() throws IOException {

        // given
        given(userRepository.findById(any())).willReturn(Optional.empty());
        String content = "content";
        ArrayList<String> hashtags = new ArrayList<>();
        hashtags.add("hash1");
        hashtags.add("hash2");
        PostDto.Request requestDto = PostDto.Request.builder()
                .content("content")
                .hashtags(hashtags)
                .build();
        String fileName = "tet";
        String contentType = "image";
        String filePath = "src/test/resources/img/tet.png";
        MockMultipartFile image
                = new MockMultipartFile("images", fileName, contentType, new FileInputStream(filePath));

        // when & then
        CustomException e = assertThrows(CustomException.class,
                () -> postService.createPost(requestDto, List.of(image)));
        assertThat(e.getErrorCode()).isEqualTo(USER_NOT_FOUND);
    }

    @DisplayName("post 등록 시 이미지가 존재하지 않으면 실패한다 (null)")
    @Test
    void create_post_image_not_exist_fail() {

        // given
        String content = "content";
        ArrayList<String> hashtags = new ArrayList<>();
        hashtags.add("hash1");
        hashtags.add("hash2");
        PostDto.Request requestDto = PostDto.Request.builder()
                .content("content")
                .hashtags(hashtags)
                .build();

        // when & then
        CustomException e = assertThrows(CustomException.class,
                () -> postService.createPost(requestDto, null));
        assertThat(e.getErrorCode()).isEqualTo(IMAGE_NOT_EXIST);
    }

    @DisplayName("post 등록 시 이미지가 존재하지 않으면 실패한다 (empty list)")
    @Test
    void create_post_image_empty_list_fail() {

        // given
        String content = "content";
        ArrayList<String> hashtags = new ArrayList<>();
        hashtags.add("hash1");
        hashtags.add("hash2");
        PostDto.Request requestDto = PostDto.Request.builder()
                .content("content")
                .hashtags(hashtags)
                .build();

        // when & then
        CustomException e = assertThrows(CustomException.class,
                () -> postService.createPost(requestDto, List.of()));
        assertThat(e.getErrorCode()).isEqualTo(IMAGE_NOT_EXIST);
    }

    @DisplayName("post 삭제에 성공한다")
    @Test
    void delete_post_success() {

        // given
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("test12345!")
                .name("test")
                .nickname("test")
                .build();
        PostImage postImage = new PostImage("test", null);
        Post post = Post.builder()
                .id(1L)
                .postImages(List.of(postImage))
                .user(user)
                .build();

        given(postRepository.findByIdWithImageAndUser(any())).willReturn(Optional.of(post));
        given(jwtService.getId()).willReturn(user.getId());

        // when & then
        postService.deletePost(post.getId());
    }

    @DisplayName("post 삭제 시, post가 존재하지 않으면 실패한다")
    @Test
    void delete_post_not_found_fail() {

        // given
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("test12345!")
                .name("test")
                .nickname("test")
                .build();
        PostImage postImage = new PostImage("test", null);
        Post post = Post.builder()
                .id(1L)
                .postImages(List.of(postImage))
                .user(user)
                .build();

        given(postRepository.findByIdWithImageAndUser(any())).willReturn(Optional.of(post));
        given(jwtService.getId()).willReturn(2L);

        // when & then
        CustomException e = assertThrows(CustomException.class,
                () -> postService.deletePost(post.getId()));
        assertThat(e.getErrorCode()).isEqualTo(NOT_USER_OWN_POST);
    }

    @DisplayName("post 삭제 시, 로그인 한 사용자가 작성한 post가 아니면 실패한다")
    @Test
    void delete_not_own_post_fail() {

        // given
        given(postRepository.findByIdWithImageAndUser(any())).willReturn(Optional.empty());

        // when & then
        CustomException e = assertThrows(CustomException.class,
                () -> postService.deletePost(1L));
        assertThat(e.getErrorCode()).isEqualTo(POST_NOT_FOUND);
    }

    @DisplayName("post 좋아요에 성공한다 - 추가")
    @Test
    void add_like_post_success() {

        // given
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("test12345!")
                .name("test")
                .nickname("test")
                .build();
        PostImage postImage = new PostImage("test", null);
        Post post = Post.builder()
                .id(1L)
                .postImages(List.of(postImage))
                .user(user)
                .build();
        given(postRepository.findById(any())).willReturn(Optional.of(post));
        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(likeRepository.findByPostAndUserId(any(), any())).willReturn(Optional.empty());

        // when
        LikeDto.Response response = postService.like(post.getId());

        // then
        assertThat(response.getIsLikeAdd()).isTrue();
    }

    @DisplayName("post 좋아요에 성공한다 - 취소")
    @Test
    void cancel_like_post_success() {

        // given
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("test12345!")
                .name("test")
                .nickname("test")
                .build();
        PostImage postImage = new PostImage("test", null);
        Post post = Post.builder()
                .id(1L)
                .postImages(List.of(postImage))
                .user(user)
                .build();
        given(postRepository.findById(any())).willReturn(Optional.of(post));
        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(likeRepository.findByPostAndUserId(any(), any())).willReturn(Optional.of(Like.builder().build()));

        // when
        LikeDto.Response response = postService.like(post.getId());

        // then
        assertThat(response.getIsLikeAdd()).isFalse();
    }

    @DisplayName("post 좋아요 시 게시글이 존재하지 않으면 실패한다")
    @Test
    void like_post_not_found_fail() {

        // given
        given(postRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        CustomException e = assertThrows(CustomException.class, () -> postService.like(1L));
        assertThat(e.getErrorCode()).isEqualTo(POST_NOT_FOUND);
    }

    @DisplayName("post 좋아요 시 사용자가 존재하지 않으면 실패한다")
    @Test
    void like_post_user_not_found_fail() {

        // given
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("test12345!")
                .name("test")
                .nickname("test")
                .build();
        PostImage postImage = new PostImage("test", null);
        Post post = Post.builder()
                .id(1L)
                .postImages(List.of(postImage))
                .user(user)
                .build();
        given(postRepository.findById(any())).willReturn(Optional.of(post));
        given(userRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        CustomException e = assertThrows(CustomException.class, () -> postService.like(1L));
        assertThat(e.getErrorCode()).isEqualTo(USER_NOT_FOUND);
    }
}
