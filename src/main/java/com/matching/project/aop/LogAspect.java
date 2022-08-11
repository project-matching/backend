package com.matching.project.aop;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.matching.project.dto.ResponseDto;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.Valid;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LogAspect {
    private final Validator validator;

    @Pointcut("execution(* com.matching.project.controller..*.*(..))") // 3
    public void onRequest() {}


    @Before("onRequest() && @args(org.springframework.web.bind.annotation.RequestBody,..)") // 4
    public void doBeforeLogging(JoinPoint jp) throws Throwable {
        Object[] args = jp.getArgs();
        for (Object arg : args) {
            log.info("test : {}", arg);
        }
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        loggingRequestUri(request);
        loggingHeader(request);
        loggingQueryParameter(request);
        loggingBody(jp);

        //loggingValidation(jp);
    }

    @AfterReturning(value = "com.matching.project.aop.LogAspect.onRequest()", returning = "responseEntity")
    public void doAfterSuccessLogging(ResponseEntity responseEntity){
        ResponseDto body = (ResponseDto) responseEntity.getBody();
        log.info("------------[Response Body Start]-----------");
        log.info("[Response Status Code] : {}", responseEntity.getStatusCode().value());
        log.info("[Response Status Name] : {}", responseEntity.getStatusCode().name());
        log.info("------------[Response Body End]-----------");
    }

    @AfterThrowing(value = "com.matching.project.aop.LogAspect.onRequest()", throwing = "exception")
    public void doAfterExceptionLogging(Exception exception) {
        log.info("------------[Exception Start]-----------");
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        log.info("[Request Method] : {}", request.getMethod());
        log.info("[Request Uri] : {}", request.getRequestURI());
        log.info("[Request RemoteHost] : {}", request.getRemoteHost());
        if (exception instanceof CustomException) {
            CustomException customException = (CustomException)exception;
            log.info("[Response Status Value] : {}", customException.getErrorCode().getHttpStatus().value());
            log.info("[Response Status Name] : {}", customException.getErrorCode().getHttpStatus().name());
            log.info("[Response ErrorCode Detail] : {}", customException.getErrorCode().getDetail());
        } else {
            StringWriter errors = new StringWriter();
            exception.printStackTrace(new PrintWriter(errors));
            log.info("[Exception] : {}", errors);
        }
        log.info("------------[Exception End]-----------");
    }

    public void loggingRequestUri(HttpServletRequest request) {
        log.info("------------[Request Uri Start]-----------");
        log.info("[Request Method] : {}", request.getMethod());
        log.info("[Request Uri] : {}", request.getRequestURI());
        log.info("[Request RemoteHost] : {}", request.getRemoteHost());
        log.info("------------[Request Uri End]-----------");
    }

    public void loggingHeader(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        log.info("------------[Request Header Start]-----------");
        while(headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            log.info("[{}] -> {}", name, value);
        }
        log.info("------------[Request Header End]-------------");
    }

    private void loggingQueryParameter(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Iterator<String> keys = request.getParameterMap().keySet().iterator();

        log.info("------------[Request QueryParameter Start]-----------");
        if (parameterMap.isEmpty()) {
            log.info("None QueryParameter");
        } else {
            while (keys.hasNext()) {
                String name = keys.next();
                String[] value = parameterMap.get(name);
                log.info("[{}] -> {}", name, value);
            }
        }
        log.info("------------[Request QueryParameter End]-------------");
    }

    private void loggingBody(JoinPoint jp) throws JsonProcessingException {
        Signature signature = jp.getSignature();
        for (Object arg : jp.getArgs()) {
            if (!isBindingResult(arg)) {
                String jsonString = new ObjectMapper().writeValueAsString(arg);
                Map<String,Object> bodyMap = new HashMap<>();
                bodyMap = (Map<String,Object>) new Gson().fromJson(jsonString, bodyMap.getClass());

                Iterator<String> keys = bodyMap.keySet().iterator();
                log.info("------------[Request Body Start]-----------");
                while (keys.hasNext()) {
                    String name = keys.next();
                    Object value = bodyMap.get(name);
                    log.info("[{}] -> {}", name, value);
                }
                log.info("------------[Request Body End]-----------");
            }
        }
    }

//    private void loggingValidation(JoinPoint jp) {
//        Object[] args = jp.getArgs();
//        for (Object arg : args) {
//            if (isBindingResult(arg)) {
//                log.info("------------[Request Binding Error Start]-----------");
//                BindingResult result = (BindingResult) arg;
//                if (result.hasErrors()) {
//                    List<ObjectError> list = result.getAllErrors();
//                    for (ObjectError e : list) {
//                        log.info("[{}] -> {}", e.getObjectName(), e.getDefaultMessage());
//                    }
//                }
//                log.info("------------[Request Binding Error End]-----------");
//            }
//        }
//    }

    private boolean isBindingResult(Object arg) {
        return arg instanceof BindingResult;
    }
}
