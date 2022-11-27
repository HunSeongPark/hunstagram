package com.example.hunstagram.domain.user.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.hunstagram.domain.follow.entity.FollowRepository;
import com.example.hunstagram.domain.post.dto.PostDto;
import com.example.hunstagram.domain.post.entity.PostRepository;
import com.example.hunstagram.domain.user.dto.UserDto;
import com.example.hunstagram.domain.user.entity.User;
import com.example.hunstagram.domain.user.entity.UserRepository;
import com.example.hunstagram.global.aws.service.AwsS3Service;
import com.example.hunstagram.global.exception.CustomException;
import com.example.hunstagram.global.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.hunstagram.global.exception.CustomErrorCode.*;
import static com.example.hunstagram.global.security.service.JwtService.*;
import static com.example.hunstagram.global.type.RoleType.USER;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-08
 */
@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final FollowRepository followRepository;
    private final AwsS3Service awsS3Service;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserDto.SignUpResponse signup(UserDto.SignUpRequest requestDto) {
        validateDuplicateEmail(requestDto.getEmail());
        return UserDto.SignUpResponse.fromRequestDto(requestDto);
    }

    private void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(EMAIL_ALREADY_EXIST);
        }
    }

    public void signupInfo(UserDto.SignUpInfoRequest requestDto, MultipartFile image) {
        validateDuplicateEmail(requestDto.getEmail());
        validateDuplicateNickname(requestDto.getNickname());
        requestDto.encodePassword(passwordEncoder.encode(requestDto.getPassword()));
        String profileImage = null;
        if (image != null) {
            profileImage = awsS3Service.uploadImage(image);
        }
        userRepository.save(requestDto.toEntity(profileImage));
    }

    private void validateDuplicateNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(NICKNAME_ALREADY_EXIST);
        }
    }

    public Map<String, String> refresh(String refreshToken) {

        // Refresh Token 유효성 검사
        DecodedJWT decodedJWT = jwtService.verifyToken(refreshToken);

        String email = decodedJWT.getSubject();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(INVALID_TOKEN));

        if (user.getRefreshToken() == null || !user.getRefreshToken().equals(refreshToken)) {
            throw new CustomException(INVALID_TOKEN);
        }

        // refresh token 유효성 검사 완료 후 -> access token 재발급
        Map<String, String> result = new HashMap<>();

        String accessToken = jwtService.createAccessToken(user.getEmail(), USER, user.getId());

        // Refresh Token 만료시간 계산해 1개월 미만일 시 refresh token도 발급
        long diffDays = jwtService.calculateRefreshExpiredDays(decodedJWT);
        if (diffDays < TOKEN_REFRESH_DAYS) {
            String newRefreshToken = jwtService.createRefreshToken(user.getEmail());
            result.put(RT_HEADER, newRefreshToken);
            user.updateRefreshToken(newRefreshToken);
        }

        result.put(AT_HEADER, accessToken);
        return result;
    }

    public void logout() {
        User user = getUserFromJWT();
        user.deleteRefreshToken();
    }

    public UserDto.MyProfileResponse getMyProfile() {
        User user = getUserFromJWT();
        List<PostDto.PostThumbnailResponse> postThumbnails =
                postRepository.findAllByUserId(user.getId())
                        .stream()
                        .map(PostDto.PostThumbnailResponse::fromEntity).toList();

        Integer follower = followRepository.countFolloweeByUserId(user.getId());
        Integer following = followRepository.countFollowingByUserId(user.getId());

        return UserDto.MyProfileResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .postCount(countFormatting(postThumbnails.size()))
                .followerCount(countFormatting(follower))
                .followingCount(countFormatting(following))
                .postThumbnails(postThumbnails)
                .build();
    }

    private User getUserFromJWT() {
        return userRepository.findById(jwtService.getId())
                .orElseThrow(() -> new CustomException(INVALID_TOKEN));
    }

    private String countFormatting(Integer count) {
        if (count < 1000) {
            return count.toString();
        } else if (count < 10000) {
            return String.format("%.2f", count / 1000.0) + "K";
        } else {
            return String.format("%.2f", count / 10000.0) + "M";
        }
    }
}
