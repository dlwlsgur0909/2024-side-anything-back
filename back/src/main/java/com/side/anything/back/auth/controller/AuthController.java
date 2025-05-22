package com.side.anything.back.auth.controller;

import com.side.anything.back.auth.dto.request.*;
import com.side.anything.back.auth.dto.response.LoginResponse;
import com.side.anything.back.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    // 아이디 중복 확인 API
    @PostMapping("/duplicate/username")
    public ResponseEntity<Boolean> isUniqueUsername(@RequestBody @Valid DuplicateCheckRequest request) {

        return ResponseEntity
                .ok(authService.isUniqueUsername(request));
    }

    // 이메일 중복 확인 API
    @PostMapping("/duplicate/email")
    public ResponseEntity<Boolean> isUniqueEmail(@RequestBody @Valid DuplicateCheckRequest request) {

        return ResponseEntity
                .ok(authService.isUniqueEmail(request));
    }

    // 회원가입 API
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid JoinRequest request) {

        authService.join(request);

        return ResponseEntity
                .ok()
                .build();
    }

    // 인증 메일 재발송 API
    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestBody @Valid DuplicateCheckRequest request) {

        authService.sendEmail(request);

        return ResponseEntity
                .ok()
                .build();
    }

    // 인증 API
    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody @Valid VerifyRequest request) {

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

    @PostMapping("/login-success")
    public ResponseEntity<?> socialLoginSuccess(HttpServletResponse response, HttpServletRequest request) {

        return ResponseEntity
                .ok()
                .body(authService.socialLoginSuccess(response, request));
    }

    // 로그아웃 API
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {

        authService.logout(request, response);

        return ResponseEntity
                .ok()
                .build();
    }

}
