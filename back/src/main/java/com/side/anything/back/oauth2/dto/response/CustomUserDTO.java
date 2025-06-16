package com.side.anything.back.oauth2.dto.response;

import com.side.anything.back.member.entity.Member;
import com.side.anything.back.member.entity.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class CustomUserDTO { // CustomOAuth2User에 넘겨줄 DTO

    private Long id;
    private String username;
    private String name;
    private LocalDate dob;
    private String gender;
    private String nickname;
    private String email;
    private Role role;
    private Boolean isProfileCompleted;

    // 로그인 성공 시 CustomUserDTO에 있는 정보를 기반으로 토큰을 생성하기 때문에 값을 세팅해준다

    // 신규 회원
    public void setNewMember(Long id) {
        this.id = id;
    }

    // 기존 회원
    public void setExistMember(Member member) {
        this.id = member.getId();
        this.dob = member.getDob();
        this.gender = member.getGender();
        this.nickname = member.getNickname();
        this.isProfileCompleted = member.getIsProfileCompleted();
    }

}
