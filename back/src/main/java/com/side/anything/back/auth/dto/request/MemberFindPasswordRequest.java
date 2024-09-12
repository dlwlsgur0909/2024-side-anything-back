package com.side.anything.back.auth.dto.request;

import lombok.*;

@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberFindPasswordRequest {

    private String email;
    private String username;
}
