package com.matching.project.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    METHOD_ARGUMENT_EXCEPTION(BAD_REQUEST, "잘못된 REQUEST 인자입니다."),
    POSITION_NO_SUCH_ELEMENT_EXCEPTION(INTERNAL_SERVER_ERROR, "잘못된 포지션"),

    //Common
    INCORRECT_PASSWORD_EXCEPTION(INTERNAL_SERVER_ERROR, "This is an incorrect password"),
    BLOCKED_EXCEPTION(INTERNAL_SERVER_ERROR, "This is blocked User ID"),
    WITHDRAWAL_EXCEPTION(INTERNAL_SERVER_ERROR, "This is withdrawal User ID"),
    UNSIGNED_EMAIL_EXCEPTION(INTERNAL_SERVER_ERROR, "This is an unsigned email"),

    //Email
    SOCIAL_USER_NOT_ALLOWED_FEATURE_EXCEPTION(INTERNAL_SERVER_ERROR, "Social users are not allowed feature"),
    ALREADY_AUTHENTICATED_AUTH_TOKEN_EXCEPTION(INTERNAL_SERVER_ERROR, "Already Authentication Completed"),
    NOT_FOUND_AUTH_TOKEN_EXCEPTION(INTERNAL_SERVER_ERROR, "Email AuthToken Not Found"),
    EXPIRED_AUTH_TOKEN_EXCEPTION(INTERNAL_SERVER_ERROR, "Email AuthToken Expired"),

    //User
    GET_USER_AUTHENTICATION_EXCEPTION(INTERNAL_SERVER_ERROR, "Failed to load currently connected user information."),
    NOT_FIND_USER_EXCEPTION(INTERNAL_SERVER_ERROR, "Not Find User"),
    NOT_REGISTERED_EMAIL_EXCEPTION(INTERNAL_SERVER_ERROR, "This is not a registered email"),
    DUPLICATE_EMAIL_EXCEPTION(INTERNAL_SERVER_ERROR, "Duplicated Email"),
    UNREGISTERED_TECHNICAL_STACK_EXCEPTION(INTERNAL_SERVER_ERROR, "Unregistered TechnicalStack"),

    //Images
    FILE_READ_FAIL_EXCEPTION(INTERNAL_SERVER_ERROR, "파일 읽기 실패"),
    FILE_WRITE_FAIL_EXCEPTION(INTERNAL_SERVER_ERROR, "파일 쓰기 실패"),
    FILE_CONVERSION_EXCEPTION(INTERNAL_SERVER_ERROR, "파일 변환 실패"),
    NOT_FOUND_FILE_EXCEPTION(INTERNAL_SERVER_ERROR, "존재하지 않는 파일"),
    SIZE_OVER_EXCEPTION(INTERNAL_SERVER_ERROR, "정해진 용량 이상의 파일은 허용되지 않음"),
    NONEXISTENT_EXT_EXCEPTION(INTERNAL_SERVER_ERROR, "확장자가 존재하지 않음"),
    NOT_ALLOWED_EXT_EXCEPTION(INTERNAL_SERVER_ERROR, "허용하지 않는 확장자"),
    NOT_FIND_IMAGE_EXCEPTION(INTERNAL_SERVER_ERROR, "Not Find Image"),

    //ADMIN
    USER_ALREADY_WITHDRAWAL_EXCEPTION(INTERNAL_SERVER_ERROR, "User already withdrawal"),
    USER_ALREADY_BLOCKED_EXCEPTION(INTERNAL_SERVER_ERROR, "User already blocked"),
    USER_ALREADY_UNBLOCKED_EXCEPTION(INTERNAL_SERVER_ERROR, "User already unblocked"),

    // ProjectPosition
    NOT_FIND_PROJECT_POSITION_EXCEPTION(INTERNAL_SERVER_ERROR, "Not Find ProjectPosition"),
    PROJECT_POSITION_EXISTENCE_USER(INTERNAL_SERVER_ERROR, "ProjectPosition existence user"),
    PROJECT_POSITION_NOT_EXISTENCE_USER(INTERNAL_SERVER_ERROR, "ProjectPosition not existence user"),
    PROJECT_POSITION_NOT_EQUAL_USER(INTERNAL_SERVER_ERROR, "ProjectPosition not equal user"),

    // TechnicalStack
    NOT_FIND_TECHNICAL_STACK_EXCEPTION(INTERNAL_SERVER_ERROR, "Not Find TechnicalStack"),

    //ProjectTechnicalStack
    NOT_FIND_PROJECT_TECHNICAL_STACK_EXCEPTION(INTERNAL_SERVER_ERROR, "Not Find ProjectTechnicalStack"),

    //UserTechnicalStack
    NOT_FIND_USER_TECHNICAL_STACK_EXCEPTION(INTERNAL_SERVER_ERROR, "Not Find UserTechnicalStack"),

    // Project
    NOT_FIND_PROJECT_EXCEPTION(INTERNAL_SERVER_ERROR, "Not Find Project"),
    PROJECT_NOT_REGISTER_USER(INTERNAL_SERVER_ERROR, "Project not register user"),

    //Comment
    NOT_FIND_COMMENT_EXCEPTION(INTERNAL_SERVER_ERROR, "Not Find Comment"),
    UNAUTHORIZED_USER_ACCESS_EXCEPTION(INTERNAL_SERVER_ERROR, "Unauthorized User Access"),

    //Position
    UNREGISTERED_POSITION_EXCEPTION(INTERNAL_SERVER_ERROR, "Unregistered Position"),
    ALREADY_REGISTERED_POSITION_EXCEPTION(INTERNAL_SERVER_ERROR, "Already Registered Position"),
    NOT_FIND_POSITION_EXCEPTION(INTERNAL_SERVER_ERROR, "Not Find Position"),

    //Notification
    NOT_FIND_NOTIFICATION_EXCEPTION(INTERNAL_SERVER_ERROR, "Not Find Notification"),

    //BookMark
    NOT_FIND_BOOKMARK_EXCEPTION(INTERNAL_SERVER_ERROR, "Not Find BookMark"),

    //ProjectParticipateRequest
    NOT_FIND_PROJECT_PARTICIPATE_REQUEST_EXCEPTION(INTERNAL_SERVER_ERROR, "Not Find ProjectParticipateRequest")
    ;


    // enum 첫번째 인자
    private final HttpStatus httpStatus;

    // enum 두번째 인자
    private final String detail;
}
