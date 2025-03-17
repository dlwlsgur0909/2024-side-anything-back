package com.side.anything.back.security.oauth2.dto.response;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final CustomUserDTO userDTO;


    @Override
    public Map<String, Object> getAttributes() { // attribute의 형태가 서비스 마다 (google, naver) 다르기 때문에 사용하지 않음
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return userDTO.getRole().name();
            }
        });

        return authorities;
    }

    @Override
    public String getName() {
        return userDTO.getName();
    }

    public String getEmail() {
        return userDTO.getEmail();
    }

    public String getUsername() {
        return userDTO.getUsername();
    }

    public Long getId() {
        return userDTO.getId();
    }


}
