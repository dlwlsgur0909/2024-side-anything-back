package com.side.anything.back.auth.dto.response;

import com.side.anything.back.member.domain.Role;
import lombok.*;

@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {

    private Long id;
    private String nickname;
    private String name;
    private Role role;
    private Boolean isProfileCompleted;
    private String accessToken;

}
