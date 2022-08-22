package com.matching.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matching.project.dto.common.NormalLoginRequestDto;
import com.matching.project.dto.common.PasswordInitRequestDto;
import com.matching.project.dto.token.TokenClaimsDto;
import com.matching.project.dto.enumerate.EmailAuthPurpose;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.entity.EmailAuth;
import com.matching.project.entity.User;
import com.matching.project.repository.*;
import com.matching.project.service.EmailService;
import com.matching.project.service.JwtTokenService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CommonControllerTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenService jwtTokenService;

    @Autowired
    EmailService emailService;


    User saveUser() {
        User user = User.builder()
                .name("testUser")
                .sex("M")
                .email("leeworld9@naver.com")
                .password(passwordEncoder.encode("test"))
                .github(null)
                .selfIntroduction(null)
                .block(false)
                .blockReason(null)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(true)
                .imageNo(null)
                .position(null)
                .build();

        return userRepository.save(user);
    }

    String getToken(User user) {
        return jwtTokenService.createToken(TokenClaimsDto.builder().email(user.getEmail()).build());
    }


    @Nested
    @DisplayName("일반 로그인")
    class NormalLogin {
        @DisplayName("성공")
        @Test
        void success() throws Exception{
            //given
            User user = saveUser();

            NormalLoginRequestDto requestDto = NormalLoginRequestDto.builder()
                    .email(user.getEmail())
                    .password("test")
                    .build();

            //when
            ResultActions resultActions = mvc.perform(post("/v1/common/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(requestDto)));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("로그아웃")
    class logout {
        @DisplayName("성공")
        @Test
        void success() throws Exception {
            //given
            User user = saveUser();
            String token = getToken(user);
            //when
            ResultActions resultActions = mvc.perform(get("/v1/common/logout")
                    .header("Authorization", "Bearer " + token));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("비밀번호 초기화")
    class userPasswordUpdate {

        @DisplayName("성공")
        @Test
        void success() throws Exception {
            //given
            User user = saveUser();
            EmailAuth emailAuth = emailService.emailAuthTokenSave(user.getEmail(), EmailAuthPurpose.PASSWORD_INIT);

            PasswordInitRequestDto requestDto = PasswordInitRequestDto.builder()
                    .email(user.getEmail())
                    .password("updateTest")
                    .authToken(emailAuth.getAuthToken())
                    .build();

            //when
            ResultActions resultActions = mvc.perform(patch("/v1/common/password/confirm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(requestDto))
            );

            //then
            resultActions.andDo(print())
                    .andExpect(status().isOk());
            assertThat(passwordEncoder.matches(requestDto.getPassword(), user.getPassword())).isTrue();
        }
    }
}