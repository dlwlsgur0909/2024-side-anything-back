package com.side.anything.back.auth.dto.request;

import com.side.anything.back.member.domain.Member;
import com.side.anything.back.member.domain.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberJoinRequest {

    @NotBlank
    private String username;
    @NotBlank
    private String name;
    @NotBlank
    private String password;
    @NotBlank
    private String email;

    public Member toEntity(String encodedPassword, String authentication) {

        return Member.builder()
                .username(username)
                .password(encodedPassword)
                .name(name)
                .email(email)
                .role(Role.ROLE_USER)
                .verified(false)
                .authentication(authentication)
                .build();
    }

}
