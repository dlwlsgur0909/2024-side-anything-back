package com.side.anything.back.portfolio.service;

import com.side.anything.back.exception.CustomException;
import com.side.anything.back.member.domain.Member;
import com.side.anything.back.member.repository.MemberRepository;
import com.side.anything.back.portfolio.domain.Portfolio;
import com.side.anything.back.portfolio.dto.request.PortfolioSaveRequest;
import com.side.anything.back.portfolio.dto.response.PortfolioDetailResponse;
import com.side.anything.back.portfolio.repository.PortfolioRepository;
import com.side.anything.back.security.jwt.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.side.anything.back.exception.BasicExceptionEnum.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final MemberRepository memberRepository;

    // 포트폴리오 저장
    @Transactional
    public PortfolioDetailResponse savePortfolio(final TokenInfo tokenInfo, final PortfolioSaveRequest portfolioSaveRequest) {

        Member findMember = findMemberById(tokenInfo.getId());
        Portfolio savedPortfolio = portfolioRepository.save(Portfolio.of(portfolioSaveRequest, findMember));

        return new PortfolioDetailResponse(savedPortfolio);
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

    // 회원의 포트폴리오 목록 조회
    public List<PortfolioDetailResponse> findPortfolioList(final TokenInfo tokenInfo) {

        return portfolioRepository.findAllByMemberId(tokenInfo.getId())
                .stream()
                .map(PortfolioDetailResponse::new)
                .toList();
    }

    private Portfolio findPortfolioById(Long id) {
        return portfolioRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND, "포트폴리오를 찾을 수 없습니다"));
    }

    private Member findMemberById(Long id) {
        return memberRepository.findByIdAndVerifiedTrue(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND, "회원 정보를 찾을 수 없습니다"));
    }

}
