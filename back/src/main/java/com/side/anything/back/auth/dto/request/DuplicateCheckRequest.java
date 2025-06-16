package com.side.anything.back.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DuplicateCheckRequest {

    @NotBlank
    private String username;
    @NotBlank
    private String nickname;
    @NotBlank
    private String email;
}


