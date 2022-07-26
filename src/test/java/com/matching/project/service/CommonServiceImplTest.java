package com.matching.project.service;

import com.matching.project.dto.common.NormalLoginRequestDto;
import com.matching.project.dto.common.TokenDto;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.entity.Position;
import com.matching.project.entity.User;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommonServiceImplTest {

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private CommonServiceImpl commonService;

    @DisplayName("노말 로그인 실패 : 이메일 인증을 받지 않은 로그인")
    @Test
    void normalLoginFail1() {
        //given
        String email = "test@naver.com";
        String password = "test";

        NormalLoginRequestDto dto = NormalLoginRequestDto.builder()
                .email(email)
                .password(password)
                .build();

        User mockUser = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .email_auth(false)
                .build();

        given((User) customUserDetailsService.loadUserByUsername(email)).willReturn(mockUser);

        //when
        CustomException e = Assertions.assertThrows(CustomException.class, () -> {
            commonService.normalLogin(dto);
        });

        //then
        assertThat(e.getErrorCode().getDetail()).isEqualTo("This is an unsigned email");
    }

    @DisplayName("노말 로그인 실패 : 존재하지 않는 이메일")
    @Test
    void normalLoginFail2() {
        //given
        String email = "test@naver.com";
        String password = "test";

        NormalLoginRequestDto dto = NormalLoginRequestDto.builder()
                .email(email)
                .password(password)
                .build();

        given((User) customUserDetailsService.loadUserByUsername(email)).willThrow(new CustomException(ErrorCode.NOT_REGISTERED_EMAIL_EXCEPTION));

        //when
        CustomException e = Assertions.assertThrows(CustomException.class, () -> {
            commonService.normalLogin(dto);
        });

        //then
        assertThat(e.getErrorCode().getDetail()).isEqualTo("This is not a registered email");
    }

    @DisplayName("노말 로그인 실패 : 패스워드가 틀림")
    @Test
    void normalLoginFail3() {
        //given
        String email = "test@naver.com";
        String password = "test";

        NormalLoginRequestDto dto = NormalLoginRequestDto.builder()
                .email(email)
                .password(password)
                .build();

        User mockUser = User.builder()
                .email(email)
                .password(passwordEncoder.encode("whowho"))
                .email_auth(true)
                .build();
        given((User) customUserDetailsService.loadUserByUsername(email)).willReturn(mockUser);

        //when
        CustomException e = Assertions.assertThrows(CustomException.class, () -> {
            commonService.normalLogin(dto);
        });

        //then
        assertThat(e.getErrorCode().getDetail()).isEqualTo("This is an incorrect password");

    }

    @DisplayName("노말 로그인 실패 : 차단된 사용자")
    @Test
    void normalLoginFail4() {
        //given
        String email = "test@naver.com";
        String password = "test";

        User mockUser = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .block(true)
                .email_auth(false)
                .build();

        NormalLoginRequestDto dto = NormalLoginRequestDto.builder()
                .email(email)
                .password(password)
                .build();

        given(customUserDetailsService.loadUserByUsername(email)).willReturn(mockUser);

        //when
        CustomException e = Assertions.assertThrows(CustomException.class, () -> {
            commonService.normalLogin(dto);
        });

        //then
        assertThat(e.getErrorCode().getDetail()).isEqualTo("This is blocked User ID");
    }

    @DisplayName("노말 로그인 실패 : 탈퇴한 사용자")
    @Test
    void normalLoginFail5() {
        //given
        String email = "test@naver.com";
        String password = "test";

        User mockUser = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .withdrawal(true)
                .email_auth(true)
                .build();

        NormalLoginRequestDto dto = NormalLoginRequestDto.builder()
                .email(email)
                .password(password)
                .build();

        given(customUserDetailsService.loadUserByUsername(email)).willReturn(mockUser);

        //when
        CustomException e = Assertions.assertThrows(CustomException.class, () -> {
            commonService.normalLogin(dto);
        });

        //then
        assertThat(e.getErrorCode().getDetail()).isEqualTo("This is withdrawal User ID");
    }

    @DisplayName("노말 로그인 성공")
    @Test
    void normalLoginSuccess() {
        //given
        String email = "test@naver.com";
        String password = "test";

        NormalLoginRequestDto dto = NormalLoginRequestDto.builder()
                .email(email)
                .password(password)
                .build();

        User mockUser = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .email_auth(true)
                .build();

        given((User) customUserDetailsService.loadUserByUsername(email)).willReturn(mockUser);

        //when
        User user = commonService.normalLogin(dto);

        //then
        assertThat(email).isEqualTo(user.getEmail());
    }
}