package com.matching.project.aop;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.matching.project.dto.ResponseDto;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import io.swagger.models.auth.In;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.InputStreamEntity;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.FieldSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.util.Reflection;
import org.jboss.jandex.PrimitiveType;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LogAspect {

    @Pointcut("execution(* com.matching.project.controller..*.*(..))")
    public void onRequest() {}

    @Before("onRequest()")
    public void doBeforeLogging(JoinPoint jp) throws Throwable {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        loggingRequestUri(request);
        loggingHeader(request);
        loggingRequest(jp);
    }

    @AfterReturning(value = "onRequest()", returning = "responseEntity")
    public void doAfterSuccessLogging(ResponseEntity responseEntity){
        log.info("------------[Response Body Start]-----------");
        log.info("[Response Status] : {} -> {}", responseEntity.getStatusCode().value(), responseEntity.getStatusCode().name());
        log.info("[Response data] : {}", responseEntity.getBody());
        log.info("------------[Response Body End]-----------");
    }

    public void loggingHeader(HttpServletRequest request) {
        log.info("------------[Request Header Start]-----------");
        log.info("[Request Host] : {} {}:{}", request.getMethod(), request.getRemoteHost(), request.getRemotePort(), request.getRequestURI());
        log.info("[Request Uri] : {}", request.getRequestURI());
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            log.info("[{}] -> {}", name, value);
        }
        log.info("------------[Request Header End]-------------");
    }

    public void loggingRequestUri(HttpServletRequest request) {
        log.info("------------[Request Uri Start]-----------");
        log.info("[Request Host] : {} {}:{}", request.getMethod(), request.getRemoteHost(), request.getRemotePort(), request.getRequestURI());
        log.info("[Request Uri] : {}", request.getRequestURI());
        log.info("------------[Request Uri End]-------------");
    }

    private void loggingRequest(JoinPoint jp) throws JsonProcessingException {
        log.info("------------[Request Start]-----------");
        for (Object arg : jp.getArgs()) {
            if (arg == null) {
                continue;
            }
            if (isPrimitiveType(arg) || arg instanceof String) {
                log.info("[{}] -> {}", arg.getClass().getSimpleName(), arg);
            } else if(arg instanceof MultipartFile) {
                MultipartFile file = (MultipartFile)arg;
                if (!file.isEmpty()) {
                    log.info("[Image Type] -> {}", file.getContentType());
                    log.info("[Image FileName] -> {}", file.getOriginalFilename());
                    log.info("[Image Size] -> {}", file.getSize());
                }
            } else {
                String jsonString = new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(arg);
                log.info("[{}] -> {}", arg.getClass().getSimpleName(), jsonString);
            }
        }
        log.info("------------[Request End]-------------");
    }

    private boolean isPrimitiveType(Object o) {
        if (o instanceof Short || o instanceof Integer || o instanceof Long || o instanceof Float || o instanceof Double || o instanceof Boolean || o instanceof Byte) {
            return true;
        }
         return false;
    }
}
