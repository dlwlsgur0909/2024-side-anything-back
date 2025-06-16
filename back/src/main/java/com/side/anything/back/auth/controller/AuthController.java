package com.side.anything.back.auth.controller;

import com.side.anything.back.auth.dto.request.*;
import com.side.anything.back.auth.dto.response.LoginResponse;
import com.side.anything.back.auth.dto.response.SocialJoinResponse;
import com.side.anything.back.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    // 회원가입 API
    @PostMapping("/join")
    public ResponseEntity<Void> join(@RequestBody @Valid JoinRequest request) {

        authService.join(request);

        return ResponseEntity
                .ok()
                .build();
    }

    // 인증 메일 재발송 API
    @PostMapping("/send")
    public ResponseEntity<Void> sendEmail(@RequestBody @Valid EmailRequest request) {

        authService.sendEmail(request);

        return ResponseEntity
                .ok()
                .build();
    }

    // 인증 API
    @PostMapping("/verify")
    public ResponseEntity<Void> verify(@RequestBody @Valid VerifyRequest request) {

        authService.verify(request);

        return ResponseEntity
                .ok()
                .build();
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(HttpServletResponse response, @RequestBody @Valid LoginRequest request) {

        return ResponseEntity
                .ok(authService.login(response, request));
    }

    // 아이디 찾기 API
    @PostMapping("/find/username")
    public ResponseEntity<String> findUsername(@RequestBody @Valid FindUsernameRequest request) {

        return ResponseEntity
                .ok(authService.findUsername(request));
    }

    // 비밀번호 찾기 API
    @PostMapping("/find/password")
    public ResponseEntity<?> findPassword(@RequestBody @Valid FindPasswordRequest request) {

        authService.findPassword(request);

        return ResponseEntity
                .ok()
                .build();
    }

    // 토큰 재발급 API
    @PostMapping("/reissue")
    public ResponseEntity<LoginResponse> reissue(HttpServletResponse response, HttpServletRequest request) {

        return ResponseEntity
                .ok(authService.reissue(response, request));
    }

    // 소셜 로그인 회원가입
    @PatchMapping("/social-join")
    public ResponseEntity<LoginResponse> socialJoin(HttpServletResponse response, HttpServletRequest request,
                                                    @RequestBody @Valid SocialJoinRequest socialJoinRequest) {

        return ResponseEntity
                .ok(authService.socialJoin(response, request, socialJoinRequest));
    }

    // 소셜 로그인 성공
    @PostMapping("/login-success")
    public ResponseEntity<LoginResponse> socialLoginSuccess(HttpServletResponse response, HttpServletRequest request) {

        return ResponseEntity
                .ok()
                .body(authService.socialLoginSuccess(response, request));
    }

    // 로그아웃 API
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {

        authService.logout(request, response);

        return ResponseEntity
                .ok()
                .build();
    }

}
