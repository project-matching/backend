package com.matching.project.service;

import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.user.EmailAuthRequestDto;
import com.matching.project.entity.EmailAuth;
import com.matching.project.entity.User;
import com.matching.project.repository.EmailAuthRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class EmailServiceImplTest {
    private static final Long MAX_EXPIRE_TIME = 5L;

    @Spy
    private JavaMailSender javaMailSender;

    @Mock
    private EmailAuthRepository emailAuthRepository;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private EmailServiceImpl emailService;

    @DisplayName("인증 이메일 토큰 기한 초과")
    @Test
    public void emailAuthFail1() {
        //given
        String email = "test9@naver.com";
        String authToken = UUID.randomUUID().toString();

        EmailAuthRequestDto dto = EmailAuthRequestDto.builder()
                .email(email)
                .authToken(authToken)
                .build();

        Optional<EmailAuth> emailAuth = Optional.of(EmailAuth.builder()
                .email(email)
                .authToken(authToken)
                .expired(false)
                .build()
        );
        emailAuth.get().setExpireTimeForTest(LocalDateTime.now().minusMinutes(10));
        given(emailAuthRepository.findByEmailAndAndAuthToken(dto.getEmail(), dto.getAuthToken())).willReturn(emailAuth);

        //when
        Exception e = Assertions.assertThrows(RuntimeException.class, () -> {
            emailService.confirmEmail(dto);
        });

        //then
        assertThat(e.getMessage()).isEqualTo("Email AuthToken Expired");
    }

    @DisplayName("인증 이메일 토큰 기한 초과")
    @Test
    public void emailAuthFail() {
        //given
        String email = "test9@naver.com";
        String authToken = UUID.randomUUID().toString();

        EmailAuthRequestDto dto = EmailAuthRequestDto.builder()
                .email(email)
                .authToken(authToken)
                .build();

        Optional<EmailAuth> emailAuth = Optional.of(EmailAuth.builder()
                .email(email)
                .authToken(authToken)
                .expired(false)
                .build()
        );
        emailAuth.get().setExpireTimeForTest(LocalDateTime.now().minusMinutes(10));
        given(emailAuthRepository.findByEmailAndAndAuthToken(dto.getEmail(), dto.getAuthToken())).willReturn(emailAuth);

        //when
        Exception e = Assertions.assertThrows(RuntimeException.class, () -> {
            emailService.confirmEmail(dto);
        });

        //then
        assertThat(e.getMessage()).isEqualTo("Email AuthToken Expired");
    }

    @DisplayName("이미 인증 받은 이메일")
    @Test
    public void emailAuthFail2() {
        //given
        String email = "test9@naver.com";
        String authToken = UUID.randomUUID().toString();

        EmailAuthRequestDto dto = EmailAuthRequestDto.builder()
                .email(email)
                .authToken(authToken)
                .build();

        Optional<EmailAuth> emailAuth = Optional.of(EmailAuth.builder()
                .email(email)
                .authToken(authToken)
                .expired(false)
                .build()
        );
        given(emailAuthRepository.findByEmailAndAndAuthToken(dto.getEmail(), dto.getAuthToken())).willReturn(emailAuth);


        User user = User.builder()
                .email(email)
                .email_auth(true)
                .build();
        given((User) customUserDetailsService.loadUserByUsername(emailAuth.get().getEmail())).willReturn(user);

        //when
        Exception e = Assertions.assertThrows(RuntimeException.class, () -> {
            emailService.confirmEmail(dto);
        });

        //then
        assertThat(e.getMessage()).isEqualTo("Already Authentication Completed");
    }

    @DisplayName("OAuth 유저는 인증 메일을 재발송 하지 않음")
    @Test
    public void emailAuthFail3() {
        //given
        String email = "test9@naver.com";

        User user = User.builder()
                .email(email)
                .oauthCategory(OAuth.GITHUB)
                .build();
        given((User) customUserDetailsService.loadUserByUsername(email)).willReturn(user);

        //when
        Exception e = Assertions.assertThrows(RuntimeException.class, () -> {
            emailService.emailAuthReSend(email);
        });

        //then
        assertThat(e.getMessage()).isEqualTo("Not applicable for Oauth login users");
    }

    @DisplayName("인증 이메일 인증 성공")
    @Test
    public void emailAuthSuccess() {
        //given
        String email = "test9@naver.com";
        String authToken = UUID.randomUUID().toString();

        EmailAuthRequestDto dto = EmailAuthRequestDto.builder()
                .email(email)
                .authToken(authToken)
                .build();

        Optional<EmailAuth> emailAuth = Optional.of(EmailAuth.builder()
                .email(email)
                .authToken(authToken)
                .expired(false)
                .build()
        );
        given(emailAuthRepository.findByEmailAndAndAuthToken(dto.getEmail(), dto.getAuthToken())).willReturn(emailAuth);

        User user = User.builder()
                .email(email)
                .email_auth(false)
                .build();
        given((User) customUserDetailsService.loadUserByUsername(emailAuth.get().getEmail())).willReturn(user);

        //when
        User resUser = emailService.confirmEmail(dto);

        //then
        assertThat(resUser.isEmail_auth()).isTrue();

    }



}