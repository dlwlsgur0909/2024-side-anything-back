package com.side.anything.back.auth.dto.response;

import lombok.*;

@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberLoginResponse {

    private String username;
    private String name;
    private String accessToken;
    private String refreshToken;

}
