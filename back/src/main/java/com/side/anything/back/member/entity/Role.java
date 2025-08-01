package com.side.anything.back.member.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {

    USER("USER", "회원"),
    ADMIN("ADMIN", "관리자")
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
