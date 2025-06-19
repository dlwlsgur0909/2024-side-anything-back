package com.side.anything.back.companion.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CompanionApplicationSaveRequest {

    @NotBlank
    private String message;

}
