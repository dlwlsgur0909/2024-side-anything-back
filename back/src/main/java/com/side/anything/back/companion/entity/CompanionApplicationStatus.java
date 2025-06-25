package com.side.anything.back.companion.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompanionApplicationStatus {

    PENDING("대기중"),
    APPROVED("승인됨"),
    REJECTED("거절됨"),
    CANCELLED("취소됨"),
    CANCELLED_BY_HOST("철회됨"),
    ;

    private final String description;
}
