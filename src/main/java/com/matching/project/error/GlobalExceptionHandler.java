package com.matching.project.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        HttpServletRequest httpServletRequest =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        List<String> valid = ex.getFieldErrors()
                .stream()
                .map(fieldError ->
                        "[" + fieldError.getObjectName() + "] => "
                                + fieldError.getField() + " : " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        log.error("MethodArgumentError : {} {}:{} ({}) -> {}", httpServletRequest.getMethod(), httpServletRequest.getRemoteHost(), httpServletRequest.getRemotePort(), ErrorCode.METHOD_ARGUMENT_EXCEPTION.getHttpStatus().value(), valid );
        return ErrorResponse.toResponseEntity(ErrorCode.METHOD_ARGUMENT_EXCEPTION ,ex.getBindingResult());
    }

    @ExceptionHandler(value = {CustomException.class})
    protected ResponseEntity handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        log.error("CustomException : {} {}:{} ({}) -> {}", request.getMethod(), request.getRemoteHost(), request.getRemotePort(), errorCode.getHttpStatus().value(), errorCode.getDetail());
        return ErrorResponse.toResponseEntity(errorCode);
    }
}
