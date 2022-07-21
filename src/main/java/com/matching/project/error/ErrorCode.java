package com.matching.project.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 400
    Method_ARGUMENT_EXCETPION(BAD_REQUEST, "잘못된 REQUEST 인자입니다."),
    // 500 INTERNAL_SERVER_ERROR : 서버 내부 오류
    POSITION_NO_SUCH_ELEMENT_EXCEPTION(INTERNAL_SERVER_ERROR, "잘못된 포지션")
    ;
    private final HttpStatus httpStatus;
    private final String detail;
}
