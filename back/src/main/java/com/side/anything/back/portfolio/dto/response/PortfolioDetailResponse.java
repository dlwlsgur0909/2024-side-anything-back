package com.side.anything.back.portfolio.dto.response;

import com.side.anything.back.portfolio.domain.Portfolio;
import lombok.Getter;

@Getter
public class PortfolioDetailResponse {

    private Long portfolioId;
    private String portfolioName;
    private String portfolioContent;
    private String portfolioUrl;
    private Boolean isPublic;
    private Boolean hasPortfolioFile;
    private Long memberId;
    private String memberName;

    public PortfolioDetailResponse(Portfolio portfolio, Boolean hasPortfolioFile) {
        this.portfolioId = portfolio.getId();
        this.portfolioName = portfolio.getName();
        this.portfolioContent = portfolio.getContent();
        this.portfolioUrl = portfolio.getUrl();
        this.isPublic = portfolio.getIsPublic();
        this.hasPortfolioFile = hasPortfolioFile;
        this.memberId = portfolio.getMember().getId();
        this.memberName = portfolio.getMember().getName();
    }
}
