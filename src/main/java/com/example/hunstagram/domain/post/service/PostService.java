package com.example.hunstagram.domain.post.service;

import com.example.hunstagram.domain.hashtag.entity.Hashtag;
import com.example.hunstagram.domain.hashtag.entity.HashtagRepository;
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

import static com.example.hunstagram.global.exception.CustomErrorCode.IMAGE_NOT_EXIST;
import static com.example.hunstagram.global.exception.CustomErrorCode.USER_NOT_FOUND;

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
}
