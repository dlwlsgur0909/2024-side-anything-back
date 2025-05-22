package com.side.anything.back.portfolio.dto.response;

import com.side.anything.back.portfolio.domain.Portfolio;
import lombok.Getter;

@Getter
public class PortfolioResponse {

    private Long portfolioId;
    private String portfolioName;
    private String memberName;

    public PortfolioResponse(Portfolio portfolio) {
        this.portfolioId = portfolio.getId();
        this.portfolioName = portfolio.getName();
        this.memberName = portfolio.getMember().getName();
    }

}
