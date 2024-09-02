package com.side.anything.back.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BasicExceptionEntity {

    private String errorCode;
    private String errorMessage;
}
