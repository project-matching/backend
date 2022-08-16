package com.matching.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matching.project.dto.common.NormalLoginRequestDto;
import com.matching.project.dto.common.TokenDto;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.position.PositionRequestDto;
import com.matching.project.entity.Position;
import com.matching.project.entity.User;
import com.matching.project.repository.*;
import com.matching.project.service.EmailService;
import com.matching.project.service.JwtTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.JsonPath;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class PositionControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenService jwtTokenService;

    @Autowired
    PositionRepository positionRepository;

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
                .email_auth(false)
                .imageNo(null)
                .position(null)
                .build();

        return userRepository.save(user);
    }

    User saveAdmin() {
        User user = User.builder()
                .name("testUser")
                .sex("M")
                .email("leeworld9@gmail.com")
                .password(passwordEncoder.encode("test"))
                .github(null)
                .selfIntroduction(null)
                .block(false)
                .blockReason(null)
                .permission(Role.ROLE_ADMIN)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(false)
                .imageNo(null)
                .position(null)
                .build();

        return userRepository.save(user);
    }

    String getToken(User user) {
        return jwtTokenService.createToken(TokenDto.builder().email(user.getEmail()).build());
    }

    List<Position> savePosition() {
        Position p1 = positionRepository.save(Position.builder().name("FRONTEND").build());
        Position p2 = positionRepository.save(Position.builder().name("BACKEND").build());
        Position p3 = positionRepository.save(Position.builder().name("FULLSTACK").build());
        return List.of(p1, p2, p3);
    }


    @Nested
    @DisplayName("포지션 리스트 조회")
    class positionRegisterForm {

        @DisplayName("성공")
        @Test
        void success() throws Exception {
            //given
            User user = saveAdmin();
            String token = getToken(user);
            savePosition();

            //when
            ResultActions resultActions = mvc.perform(get("/v1/position")
                    .header("Authorization", "Bearer " + token));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.[0].positionName").value("FRONTEND"))
                    .andExpect(jsonPath("$.data.[1].positionName").value("BACKEND"))
                    .andExpect(jsonPath("$.data.[2].positionName").value("FULLSTACK"));
        }

        @DisplayName("실패 : 로그인 하지 않는 경우")
        @Test
        void fail1() throws Exception {
            //given
            savePosition();

            //when
            ResultActions resultActions = mvc.perform(get("/v1/position"));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("포지션 추가 (관리자)")
    class positionRegister {

        @DisplayName("성공")
        @Test
        void success() throws Exception {
            //given
            User user = saveAdmin();
            String token = getToken(user);

            PositionRequestDto requestDto = PositionRequestDto.builder()
                    .positionName("TEST")
                    .build();

            //when
            ResultActions resultActions = mvc.perform(post("/v1/position")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(new ObjectMapper().writeValueAsString(requestDto)));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isOk());
            List<Position> positionList = positionRepository.findAll();
            assertThat(positionList.get(0).getName()).isEqualTo(requestDto.getPositionName());
        }

        @DisplayName("실패 : 로그인 하지 않는 경우")
        @Test
        void fail1() throws Exception {
            //given
            PositionRequestDto requestDto = PositionRequestDto.builder()
                    .positionName("TEST")
                    .build();

            //when
            ResultActions resultActions = mvc.perform(post("/v1/position")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(new ObjectMapper().writeValueAsString(requestDto)));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @DisplayName("실패 : 접근 권한이 없는 경우")
        @Test
        void fail2() throws Exception {
            //given
            User user = saveUser();
            String token = getToken(user);

            PositionRequestDto requestDto = PositionRequestDto.builder()
                    .positionName("TEST")
                    .build();

            //when
            ResultActions resultActions = mvc.perform(post("/v1/position")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(new ObjectMapper().writeValueAsString(requestDto)));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isForbidden());
        }
    }
    @Nested
    @DisplayName("포지션 수정 (관리자)")
    class positionUpdate {

        @DisplayName("성공")
        @Test
        void success() throws Exception {
            //given
            User user = saveAdmin();
            String token = getToken(user);
            List<Position> p = savePosition();

            PositionRequestDto requestDto = PositionRequestDto.builder()
                    .positionName("TEST")
                    .build();

            //when
            ResultActions resultActions = mvc.perform(put("/v1/position/" + p.get(0).getNo())
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(new ObjectMapper().writeValueAsString(requestDto)));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isOk());
            List<Position> positionList = positionRepository.findAll();
            assertThat(positionList.get(0).getName()).isEqualTo(requestDto.getPositionName());
        }

        @DisplayName("실패 : 로그인 하지 않는 경우")
        @Test
        void fail1() throws Exception {
            //given
            List<Position> p = savePosition();

            PositionRequestDto requestDto = PositionRequestDto.builder()
                    .positionName("TEST")
                    .build();

            //when
            ResultActions resultActions = mvc.perform(put("/v1/position/" + p.get(0).getNo())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(new ObjectMapper().writeValueAsString(requestDto)));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @DisplayName("실패 : 접근 권한이 없는 경우")
        @Test
        void fail2() throws Exception {
            //given
            User user = saveUser();
            String token = getToken(user);
            List<Position> p = savePosition();

            PositionRequestDto requestDto = PositionRequestDto.builder()
                    .positionName("TEST")
                    .build();

            //when
            ResultActions resultActions = mvc.perform(put("/v1/position/" + p.get(0).getNo())
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(new ObjectMapper().writeValueAsString(requestDto)));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isForbidden());
        }
    }
}