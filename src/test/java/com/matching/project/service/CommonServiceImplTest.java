package com.matching.project.service;

import com.matching.project.dto.common.NormalLoginRequestDto;
import com.matching.project.entity.User;
import com.matching.project.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommonServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CommonServiceImpl commonService;

    @DisplayName("아이디가 존재하지 않는 경우의 로그인 테스트")
    @Test
    void normalLoginFail1() {
        //given
        String email = "test@naver.com";
        String password = "test";

        NormalLoginRequestDto dto = NormalLoginRequestDto.builder()
                .email(email)
                .password(password)
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        //when
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            User user = commonService.normalLogin(dto);
        });

        //then
        assertThat(e.getMessage()).isEqualTo("가입되지 않은 E-MAIL 입니다.");
    }

    @DisplayName("패스워드가 틀린 경우의 경우의 로그인 테스트")
    @Test
    void normalLoginFail2() {
        //given
        String email = "test@naver.com";
        String password = "test";

        NormalLoginRequestDto dto = NormalLoginRequestDto.builder()
                .email(email)
                .password(password)
                .build();

        Optional<User> optionalUser = Optional.of(User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .build()
        );
        given(userRepository.findByEmail(email)).willReturn(optionalUser);
        given(passwordEncoder.matches(any(), any())).willReturn(false);

        //when
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            User user = commonService.normalLogin(dto);
        });

        //then
        assertThat(e.getMessage()).isEqualTo("잘못된 비밀번호입니다.");

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

        Optional<User> optionalUser = Optional.of(User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .build()
        );
        given(userRepository.findByEmail(email)).willReturn(optionalUser);
        given(passwordEncoder.matches(dto.getPassword(), optionalUser.get().getPassword())).willReturn(true);

        //when
        User user = commonService.normalLogin(dto);

        //then
        assertThat(user.getEmail()).isEqualTo(dto.getEmail());
    }
}