package com.side.anything.back.security.jwt;

import com.side.anything.back.member.entity.Member;
import com.side.anything.back.member.entity.Role;
import com.side.anything.back.oauth2.dto.response.CustomOAuth2User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class TokenInfo {

    private Long id;
    private Role role;
    private String name;
    private String username;
    private String dob;
    private String gender;
    private String nickname;
    private Boolean isProfileCompleted;

    public TokenInfo(Member member) {
        this.id = member.getId();
        this.role = member.getRole();
        this.name = member.getName();
        this.username = member.getUsername();
        this.dob = member.getDob().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.gender = member.getGender();
        this.nickname = member.getNickname();
        this.isProfileCompleted = member.getIsProfileCompleted();
    }

    public TokenInfo(CustomOAuth2User userDTO) {
        this.id = userDTO.getId();
        this.role = Role.valueOf(userDTO.getAuthorities().iterator().next().getAuthority());
        this.name = userDTO.getName();
        this.username = userDTO.getUsername();
        this.dob = userDTO.getDob() == null ? null : userDTO.getDob().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.gender = userDTO.getGender();
        this.nickname = userDTO.getNickname();
        this.isProfileCompleted = userDTO.getIsProfileCompleted();
    }

}
