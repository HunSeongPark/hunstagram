package com.example.hunstagram.domain.user.service;

import com.example.hunstagram.domain.user.dto.UserDto;
import com.example.hunstagram.domain.user.entity.UserRepository;
import com.example.hunstagram.global.exception.CustomErrorCode;
import com.example.hunstagram.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.hunstagram.global.exception.CustomErrorCode.*;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-08
 */
@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserDto.SignUpResponse signup(UserDto.SignUpRequest requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new CustomException(EMAIL_ALREADY_EXISTS);
        }
        return UserDto.SignUpResponse.fromRequestDto(requestDto);
    }

    public void signupInfo(UserDto.SignUpInfoRequest requestDto) {
        if (userRepository.existsByNickname(requestDto.getNickname())) {
            throw new CustomException(NICKNAME_ALREADY_EXISTS);
        }
        requestDto.encodePassword(passwordEncoder.encode(requestDto.getPassword()));
        userRepository.save(requestDto.toEntity(null));
    }
}
