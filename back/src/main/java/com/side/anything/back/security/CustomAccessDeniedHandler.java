package com.side.anything.back.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.side.anything.back.exception.BasicExceptionEntity;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        StringBuffer requestURL = request.getRequestURL();
        log.error("AccessDenied - requestURL = {}", requestURL);

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BasicExceptionEntity exception = new BasicExceptionEntity("403", "FORBIDDEN");
        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(exception);
        response.getWriter().write(body);
    }
}
