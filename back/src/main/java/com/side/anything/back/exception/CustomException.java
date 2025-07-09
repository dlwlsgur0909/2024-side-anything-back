package com.side.anything.back.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException{

    private final HttpStatus status;
    private final Integer errorCode;
    private final String errorMessage;

    public CustomException(BasicExceptionEnum basicExceptionEnum, String errorMessage) {
        super(errorMessage);
        this.status = basicExceptionEnum.getStatus();
        this.errorCode = basicExceptionEnum.getErrorCode();
        this.errorMessage = StringUtils.hasText(errorMessage) ? errorMessage : basicExceptionEnum.getErrorMessage();
    }

    public CustomException(BasicExceptionEnum basicExceptionEnum) {
        super(basicExceptionEnum.getErrorMessage());
        this.status = basicExceptionEnum.getStatus();
        this.errorCode = basicExceptionEnum.getErrorCode();
        this.errorMessage = basicExceptionEnum.getErrorMessage();
    }

}
