package com.example.hunstagram.domain.user.service;

import com.example.hunstagram.domain.user.dto.UserDto;
import com.example.hunstagram.domain.user.entity.UserRepository;
import com.example.hunstagram.global.aws.service.AwsS3Service;
import com.example.hunstagram.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.example.hunstagram.global.exception.CustomErrorCode.EMAIL_ALREADY_EXISTS;
import static com.example.hunstagram.global.exception.CustomErrorCode.NICKNAME_ALREADY_EXISTS;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-08
 */
@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final AwsS3Service awsS3Service;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserDto.SignUpResponse signup(UserDto.SignUpRequest requestDto) {
        validateDuplicateEmail(requestDto.getEmail());
        return UserDto.SignUpResponse.fromRequestDto(requestDto);
    }

    private void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(EMAIL_ALREADY_EXISTS);
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
            throw new CustomException(NICKNAME_ALREADY_EXISTS);
        }
    }
}
