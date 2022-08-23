package com.matching.project.service;

import com.matching.project.dto.common.NormalLoginRequestDto;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.entity.User;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

    @Nested
    @DisplayName("일반 로그인")
    class NormalLogin {

        @DisplayName("성공")
        @Test
        void success() {
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

        @DisplayName("실패 : 이메일 인증을 받지 않은 로그인")
        @Test
        void fail1() {
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

        @DisplayName("실패 : 존재하지 않는 이메일")
        @Test
        void fail2() {
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

        @DisplayName("실패 : 패스워드가 틀림")
        @Test
        void fail3() {
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

        @DisplayName("실패 : 차단된 사용자")
        @Test
        void fail4() {
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

        @DisplayName("실패 : 탈퇴한 사용자")
        @Test
        void fail5() {
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
    }

    @Nested
    @DisplayName("로그아웃")
    class Logout {

        @DisplayName("성공")
        @Test
        void success() {
            //given
            Long no = 2L;
            String name = "테스터";
            String email = "leeworld9@gmail.com";

            Optional<User> user = Optional.of(User.builder()
                    .no(no)
                    .name(name)
                    .email(email)
                    .permission(Role.ROLE_USER)
                    .build()
            );

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(user.get(), user.get().getEmail(), user.get().getAuthorities()));

            String accessToken = "askldfjl2kj3lrkjasld";

            //when
            commonService.userLogout(accessToken);

            //then
            //None

            //verify
            verify(jwtTokenService,times(1)).deleteRefreshToken(user.get().getEmail());
            verify(jwtTokenService, times(1)).setBlackList(accessToken);
        }
    }
}