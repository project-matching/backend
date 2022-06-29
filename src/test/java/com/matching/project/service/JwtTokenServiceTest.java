package com.matching.project.service;

import com.matching.project.dto.common.TokenDto;
import com.matching.project.dto.enumerate.Role;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;

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
                .no(no)
                .email(email)
                .build();

        String jwtToken = jwtTokenService.createToken(tokenDto, Role.ROLE_USER);

        //when
        String token = jwtToken.replaceAll("^Bearer( )*", "");
        boolean result = jwtTokenService.verifyToken(token);
        Long userNo = jwtTokenService.getUserNo(token);
        String userEmail = jwtTokenService.getUserEmail(token);

        //then
        assertThat(result).isTrue();
        assertThat(userNo).isEqualTo(no);
        assertThat(userEmail).isEqualTo(email);

    }

}