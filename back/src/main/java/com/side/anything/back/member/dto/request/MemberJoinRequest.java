package com.side.anything.back.member.dto.request;

import com.side.anything.back.member.domain.Member;
import com.side.anything.back.member.domain.Role;
import lombok.*;

@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberJoinRequest {

    private String username;
    private String password;
    private String email;

    public Member toEntity(String encodedPassword, String authentication) {

        return Member.builder()
                .username(username)
                .password(encodedPassword)
                .email(email)
                .role(Role.ROLE_USER)
                .verified(false)
                .authentication(authentication)
                .build();
    }

}
