package com.matching.project.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.matching.project.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Object object = request.getAttribute("exception");
        if (object == null) {
            log.error("AuthenticationError : {} {}:{} (401) -> {}", request.getMethod(), request.getRemoteHost(), request.getRemotePort(), authException.getMessage());
            response.setStatus(401);
        } else {
            if (object instanceof CustomException) {
                CustomException cs = (CustomException) object;
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(401);
                response.getWriter().println(new ObjectMapper()
                        .registerModule(new JavaTimeModule())
                        .writeValueAsString(ErrorResponse.toResponseEntity(cs.getErrorCode()).getBody()));
            }
        }
    }

}
