package com.side.anything.back.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum FileCategory {

    PORTFOLIO("portfolios", "포트폴리오")
    ;

    private final String path;
    private final String name;


}
