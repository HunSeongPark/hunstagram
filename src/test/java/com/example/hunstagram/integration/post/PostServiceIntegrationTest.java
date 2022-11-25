package com.example.hunstagram.integration.post;

import com.example.hunstagram.config.AwsS3MockConfig;
import com.example.hunstagram.domain.hashtag.entity.Hashtag;
import com.example.hunstagram.domain.hashtag.entity.HashtagRepository;
import com.example.hunstagram.domain.post.dto.PostDto;
import com.example.hunstagram.domain.post.entity.Post;
import com.example.hunstagram.domain.post.entity.PostRepository;
import com.example.hunstagram.domain.post.service.PostService;
import com.example.hunstagram.domain.user.entity.User;
import com.example.hunstagram.domain.user.entity.UserRepository;
import com.example.hunstagram.global.exception.CustomException;
import com.example.hunstagram.global.security.service.JwtService;
import com.example.hunstagram.global.type.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.hunstagram.global.exception.CustomErrorCode.IMAGE_NOT_EXIST;
import static com.example.hunstagram.global.exception.CustomErrorCode.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-23
 */
@Import(AwsS3MockConfig.class)
@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class PostServiceIntegrationTest {

    @Autowired
    PostService postService;

    @Autowired
    JwtService jwtService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    HashtagRepository hashtagRepository;

    @Autowired
    EntityManager em;

    private User createUser(Long id) {
        return User.builder()
                .email("test" + id + "@test.com")
                .password("test123!" + id)
                .name("test" + id)
                .nickname("test" + id)
                .build();
    }

    @DisplayName("post 등록에 성공한다 (content, hashtag 존재)")
    @Test
    void create_post_success_with_content_hashtag() throws IOException {

        // given
        User user = createUser(1L);
        userRepository.save(user);

        // SecurityContextHolder에 로그인 정보 저장
        String accessToken = jwtService.createAccessToken(user.getEmail(), RoleType.USER, user.getId());
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        Authentication authToken = new UsernamePasswordAuthenticationToken(user.getEmail(), accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        String content = "content";
        ArrayList<String> hashtags = new ArrayList<>();
        hashtags.add("hash1");
        hashtags.add("hash2");
        PostDto.CreateRequest requestDto = PostDto.CreateRequest.builder()
                .content(content)
                .hashtags(hashtags)
                .build();

        String fileName = "tet";
        String contentType = "image";
        String filePath = "src/test/resources/img/tet.png";
        MockMultipartFile image
                = new MockMultipartFile("images", fileName, contentType, new FileInputStream(filePath));

        // when
        postService.createPost(requestDto, List.of(image));

        // then
        Post post = postRepository.findAll().get(0);
        assertThat(post.getThumbnailImage()).isNotEmpty();
        assertThat(post.getContent()).isEqualTo(content);
        assertThat(post.getUser().getId()).isEqualTo(user.getId());
        List<Hashtag> findHashtags = hashtagRepository.findAll();
        assertThat(findHashtags.size()).isEqualTo(2);
        assertThat(findHashtags.get(0).getHashtag()).isEqualTo(hashtags.get(0));
        assertThat(findHashtags.get(1).getHashtag()).isEqualTo(hashtags.get(1));
    }

    @DisplayName("post 등록에 성공한다 (content 존재)")
    @Test
    void create_post_success_with_content() throws IOException {

        // given
        User user = createUser(1L);
        userRepository.save(user);

        // SecurityContextHolder에 로그인 정보 저장
        String accessToken = jwtService.createAccessToken(user.getEmail(), RoleType.USER, user.getId());
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        Authentication authToken = new UsernamePasswordAuthenticationToken(user.getEmail(), accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        String content = "content";
        PostDto.CreateRequest requestDto = PostDto.CreateRequest.builder()
                .content(content)
                .build();

        String fileName = "tet";
        String contentType = "image";
        String filePath = "src/test/resources/img/tet.png";
        MockMultipartFile image
                = new MockMultipartFile("images", fileName, contentType, new FileInputStream(filePath));

        // when
        postService.createPost(requestDto, List.of(image));

        // then
        Post post = postRepository.findAll().get(0);
        assertThat(post.getThumbnailImage()).isNotEmpty();
        assertThat(post.getContent()).isEqualTo(content);
        assertThat(post.getUser().getId()).isEqualTo(user.getId());
        List<Hashtag> findHashtags = hashtagRepository.findAll();
        assertThat(findHashtags.size()).isEqualTo(0);
    }

    @DisplayName("post 등록에 성공한다 (hashtag 존재)")
    @Test
    void create_post_success_with_hashtag() throws IOException {

        // given
        User user = createUser(1L);
        userRepository.save(user);

        // SecurityContextHolder에 로그인 정보 저장
        String accessToken = jwtService.createAccessToken(user.getEmail(), RoleType.USER, user.getId());
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        Authentication authToken = new UsernamePasswordAuthenticationToken(user.getEmail(), accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        ArrayList<String> hashtags = new ArrayList<>();
        hashtags.add("hash1");
        hashtags.add("hash2");
        PostDto.CreateRequest requestDto = PostDto.CreateRequest.builder()
                .hashtags(hashtags)
                .build();

        String fileName = "tet";
        String contentType = "image";
        String filePath = "src/test/resources/img/tet.png";
        MockMultipartFile image
                = new MockMultipartFile("images", fileName, contentType, new FileInputStream(filePath));

        // when
        postService.createPost(requestDto, List.of(image));

        // then
        Post post = postRepository.findAll().get(0);
        assertThat(post.getThumbnailImage()).isNotEmpty();
        assertThat(post.getContent()).isEqualTo(null);
        assertThat(post.getUser().getId()).isEqualTo(user.getId());
        List<Hashtag> findHashtags = hashtagRepository.findAll();
        assertThat(findHashtags.size()).isEqualTo(2);
        assertThat(findHashtags.get(0).getHashtag()).isEqualTo(hashtags.get(0));
        assertThat(findHashtags.get(1).getHashtag()).isEqualTo(hashtags.get(1));
    }

    @DisplayName("post 등록에 성공한다 (requestDto 존재 X)")
    @Test
    void create_post_success_without_request_dto() throws IOException {

        // given
        User user = createUser(1L);
        userRepository.save(user);

        // SecurityContextHolder에 로그인 정보 저장
        String accessToken = jwtService.createAccessToken(user.getEmail(), RoleType.USER, user.getId());
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        Authentication authToken = new UsernamePasswordAuthenticationToken(user.getEmail(), accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        String fileName = "tet";
        String contentType = "image";
        String filePath = "src/test/resources/img/tet.png";
        MockMultipartFile image
                = new MockMultipartFile("images", fileName, contentType, new FileInputStream(filePath));

        // when
        postService.createPost(null, List.of(image));

        // then
        Post post = postRepository.findAll().get(0);
        assertThat(post.getThumbnailImage()).isNotEmpty();
        assertThat(post.getContent()).isEqualTo(null);
        assertThat(post.getUser().getId()).isEqualTo(user.getId());
        List<Hashtag> findHashtags = hashtagRepository.findAll();
        assertThat(findHashtags.size()).isEqualTo(0);
    }

    @DisplayName("post 등록시 사용자가 존재하지 않으면 실패한다")
    @Test
    void create_post_user_not_found_fail() throws IOException {

        // given
        User user = createUser(1L);
        // * User 저장 X

        // SecurityContextHolder에 로그인 정보 저장
        String accessToken = jwtService.createAccessToken(user.getEmail(), RoleType.USER, 1L);
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        Authentication authToken = new UsernamePasswordAuthenticationToken(user.getEmail(), accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        String content = "content";
        ArrayList<String> hashtags = new ArrayList<>();
        hashtags.add("hash1");
        hashtags.add("hash2");
        PostDto.CreateRequest requestDto = PostDto.CreateRequest.builder()
                .content(content)
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

    @DisplayName("post 등록시 이미지가 존재하지 않으면 실패한다 (null)")
    @Test
    void create_post_image_not_exist_fail() {

        // given
        User user = createUser(1L);
        userRepository.save(user);

        // SecurityContextHolder에 로그인 정보 저장
        String accessToken = jwtService.createAccessToken(user.getEmail(), RoleType.USER, user.getId());
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        Authentication authToken = new UsernamePasswordAuthenticationToken(user.getEmail(), accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        String content = "content";
        ArrayList<String> hashtags = new ArrayList<>();
        hashtags.add("hash1");
        hashtags.add("hash2");
        PostDto.CreateRequest requestDto = PostDto.CreateRequest.builder()
                .content(content)
                .hashtags(hashtags)
                .build();

        // when & then
        CustomException e = assertThrows(CustomException.class,
                () -> postService.createPost(requestDto, null));
        assertThat(e.getErrorCode()).isEqualTo(IMAGE_NOT_EXIST);
    }

    @DisplayName("post 등록시 이미지가 존재하지 않으면 실패한다 (empty list)")
    @Test
    void create_post_image_empty_list_fail() {

        // given
        User user = createUser(1L);
        userRepository.save(user);

        // SecurityContextHolder에 로그인 정보 저장
        String accessToken = jwtService.createAccessToken(user.getEmail(), RoleType.USER, user.getId());
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        Authentication authToken = new UsernamePasswordAuthenticationToken(user.getEmail(), accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        String content = "content";
        ArrayList<String> hashtags = new ArrayList<>();
        hashtags.add("hash1");
        hashtags.add("hash2");
        PostDto.CreateRequest requestDto = PostDto.CreateRequest.builder()
                .content(content)
                .hashtags(hashtags)
                .build();

        // when & then
        CustomException e = assertThrows(CustomException.class,
                () -> postService.createPost(requestDto, List.of()));
        assertThat(e.getErrorCode()).isEqualTo(IMAGE_NOT_EXIST);
    }
}
