package com.example.hunstagram.domain.post.service;

import com.example.hunstagram.domain.hashtag.entity.Hashtag;
import com.example.hunstagram.domain.hashtag.entity.HashtagRepository;
import com.example.hunstagram.domain.like.dto.LikeDto;
import com.example.hunstagram.domain.like.entity.Like;
import com.example.hunstagram.domain.like.entity.LikeRepository;
import com.example.hunstagram.domain.post.dto.PostDto;
import com.example.hunstagram.domain.post.entity.Post;
import com.example.hunstagram.domain.post.entity.PostRepository;
import com.example.hunstagram.domain.postimage.entity.PostImage;
import com.example.hunstagram.domain.postimage.entity.PostImageRepository;
import com.example.hunstagram.domain.user.entity.User;
import com.example.hunstagram.domain.user.entity.UserRepository;
import com.example.hunstagram.global.aws.service.AwsS3Service;
import com.example.hunstagram.global.exception.CustomException;
import com.example.hunstagram.global.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.example.hunstagram.global.exception.CustomErrorCode.*;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-08
 */
@RequiredArgsConstructor
@Transactional
@Service
public class PostService {

    private final JwtService jwtService;
    private final AwsS3Service awsS3Service;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final HashtagRepository hashtagRepository;
    private final PostImageRepository postImageRepository;
    private final LikeRepository likeRepository;

    public void createPost(PostDto.Request requestDto, List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            throw new CustomException(IMAGE_NOT_EXIST);
        }

        User user = userRepository.findById(jwtService.getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        List<String> imagePaths = images.stream().map(awsS3Service::uploadImage).toList();

        // Post 저장
        Post post = Post.builder()
                .user(user)
                .content(
                        (requestDto == null || requestDto.getContent() == null) ? null : requestDto.getContent()
                )
                .thumbnailImage(imagePaths.get(0))
                .build();
        postRepository.save(post);

        // 연관된 Hashtag 저장
        if (requestDto != null && requestDto.getHashtags() != null) {
            List<Hashtag> hashtags = requestDto.getHashtags()
                    .stream().map(h -> new Hashtag(h, post)).toList();
            hashtagRepository.saveAll(hashtags);
        }

        // 연관된 PostImage 저장
        List<PostImage> postImages = imagePaths.stream().map(i -> new PostImage(i, post)).toList();
        postImageRepository.saveAll(postImages);
    }

    public void updatePost(PostDto.Request requestDto, Long postId) {
        Post post = postRepository.findByIdWithHashtagAndUser(postId)
                .orElseThrow(() -> new CustomException(POST_NOT_FOUND));
        Long userId = jwtService.getId();

        // 로그인 한 사용자가 작성한 게시글인지 판단
        if (!Objects.equals(post.getUser().getId(), userId)) {
            throw new CustomException(NOT_USER_OWN_POST);
        }
        hashtagRepository.deleteAll(post.getHashtags());
        post.update(requestDto);
    }

    public void deletePost(Long postId) {
        Post post = postRepository.findByIdWithImageAndUser(postId)
                .orElseThrow(() -> new CustomException(POST_NOT_FOUND));
        Long userId = jwtService.getId();

        // 로그인 한 사용자가 작성한 게시글인지 판단
        if (!Objects.equals(post.getUser().getId(), userId)) {
            throw new CustomException(NOT_USER_OWN_POST);
        }
        post.getPostImages().forEach(i -> awsS3Service.deleteImage(i.getImageUrl()));
        postRepository.delete(post);
    }

    public LikeDto.Response like(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(POST_NOT_FOUND));
        User user = userRepository.findById(jwtService.getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        Optional<Like> like = likeRepository.findByPostAndUserId(post.getId(), user.getId());

        // 좋아요 취소
        if (like.isPresent()) {
            likeRepository.delete(like.get());
            return new LikeDto.Response(false);
        } else {
            // 좋아요 추가
            Like newLike = Like.builder()
                    .user(user)
                    .post(post)
                    .build();
            likeRepository.save(newLike);
            return new LikeDto.Response(true);
        }
    }
}
