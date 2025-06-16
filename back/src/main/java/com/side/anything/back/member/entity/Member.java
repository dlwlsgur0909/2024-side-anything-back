package com.side.anything.back.member.entity;

import com.side.anything.back.auth.dto.request.JoinRequest;
import com.side.anything.back.auth.dto.request.SocialJoinRequest;
import com.side.anything.back.base.BaseTimeEntity;
import com.side.anything.back.oauth2.dto.response.CustomUserDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "gender")
    private String gender;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "email")
    private String email;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "authentication")
    private String authentication;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(name = "is_profile_completed")
    private Boolean isProfileCompleted;

    // 인증
    public void verify() {
        this.isVerified = true;
    }

    // 인증번호 갱신
    public void updateAuthentication(String authentication) {
        this.authentication = authentication;
    }

    // 비밀번호 변경
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    // 추후 비즈니스 로직에 따라 수정 필요
    public void updateOAuth2(CustomUserDTO userDTO) {
        this.email = userDTO.getEmail();
    }

    // 소셜 회원 추가정보 입력
    public void socialJoin(SocialJoinRequest request) {
        this.dob = LocalDate.parse(request.getDob());
        this.gender = request.getGender();
        this.nickname = request.getNickname();
        this.isProfileCompleted = true;
    }

    // 소셜 회원가입
    public static Member of(CustomUserDTO userDTO, String registrationId) {

        Member member = new Member();

        member.username = userDTO.getUsername();
        member.password = "";
        member.name = userDTO.getName();
        member.dob = userDTO.getDob();
        member.gender = userDTO.getGender();
        member.nickname = userDTO.getNickname();
        member.email = userDTO.getEmail();
        member.role = userDTO.getRole();
        member.authentication = registrationId;
        member.isVerified = true;
        member.isProfileCompleted = userDTO.getIsProfileCompleted();

        return member;
    }

    // 회원가입
    public static Member of(JoinRequest request, String encodedPassword, String authentication) {

        Member member = new Member();

        member.username = request.getUsername();
        member.password = encodedPassword;
        member.name = request.getName();
        member.dob = request.getDob();
        member.gender = request.getGender();
        member.nickname = request.getNickname();
        member.email = request.getEmail();
        member.role = Role.USER;
        member.authentication = authentication;
        member.isVerified = false;
        member.isProfileCompleted = true;

        return member;
    }

}
