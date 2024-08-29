package com.side.anything.back.member.dto.response;

import lombok.*;

@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberLoginResponse {

    private String username;
    private String accessToken;
    private String refreshToken;

}
