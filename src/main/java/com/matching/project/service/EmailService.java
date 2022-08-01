package com.matching.project.service;

import com.matching.project.dto.common.PasswordInitRequestDto;
import com.matching.project.dto.enumerate.EmailAuthPurpose;
import com.matching.project.dto.user.EmailAuthRequestDto;
import com.matching.project.entity.EmailAuth;
import com.matching.project.entity.User;

public interface EmailService {
    // 유요한 인증 토큰 생성 및 저장
    EmailAuth emailAuthTokenSave(String email, EmailAuthPurpose purpose);

    // 메일 발송 전 작업 ( valid check, email auth token gen )
    EmailAuth beforeSendWork(String email, EmailAuthPurpose purpose);

    // 인증 이메일 '확인' -> 유효성 체크 포함
    User checkConfirmEmail(EmailAuthRequestDto emailAuthRequestDto, EmailAuthPurpose purpose);

    // 인증 이메일 발송
    void sendConfirmEmail(String email, String authToken);

    // 비밀번호 초기 이메일 '확인' -> 유효성 체크 포함
    User checkPasswordInitEmail(PasswordInitRequestDto dto, EmailAuthPurpose purpose);

    // 비밀번호 초기화 이메일 발송
    void sendPasswordInitEmail(String email, String authToken);


}
