package com.side.anything.back.portfolio.dto.response;

import com.side.anything.back.portfolio.domain.Portfolio;
import com.side.anything.back.portfolio.domain.PortfolioFile;
import lombok.Getter;

@Getter
public class PortfolioDetailResponse {

    private Long portfolioId;
    private String portfolioName;
    private String portfolioContent;
    private String portfolioUrl;
    private Boolean isPublic;
    private Long portfolioFileId;
    private Long memberId;
    private String memberName;

    public PortfolioDetailResponse(Portfolio portfolio, PortfolioFile portfolioFile) {
        this.portfolioId = portfolio.getId();
        this.portfolioName = portfolio.getName();
        this.portfolioContent = portfolio.getContent();
        this.portfolioUrl = portfolio.getUrl();
        this.isPublic = portfolio.getIsPublic();
        this.portfolioFileId = portfolioFile == null ? null : portfolioFile.getId();
        this.memberId = portfolio.getMember().getId();
        this.memberName = portfolio.getMember().getName();
    }
}
