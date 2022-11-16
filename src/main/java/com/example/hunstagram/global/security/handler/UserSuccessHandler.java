package com.example.hunstagram.global.security.handler;

import com.example.hunstagram.domain.user.entity.User;
import com.example.hunstagram.domain.user.entity.UserRepository;
import com.example.hunstagram.global.security.service.CustomUserDetails;
import com.example.hunstagram.global.security.service.JwtService;
import com.example.hunstagram.global.type.RoleType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.example.hunstagram.global.type.RoleType.USER;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-16
 */
@RequiredArgsConstructor
@Component
public class UserSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("가입된 이메일이 존재하지 않습니다."));

        // accessToken , refreshToken 생성
        String accessToken = jwtService.createAccessToken(user.getEmail(), USER, user.getId());
        String refreshToken = jwtService.createRefreshToken(user.getEmail());
        user.updateRefreshToken(refreshToken);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getWriter(), new TokenResponseDto(accessToken, refreshToken));
    }

    @Getter
    @AllArgsConstructor
    private static class TokenResponseDto {
        private String accessToken;
        private String refreshToken;
    }
}
