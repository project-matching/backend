package com.matching.project.service;

import com.matching.project.dto.common.PasswordInitRequestDto;
import com.matching.project.dto.enumerate.EmailAuthPurpose;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.user.EmailAuthRequestDto;
import com.matching.project.entity.EmailAuth;
import com.matching.project.entity.User;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import com.matching.project.repository.EmailAuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@EnableAsync
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final EmailAuthRepository emailAuthRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Value("${front.url}")
    private String frontUrl;

    public User userValidCheck(String email, EmailAuthPurpose purpose) {
        User user = (User) customUserDetailsService.loadUserByUsername(email);
        if (user.getOauthCategory() != OAuth.NORMAL)
            throw new CustomException(ErrorCode.SOCIAL_USER_NOT_ALLOWED_FEATURE_EXCEPTION);
        else if (purpose == EmailAuthPurpose.EMAIL_AUTHENTICATION && user.isEmail_auth())
            throw new CustomException(ErrorCode.ALREADY_AUTHENTICATED_AUTH_TOKEN_EXCEPTION);
        return user;
    }

    public void emailAuthValidCheck(String email, String authToken, EmailAuthPurpose purpose) {
        Optional<EmailAuth> emailAuth = emailAuthRepository.findByEmailAndAuthTokenAndPurpose(
                email, authToken, purpose);
        if (emailAuth.isEmpty())
            throw new CustomException(ErrorCode.NOT_FOUND_AUTH_TOKEN_EXCEPTION);
        else if (emailAuth.get().getExpireDate().isBefore(LocalDateTime.now()))
            throw new CustomException(ErrorCode.EXPIRED_AUTH_TOKEN_EXCEPTION);
    }

    @Transactional
    @Override
    public EmailAuth emailAuthTokenSave(String email, EmailAuthPurpose purpose)
    {
        EmailAuth emailAuth = EmailAuth.builder()
                .email(email)
                .authToken(UUID.randomUUID().toString())
                .purpose(purpose)
                .build();
        return emailAuthRepository.save(emailAuth);
    }

    @Transactional
    @Override
    public EmailAuth beforeSendWork(String email, EmailAuthPurpose purpose) {
        // User Valid Check
        userValidCheck(email, purpose);

        // Delete expired authentication token OR unused authentication token
        emailAuthRepository.deleteAllByEmailAndPurpose(email, purpose);

        // Valid Authentication Token Save
        EmailAuth emailAuth = emailAuthTokenSave(email, purpose);

        return emailAuth;
    }

    @Transactional
    @Override
    public User checkConfirmEmail(EmailAuthRequestDto dto, EmailAuthPurpose purpose) {
        // User Valid Check
        User user = userValidCheck(dto.getEmail(), purpose);

        // Email Authentication Valid Check
        emailAuthValidCheck(dto.getEmail(), dto.getAuthToken(), purpose);

        // Email Verify Save
        user.emailVerifiedSuccess();

        // Delete used Authentication token
        emailAuthRepository.deleteAllByEmailAndPurpose(dto.getEmail(), purpose);

        return user;
    }

    @Async
    @Override
    public void sendConfirmEmail(String email, String authToken) {
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setTo(email);
        smm.setSubject("회원가입 이메일 인증");
        smm.setText(frontUrl + "/auth/signup?email="+email+"&authToken="+authToken);

        javaMailSender.send(smm);
        log.info("회원가입 이메일 전송 to {}", email);
    }

    @Transactional
    @Override
    public User checkPasswordInitEmail(PasswordInitRequestDto dto, EmailAuthPurpose purpose) {
        // User Valid Check
        User user = userValidCheck(dto.getEmail(), purpose);

        // Email Authentication Valid Check
        emailAuthValidCheck(dto.getEmail(), dto.getAuthToken(), purpose);

        // Password Update
        user.updatePassword(passwordEncoder, dto.getPassword());

        // Delete used Authentication token
        emailAuthRepository.deleteAllByEmailAndPurpose(dto.getEmail(), purpose);

        return user;
    }

    @Async
    @Override
    public void sendPasswordInitEmail(String email, String authToken) {
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setTo(email);
        smm.setSubject("비밀번호 재발급 요청하기");
        // 임시 -> 프론트 주소로 바뀌여야함.
        smm.setText(frontUrl + "/auth/pwd?email="+email+"&authToken="+authToken);

        javaMailSender.send(smm);
        log.info("비밀번호 재발급 요청 to {}", email);
    }
}
