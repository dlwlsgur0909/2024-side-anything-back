package com.side.anything.back.exception;

import jakarta.mail.SendFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            NoHandlerFoundException.class,
            NoResourceFoundException.class,
    })
    public ResponseEntity<?> notFoundExceptionHandler(Exception e) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build();
    }

    @ExceptionHandler({
            MailSendException.class
    })
    public ResponseEntity<?> mailExceptionHandler(Exception e) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BasicExceptionEntity.builder()
                        .errorCode("500")
                        .errorMessage("메일이 정상적으로 발송되지 않았습니다.")
                        .build()
                );
    }

    @ExceptionHandler({
            BasicCustomException.class
    })
    public ResponseEntity<?> customExceptionHandler(BasicCustomException e) {

        return ResponseEntity
                .status(e.getErrorStatus())
                .body(BasicExceptionEntity.builder()
                        .errorCode(e.getErrorCode())
                        .errorMessage(e.getErrorMessage())
                        .build()
                );
    }

}
