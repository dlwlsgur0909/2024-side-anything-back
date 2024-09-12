package com.side.anything.back.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberChangePasswordRequest {

    @NotBlank
    private String originalPassword;
    @NotBlank
    private String newPassword;
}
