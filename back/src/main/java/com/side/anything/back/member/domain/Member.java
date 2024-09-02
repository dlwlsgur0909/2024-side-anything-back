package com.side.anything.back.member.domain;

import com.side.anything.back.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "member_username")
    private String username;

    @Column(name = "member_password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role")
    private Role role;

    @Column(name = "member_email")
    private String email;

    @Column(name = "member_verified")
    private Boolean verified;

    @Column(name = "member_authentication")
    private String authentication;
}
