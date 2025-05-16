package com.side.anything.back.oauth2.dto.response;

import com.side.anything.back.member.domain.Role;
import lombok.*;

@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomUserDTO { // CustomOAuth2User에 넘겨줄 DTO

    private Long id;
    private String username;
    private String name;
    private String email;
    private Role role;

    // 로그인 성공 시 CustomUserDTO에 있는 정보를 기반으로 토큰을 생성하기 때문에 id 값을 세팅해준다
    public void setId(Long id) {
        this.id = id;
    }

}
