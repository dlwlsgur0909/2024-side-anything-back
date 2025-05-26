package com.side.anything.back.portfolio.dto.response;

import com.side.anything.back.portfolio.domain.Portfolio;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MyPortfolioListResponse {

    private List<MyPortfolioResponse> myPortfolioList = new ArrayList<>();
    private int totalPages;

    public MyPortfolioListResponse(List<Portfolio> portfolioList, int totalPages) {
        this.myPortfolioList = portfolioList.stream().map(MyPortfolioResponse::new).toList();
        this.totalPages = totalPages;
    }

    @Getter
    private static class MyPortfolioResponse {

        private Long portfolioId;
        private String portfolioName;
        private Boolean isPublic;

        public MyPortfolioResponse(Portfolio portfolio) {
            this.portfolioId = portfolio.getId();
            this.portfolioName = portfolio.getName();
            this.isPublic = portfolio.getIsPublic();
        }

    }

}
