package com.example.hunstagram.integration.comment;

import com.example.hunstagram.domain.comment.entity.Comment;
import com.example.hunstagram.domain.comment.entity.CommentRepository;
import com.example.hunstagram.domain.comment.service.CommentService;
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
import java.util.Collections;
import java.util.List;

import static com.example.hunstagram.global.exception.CustomErrorCode.COMMENT_NOT_FOUND;
import static com.example.hunstagram.global.exception.CustomErrorCode.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author : Hunseong-Park
 * @date : 2023-01-04
 */
@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class CommentServiceIntegrationTest {

    @Autowired
    CommentService commentService;

    @Autowired
    JwtService jwtService;

    @Autowired
    PostService postService;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

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

    @DisplayName("comment 좋아요에 성공한다 - 추가 / 취소")
    @Test
    void add_and_cancel_like_comment_success() throws IOException {

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

        // SecurityContextHolder에 로그인 정보 저장
        String accessToken = jwtService.createAccessToken(user.getEmail(), RoleType.USER, user.getId());
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        Authentication authToken = new UsernamePasswordAuthenticationToken(user.getEmail(), accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        postService.createPost(requestDto, List.of(image));
        Post post = postRepository.findAll().get(0);

        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .content("test")
                .build();

        commentRepository.save(comment);

        em.flush();
        em.clear();

        // when & then - add
        LikeDto.Response response = commentService.like(commentRepository.findAll().get(0).getId());
        assertThat(response.getIsLikeAdd()).isTrue();
        assertThat(likeRepository.findAll().size()).isEqualTo(1);

        // when & then - cancel
        LikeDto.Response response2 = commentService.like(commentRepository.findAll().get(0).getId());
        assertThat(response2.getIsLikeAdd()).isFalse();
        assertThat(likeRepository.findAll().size()).isEqualTo(0);
    }

    @DisplayName("comment 좋아요 시 댓글이 존재하지 않으면 실패한다")
    @Test
    void like_comment_not_found_fail() {

        // given
        User user = createUser(1L);
        userRepository.save(user);

        PostDto.Request requestDto = PostDto.Request.builder()
                .build();

        // SecurityContextHolder에 로그인 정보 저장
        String accessToken = jwtService.createAccessToken(user.getEmail(), RoleType.USER, user.getId());
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        Authentication authToken = new UsernamePasswordAuthenticationToken(user.getEmail(), accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        em.flush();
        em.clear();

        // when & then
        CustomException e = assertThrows(CustomException.class, () -> commentService.like(1L));
        assertThat(e.getErrorCode()).isEqualTo(COMMENT_NOT_FOUND);
    }

    @DisplayName("comment 좋아요 시 사용자가 존재하지 않으면 실패한다")
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

        // SecurityContextHolder에 로그인 정보 저장
        String accessToken = jwtService.createAccessToken(user.getEmail(), RoleType.USER, user.getId());
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        Authentication authToken = new UsernamePasswordAuthenticationToken(user.getEmail(), accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        postService.createPost(requestDto, List.of(image));
        Post post = postRepository.findAll().get(0);

        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .content("test")
                .build();

        commentRepository.save(comment);

        // 로그인 정보 변경
        String accessToken2 = jwtService.createAccessToken("testa@testa.com", RoleType.USER, 3L);
        List<SimpleGrantedAuthority> authorities2
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        Authentication authToken2 = new UsernamePasswordAuthenticationToken(
                "testa@testa.com", accessToken2, authorities2);
        SecurityContextHolder.getContext().setAuthentication(authToken2);

        em.flush();
        em.clear();

        // when & then
        CustomException e = assertThrows(CustomException.class,
                () -> commentService.like(commentRepository.findAll().get(0).getId()));
        assertThat(e.getErrorCode()).isEqualTo(USER_NOT_FOUND);
    }
}
