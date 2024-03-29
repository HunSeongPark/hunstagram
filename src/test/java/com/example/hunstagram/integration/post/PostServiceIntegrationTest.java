package com.example.hunstagram.integration.post;

import com.example.hunstagram.config.AwsS3MockConfig;
import com.example.hunstagram.domain.hashtag.entity.Hashtag;
import com.example.hunstagram.domain.hashtag.entity.HashtagRepository;
import com.example.hunstagram.domain.like.dto.LikeDto;
import com.example.hunstagram.domain.like.entity.LikeRepository;
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
import java.util.stream.Stream;

import static com.example.hunstagram.global.exception.CustomErrorCode.*;
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
    LikeRepository likeRepository;

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

    private void loginUser(User user) {
        String accessToken = jwtService.createAccessToken(user.getEmail(), RoleType.USER, user.getId());
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        Authentication authToken = new UsernamePasswordAuthenticationToken(user.getEmail(), accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    @DisplayName("post 등록에 성공한다 (content, hashtag 존재)")
    @Test
    void create_post_success_with_content_hashtag() throws IOException {

        // given
        User user = createUser(1L);
        userRepository.save(user);

        loginUser(user);

        String content = "content";
        ArrayList<String> hashtags = new ArrayList<>();
        hashtags.add("hash1");
        hashtags.add("hash2");
        PostDto.Request requestDto = PostDto.Request.builder()
                .content(content)
                .hashtags(hashtags)
                .build();

        String fileName = "tet";
        String contentType = "image/png";
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

        loginUser(user);

        String content = "content";
        PostDto.Request requestDto = PostDto.Request.builder()
                .content(content)
                .build();

        String fileName = "tet";
        String contentType = "image/png";
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

        loginUser(user);

        ArrayList<String> hashtags = new ArrayList<>();
        hashtags.add("hash1");
        hashtags.add("hash2");
        PostDto.Request requestDto = PostDto.Request.builder()
                .hashtags(hashtags)
                .build();

        String fileName = "tet";
        String contentType = "image/png";
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

        loginUser(user);

        String fileName = "tet";
        String contentType = "image/png";
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
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("test123!")
                .name("test")
                .nickname("test")
                .build();

        loginUser(user);

        String content = "content";
        ArrayList<String> hashtags = new ArrayList<>();
        hashtags.add("hash1");
        hashtags.add("hash2");
        PostDto.Request requestDto = PostDto.Request.builder()
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

        loginUser(user);

        String content = "content";
        ArrayList<String> hashtags = new ArrayList<>();
        hashtags.add("hash1");
        hashtags.add("hash2");
        PostDto.Request requestDto = PostDto.Request.builder()
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

        loginUser(user);

        String content = "content";
        ArrayList<String> hashtags = new ArrayList<>();
        hashtags.add("hash1");
        hashtags.add("hash2");
        PostDto.Request requestDto = PostDto.Request.builder()
                .content(content)
                .hashtags(hashtags)
                .build();

        // when & then
        CustomException e = assertThrows(CustomException.class,
                () -> postService.createPost(requestDto, List.of()));
        assertThat(e.getErrorCode()).isEqualTo(IMAGE_NOT_EXIST);
    }

    @DisplayName("post 수정에 성공한다 (content, hashtag 수정)")
    @Test
    void update_post_content_hashtag_success() {

        // given
        User user = createUser(1L);
        userRepository.save(user);

        Post post = Post.builder()
                .content("content")
                .user(user)
                .thumbnailImage("image")
                .build();
        postRepository.save(post);

        Hashtag hash1 = new Hashtag("hash1", post);
        Hashtag hash2 = new Hashtag("hash2", post);
        hashtagRepository.save(hash1);
        hashtagRepository.save(hash2);

        em.flush();
        em.clear();

        String changedContent = "changed Content";
        List<String> changedHashtags = List.of("hash3", "hash4", "hash5");

        PostDto.Request requestDto = PostDto.Request.builder()
                .content(changedContent)
                .hashtags(changedHashtags)
                .build();

        loginUser(user);

        // when
        postService.updatePost(requestDto, post.getId());

        // then
        Post changedPost = postRepository.findById(post.getId()).get();
        assertThat(changedPost.getContent()).isEqualTo(changedContent);
        assertThat(changedPost.getHashtags().size()).isEqualTo(changedHashtags.size());
        assertThat(changedPost.getHashtags().get(0).getHashtag()).isEqualTo(changedHashtags.get(0));
        assertThat(changedPost.getHashtags().get(1).getHashtag()).isEqualTo(changedHashtags.get(1));
        assertThat(changedPost.getHashtags().get(2).getHashtag()).isEqualTo(changedHashtags.get(2));
    }

    @DisplayName("post 수정에 성공한다 (content 수정)")
    @Test
    void update_post_content_success() {

        // given
        User user = createUser(1L);
        userRepository.save(user);

        Post post = Post.builder()
                .content("content")
                .user(user)
                .thumbnailImage("image")
                .build();
        postRepository.save(post);

        Hashtag hash1 = new Hashtag("hash1", post);
        Hashtag hash2 = new Hashtag("hash2", post);
        hashtagRepository.save(hash1);
        hashtagRepository.save(hash2);

        em.flush();
        em.clear();

        String changedContent = "changed Content";

        PostDto.Request requestDto = PostDto.Request.builder()
                .content(changedContent)
                .hashtags(Stream.of(hash1, hash2).map(Hashtag::getHashtag).toList()) // 기존 Hashtag 그대로
                .build();

        loginUser(user);

        // when
        postService.updatePost(requestDto, post.getId());

        // then
        Post changedPost = postRepository.findById(post.getId()).get();
        assertThat(changedPost.getContent()).isEqualTo(changedContent);
        assertThat(changedPost.getHashtags().size()).isEqualTo(2);
        assertThat(changedPost.getHashtags().get(0).getHashtag()).isEqualTo(hash1.getHashtag());
        assertThat(changedPost.getHashtags().get(1).getHashtag()).isEqualTo(hash2.getHashtag());
    }

    @DisplayName("post 수정에 성공한다 (hashtag 수정)")
    @Test
    void update_post_hashtag_success() {

        // given
        User user = createUser(1L);
        userRepository.save(user);

        Post post = Post.builder()
                .content("content")
                .user(user)
                .thumbnailImage("image")
                .build();
        postRepository.save(post);

        Hashtag hash1 = new Hashtag("hash1", post);
        Hashtag hash2 = new Hashtag("hash2", post);
        hashtagRepository.save(hash1);
        hashtagRepository.save(hash2);

        em.flush();
        em.clear();

        List<String> changedHashtags = List.of("hash3", "hash4", "hash5");

        PostDto.Request requestDto = PostDto.Request.builder()
                .content(post.getContent()) // 기존 Content 그대로
                .hashtags(changedHashtags)
                .build();

        loginUser(user);

        // when
        postService.updatePost(requestDto, post.getId());

        // then
        Post changedPost = postRepository.findById(post.getId()).get();
        assertThat(changedPost.getContent()).isEqualTo(post.getContent());
        assertThat(changedPost.getHashtags().size()).isEqualTo(changedHashtags.size());
        assertThat(changedPost.getHashtags().get(0).getHashtag()).isEqualTo(changedHashtags.get(0));
        assertThat(changedPost.getHashtags().get(1).getHashtag()).isEqualTo(changedHashtags.get(1));
        assertThat(changedPost.getHashtags().get(2).getHashtag()).isEqualTo(changedHashtags.get(2));
    }

    @DisplayName("post 수정 시 post가 없으면 실패한다")
    @Test
    void update_post_not_found_fail() {

        // given
        User user = createUser(1L);
        userRepository.save(user);

        Post post = Post.builder()
                .content("content")
                .user(user)
                .thumbnailImage("image")
                .build();
        postRepository.save(post);

        Hashtag hash1 = new Hashtag("hash1", post);
        Hashtag hash2 = new Hashtag("hash2", post);
        hashtagRepository.save(hash1);
        hashtagRepository.save(hash2);

        em.flush();
        em.clear();

        String changedContent = "changed Content";
        List<String> changedHashtags = List.of("hash3", "hash4", "hash5");

        PostDto.Request requestDto = PostDto.Request.builder()
                .content(changedContent)
                .hashtags(changedHashtags)
                .build();

        // when & then
        CustomException e = assertThrows(CustomException.class,
                () -> postService.updatePost(requestDto, post.getId() + 1));// 존재하지 않는 postId
        assertThat(e.getErrorCode()).isEqualTo(POST_NOT_FOUND);
    }

    @DisplayName("post 수정 시 로그인 한 사용자가 쓴 post가 아니면 실패한다")
    @Test
    void update_not_own_post_fail() {

        // given
        User writer = createUser(1L);
        User other = createUser(2L);
        userRepository.save(writer);
        userRepository.save(other);

        Post post = Post.builder()
                .content("content")
                .user(writer)
                .thumbnailImage("image")
                .build();
        postRepository.save(post);

        Hashtag hash1 = new Hashtag("hash1", post);
        Hashtag hash2 = new Hashtag("hash2", post);
        hashtagRepository.save(hash1);
        hashtagRepository.save(hash2);

        em.flush();
        em.clear();

        String changedContent = "changed Content";
        List<String> changedHashtags = List.of("hash3", "hash4", "hash5");

        PostDto.Request requestDto = PostDto.Request.builder()
                .content(changedContent)
                .hashtags(changedHashtags)
                .build();

        loginUser(other);

        // when & then
        CustomException e = assertThrows(CustomException.class,
                () -> postService.updatePost(requestDto, post.getId()));
        assertThat(e.getErrorCode()).isEqualTo(NOT_USER_OWN_POST);
    }

    @DisplayName("post 삭제에 성공한다")
    @Test
    void delete_post_success() throws IOException {
        // given
        User user = createUser(1L);
        userRepository.save(user);

        PostDto.Request requestDto = PostDto.Request.builder()
                .build();

        String fileName = "tet";
        String contentType = "image/png";
        String filePath = "src/test/resources/img/tet.png";
        MockMultipartFile image
                = new MockMultipartFile("images", fileName, contentType, new FileInputStream(filePath));

        loginUser(user);

        postService.createPost(requestDto, List.of(image));

        em.flush();
        em.clear();

        // when & then
        Post post = postRepository.findAll().get(0);
        postService.deletePost(post.getId());
    }

    @DisplayName("post 삭제 시, post가 존재하지 않으면 실패한다")
    @Test
    void delete_post_not_found_fail() {
        // given
        User user = createUser(1L);
        userRepository.save(user);

        Post post = Post.builder()
                .content("content")
                .user(user)
                .thumbnailImage("image")
                .build();
        postRepository.save(post);

        em.flush();
        em.clear();

        // when & then
        CustomException e = assertThrows(CustomException.class,
                () -> postService.deletePost(post.getId() + 1));// 존재하지 않는 postId
        assertThat(e.getErrorCode()).isEqualTo(POST_NOT_FOUND);
    }

    @DisplayName("post 삭제 시, 로그인 한 사용자가 작성한 post가 아니면 실패한다")
    @Test
    void delete_not_own_post_fail() throws IOException {
        // given
        User writer = createUser(1L);
        User other = createUser(2L);
        userRepository.save(writer);
        userRepository.save(other);

        PostDto.Request requestDto = PostDto.Request.builder()
                .build();

        String fileName = "tet";
        String contentType = "image/png";
        String filePath = "src/test/resources/img/tet.png";
        MockMultipartFile image
                = new MockMultipartFile("images", fileName, contentType, new FileInputStream(filePath));

        loginUser(writer);

        postService.createPost(requestDto, List.of(image));

        em.flush();
        em.clear();

        loginUser(other);

        // when & then
        Post post = postRepository.findAll().get(0);
        CustomException e = assertThrows(CustomException.class,
                () -> postService.deletePost(post.getId()));
        assertThat(e.getErrorCode()).isEqualTo(NOT_USER_OWN_POST);
    }

    @DisplayName("post 좋아요에 성공한다 - 추가 / 취소")
    @Test
    void add_and_cancel_like_post_success() throws IOException {

        // given
        User user = createUser(1L);
        userRepository.save(user);

        PostDto.Request requestDto = PostDto.Request.builder()
                .build();

        String fileName = "tet";
        String contentType = "image/png";
        String filePath = "src/test/resources/img/tet.png";
        MockMultipartFile image
                = new MockMultipartFile("images", fileName, contentType, new FileInputStream(filePath));

        loginUser(user);

        postService.createPost(requestDto, List.of(image));

        em.flush();
        em.clear();

        // when & then - add
        LikeDto.Response response = postService.like(postRepository.findAll().get(0).getId());
        assertThat(response.getIsLikeAdd()).isTrue();
        assertThat(likeRepository.findAll().size()).isEqualTo(1);

        // when & then - cancel
        LikeDto.Response response2 = postService.like(postRepository.findAll().get(0).getId());
        assertThat(response2.getIsLikeAdd()).isFalse();
        assertThat(likeRepository.findAll().size()).isEqualTo(0);
    }

    @DisplayName("post 좋아요 시 게시글이 존재하지 않으면 실패한다")
    @Test
    void like_post_not_found_fail() {

        // given
        User user = createUser(1L);
        userRepository.save(user);

        loginUser(user);

        em.flush();
        em.clear();

        // when & then
        CustomException e = assertThrows(CustomException.class, () -> postService.like(1L));
        assertThat(e.getErrorCode()).isEqualTo(POST_NOT_FOUND);
    }

    @DisplayName("post 좋아요 시 사용자가 존재하지 않으면 실패한다")
    @Test
    void like_post_user_not_found_fail() throws IOException {

        // given
        User user = createUser(1L);
        userRepository.save(user);

        PostDto.Request requestDto = PostDto.Request.builder()
                .build();

        String fileName = "tet";
        String contentType = "image/png";
        String filePath = "src/test/resources/img/tet.png";
        MockMultipartFile image
                = new MockMultipartFile("images", fileName, contentType, new FileInputStream(filePath));

        loginUser(user);

        postService.createPost(requestDto, List.of(image));

        em.flush();
        em.clear();

        // 로그인 정보 변경
        User user2 = User.builder()
                .id(user.getId() + 10)
                .email("test@test.com")
                .password("test123!")
                .name("test2")
                .nickname("test2")
                .build();
        loginUser(user2);

        // when & then
        CustomException e = assertThrows(CustomException.class,
                () -> postService.like(postRepository.findAll().get(0).getId()));
        assertThat(e.getErrorCode()).isEqualTo(USER_NOT_FOUND);
    }
}
