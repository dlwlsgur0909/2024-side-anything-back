package com.side.anything.back.jwt;

import com.side.anything.back.member.domain.Member;
import com.side.anything.back.member.domain.Role;
import lombok.*;

@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenInfo {

    private Long id;
    private Role role;
    private String name;
    private String username;
    private String type;

    public TokenInfo(Member member, String type) {
        this.id = member.getId();
        this.role = member.getRole();
        this.name = member.getName();
        this.username = member.getUsername();
        this.type = type;
    }

}
