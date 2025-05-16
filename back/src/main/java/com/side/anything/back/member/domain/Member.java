package com.side.anything.back.member.domain;

import com.side.anything.back.base.BaseTimeEntity;
import com.side.anything.back.oauth2.dto.response.CustomUserDTO;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "member_username")
    private String username;

    @Column(name = "member_password")
    private String password;

    @Column(name = "member_name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role")
    private Role role;

    @Column(name = "member_email")
    private String email;

    @Column(name = "member_verified")
    private Boolean verified;

    @Column(name = "member_authentication")
    private String authentication;

    public void verify() {
        this.verified = true;
    }

    public void updateAuthentication(String authentication) {
        this.authentication = authentication;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    // 추후 비즈니스 로직에 따라 수정 필요
    public void updateOAuth2(CustomUserDTO userDTO) {
        this.email = userDTO.getEmail();
    }

    public static Member of(CustomUserDTO userDTO, String registrationId) {
        Member member = new Member();
        member.username = userDTO.getUsername();
        member.password = "";
        member.name = userDTO.getName();
        member.email = userDTO.getEmail();
        member.role = userDTO.getRole();
        member.authentication = registrationId;
        member.verified = true;

        return member;
    }

}
