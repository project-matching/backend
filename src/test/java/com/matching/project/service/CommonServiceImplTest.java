package com.matching.project.service;

import com.matching.project.dto.common.NormalLoginRequestDto;
import com.matching.project.entity.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommonServiceImplTest {

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CommonServiceImpl commonService;

    @DisplayName("이메일 인증을 받지 않은 로그인 테스트")
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
        given(passwordEncoder.matches(any(), any())).willReturn(true);

        //when
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            User user = commonService.normalLogin(dto);
        });

        //then
        assertThat(e.getMessage()).isEqualTo("This is an unsigned email");
    }

    @DisplayName("아이디가 존재하지 않는 경우의 로그인 테스트")
    @Test
    void normalLoginFail2() {
        //given
        String email = "test@naver.com";
        String password = "test";

        NormalLoginRequestDto dto = NormalLoginRequestDto.builder()
                .email(email)
                .password(password)
                .build();

        given((User) customUserDetailsService.loadUserByUsername(email)).willThrow(new UsernameNotFoundException("This is not a registered email"));

        //when
        Exception e = Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            User user = commonService.normalLogin(dto);
        });

        //then
        assertThat(e.getMessage()).isEqualTo("This is not a registered email");
    }

    @DisplayName("패스워드가 틀린 경우의 경우의 로그인 테스트")
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
                .password(passwordEncoder.encode(password))
                .email_auth(true)
                .build();
        given((User) customUserDetailsService.loadUserByUsername(email)).willReturn(mockUser);
        given(passwordEncoder.matches(any(), any())).willReturn(false);

        //when
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            User user = commonService.normalLogin(dto);
        });

        //then
        assertThat(e.getMessage()).isEqualTo("This is an incorrect password.");

    }

    @DisplayName("정상 로그인")
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
        given(passwordEncoder.matches(dto.getPassword(), mockUser.getPassword())).willReturn(true);

        //when
        User user = commonService.normalLogin(dto);

        //then
        assertThat(user.getEmail()).isEqualTo(dto.getEmail());
    }
}