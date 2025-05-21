package com.side.anything.back.portfolio.dto.response;

import com.side.anything.back.member.domain.Member;
import com.side.anything.back.portfolio.domain.Portfolio;
import lombok.Getter;

@Getter
public class PortfolioDetailResponse {

    private Long portfolioId;
    private String portfolioName;
    private String portfolioContent;
    private String portfolioUrl;
    private Boolean isPublic;
    private Long memberId;
    private String memberName;

    public PortfolioDetailResponse(Portfolio portfolio) {
        this.portfolioId = portfolio.getId();
        this.portfolioName = portfolio.getName();
        this.portfolioContent = portfolio.getContent();
        this.portfolioUrl = portfolio.getUrl();
        this.isPublic = portfolio.getIsPublic();
        this.memberId = portfolio.getMember().getId();
        this.memberName = portfolio.getMember().getName();
    }
}
