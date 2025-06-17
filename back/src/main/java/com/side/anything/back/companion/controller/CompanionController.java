package com.side.anything.back.companion.controller;

import com.side.anything.back.companion.dto.request.CompanionPostSaveRequest;
import com.side.anything.back.companion.dto.response.CompanionPostDetailResponse;
import com.side.anything.back.companion.dto.response.CompanionPostListResponse;
import com.side.anything.back.companion.service.CompanionService;
import com.side.anything.back.security.jwt.TokenInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/companions")
public class CompanionController {

    private final CompanionService companionService;

    @GetMapping
    public ResponseEntity<CompanionPostListResponse> findCompanionPostList(@RequestParam(name = "keyword", defaultValue = "") String keyword,
                                                                           @RequestParam(name = "page", defaultValue = "1") int page) {

        return ResponseEntity
                .ok(companionService.findCompanionPostList(keyword, page));
    }

    @GetMapping("/{companionPostId}")
    public ResponseEntity<CompanionPostDetailResponse> findCompanionPostDetail(@PathVariable Long companionPostId) {

        return ResponseEntity
                .ok(companionService.findCompanionPostDetail(companionPostId));
    }

    @PostMapping
    public ResponseEntity<Void> saveCompanionPost(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                  @RequestBody @Valid CompanionPostSaveRequest request) {

        companionService.saveCompanionPost(tokenInfo, request);

        return ResponseEntity
                .ok()
                .build();
    }
}
