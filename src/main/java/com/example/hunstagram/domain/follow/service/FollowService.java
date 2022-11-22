package com.example.hunstagram.domain.follow.service;

import com.example.hunstagram.domain.follow.dto.FollowDto;
import com.example.hunstagram.domain.follow.entity.Follow;
import com.example.hunstagram.domain.follow.entity.FollowRepository;
import com.example.hunstagram.domain.user.entity.User;
import com.example.hunstagram.domain.user.entity.UserRepository;
import com.example.hunstagram.global.exception.CustomException;
import com.example.hunstagram.global.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.hunstagram.global.exception.CustomErrorCode.USER_NOT_FOUND;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-08
 */
@RequiredArgsConstructor
@Transactional
@Service
public class FollowService {

    private final JwtService jwtService;
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowDto.FollowResponse follow(Long toUserId) {
        Long fromUserId = jwtService.getId();
        Follow follow = followRepository.findByFromAndToUserId(fromUserId, toUserId)
                .orElse(null);

        // 팔로우 추가
        if (follow == null) {
            User fromUser = userRepository.findById(fromUserId)
                    .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
            User toUser = userRepository.findById(toUserId)
                    .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

            follow = Follow.builder()
                    .fromUser(fromUser)
                    .toUser(toUser)
                    .build();
            followRepository.save(follow);
            return new FollowDto.FollowResponse(true);
        } else {
            // 팔로우 취소
            followRepository.delete(follow);
            return new FollowDto.FollowResponse(false);
        }
    }

    @Transactional(readOnly = true)
    public Page<FollowDto.FollowListResponse> getFolloweeList(Pageable pageable, Long userId) {
        validateUserExists(userId);
        return followRepository.findFolloweeList(pageable, userId)
                .map(f -> FollowDto.FollowListResponse.fromEntity(f.getFromUser()));
    }

    public Page<FollowDto.FollowListResponse> getFollowingList(Pageable pageable, Long userId) {
        validateUserExists(userId);
        return followRepository.findFollowingList(pageable, userId)
                .map(f -> FollowDto.FollowListResponse.fromEntity(f.getToUser()));
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException(USER_NOT_FOUND);
        }
    }
}
