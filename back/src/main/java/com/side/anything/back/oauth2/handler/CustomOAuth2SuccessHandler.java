package com.side.anything.back.oauth2.handler;

import com.side.anything.back.security.jwt.JwtUtil;
import com.side.anything.back.security.jwt.TokenInfo;
import com.side.anything.back.oauth2.dto.response.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler { // OAuth2 로그인이 성공하면 실행될 성공 핸들러

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();

        TokenInfo tokenInfo = new TokenInfo(principal);
        String accessToken = jwtUtil.createAccessToken(tokenInfo);

        String cookiePath = "/auth/login-success";
        String frontUrl = "http://localhost:5173/login-success";

        response.addCookie(createCookie("Access", accessToken, cookiePath));
        response.sendRedirect(frontUrl); // 성공 시 redirect할 프론트 url
    }

    private Cookie createCookie(String key, String value, String cookiePath) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60);
        // HTTPS에서만 동작하도록 하는 설정
//        cookie.setSecure(true);
        // 지정한 경로에 대해서만 Access 쿠키 허용
        cookie.setPath(cookiePath);
        cookie.setHttpOnly(true);

        return cookie;
    }

}
