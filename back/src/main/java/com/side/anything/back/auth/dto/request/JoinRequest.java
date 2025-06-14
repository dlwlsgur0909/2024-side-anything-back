package com.side.anything.back.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class JoinRequest {

    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String name;
    @NotNull
    private LocalDate dob;
    @NotBlank
    private String gender;
    @NotBlank
    private String nickname;
    @NotBlank
    private String email;

}
