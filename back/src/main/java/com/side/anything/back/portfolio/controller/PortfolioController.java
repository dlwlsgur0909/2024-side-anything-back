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
    public ResponseEntity<PortfolioDetailResponse> savePortfolio(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                                 @RequestBody @Valid PortfolioSaveRequest request) {

        return ResponseEntity
                .ok(portfolioService.savePortfolio(tokenInfo, request));
    }

    @GetMapping
    public ResponseEntity<List<PortfolioDetailResponse>> findPortfolioList(@AuthenticationPrincipal TokenInfo tokenInfo) {

        return ResponseEntity
                .ok(portfolioService.findPortfolioList(tokenInfo));
    }

    @GetMapping("/{portfolioId}")
    public ResponseEntity<PortfolioDetailResponse> findPortfolioDetail(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                                       @PathVariable Long portfolioId) {

        return ResponseEntity
                .ok(portfolioService.findPortfolioDetail(tokenInfo, portfolioId));
    }

    @PatchMapping("/{portfolioId}")
    public ResponseEntity<PortfolioDetailResponse> updatePortfolio(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                                   @PathVariable Long portfolioId,
                                                                   @RequestBody @Valid PortfolioSaveRequest request) {

        return ResponseEntity
                .ok(portfolioService.updatePortfolio(tokenInfo, portfolioId, request));
    }

}
