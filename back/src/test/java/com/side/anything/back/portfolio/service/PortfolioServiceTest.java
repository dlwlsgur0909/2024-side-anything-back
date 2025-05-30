package com.side.anything.back.portfolio.service;

import com.side.anything.back.member.domain.Member;
import com.side.anything.back.member.domain.Role;
import com.side.anything.back.member.repository.MemberRepository;
import com.side.anything.back.portfolio.dto.request.PortfolioSaveRequest;
import com.side.anything.back.portfolio.dto.response.PortfolioDetailResponse;
import com.side.anything.back.security.jwt.TokenInfo;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class PortfolioServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PortfolioService portfolioService;

    @Autowired
    EntityManager em;

    @BeforeEach
    void saveTestMember() {
        Member member = Member.builder()
                .username("myTest")
                .password("testPassword")
                .name("테스트 회원")
                .role(Role.USER)
                .email("myTest@test.com")
                .authentication("TEST")
                .isVerified(true)
                .build();
        memberRepository.save(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new TokenInfo(member), null, List.of(new SimpleGrantedAuthority(member.getRole().name()))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("포트폴리오 저장/조회 테스트")
    void saveAndFindPortfolioTest() {

        // given
        Member findMember = memberRepository.findByUsernameAndIsVerifiedTrue("myTest").orElse(null);
        if(findMember == null) {
            throw new RuntimeException();
        }
        TokenInfo tokenInfo = new TokenInfo(findMember);

        // when
        String portfolioName = "테스트 포트폴리오";
        String portfolioContent = "테스트 포트폴리오 내용입니다";
        String portfolioUrl = "https://github.com/dlwlsgur0909";
        Boolean isPublic = true;
        PortfolioSaveRequest request = new PortfolioSaveRequest(portfolioName, portfolioContent, portfolioUrl, isPublic);
        Long savedPortfolioId = portfolioService.savePortfolio(tokenInfo, request, null);

        em.flush();
        em.clear();

        PortfolioDetailResponse findPortfolio = portfolioService.findPortfolioDetail(tokenInfo, savedPortfolioId);

        // then
        assertThat(findPortfolio.getPortfolioName()).isEqualTo(portfolioName);
        assertThat(findPortfolio.getPortfolioContent()).isEqualTo(portfolioContent);
        assertThat(findPortfolio.getPortfolioUrl()).isEqualTo(portfolioUrl);
    }

    @Test
    @DisplayName("포트폴리오 수정 테스트")
    void updatePortfolioTest() {

        // given
        Member findMember = memberRepository.findByUsernameAndIsVerifiedTrue("myTest").orElse(null);
        if(findMember == null) {
            throw new RuntimeException();
        }
        TokenInfo tokenInfo = new TokenInfo(findMember);

        // 최초 데이터 저장
        String portfolioName = "테스트 포트폴리오";
        String portfolioContent = "테스트 포트폴리오 내용입니다";
        String portfolioUrl = "https://github.com/dlwlsgur0909";
        Boolean isPublic = true;

        PortfolioSaveRequest request = new PortfolioSaveRequest(portfolioName, portfolioContent, portfolioUrl, isPublic);
        Long savedPortfolioId = portfolioService.savePortfolio(tokenInfo, request, null);

        em.flush();
        em.clear();

        // when
        String updatePortfolioName = "테스트 포트폴리오 수정";
        String updatePortfolioContent = "테스트 포트폴리오 수정 내용";
        Boolean updateIsPublic = false;

        PortfolioSaveRequest updateRequest = new PortfolioSaveRequest(updatePortfolioName, updatePortfolioContent, portfolioUrl, updateIsPublic);
        portfolioService.updatePortfolio(tokenInfo, savedPortfolioId, updateRequest, null);

        em.flush();
        em.clear();

        PortfolioDetailResponse updatedPortfolio = portfolioService.findPortfolioDetail(tokenInfo, savedPortfolioId);

        // then
        assertThat(updatedPortfolio.getPortfolioName()).isEqualTo(updatePortfolioName);
        assertThat(updatedPortfolio.getPortfolioContent()).isEqualTo(updatePortfolioContent);
        assertThat(updatedPortfolio.getIsPublic()).isEqualTo(false);

    }

}