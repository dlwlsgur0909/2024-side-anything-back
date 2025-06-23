package com.side.anything.back.companion.controller;

import com.side.anything.back.companion.dto.request.CompanionApplicationSaveRequest;
import com.side.anything.back.companion.dto.request.CompanionPostSaveRequest;
import com.side.anything.back.companion.dto.response.CompanionApplicationListResponse;
import com.side.anything.back.companion.dto.response.CompanionPostDetailResponse;
import com.side.anything.back.companion.dto.response.CompanionPostListResponse;
import com.side.anything.back.companion.service.CompanionService;
import com.side.anything.back.companion.service.MyCompanionService;
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
    private final MyCompanionService myCompanionService;

    // 동행 모집 목록 조회
    @GetMapping
    public ResponseEntity<CompanionPostListResponse> findCompanionPostList(@RequestParam(name = "keyword", defaultValue = "") String keyword,
                                                                           @RequestParam(name = "page", defaultValue = "1") int page) {

        return ResponseEntity
                .ok(companionService.findCompanionPostList(keyword, page));
    }

    // 동행 모집 단건 조회
    @GetMapping("/{companionPostId}")
    public ResponseEntity<CompanionPostDetailResponse> findCompanionPostDetail(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                                               @PathVariable Long companionPostId) {

        return ResponseEntity
                .ok(companionService.findCompanionPostDetail(tokenInfo, companionPostId));
    }

    // 동행 모집 저장
    @PostMapping
    public ResponseEntity<Void> saveCompanionPost(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                  @RequestBody @Valid CompanionPostSaveRequest request) {

        companionService.saveCompanionPost(tokenInfo, request);

        return ResponseEntity
                .ok()
                .build();
    }

    // 동행 모집 마감
    @PatchMapping("/{companionPostId}")
    public ResponseEntity<Void> closeCompanionPost(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                   @PathVariable Long companionPostId) {

        companionService.closeCompanionPost(tokenInfo, companionPostId);

        return ResponseEntity
                .ok()
                .build();
    }

    // 동행 모집 삭제
    @DeleteMapping("/{companionPostId}")
    public ResponseEntity<Void> deleteCompanionPost(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                    @PathVariable Long companionPostId) {

        companionService.deleteCompanionPost(tokenInfo, companionPostId);

        return ResponseEntity
                .ok()
                .build();
    }

    // 동행 신청
    @PostMapping("/{companionPostId}/application")
    public ResponseEntity<Void> saveCompanionApplication(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                         @PathVariable Long companionPostId,
                                                         @RequestBody @Valid CompanionApplicationSaveRequest request) {

        companionService.saveCompanionApplication(tokenInfo, companionPostId, request);

        return ResponseEntity
                .ok()
                .build();
    }

    // 내 동행 모집 목록
    @GetMapping("/my-posts")
    public ResponseEntity<CompanionPostListResponse> findMyCompanionPostList(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                                             @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                                                             @RequestParam(name = "page", defaultValue = "1") int page) {

        return ResponseEntity
                .ok(myCompanionService.findMyCompanionPostList(tokenInfo, keyword, page));
    }

    // 내 동행 신청 목록
    @GetMapping("/my-applications")
    public ResponseEntity<CompanionApplicationListResponse> findMyCompanionApplicationList(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                                                           @RequestParam(name = "page", defaultValue = "1") int page) {

        return ResponseEntity
                .ok(myCompanionService.findMyCompanionApplicationList(tokenInfo, page));
    }

}
