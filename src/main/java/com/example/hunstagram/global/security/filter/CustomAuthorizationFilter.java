package com.example.hunstagram.global.security.filter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.hunstagram.global.exception.CustomErrorCode;
import com.example.hunstagram.global.exception.ErrorResponse;
import com.example.hunstagram.global.security.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.example.hunstagram.global.security.service.JwtService.TOKEN_HEADER_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-20
 */
@RequiredArgsConstructor
@Component
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return !(
                pathMatcher.match("/v1/users/logout", path) ||
                        pathMatcher.match("/v1/follow/{toUserId:\\d+}", path)
        );
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        CustomErrorCode errorCode = null;

        // Header에 토큰이 존재하지 않을 시
        if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_HEADER_PREFIX)) {
            errorCode = CustomErrorCode.TOKEN_NOT_FOUND;
        } else {
            try {
                String accessToken = authorizationHeader.substring(TOKEN_HEADER_PREFIX.length());
                DecodedJWT decodedJWT = jwtService.verifyToken(accessToken);

                String role = decodedJWT.getClaim("role").asString();
                List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
                String email = decodedJWT.getSubject();

                // SecurityContextHolder에 accessToken 포함하여 저장
                Authentication authToken = new UsernamePasswordAuthenticationToken(email, accessToken, authorities);
                SecurityContextHolder.getContext().setAuthentication(authToken);
                filterChain.doFilter(request, response);
            } catch (TokenExpiredException e) {
                // Access Token 만료
                errorCode = CustomErrorCode.ACCESS_TOKEN_EXPIRED;
            } catch (Exception e) {
                // 유효하지 않은 Access Token
                errorCode = CustomErrorCode.INVALID_TOKEN;
            }
        }

        if (errorCode != null) {
            response.setStatus(errorCode.getHttpStatus().value());
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("utf-8");
            ErrorResponse errorResponse = new ErrorResponse(errorCode);
            new ObjectMapper().writeValue(response.getWriter(), errorResponse);
        }
    }
}
