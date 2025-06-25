package com.side.anything.back.companion.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompanionPostStatus {

    OPEN("모집중"),
    CLOSED("마감됨"),
    DELETED("삭제됨")
    ;

    private final String description;
}
