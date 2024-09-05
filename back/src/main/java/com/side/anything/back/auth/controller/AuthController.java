package com.side.anything.back.auth.controller;

import com.side.anything.back.auth.dto.request.*;
import com.side.anything.back.auth.service.AuthService;
import com.side.anything.back.jwt.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping
    public ResponseEntity<?> home(@AuthenticationPrincipal TokenInfo tokenInfo) {

        return ResponseEntity
                .ok()
                .build();
    }

    @PostMapping("/duplicate/username")
    public ResponseEntity<?> isUniqueUsername(@RequestBody MemberDuplicateCheckRequest request) {

        return ResponseEntity
                .ok()
                .body(authService.isUniqueUsername(request));
    }

    @PostMapping("/duplicate/email")
    public ResponseEntity<?> isUniqueEmail(@RequestBody MemberDuplicateCheckRequest request) {

        return ResponseEntity
                .ok()
                .body(authService.isUniqueEmail(request));
    }

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody MemberJoinRequest request) {

        authService.join(request);

        return ResponseEntity
                .ok()
                .build();
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestBody MemberDuplicateCheckRequest request) {

        authService.sendEmail(request);

        return ResponseEntity
                .ok()
                .build();
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody MemberVerifyRequest request) {

        authService.verify(request);

        return ResponseEntity
                .ok()
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberLoginRequest request) {

        return ResponseEntity
                .ok()
                .body(authService.login(request));
    }

    @PostMapping("/find/username")
    public ResponseEntity<?> findUsername(@RequestBody MemberFindUsernameRequest request) {

        return ResponseEntity
                .ok()
                .body(authService.findUsername(request));
    }

    @PostMapping("/find/password")
    public ResponseEntity<?> findPassword(@RequestBody MemberFindPasswordRequest request) {

        authService.findPassword(request);

        return ResponseEntity
                .ok()
                .build();
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody ReissueRequest request) {

        return ResponseEntity
                .ok()
                .body(authService.reissue(request));
    }

}
