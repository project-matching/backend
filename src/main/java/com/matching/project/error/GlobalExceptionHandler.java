package com.matching.project.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        return ErrorResponse.toResponseEntity(ErrorCode.METHOD_ARGUMENT_EXCEPTION ,ex.getBindingResult());
    }

    @ExceptionHandler(value = {CustomException.class})
    protected ResponseEntity handleCustomException(CustomException e) {
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }
}
