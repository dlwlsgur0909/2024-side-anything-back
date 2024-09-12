package com.side.anything.back.auth.dto.request;

import lombok.*;

@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberLoginRequest {

    private String username;
    private String password;
}
