package com.side.anything.back.companion.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CompanionPostSaveRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    private String location;

    @NotNull
    @Positive
    private Integer recruitCount;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

}
