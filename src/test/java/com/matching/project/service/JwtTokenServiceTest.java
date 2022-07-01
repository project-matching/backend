package com.matching.project.service;

import com.matching.project.dto.common.TokenDto;
import org.junit.jupiter.api.DisplayName;
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

    @DisplayName("토큰 생성 및 유효성 체크 성공")
    @Test
    public void jwtTokenSuccess() {
        //given
        Long no = 1L;
        String email = "test@naver.com";

        TokenDto tokenDto = TokenDto.builder()
                .email(email)
                .build();
        jwtTokenService.setSecretKeyForTest("testfasdfasdfsdafasfasfdsafsadfsafsasfasfasfasfasfasfasfasfa");
        String token = jwtTokenService.createToken(tokenDto);

        //when
        boolean result = jwtTokenService.verifyToken(token);
        String userEmail = jwtTokenService.getUserEmail(token);

        //then
        assertThat(result).isTrue();
        assertThat(userEmail).isEqualTo(email);

    }

}