package com.side.anything.back.exception;

import lombok.Getter;

@Getter
public class BasicExceptionResponse {

    private final Integer errorCode;
    private final String errorMessage;

    public BasicExceptionResponse(BasicExceptionEnum exceptionEnum) {
        this.errorCode = exceptionEnum.getErrorCode();
        this.errorMessage = exceptionEnum.getErrorMessage();
    }

    public BasicExceptionResponse(CustomException customException) {
        this.errorCode = customException.getErrorCode();
        this.errorMessage = customException.getErrorMessage();
    }

}
