package com.example.hunstagram.global.security;

import com.example.hunstagram.global.security.filter.CustomAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-13
 */
@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationFailureHandler failureHandler;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authManagerBuilder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        CustomAuthenticationFilter authenticationFilter
                = new CustomAuthenticationFilter(authManagerBuilder.getOrBuild());
        // 로그인 인증 필터
        authenticationFilter.setFilterProcessesUrl("/v1/users/login");
        authenticationFilter.setAuthenticationSuccessHandler(successHandler);
        authenticationFilter.setAuthenticationFailureHandler(failureHandler);

        http.csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(STATELESS) // Using JWT
                .and()
                .addFilter(authenticationFilter)
                .authenticationProvider(authenticationProvider)
                .authorizeRequests()
                .anyRequest().permitAll();

        return http.build();
    }
}
