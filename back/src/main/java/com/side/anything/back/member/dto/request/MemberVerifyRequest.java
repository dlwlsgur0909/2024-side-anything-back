package com.side.anything.back.member.dto.request;

import lombok.*;

@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberVerifyRequest {

    private String username;
    private String authentication;
}
