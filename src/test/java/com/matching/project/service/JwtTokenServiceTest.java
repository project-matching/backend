package com.matching.project.service;

import com.matching.project.dto.common.TokenDto;
import com.matching.project.entity.User;
import com.matching.project.error.CustomException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtTokenServiceTest {

    @Mock
    CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    JwtTokenService jwtTokenService;

    @Nested
    @DisplayName("토큰 생성 및 유효성 체크 성공")
    class jwtToken {
        @DisplayName("성공")
        @Test
        public void success() {
            //given
            Long no = 1L;
            String email = "test@naver.com";

            TokenDto tokenDto = TokenDto.builder()
                    .email(email)
                    .build();
            jwtTokenService.setSecretKeyForTest("awlsdfklj1l3kjrlkasjflk2jeofasldkfj2lkj3lrh120efh0");
            String token = jwtTokenService.createToken(tokenDto);

            //when
            boolean result = jwtTokenService.verifyToken(token);

            //then
            assertThat(result).isTrue();
            assertThat(jwtTokenService.getUserEmail(token)).isEqualTo(email);
        }

        @DisplayName("실패 : 유효하지 않는 토큰")
        @Test
        public void fail() {
            //given
            Long no = 1L;
            String email = "test@naver.com";

            TokenDto tokenDto = TokenDto.builder()
                    .email(email)
                    .build();
            jwtTokenService.setSecretKeyForTest("awlsdfklj1l3kjrlkasjflk2jeofasldkfj2lkj3lrh120efh0");
            String token = jwtTokenService.createToken(tokenDto);

            // 생성한 토큰을 다른 시크릿키로 검증하기 위해 셋팅
            jwtTokenService.setSecretKeyForTest("smdjfhkjh2qjkefh2kjef2awerfjk2hk3jhrk298rysuZsag99");

            //when
            boolean result = jwtTokenService.verifyToken(token);

            //then
            assertThat(result).isFalse();
        }
    }
}