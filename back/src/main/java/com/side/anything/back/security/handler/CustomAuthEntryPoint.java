package com.side.anything.back.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.side.anything.back.exception.BasicExceptionEnum;
import com.side.anything.back.exception.BasicExceptionResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        StringBuffer requestURL = request.getRequestURL();
        log.error("AuthEntryPoint - requestURL = {}", requestURL);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BasicExceptionResponse exception = BasicExceptionResponse.builder()
                .errorCode(BasicExceptionEnum.UNAUTHORIZED.getErrorCode())
                .errorMessage(BasicExceptionEnum.UNAUTHORIZED.getErrorMessage())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(exception);
        response.getWriter().write(body);
    }
}
