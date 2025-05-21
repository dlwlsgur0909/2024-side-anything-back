package com.side.anything.back.portfolio.controller;

import com.side.anything.back.portfolio.dto.request.PortfolioSaveRequest;
import com.side.anything.back.portfolio.dto.response.PortfolioDetailResponse;
import com.side.anything.back.portfolio.service.PortfolioService;
import com.side.anything.back.security.jwt.TokenInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    // 포트폴리오 저장 API
    @PostMapping
    public ResponseEntity<Long> savePortfolio(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                                 @RequestBody @Valid PortfolioSaveRequest request) {

        return ResponseEntity
                .ok(portfolioService.savePortfolio(tokenInfo, request));
    }

    // 포트폴리오 목록 조회 API
    @GetMapping
    public ResponseEntity<List<PortfolioDetailResponse>> findPortfolioList(@AuthenticationPrincipal TokenInfo tokenInfo) {

        return ResponseEntity
                .ok(portfolioService.findPortfolioList(tokenInfo));
    }

    // 포트폴리오 단건 조회 API
    @GetMapping("/{portfolioId}")
    public ResponseEntity<PortfolioDetailResponse> findPortfolioDetail(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                                       @PathVariable Long portfolioId) {

        return ResponseEntity
                .ok(portfolioService.findPortfolioDetail(tokenInfo, portfolioId));
    }

    // 포트폴리오 수정 API
    @PatchMapping("/{portfolioId}")
    public ResponseEntity<Void> updatePortfolio(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                                   @PathVariable Long portfolioId,
                                                                   @RequestBody @Valid PortfolioSaveRequest request) {

        portfolioService.updatePortfolio(tokenInfo, portfolioId, request);

        return ResponseEntity
                .ok()
                .build();
    }

    // 포트폴리오 삭제 API
    @DeleteMapping("/{portfolioId}")
    public ResponseEntity<Void> deletePortfolio(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                @PathVariable Long portfolioId) {

        portfolioService.deletePortfolio(tokenInfo, portfolioId);

        return ResponseEntity
                .ok()
                .build();
    }

}
