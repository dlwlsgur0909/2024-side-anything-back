package com.side.anything.back.portfolio.controller;

import com.side.anything.back.portfolio.dto.request.PortfolioSaveRequest;
import com.side.anything.back.portfolio.dto.response.MyPortfolioListResponse;
import com.side.anything.back.portfolio.dto.response.PortfolioDetailResponse;
import com.side.anything.back.portfolio.dto.response.PortfolioListResponse;
import com.side.anything.back.portfolio.service.PortfolioService;
import com.side.anything.back.security.jwt.TokenInfo;
import com.side.anything.back.util.dto.response.FileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    // 포트폴리오 저장 API
    @PostMapping
    public ResponseEntity<Long> savePortfolio(@AuthenticationPrincipal TokenInfo tokenInfo,
                                              @RequestPart(name = "request") @Valid PortfolioSaveRequest request,
                                              @RequestPart(name = "file", required = false) MultipartFile file) {

        return ResponseEntity
                .ok(portfolioService.savePortfolio(tokenInfo, request, file));
    }

    // 포트폴리오 목록 조회 API
    @GetMapping
    public ResponseEntity<PortfolioListResponse> findPortfolioList(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                                   @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                                                   @RequestParam(name = "page", defaultValue = "1") int page) {

        return ResponseEntity
                .ok(portfolioService.findPortfolioList(tokenInfo, keyword, page));
    }

    // 내 포트폴리오 목록 조회 API
    @GetMapping("/me")
    public ResponseEntity<MyPortfolioListResponse> findMyPortfolioList(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                                       @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                                                       @RequestParam(name = "page", defaultValue = "1") int page) {

        return ResponseEntity
                .ok(portfolioService.findMyPortfolioList(tokenInfo, keyword, page));
    }

    // 포트폴리오 상세 조회 API
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
                                                @RequestPart(name = "request") @Valid PortfolioSaveRequest request,
                                                @RequestPart(name = "file", required = false) MultipartFile file) {

        portfolioService.updatePortfolio(tokenInfo, portfolioId, request, file);

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

    // 포트폴리오 PDF 파일 조회 API
    @GetMapping("/{portfolioId}/files/{portfolioFileId}")
    public ResponseEntity<Resource> findPortfolioFile(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                      @PathVariable Long portfolioId,
                                                      @PathVariable Long portfolioFileId) {

        FileResponse fileResponse = portfolioService.findPortfolioFile(tokenInfo, portfolioId, portfolioFileId);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, fileResponse.getContentDisposition())
                .body(fileResponse.getResource());
    }

    // 포트폴리오 PDF 파일 삭제 API
    @DeleteMapping("/{portfolioId}/files/{portfolioFileId}")
    public ResponseEntity<Void> deletePortfolioFile(@AuthenticationPrincipal TokenInfo tokenInfo,
                                                    @PathVariable Long portfolioId,
                                                    @PathVariable Long portfolioFileId) {

        portfolioService.deletePortfolioFile(tokenInfo, portfolioId, portfolioFileId);

        return ResponseEntity
                .ok()
                .build();
    }


}
