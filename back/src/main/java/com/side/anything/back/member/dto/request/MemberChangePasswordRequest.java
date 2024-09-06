package com.side.anything.back.member.dto.request;

import lombok.*;

@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberChangePasswordRequest {

    private String originalPassword;
    private String newPassword;
}
