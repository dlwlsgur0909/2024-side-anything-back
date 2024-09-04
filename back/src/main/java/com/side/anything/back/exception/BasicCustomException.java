package com.side.anything.back.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class BasicCustomException extends RuntimeException{

    private final HttpStatus errorStatus;
    private final String errorCode;
    private final String errorMessage;

}
