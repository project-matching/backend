package com.matching.project.service;

import com.matching.project.dto.common.PasswordInitRequestDto;
import com.matching.project.dto.common.TokenDto;
import com.matching.project.dto.enumerate.EmailAuthPurpose;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.user.EmailAuthRequestDto;
import com.matching.project.entity.EmailAuth;
import com.matching.project.entity.User;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import com.matching.project.repository.EmailAuthRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private EmailAuthRepository emailAuthRepository;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private EmailServiceImpl emailService;

    @DisplayName("이메일 인증 실패 : 이미 인증 받은 토큰")
    @Test
    void checkConfirmEmailFail1() {
        //given
        Long no = 3L;
        String name = "테스터";
        String email = "leeworld9@gmail.com";
        String authToken = "testToken";
        EmailAuthPurpose purpose = EmailAuthPurpose.EMAIL_AUTHENTICATION;

        User mockUser = User.builder()
                .email(email)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(true)
                .build();

        given((User) customUserDetailsService.loadUserByUsername(email)).willReturn(mockUser);
        EmailAuthRequestDto dto = EmailAuthRequestDto.builder()
                .email(email)
                .authToken(authToken)
                .build();

        //when
        CustomException e = Assertions.assertThrows(CustomException.class, () -> {
            emailService.checkConfirmEmail(dto, purpose);
        });

        //then
        assertThat(e.getErrorCode().getDetail()).isEqualTo("Already Authentication Completed");
    }

    @DisplayName("이메일 인증 실패 : 소셜 사용자가 인증 시도")
    @Test
    void checkConfirmEmailFail2() {
        //given
        Long no = 3L;
        String name = "테스터";
        String email = "leeworld9@gmail.com";
        String authToken = "testToken";
        EmailAuthPurpose purpose = EmailAuthPurpose.EMAIL_AUTHENTICATION;

        User mockUser = User.builder()
                .email(email)
                .oauthCategory(OAuth.GOOGLE)
                .email_auth(true)
                .build();

        given((User) customUserDetailsService.loadUserByUsername(email)).willReturn(mockUser);
        EmailAuthRequestDto dto = EmailAuthRequestDto.builder()
                .email(email)
                .authToken(authToken)
                .build();

        //when
        CustomException e = Assertions.assertThrows(CustomException.class, () -> {
            emailService.checkConfirmEmail(dto, purpose);
        });

        //then
        assertThat(e.getErrorCode().getDetail()).isEqualTo("Social users are not allowed feature");
    }

    @DisplayName("이메일 인증 성공")
    @Test
    void checkConfirmEmailSuccess() {
        //given
        Long no = 3L;
        String name = "테스터";
        String email = "leeworld9@gmail.com";
        String authToken = "testToken";
        EmailAuthPurpose purpose = EmailAuthPurpose.EMAIL_AUTHENTICATION;

        User mockUser = User.builder()
                .email(email)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(false)
                .build();

        EmailAuth emailAuth = EmailAuth.builder()
                .email(email)
                .authToken(authToken)
                .purpose(purpose)
                .build();

        given((User) customUserDetailsService.loadUserByUsername(email)).willReturn(mockUser);
        given(emailAuthRepository.findByEmailAndAndAuthTokenAndPurpose(email, authToken, purpose)).
                willReturn(Optional.ofNullable(emailAuth));

        EmailAuthRequestDto dto = EmailAuthRequestDto.builder()
                .email(email)
                .authToken(authToken)
                .build();

        //when
        User user = emailService.checkConfirmEmail(dto, purpose);


        //then
        assertThat(user.isEmail_auth()).isTrue();
    }

    @DisplayName("비밀번호 초기화 실패 : 소셜 사용자가 인증 시도")
    @Test
    void checkPasswordInitEmailFail1() {
        //given
        Long no = 3L;
        String name = "테스터";
        String email = "leeworld9@gmail.com";
        String password = "test";
        String authToken = "testToken";
        EmailAuthPurpose purpose = EmailAuthPurpose.PASSWORD_INIT;

        User mockUser = User.builder()
                .email(email)
                .oauthCategory(OAuth.GOOGLE)
                .email_auth(true)
                .build();

        given((User) customUserDetailsService.loadUserByUsername(email)).willReturn(mockUser);
        PasswordInitRequestDto dto = PasswordInitRequestDto.builder()
                .email(email)
                .password(password)
                .authToken(authToken)
                .build();

        //when
        CustomException e = Assertions.assertThrows(CustomException.class, () -> {
            emailService.checkPasswordInitEmail(dto, purpose);
        });

        //then
        assertThat(e.getErrorCode().getDetail()).isEqualTo("Social users are not allowed feature");
    }

    @DisplayName("비밀번호 초기화 실패 : 토큰이 존재하지 않는 경우")
    @Test
    void checkPasswordInitEmailFail2() {
        //given
        Long no = 3L;
        String name = "테스터";
        String email = "leeworld9@gmail.com";
        String password = "test";
        String authToken = "testToken";
        EmailAuthPurpose purpose = EmailAuthPurpose.PASSWORD_INIT;

        User mockUser = User.builder()
                .email(email)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(true)
                .build();

        given((User) customUserDetailsService.loadUserByUsername(email)).willReturn(mockUser);
        given(emailAuthRepository.findByEmailAndAndAuthTokenAndPurpose(email, authToken, purpose))
                .willReturn(Optional.empty());
        PasswordInitRequestDto dto = PasswordInitRequestDto.builder()
                .email(email)
                .password(password)
                .authToken(authToken)
                .build();

        //when
        CustomException e = Assertions.assertThrows(CustomException.class, () -> {
            emailService.checkPasswordInitEmail(dto, purpose);
        });

        //then
        assertThat(e.getErrorCode().getDetail()).isEqualTo("Email AuthToken Not Found");
    }

    @DisplayName("비밀번호 초기화 실패 : 토큰이 만료된 경우")
    @Test
    void checkPasswordInitEmailFail3() {
        //given
        Long no = 3L;
        String name = "테스터";
        String email = "leeworld9@gmail.com";
        String password = "test";
        String authToken = "testToken";
        EmailAuthPurpose purpose = EmailAuthPurpose.PASSWORD_INIT;

        User mockUser = User.builder()
                .email(email)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(true)
                .build();

        given((User) customUserDetailsService.loadUserByUsername(email)).willReturn(mockUser);
        given(emailAuthRepository.findByEmailAndAndAuthTokenAndPurpose(email, authToken, purpose)).
                willThrow(new CustomException(ErrorCode.EXPIRED_AUTH_TOKEN_EXCEPTION));
        PasswordInitRequestDto dto = PasswordInitRequestDto.builder()
                .email(email)
                .password(password)
                .authToken(authToken)
                .build();

        //when
        CustomException e = Assertions.assertThrows(CustomException.class, () -> {
            emailService.checkPasswordInitEmail(dto, purpose);
        });

        //then
        assertThat(e.getErrorCode().getDetail()).isEqualTo("Email AuthToken Expired");
    }


    @DisplayName("비밀번호 초기화 성공")
    @Test
    void checkPasswordInitEmailSuccess() {
        //given
        Long no = 3L;
        String name = "테스터";
        String email = "leeworld9@gmail.com";
        String oldPassword = "test";
        String authToken = "testToken";
        EmailAuthPurpose purpose = EmailAuthPurpose.PASSWORD_INIT;

        User mockUser = User.builder()
                .email(email)
                .oauthCategory(OAuth.NORMAL)
                .password(passwordEncoder.encode(oldPassword))
                .email_auth(true)
                .build();

        EmailAuth emailAuth = EmailAuth.builder()
                .email(email)
                .authToken(authToken)
                .purpose(purpose)
                .build();

        given((User) customUserDetailsService.loadUserByUsername(email)).willReturn(mockUser);
        given(emailAuthRepository.findByEmailAndAndAuthTokenAndPurpose(email, authToken, purpose)).
                willReturn(Optional.ofNullable(emailAuth));

        String newPassword = "test2";
        PasswordInitRequestDto dto = PasswordInitRequestDto.builder()
                .email(email)
                .password(newPassword)
                .authToken(authToken)
                .build();

        //when
        User user = emailService.checkPasswordInitEmail(dto, purpose);

        //then
        assertThat(passwordEncoder.matches(newPassword, user.getPassword())).isTrue();

    }
}