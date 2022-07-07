package com.matching.project.service;

import com.matching.project.dto.common.PasswordReissueRequestDto;
import com.matching.project.dto.enumerate.EmailAuthPurpose;
import com.matching.project.dto.user.EmailAuthReSendRequestDto;
import com.matching.project.dto.user.EmailAuthRequestDto;
import com.matching.project.entity.EmailAuth;
import com.matching.project.entity.User;

public interface EmailService {
    // 유요한 인증 토큰 생성 및 저장
    EmailAuth emailAuthTokenSave(String email, EmailAuthPurpose purpose);

    // 메일 발송 전 작업 ( valid check, email auth token gen )
    EmailAuth beforeSendWork(String email, EmailAuthPurpose purpose);

    // 인증 이메일 '확인' -> 유효성 체크 포함
    User CheckConfirmEmail(EmailAuthRequestDto emailAuthRequestDto);

    // 인증 이메일 발송
    void sendConfirmEmail(String email, String authToken);

    // 비밀번호 재발급 이메일 '확인' -> 유효성 체크 포함
    String CheckPasswordReissueEmail(PasswordReissueRequestDto dto);

    // 비밀번호 재발급 이메일 발송
    void sendPasswordReissueEmail(String email, String authToken);

}
