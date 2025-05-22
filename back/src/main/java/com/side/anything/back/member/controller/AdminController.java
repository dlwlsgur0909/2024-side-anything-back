package com.side.anything.back.member.controller;

import com.side.anything.back.security.jwt.TokenInfo;
import com.side.anything.back.member.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<?> getAdmin(@AuthenticationPrincipal TokenInfo tokenInfo) {

        return ResponseEntity
                .ok()
                .body(adminService.findAdmin(tokenInfo));
    }

    @GetMapping("/members")
    public ResponseEntity<?> findMemberList() {

        return ResponseEntity
                .ok()
                .body(adminService.findMemberList());
    }
}
