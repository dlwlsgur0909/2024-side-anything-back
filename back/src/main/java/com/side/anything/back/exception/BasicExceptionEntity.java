package com.side.anything.back.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BasicExceptionEntity {

    private String errorCode;
    private String errorMessage;
}
