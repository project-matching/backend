package com.matching.project.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matching.project.dto.common.NormalLoginRequestDto;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.MimeTypeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authRequest;
        if (request.getContentType().equals(MimeTypeUtils.APPLICATION_JSON_VALUE)) {
            // json request
            try {
                // read request body and mapping to login dto class by object mapper
                NormalLoginRequestDto dto = objectMapper.readValue(request.getReader().lines().collect(Collectors.joining()), NormalLoginRequestDto.class);
                authRequest = UsernamePasswordAuthenticationToken.unauthenticated(dto.getEmail(), dto.getPassword());
            } catch (IOException e) {
                e.printStackTrace();
                throw new AuthenticationServiceException("Request Content-Type(application/json) Parsing Error");
            }
        } else {
            // form-request
            String username = obtainUsername(request);
            username = (username != null) ? username.trim() : "";
            String password = obtainPassword(request);
            password = (password != null) ? password : "";
            authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        }
        this.setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
