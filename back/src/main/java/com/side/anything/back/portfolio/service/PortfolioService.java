package com.side.anything.back.portfolio.service;

import com.side.anything.back.exception.CustomException;
import com.side.anything.back.member.domain.Member;
import com.side.anything.back.member.repository.MemberRepository;
import com.side.anything.back.portfolio.domain.Portfolio;
import com.side.anything.back.portfolio.domain.PortfolioFile;
import com.side.anything.back.portfolio.dto.request.PortfolioSaveRequest;
import com.side.anything.back.portfolio.dto.response.MyPortfolioListResponse;
import com.side.anything.back.portfolio.dto.response.PortfolioDetailResponse;
import com.side.anything.back.portfolio.dto.response.PortfolioListResponse;
import com.side.anything.back.portfolio.dto.response.PortfolioResponse;
import com.side.anything.back.portfolio.repository.PortfolioFileRepository;
import com.side.anything.back.portfolio.repository.PortfolioRepository;
import com.side.anything.back.security.jwt.TokenInfo;
import com.side.anything.back.util.dto.response.FileInfo;
import com.side.anything.back.util.file.FileCategory;
import com.side.anything.back.util.file.FileService;
import com.side.anything.back.util.dto.response.FileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.side.anything.back.exception.BasicExceptionEnum.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortfolioService {

    private static final int SIZE = 5;

    private final PortfolioRepository portfolioRepository;
    private final PortfolioFileRepository portfolioFileRepository;
    private final MemberRepository memberRepository;

    private final FileService fileService;

    // 포트폴리오 저장
    @Transactional
    public Long savePortfolio(final TokenInfo tokenInfo,
                              final PortfolioSaveRequest portfolioSaveRequest, final MultipartFile file) {

        Member findMember = findMemberById(tokenInfo.getId());
        Portfolio savedPortfolio = portfolioRepository.save(Portfolio.of(portfolioSaveRequest, findMember));

        if(file != null) {
            fileService.validatePdfType(file);
            FileInfo fileInfo = fileService.saveFile(file, FileCategory.PORTFOLIO);
            PortfolioFile portfolioFile = PortfolioFile.of(fileInfo, savedPortfolio);
            portfolioFileRepository.save(portfolioFile);
        }

        return savedPortfolio.getId();
    }

    // 포트폴리오 목록 조회
    public PortfolioListResponse findPortfolioList(final TokenInfo tokenInfo, final String keyword, final int page) {

        PageRequest pageRequest = PageRequest.of(page - 1, SIZE, Sort.by(Sort.Direction.DESC, "id"));

        Page<Portfolio> pagedPortfolio = portfolioRepository.findPortfolioList(tokenInfo.getId(), keyword, pageRequest);

        List<PortfolioResponse> portfolioList = pagedPortfolio.getContent().stream()
                .map(PortfolioResponse::new)
                .toList();

        return PortfolioListResponse.builder()
                .portfolioList(portfolioList)
                .totalPages(pagedPortfolio.getTotalPages())
                .build();
    }

    // 내 포트폴리오 목록 조회
    public MyPortfolioListResponse findMyPortfolioList(final TokenInfo tokenInfo, final String keyword, final int page) {

        PageRequest pageRequest = PageRequest.of(page - 1, SIZE, Sort.by(Sort.Direction.DESC, "id"));

        Page<Portfolio> pagedPortfolio = portfolioRepository.findMyPortfolioList(tokenInfo.getId(), keyword, pageRequest);

        return new MyPortfolioListResponse(pagedPortfolio.getContent(), pagedPortfolio.getTotalPages());
    }

    // 포트폴리오 상세 조회
    public PortfolioDetailResponse findPortfolioDetail(final TokenInfo tokenInfo, final Long portfolioId) {

        Portfolio findPortfolio = findPortfolioById(portfolioId);

        if(!findPortfolio.getIsPublic()) {
            if(!findPortfolio.getCreatedBy().equals(tokenInfo.getId())) {
                throw new CustomException(FORBIDDEN, "해당 포트폴리오 조회 권한이 없습니다");
            }
        }

        // 첨부파일
        PortfolioFile findPortfolioFile = findPortfolioFileByPortfolioId(portfolioId);

        return new PortfolioDetailResponse(findPortfolio, findPortfolioFile);
    }

    // 포트폴리오 수정
    @Transactional
    public void updatePortfolio(final TokenInfo tokenInfo, final Long portfolioId,
                                final PortfolioSaveRequest request, final MultipartFile file) {

        Portfolio findPortfolio = findPortfolioById(portfolioId);

        if(!findPortfolio.getCreatedBy().equals(tokenInfo.getId())) {
            throw new CustomException(FORBIDDEN, "포트폴리오 수정은 작성자만 가능합니다");
        }

        findPortfolio.update(request);

        if(file != null) {
            fileService.validatePdfType(file);
            FileInfo fileInfo = fileService.saveFile(file, FileCategory.PORTFOLIO);
            PortfolioFile portfolioFile = PortfolioFile.of(fileInfo, findPortfolio);
            portfolioFileRepository.save(portfolioFile);
        }
    }

    // 포트폴리오 삭제
    @Transactional
    public void deletePortfolio(final TokenInfo tokenInfo, final Long portfolioId) {

        Portfolio findPortfolio = findPortfolioById(portfolioId);

        if(!findPortfolio.getCreatedBy().equals(tokenInfo.getId())) {
            throw new CustomException(FORBIDDEN, "포트폴리오 삭제는 작성자만 가능합니다");
        }

        PortfolioFile findPortfolioFile = findPortfolioFileByPortfolioId(portfolioId);

        if(findPortfolioFile != null) {
            FileInfo fileInfo = new FileInfo(findPortfolioFile);
            portfolioFileRepository.deleteByPortfolioId(portfolioId);
            fileService.deleteFile(FileCategory.PORTFOLIO, fileInfo);
        }

        portfolioRepository.deleteById(portfolioId);
    }

    // 포트폴리오 PDF 파일 조회
    public FileResponse findPortfolioFile(final TokenInfo tokenInfo, final Long portfolioId, final Long portfolioFileId) {

        Portfolio findPortfolio = findPortfolioById(portfolioId);

        if(!findPortfolio.getIsPublic()) {
            if(!findPortfolio.getCreatedBy().equals(tokenInfo.getId())) {
                throw new CustomException(FORBIDDEN, "해당 포트폴리오 조회 권한이 없습니다");
            }
        }

        PortfolioFile findPortfolioFile = findPortfolioFileById(portfolioFileId);

        if(findPortfolioFile == null) {
            throw new CustomException(NOT_FOUND, "존재하지 않는 파일입니다");
        }

        FileInfo fileInfo = new FileInfo(findPortfolioFile);

        return fileService.loadPdf(FileCategory.PORTFOLIO, fileInfo);
    }

    // 포트폴리오 PDF 파일 삭제
    @Transactional
    public void deletePortfolioFile(final TokenInfo tokenInfo, final Long portfolioId, final Long portfolioFileId) {

        Portfolio findPortfolio = findPortfolioById(portfolioId);

        if(!findPortfolio.getIsPublic()) {
            if(!findPortfolio.getCreatedBy().equals(tokenInfo.getId())) {
                throw new CustomException(FORBIDDEN, "해당 포트폴리오 조회 권한이 없습니다");
            }
        }

        PortfolioFile findPortfolioFile = findPortfolioFileById(portfolioFileId);

        if(findPortfolioFile == null) {
            throw new CustomException(NOT_FOUND, "존재하지 않는 파일입니다");
        }

        if(!findPortfolioFile.getCreatedBy().equals(tokenInfo.getId())) {
            throw new CustomException(FORBIDDEN, "해당 파일 접근 권한이 없습니다");
        }

        FileInfo fileInfo = new FileInfo(findPortfolioFile);
        portfolioFileRepository.deleteById(portfolioFileId);
        fileService.deleteFile(FileCategory.PORTFOLIO, fileInfo);
    }

    /* private methods */

    // 포트폴리오 id로 포트폴리오 조회
    private Portfolio findPortfolioById(Long portfolioId) {
        return portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new CustomException(NOT_FOUND, "포트폴리오를 찾을 수 없습니다"));
    }

    // 인증된 회원 id로 회원 조회
    private Member findMemberById(Long memberId) {
        return memberRepository.findByIdAndIsVerifiedTrue(memberId)
                .orElseThrow(() -> new CustomException(NOT_FOUND, "회원 정보를 찾을 수 없습니다"));
    }

    // 포트폴리오 id로 포트폴리오 파일 조회
    private PortfolioFile findPortfolioFileByPortfolioId(Long portfolioId) {
        return portfolioFileRepository.findByPortfolioId(portfolioId)
                .orElse(null);
    }

    // 포트폴리오 파일 id로 포트폴리오 파일 조회
    private PortfolioFile findPortfolioFileById(Long portfolioFileId) {
        return portfolioFileRepository.findById(portfolioFileId)
                .orElse(null);
    }

}
