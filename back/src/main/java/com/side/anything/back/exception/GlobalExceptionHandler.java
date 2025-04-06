package com.side.anything.back.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.security.InvalidParameterException;

import static com.side.anything.back.exception.BasicExceptionEnum.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            MethodArgumentNotValidException.class, // @Valid, @Validated 유효성 검사 실패 시
            MethodArgumentTypeMismatchException.class, // @RequestParam, @PathVariable에 잘못된 값 전달 시
            MissingServletRequestParameterException.class, // @RequestParam 미전달 시
            MissingPathVariableException.class, // @PathVariable 미전달 시
            HttpMessageNotReadableException.class, // Request Body의 JSON 데이터를 파싱하지 못하는 경우
            ConstraintViolationException.class, // 엔티티 제약 조건 예외 발생 시
            InvalidParameterException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<?> handleBadRequest(Exception e, HttpServletRequest request) {

        log.error("Bad Request for - {}", createRequestInfo(request));
        log.error("Error Message - {}", e.getMessage(), e);

        return ResponseEntity
                .status(BAD_REQUEST.getStatus())
                .body(new BasicExceptionResponse(BAD_REQUEST));
    }

    @ExceptionHandler({
            NoHandlerFoundException.class,
            NoResourceFoundException.class,
    })
    public ResponseEntity<?> handleNotFound(Exception e, HttpServletRequest request) {

        log.error("Not Found for - {}", createRequestInfo(request));
        log.error("Error Message - {}", e.getMessage(), e);

        return ResponseEntity
                .status(NOT_FOUND.getStatus())
                .body(new BasicExceptionResponse(NOT_FOUND));
    }

    @ExceptionHandler({
            MethodNotAllowedException.class
    })
    public ResponseEntity<?> handleMethodNotAllowed(Exception e, HttpServletRequest request) {

        log.error("Method Not Allowed for - {}", createRequestInfo(request));
        log.error("Error Message - {}", e.getMessage(), e);

        return ResponseEntity
                .status(METHOD_NOT_ALLOWED.getStatus())
                .body(new BasicExceptionResponse(METHOD_NOT_ALLOWED));
    }

    @ExceptionHandler({
            MailSendException.class
    })
    public ResponseEntity<?> handleMailException(Exception e, HttpServletRequest request) {

        log.error("Mail Send Error for - {}", createRequestInfo(request));
        log.error("Error Message - {}", e.getMessage(), e);

        return ResponseEntity
                .status(MAIL_SEND_ERROR.getStatus())
                .body(new BasicExceptionResponse(MAIL_SEND_ERROR));
    }

    @ExceptionHandler({
            Exception.class
    })
    public ResponseEntity<?> handleInternalServerError(Exception e, HttpServletRequest request) {

        log.error("Internal Server Error for - {}", createRequestInfo(request));
        log.error("Error Message - {}", e.getMessage(), e);

        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR.getStatus())
                .body(new BasicExceptionResponse(INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler({
            CustomException.class
    })
    public ResponseEntity<?> handleCustomException(CustomException ce, HttpServletRequest request) {

        log.error("Custom Exception for - {}", createRequestInfo(request));
        log.error("Error Message - {}", ce.getErrorMessage(), ce);

        return ResponseEntity
                .status(ce.getStatus())
                .body(new BasicExceptionResponse(ce));
    }

    private String createRequestInfo(HttpServletRequest request) {

        return request.getMethod() + ": " + request.getRequestURL();
    }

}
