package com.side.anything.back.companion.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CompanionApplicationUpdateStatusRequest {

    @NotNull
    private Boolean isApproval;

}
