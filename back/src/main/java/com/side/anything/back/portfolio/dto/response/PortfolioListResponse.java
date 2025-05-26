package com.side.anything.back.portfolio.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class PortfolioListResponse {

    private List<PortfolioResponse> portfolioList = new ArrayList<>();
    private int totalPages;

}
