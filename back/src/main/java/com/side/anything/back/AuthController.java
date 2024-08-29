package com.side.anything.back;

import com.side.anything.back.member.dto.request.MemberJoinRequest;
import com.side.anything.back.member.service.MemberService;
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

    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody MemberJoinRequest request) {

        return ResponseEntity
                .ok()
                .body(memberService.join(request));
    }

}
