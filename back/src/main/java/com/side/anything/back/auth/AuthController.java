package com.side.anything.back.auth;

import com.side.anything.back.jwt.TokenInfo;
import com.side.anything.back.member.dto.request.MemberDuplicateCheckRequest;
import com.side.anything.back.member.dto.request.MemberJoinRequest;
import com.side.anything.back.member.dto.request.MemberLoginRequest;
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

    @PostMapping("/duplicate")
    public ResponseEntity<?> isUnique(@RequestBody MemberDuplicateCheckRequest request) {

        return ResponseEntity
                .ok()
                .body(memberService.isUnique(request));
    }

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody MemberJoinRequest request) {

        return ResponseEntity
                .ok()
                .body(memberService.join(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberLoginRequest request) {

        return ResponseEntity
                .ok()
                .body(memberService.login(request));
    }

}
