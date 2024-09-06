package com.side.anything.back.member.controller;

import com.side.anything.back.jwt.TokenInfo;
import com.side.anything.back.member.dto.request.MemberChangePasswordRequest;
import com.side.anything.back.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/{username}")
    public ResponseEntity<?> memberDetail(@AuthenticationPrincipal TokenInfo tokenInfo,
                                          @PathVariable String username) {

        return ResponseEntity
                .ok()
                .body(memberService.memberDetail(tokenInfo, username));
    }

    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal TokenInfo tokenInfo,
                                            @RequestBody MemberChangePasswordRequest request) {

        memberService.changePassword(tokenInfo, request);

        return ResponseEntity
                .ok()
                .build();
    }



}
