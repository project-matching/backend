package com.matching.project.service;

import com.matching.project.dto.enumerate.OAuth;
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

    @Override
    public EmailAuth emailAuthSave(String email)
    {
        EmailAuth emailAuth = EmailAuth.builder()
                .email(email)
                .authToken(UUID.randomUUID().toString())
                .expired(false)
                .build();
        return emailAuthRepository.save(emailAuth);
    }

    @Transactional
    @Override
    public User confirmEmail(EmailAuthRequestDto emailAuthRequestDto) {
        Optional<EmailAuth> emailAuth = emailAuthRepository.findByEmailAndAndAuthToken(emailAuthRequestDto.getEmail(), emailAuthRequestDto.getAuthToken());
        if (emailAuth.isEmpty())
            throw new RuntimeException("Email AuthToken Not Found");
        else if (emailAuth.get().getExpireDate().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Email AuthToken Expired");

        User user = (User) customUserDetailsService.loadUserByUsername(emailAuth.get().getEmail());
        if (user.isEmail_auth())
            throw new RuntimeException("Already Authentication Completed");
        else {
            emailAuth.get().useToken();
            user.emailVerifiedSuccess();
        }
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
        log.info("회원 가입 이메일 전송 to : {}", email);
    }

    @Transactional
    @Override
    public void emailAuthReSend(String email) {
        User user = (User) customUserDetailsService.loadUserByUsername(email);
        if (user.getOauthCategory() != OAuth.NORMAL)
            throw new RuntimeException("Not applicable for Oauth login users");
        else if (user.isEmail_auth())
            throw new RuntimeException("Already Authentication Completed");

        //Delete existing authentication
        emailAuthRepository.deleteAllByEmail(email);

        //emailAuth Re Issue
        EmailAuth emailAuth = emailAuthSave(email);

        //Re Send
        sendConfirmEmail(email, emailAuth.getAuthToken());
    }
}
