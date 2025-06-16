package com.side.anything.back.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SocialJoinRequest {

    @NotNull
    private Long id;
    @NotBlank
    private String dob;
    @NotBlank
    private String gender;
    @NotBlank
    private String nickname;

}
