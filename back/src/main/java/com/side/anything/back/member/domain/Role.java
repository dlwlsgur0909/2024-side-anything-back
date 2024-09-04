package com.side.anything.back.member.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {

    ROLE_USER("USER", "회원"),
    ROLE_ADMIN("ADMIN", "관리자")
    ;

    private final String key;
    private final String title;

    public String getKey() {
        return key;
    }

    public String getTitle() {
        return title;
    }
}
