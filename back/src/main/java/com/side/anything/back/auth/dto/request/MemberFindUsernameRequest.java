package com.side.anything.back.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberFindUsernameRequest {

    @NotBlank
    private String email;
}
