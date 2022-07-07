package com.matching.project.service;

import com.matching.project.dto.common.PasswordReissueRequestDto;
import com.matching.project.dto.enumerate.EmailAuthPurpose;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.user.EmailAuthReSendRequestDto;
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
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Spy
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailAuthRepository emailAuthRepository;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private EmailServiceImpl emailService;

    @DisplayName("패스워드 재발급 성공")
    @Test
    public void passwordReissueSuccess(){
        //given
        String email = "test9@naver.com";
        String authToken = UUID.randomUUID().toString();
        EmailAuthPurpose purpose = EmailAuthPurpose.PASSWORD_REISSUE;

        PasswordReissueRequestDto dto = PasswordReissueRequestDto.builder()
                .email(email)
                .authToken(authToken)
                .purpose(purpose)
                .build();

        Optional<User> user = Optional.ofNullable(User.builder()
                .email(email)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(true)
                .build()
        );

        Optional<EmailAuth> emailAuth = Optional.ofNullable(EmailAuth.builder()
                .email(email)
                .purpose(purpose)
                .authToken(authToken)
                .build()
        );

        given(customUserDetailsService.loadUserByUsername(email)).willReturn(user.get());
        given(emailAuthRepository.findByEmailAndAndAuthTokenAndPurpose(dto.getEmail(), dto.getAuthToken(), dto.getPurpose())).willReturn(emailAuth);


        //when
        String newPassword = emailService.CheckPasswordReissueEmail(dto);

        //then
        assertThat(newPassword).isNotEmpty();
    }

    @DisplayName("인증 이메일 토큰 기한 초과")
    @Test
    public void emailAuthFail1() {
        //given
        String email = "test9@naver.com";
        String authToken = UUID.randomUUID().toString();
        EmailAuthPurpose purpose = EmailAuthPurpose.EMAIL_AUTHENTICATION;

        Optional<User> user = Optional.ofNullable(User.builder()
                .email(email)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(false)
                .build()
        );

        Optional<EmailAuth> emailAuth = Optional.ofNullable(EmailAuth.builder()
                .email(email)
                .purpose(purpose)
                .build()
        );
        emailAuth.get().setExpireTimeForTest(LocalDateTime.now().minusMinutes(10));

        EmailAuthRequestDto dto = EmailAuthRequestDto.builder()
                .email(email)
                .authToken(authToken)
                .purpose(purpose)
                .build();


        given(customUserDetailsService.loadUserByUsername(email)).willReturn(user.get());
        given(emailAuthRepository.findByEmailAndAndAuthTokenAndPurpose(dto.getEmail(), dto.getAuthToken(), dto.getPurpose())).willReturn(emailAuth);

        //when
        Exception e = Assertions.assertThrows(RuntimeException.class, () -> {
            emailService.CheckConfirmEmail(dto);
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
        EmailAuthPurpose purpose = EmailAuthPurpose.EMAIL_AUTHENTICATION;

        Optional<User> user = Optional.ofNullable(User.builder()
                .email(email)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(true)
                .build()
        );

        EmailAuthRequestDto dto = EmailAuthRequestDto.builder()
                .email(email)
                .authToken(authToken)
                .purpose(purpose)
                .build();

        given(customUserDetailsService.loadUserByUsername(email)).willReturn(user.get());

        //when
        Exception e = Assertions.assertThrows(RuntimeException.class, () -> {
            emailService.CheckConfirmEmail(dto);
        });

        //then
        assertThat(e.getMessage()).isEqualTo("Already Authentication Completed");
    }

    @DisplayName("OAuth 유저는 인증 메일을 재발송 하지 않음")
    @Test
    public void emailAuthFail3() {
        //given
        String email = "test9@naver.com";
        String authToken = UUID.randomUUID().toString();
        EmailAuthPurpose purpose = EmailAuthPurpose.EMAIL_AUTHENTICATION;

        Optional<User> user = Optional.ofNullable(User.builder()
                .email(email)
                .oauthCategory(OAuth.GOOGLE)
                .email_auth(true)
                .build()
        );

        EmailAuthRequestDto dto = EmailAuthRequestDto.builder()
                .email(email)
                .authToken(authToken)
                .purpose(purpose)
                .build();

        given(customUserDetailsService.loadUserByUsername(email)).willReturn(user.get());

        //when
        Exception e = Assertions.assertThrows(RuntimeException.class, () -> {
            emailService.CheckConfirmEmail(dto);
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
        EmailAuthPurpose purpose = EmailAuthPurpose.EMAIL_AUTHENTICATION;

        Optional<User> user = Optional.ofNullable(User.builder()
                .email(email)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(false)
                .build()
        );

        Optional<EmailAuth> emailAuth = Optional.ofNullable(EmailAuth.builder()
                .email(email)
                .purpose(purpose)
                .build()
        );

        EmailAuthRequestDto dto = EmailAuthRequestDto.builder()
                .email(email)
                .authToken(authToken)
                .purpose(purpose)
                .build();

        given(customUserDetailsService.loadUserByUsername(email)).willReturn(user.get());
        given(emailAuthRepository.findByEmailAndAndAuthTokenAndPurpose(dto.getEmail(), dto.getAuthToken(), dto.getPurpose())).willReturn(emailAuth);

        //when
        User resUser = emailService.CheckConfirmEmail(dto);

        //then
        assertThat(resUser.isEmail_auth()).isTrue();
    }



}