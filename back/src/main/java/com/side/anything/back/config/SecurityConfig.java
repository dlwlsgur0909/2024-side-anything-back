package com.side.anything.back.config;

import com.side.anything.back.jwt.JwtFilter;
import com.side.anything.back.jwt.JwtUtil;
import com.side.anything.back.security.handler.CustomAccessDeniedHandler;
import com.side.anything.back.security.handler.CustomAuthEntryPoint;
import com.side.anything.back.security.oauth2.handler.CustomOAuth2FailureHandler;
import com.side.anything.back.security.oauth2.handler.CustomOAuth2SuccessHandler;
import com.side.anything.back.security.oauth2.CustomOAuth2UserService;
import com.side.anything.back.security.oauth2.client.CustomClientRegistrationRepository;
import com.side.anything.back.security.oauth2.client.CustomOAuth2AuthorizedClientService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.time.Duration;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    private final CustomAuthEntryPoint customAuthEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final CustomOAuth2FailureHandler customOAuth2FailureHandler;
    private final CustomClientRegistrationRepository customClientRegistrationRepository;
    private final CustomOAuth2AuthorizedClientService customOAuth2AuthorizedClientService;
    private final JdbcTemplate jdbcTemplate;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .cors(cors ->
                        cors.configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                                CorsConfiguration corsConfiguration = new CorsConfiguration();

                                corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));
                                corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
                                corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
                                corsConfiguration.setMaxAge(Duration.ofHours(1));

//                                corsConfiguration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
//                                corsConfiguration.setExposedHeaders(Collections.singletonList("Access"));
//                                corsConfiguration.setExposedHeaders(Collections.singletonList("Refresh"));
//                                corsConfiguration.setAllowCredentials(true);

                                return corsConfiguration;
                            }
                        }))
                .csrf(AbstractHttpConfigurer::disable) // 세션이 아닌 JWT 사용 -> 세션은 STATELESS -> CSRF disable 처리
                .formLogin(AbstractHttpConfigurer::disable) // form login 사용 X
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP basic 사용 X
                .oauth2Login(oauth2 ->
                        oauth2
                                // 외부 서버 설정 정보를 yml이 아닌 SocialClientRegistration을 사용하기 위해 CustomClientRegistrationRepository 등록
                                .clientRegistrationRepository(customClientRegistrationRepository.clientRegistrationRepository())
                                // 인증 서버에서 발급 받은 Access 토큰과 같은 정보를 인메모리가 아닌 DB 방식으로 저장하기 위해 CustomOAuth2AuthorizedClientService를 등록
                                .authorizedClientService(customOAuth2AuthorizedClientService.oAuth2AuthorizedClientService(jdbcTemplate, customClientRegistrationRepository.clientRegistrationRepository()))
                                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOAuth2UserService)) // 외부 로그인 시 동작할 CustomOAuth2UserService 등록
                                .successHandler(customOAuth2SuccessHandler) // 외부 로그인 성공 시 동작할 CustomOAuth2SuccessHandler 등록
                                .failureHandler(customOAuth2FailureHandler) // 외부 로그인 실패 시 동작할 CustomOAuth2FailureHandler 등록
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT를 사용하기 때문에 세션을 STATELESS 하게
                .exceptionHandling(exception -> exception.authenticationEntryPoint(customAuthEntryPoint))
                .exceptionHandling(exception -> exception.accessDeniedHandler(customAccessDeniedHandler))
                .authorizeHttpRequests(request ->
                    request
                            .requestMatchers("/auth/**").permitAll()
                            .requestMatchers("/admin/**").hasRole("ADMIN")
                            .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
