package com.side.anything.back.portfolio.service;

import com.side.anything.back.exception.CustomException;
import com.side.anything.back.member.domain.Member;
import com.side.anything.back.member.repository.MemberRepository;
import com.side.anything.back.portfolio.domain.Portfolio;
import com.side.anything.back.portfolio.dto.request.PortfolioSaveRequest;
import com.side.anything.back.portfolio.dto.response.PortfolioDetailResponse;
import com.side.anything.back.portfolio.dto.response.PortfolioListResponse;
import com.side.anything.back.portfolio.dto.response.PortfolioResponse;
import com.side.anything.back.portfolio.repository.PortfolioRepository;
import com.side.anything.back.security.jwt.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.side.anything.back.exception.BasicExceptionEnum.FORBIDDEN;
import static com.side.anything.back.exception.BasicExceptionEnum.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortfolioService {

    private static final int SIZE = 1;

    private final PortfolioRepository portfolioRepository;
    private final MemberRepository memberRepository;

    // 포트폴리오 저장
    @Transactional
    public Long savePortfolio(final TokenInfo tokenInfo, final PortfolioSaveRequest portfolioSaveRequest) {

        Member findMember = findMemberById(tokenInfo.getId());
        Portfolio savedPortfolio = portfolioRepository.save(Portfolio.of(portfolioSaveRequest, findMember));

        return savedPortfolio.getId();
    }

    // 포트폴리오 상세 조회
    public PortfolioDetailResponse findPortfolioDetail(final TokenInfo tokenInfo, final Long portfolioId) {

        Portfolio findPortfolio = findPortfolioById(portfolioId);

        if(!findPortfolio.getIsPublic()) {
            if(!findPortfolio.getMember().getId().equals(tokenInfo.getId())) {
                throw new CustomException(FORBIDDEN, "해당 포트폴리오 조회 권한이 없습니다");
            }
        }

        return new PortfolioDetailResponse(findPortfolio);
    }

    // 내 포트폴리오 목록 조회
    public PortfolioListResponse findPortfolioList(final TokenInfo tokenInfo, final int page) {

        PageRequest pageRequest = PageRequest.of(page - 1, SIZE, Sort.by(Sort.Direction.DESC, "id"));

        Page<Portfolio> pagedPortfolio = portfolioRepository.findAllByMemberId(tokenInfo.getId(), pageRequest);

        List<PortfolioResponse> portfolioList = pagedPortfolio.getContent().stream()
                .map(PortfolioResponse::new)
                .toList();

        return PortfolioListResponse.builder()
                .portfolioList(portfolioList)
                .totalElements(pagedPortfolio.getTotalElements())
                .totalPages(pagedPortfolio.getTotalPages())
                .build();
    }

    // 포트폴리오 수정
    @Transactional
    public void updatePortfolio(final TokenInfo tokenInfo, final Long portfolioId, final PortfolioSaveRequest request) {

        Portfolio findPortfolio = findPortfolioById(portfolioId);

        if(!findPortfolio.getMember().getId().equals(tokenInfo.getId())) {
            throw new CustomException(FORBIDDEN, "포트폴리오 수정은 작성자만 가능합니다");
        }

        findPortfolio.update(request);
    }

    // 포트폴리오 삭제
    @Transactional
    public void deletePortfolio(final TokenInfo tokenInfo, final Long portfolioId) {

        Portfolio findPortfolio = findPortfolioById(portfolioId);

        if(!findPortfolio.getMember().getId().equals(tokenInfo.getId())) {
            throw new CustomException(FORBIDDEN, "포트폴리오 삭제는 작성자만 가능합니다");
        }

        portfolioRepository.deleteById(portfolioId);
    }

    /* private methods */

    // 포트폴리오 id로 조회
    private Portfolio findPortfolioById(Long id) {
        return portfolioRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND, "포트폴리오를 찾을 수 없습니다"));
    }

    // 인증된 회원 id로 조회
    private Member findMemberById(Long id) {
        return memberRepository.findByIdAndIsVerifiedTrue(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND, "회원 정보를 찾을 수 없습니다"));
    }

}
