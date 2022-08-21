package com.matching.project.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
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

    @Override
    protected ResponseEntity handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        HttpServletRequest httpServletRequest =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        List<String> valid = ex.getFieldErrors()
                .stream()
                .map(fieldError ->
                        "[" + fieldError.getObjectName() + "] => "
                                + fieldError.getField() + " : " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        log.error("BindError : {} {}:{} ({}) -> {}", httpServletRequest.getMethod(), httpServletRequest.getRemoteHost(), httpServletRequest.getRemotePort(), ErrorCode.METHOD_ARGUMENT_EXCEPTION.getHttpStatus().value(), valid );
        return ErrorResponse.toResponseEntity(ErrorCode.BIND_EXCEPTION ,ex.getBindingResult());
    }

    @ExceptionHandler(value = {DataAccessException.class})
    protected ResponseEntity handleDataAccessException(DataAccessException e) {
        HttpServletRequest httpServletRequest =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        if (e instanceof BadSqlGrammarException) {
            ErrorCode errorCode = ErrorCode.BAD_SQL_GRAMMAR_EXCEPTION;
            log.error("BadSqlGrammarException : {} {}:{} ({}) -> {}", httpServletRequest.getMethod(), httpServletRequest.getRemoteHost(), httpServletRequest.getRemotePort(), errorCode.getHttpStatus().value(), errorCode.getDetail());
            return ErrorResponse.toResponseEntity(errorCode);
        }
        else if (e instanceof InvalidResultSetAccessException) {
            ErrorCode errorCode = ErrorCode.INVALID_RESULT_SET_ACCESS_EXCEPTION;
            log.error("InvalidResultSetAccessException : {} {}:{} ({}) -> {}", httpServletRequest.getMethod(), httpServletRequest.getRemoteHost(), httpServletRequest.getRemotePort(), errorCode.getHttpStatus().value(), errorCode.getDetail());
            return ErrorResponse.toResponseEntity(errorCode);
        }
        else if (e instanceof DuplicateKeyException) {
            ErrorCode errorCode = ErrorCode.DUPLICATE_KEY_EXCEPTION;
            log.error("InvalidResultSetAccessException : {} {}:{} ({}) -> {}", httpServletRequest.getMethod(), httpServletRequest.getRemoteHost(), httpServletRequest.getRemotePort(), errorCode.getHttpStatus().value(), errorCode.getDetail());
            return ErrorResponse.toResponseEntity(errorCode);
        }
        else if (e instanceof DataIntegrityViolationException) {
            ErrorCode errorCode = ErrorCode.DATA_INTEGRITY_VIOLATION_EXCEPTION;
            log.error("DataIntegrityViolationException : {} {}:{} ({}) -> {}", httpServletRequest.getMethod(), httpServletRequest.getRemoteHost(), httpServletRequest.getRemotePort(), errorCode.getHttpStatus().value(), errorCode.getDetail());
            return ErrorResponse.toResponseEntity(errorCode);
        }
        else if (e instanceof DataAccessResourceFailureException) {
            ErrorCode errorCode = ErrorCode.DATA_ACCESS_RESOURCE_FAILURE_EXCEPTION;
            log.error("DataAccessResourceFailureException : {} {}:{} ({}) -> {}", httpServletRequest.getMethod(), httpServletRequest.getRemoteHost(), httpServletRequest.getRemotePort(), errorCode.getHttpStatus().value(), errorCode.getDetail());
            return ErrorResponse.toResponseEntity(errorCode);
        }
        else if (e instanceof CannotAcquireLockException) {
            ErrorCode errorCode = ErrorCode.CANNOT_ACQUIRE_LOCK_EXCEPTION;
            log.error("CannotAcquireLockException : {} {}:{} ({}) -> {}", httpServletRequest.getMethod(), httpServletRequest.getRemoteHost(), httpServletRequest.getRemotePort(), errorCode.getHttpStatus().value(), errorCode.getDetail());
            return ErrorResponse.toResponseEntity(errorCode);
        }
        else if (e instanceof DeadlockLoserDataAccessException) {
            ErrorCode errorCode = ErrorCode.DEADLOCK_LOSER_DATA_ACCESS_EXCEPTION;
            log.error("DeadlockLoserDataAccessException : {} {}:{} ({}) -> {}", httpServletRequest.getMethod(), httpServletRequest.getRemoteHost(), httpServletRequest.getRemotePort(), errorCode.getHttpStatus().value(), errorCode.getDetail());
            return ErrorResponse.toResponseEntity(errorCode);
        }
        else if (e instanceof CannotSerializeTransactionException) {
            ErrorCode errorCode = ErrorCode.CANNOT_SERIALIZE_TRANSACTION_EXCEPTION;
            log.error("CannotSerializeTransactionException : {} {}:{} ({}) -> {}", httpServletRequest.getMethod(), httpServletRequest.getRemoteHost(), httpServletRequest.getRemotePort(), errorCode.getHttpStatus().value(), errorCode.getDetail());
            return ErrorResponse.toResponseEntity(errorCode);
        } else {
            ErrorCode errorCode = ErrorCode.DATA_ACCESS_EXCEPTION;
            log.error("DataAccessException : {} {}:{} ({}) -> {}", httpServletRequest.getMethod(), httpServletRequest.getRemoteHost(), httpServletRequest.getRemotePort(), errorCode.getHttpStatus().value(), errorCode.getDetail());
            return ErrorResponse.toResponseEntity(errorCode);
        }
    }

    @ExceptionHandler(value = {NullPointerException.class})
    protected ResponseEntity handleNullPointerException(NullPointerException e) {
        HttpServletRequest httpServletRequest =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        ErrorCode errorCode = ErrorCode.NULL_POINTER_EXCEPTION;
        log.error("NullPointerException : {} {}:{} ({}) -> {}", httpServletRequest.getMethod(), httpServletRequest.getRemoteHost(), httpServletRequest.getRemotePort(), errorCode.getHttpStatus().value(), errorCode.getDetail());
        return ErrorResponse.toResponseEntity(errorCode);
    }

    @ExceptionHandler(value = {ArrayIndexOutOfBoundsException.class})
    protected ResponseEntity handleArrayIndexOutOfBoundsException(ArrayIndexOutOfBoundsException e) {
        HttpServletRequest httpServletRequest =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        ErrorCode errorCode = ErrorCode.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION;
        log.error("ArrayIndexOutOfBoundsException : {} {}:{} ({}) -> {}", httpServletRequest.getMethod(), httpServletRequest.getRemoteHost(), httpServletRequest.getRemotePort(), errorCode.getHttpStatus().value(), errorCode.getDetail());
        return ErrorResponse.toResponseEntity(errorCode);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    protected ResponseEntity handleIllegalArgumentException(IllegalArgumentException e) {
        HttpServletRequest httpServletRequest =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        ErrorCode errorCode = ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION;
        log.error("IllegalArgumentException : {} {}:{} ({}) -> {}", httpServletRequest.getMethod(), httpServletRequest.getRemoteHost(), httpServletRequest.getRemotePort(), errorCode.getHttpStatus().value(), errorCode.getDetail());
        return ErrorResponse.toResponseEntity(errorCode);
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
