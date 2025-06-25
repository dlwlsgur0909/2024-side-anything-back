package com.side.anything.back.companion.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompanionApplicationStatus {

    PENDING("대기중"),
    APPROVED("승인"),
    REJECTED("거절"),
    CANCELLED("취소"),
    CANCELLED_BY_HOST("철회"),
    ;

    private final String description;
}
