package com.matching.project.service;

import com.matching.project.dto.common.PasswordReissueRequestDto;
import com.matching.project.dto.enumerate.EmailAuthPurpose;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.user.EmailAuthReSendRequestDto;
import com.matching.project.dto.user.EmailAuthRequestDto;
import com.matching.project.entity.EmailAuth;
import com.matching.project.entity.User;
import com.matching.project.repository.EmailAuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public User userValidCheck(String email, EmailAuthPurpose purpose) {
        User user = (User) customUserDetailsService.loadUserByUsername(email);
        if (user.getOauthCategory() != OAuth.NORMAL)
            throw new RuntimeException("Not applicable for Oauth login users");
        else if (purpose == EmailAuthPurpose.EMAIL_AUTHENTICATION && user.isEmail_auth())
            throw new RuntimeException("Already Authentication Completed");
        return user;
    }

    public void emailAuthValidCheck(String email, String authToken, EmailAuthPurpose purpose) {
        Optional<EmailAuth> emailAuth = emailAuthRepository.findByEmailAndAndAuthTokenAndPurpose(
                email, authToken, purpose);
        if (emailAuth.isEmpty())
            throw new RuntimeException("Email AuthToken Not Found");
        else if (emailAuth.get().getExpireDate().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Email AuthToken Expired");
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
    public User CheckConfirmEmail(EmailAuthRequestDto dto) {
        // User Valid Check
        User user = userValidCheck(dto.getEmail(), dto.getPurpose());

        // Email Authentication Valid Check
        emailAuthValidCheck(dto.getEmail(), dto.getAuthToken(), dto.getPurpose());

        // Email Verify Save
        user.emailVerifiedSuccess();

        // Delete used Authentication token
        emailAuthRepository.deleteAllByEmailAndPurpose(dto.getEmail(), dto.getPurpose());
        return user;
    }

    @Async
    @Override
    public void sendConfirmEmail(String email, String authToken) {
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setTo(email);
        smm.setSubject("회원가입 이메일 인증");
        smm.setText("http://localhost:8080/v1/user/confirm?email="+email+"&authToken="+authToken);

        javaMailSender.send(smm);
        log.info("회원 가입 이메일 전송 to {}", email);
    }

    @Transactional
    @Override
    public String CheckPasswordReissueEmail(PasswordReissueRequestDto dto) {
        // User Valid Check
        User user = userValidCheck(dto.getEmail(), dto.getPurpose());

        // Email Authentication Valid Check
        emailAuthValidCheck(dto.getEmail(), dto.getAuthToken(), dto.getPurpose());

        // password ReIsuue
        String newPassword = Integer.toString((int)(Math.random()*1000000000));
        user.passwordReIssue(passwordEncoder.encode(newPassword));

        // Delete used Authentication token
        emailAuthRepository.deleteAllByEmailAndPurpose(dto.getEmail(), dto.getPurpose());

        return newPassword;
    }

    @Async
    @Override
    public void sendPasswordReissueEmail(String email, String authToken) {
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setTo(email);
        smm.setSubject("비밀번호 재발급 요청하기");
        smm.setText("http://localhost:8080/v1/common/password/reissue?email="+email+"&authToken="+authToken);

        javaMailSender.send(smm);
        log.info("비밀번호 재발급 요청 to {}", email);
    }

}
