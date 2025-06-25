package com.side.anything.back.companion.controller;

import com.side.anything.back.companion.dto.request.CompanionApplicationSaveRequest;
import com.side.anything.back.companion.dto.request.CompanionApplicationUpdateStatusRequest;
import com.side.anything.back.companion.dto.request.CompanionPostSaveRequest;
import com.side.anything.back.companion.dto.response.CompanionApplicationListResponse;
import com.side.anything.back.companion.dto.response.CompanionPostDetailResponse;
import com.side.anything.back.companion.dto.response.CompanionPostListResponse;
import com.side.anything.back.companion.dto.response.MyCompanionPostDetailResponse;
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
    public ResponseEntity<CompanionPostListResponse> findCompanionPostList(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                                           @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                                                           @RequestParam(name = "page", defaultValue = "1") int page) {

        return ResponseEntity
                .ok(companionService.findCompanionPostList(tokenInfo, keyword, page));
    }

    // 동행 모집 단건 조회
    @GetMapping("/{postId}")
    public ResponseEntity<CompanionPostDetailResponse> findCompanionPostDetail(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                                               @PathVariable Long postId) {

        return ResponseEntity
                .ok(companionService.findCompanionPostDetail(tokenInfo, postId));
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
    @PatchMapping("/{postId}")
    public ResponseEntity<Void> closeCompanionPost(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                   @PathVariable Long postId) {

        companionService.closeCompanionPost(tokenInfo, postId);

        return ResponseEntity
                .ok()
                .build();
    }

    // 동행 모집 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deleteCompanionPost(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                    @PathVariable Long postId) {

        companionService.deleteCompanionPost(tokenInfo, postId);

        return ResponseEntity
                .ok()
                .build();
    }

    // 동행 신청
    @PostMapping("/{postId}/applications")
    public ResponseEntity<Void> saveCompanionApplication(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                         @PathVariable Long postId,
                                                         @RequestBody @Valid CompanionApplicationSaveRequest request) {

        companionService.saveCompanionApplication(tokenInfo, postId, request);

        return ResponseEntity
                .ok()
                .build();
    }

    // 동행 신청 승인/거절
    @PatchMapping("/{postId}/applications/{applicationId}")
    public ResponseEntity<Void> updateCompanionApplicationStatus(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                                 @PathVariable Long postId,
                                                                 @PathVariable Long applicationId,
                                                                 @RequestBody @Valid CompanionApplicationUpdateStatusRequest request) {

        companionService.updateCompanionApplicationStatus(tokenInfo, postId, applicationId, request);

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

    // 내 동행 모집 상세
    @GetMapping("/my-posts/{postId}")
    public ResponseEntity<MyCompanionPostDetailResponse> findMyCompanionPostDetail(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                                                   @PathVariable Long postId) {

        return ResponseEntity
                .ok(myCompanionService.findMyCompanionPostDetail(tokenInfo, postId));
    }

    // 내 동행 신청 목록
    @GetMapping("/my-applications")
    public ResponseEntity<CompanionApplicationListResponse> findMyCompanionApplicationList(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                                                           @RequestParam(name = "page", defaultValue = "1") int page) {

        return ResponseEntity
                .ok(myCompanionService.findMyCompanionApplicationList(tokenInfo, page));
    }

    // 내 동행 신청 취소
    @PatchMapping("/my-applications/{applicationId}")
    public ResponseEntity<Void> cancelMyCompanionApplication(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                             @PathVariable Long applicationId) {

        myCompanionService.cancelMyCompanionApplication(tokenInfo, applicationId);

        return ResponseEntity
                .ok()
                .build();
    }

    // 동행 신청 삭제
    @DeleteMapping("/my-applications/{applicationId}")
    public ResponseEntity<Void> deleteMyCompanionApplication(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                             @PathVariable Long applicationId) {

        myCompanionService.deleteMyCompanionApplication(tokenInfo, applicationId);

        return ResponseEntity
                .ok()
                .build();
    }

}
