package com.side.anything.back.auth;

import com.side.anything.back.jwt.TokenInfo;
import com.side.anything.back.member.dto.request.*;
import com.side.anything.back.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final MemberService memberService;

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
                .body(memberService.isUniqueUsername(request));
    }

    @PostMapping("/duplicate/email")
    public ResponseEntity<?> isUniqueEmail(@RequestBody MemberDuplicateCheckRequest request) {

        return ResponseEntity
                .ok()
                .body(memberService.isUniqueEmail(request));
    }

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody MemberJoinRequest request) {

        memberService.join(request);

        return ResponseEntity
                .ok()
                .build();
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestBody MemberDuplicateCheckRequest request) {

        memberService.sendEmail(request);

        return ResponseEntity
                .ok()
                .build();
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody MemberVerifyRequest request) {

        memberService.verify(request);

        return ResponseEntity
                .ok()
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberLoginRequest request) {

        return ResponseEntity
                .ok()
                .body(memberService.login(request));
    }

    @PostMapping("/find/username")
    public ResponseEntity<?> findUsername(@RequestBody MemberFindUsernameRequest request) {

        return ResponseEntity
                .ok()
                .body(memberService.findUsername(request));
    }

    @PostMapping("/find/password")
    public ResponseEntity<?> findPassword(@RequestBody MemberFindPasswordRequest request) {

        memberService.findPassword(request);

        return ResponseEntity
                .ok()
                .build();
    }

}
