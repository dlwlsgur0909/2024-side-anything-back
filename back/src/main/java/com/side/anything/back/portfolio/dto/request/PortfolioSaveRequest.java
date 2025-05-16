package com.side.anything.back.portfolio.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PortfolioSaveRequest {

    @NotBlank
    private String portfolioName;

    @NotBlank
    private String portfolioContent;

    private String portfolioUrl;

    @NotNull
    private Boolean isPublic;

}
