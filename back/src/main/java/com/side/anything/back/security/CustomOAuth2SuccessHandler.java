package com.side.anything.back.security;

import com.side.anything.back.jwt.JwtUtil;
import com.side.anything.back.jwt.TokenInfo;
import com.side.anything.back.member.domain.Role;
import com.side.anything.back.security.dto.response.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();

        Long id = principal.getId();
        String username = principal.getUsername();
        String name = principal.getName();
        String role = principal.getAuthorities().iterator().next().getAuthority();

        TokenInfo tokenInfo = TokenInfo.builder()
                .id(id)
                .username(username)
                .name(name)
                .role(Role.valueOf(role))
                .build();

        String accessToken = jwtUtil.createAccessToken(tokenInfo);

        response.addCookie(createCookie("Access", accessToken));
        response.sendRedirect("http://localhost:5173/login-success");
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60);
        // HTTPS에서만 동작하도록 하는 설정
//        cookie.setSecure(true);
        cookie.setPath("/auth");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
