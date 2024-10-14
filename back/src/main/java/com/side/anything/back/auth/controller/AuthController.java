package com.side.anything.back.auth.controller;

import com.side.anything.back.auth.dto.request.*;
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

    @PostMapping("/duplicate/username")
    public ResponseEntity<?> isUniqueUsername(@RequestBody @Valid MemberDuplicateCheckRequest request) {

        return ResponseEntity
                .ok()
                .body(authService.isUniqueUsername(request));
    }

    @PostMapping("/duplicate/email")
    public ResponseEntity<?> isUniqueEmail(@RequestBody @Valid MemberDuplicateCheckRequest request) {

        return ResponseEntity
                .ok()
                .body(authService.isUniqueEmail(request));
    }

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid MemberJoinRequest request) {

        authService.join(request);

        return ResponseEntity
                .ok()
                .build();
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestBody @Valid MemberDuplicateCheckRequest request) {

        authService.sendEmail(request);

        return ResponseEntity
                .ok()
                .build();
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody @Valid MemberVerifyRequest request) {

        authService.verify(request);

        return ResponseEntity
                .ok()
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(HttpServletResponse response, @RequestBody @Valid MemberLoginRequest request) {

        return ResponseEntity
                .ok()
                .body(authService.login(response, request));
    }

    @PostMapping("/find/username")
    public ResponseEntity<?> findUsername(@RequestBody @Valid MemberFindUsernameRequest request) {

        return ResponseEntity
                .ok()
                .body(authService.findUsername(request));
    }

    @PostMapping("/find/password")
    public ResponseEntity<?> findPassword(@RequestBody @Valid MemberFindPasswordRequest request) {

        authService.findPassword(request);

        return ResponseEntity
                .ok()
                .build();
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletResponse response, HttpServletRequest request) {

        return ResponseEntity
                .ok()
                .body(authService.reissue(response, request));
    }

    @GetMapping("/login-success")
    public ResponseEntity<?> socialLoginSuccess(HttpServletResponse response, HttpServletRequest request) {

        return ResponseEntity
                .ok()
                .body(authService.socialLoginSuccess(response, request));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {

        authService.logout(request, response);

        return ResponseEntity
                .ok()
                .build();
    }

}
