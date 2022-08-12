package com.matching.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matching.project.dto.common.TokenDto;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.enumerate.Type;
import com.matching.project.dto.notification.NotificationSendRequestDto;
import com.matching.project.dto.user.UserBlockRequestDto;
import com.matching.project.entity.Notification;
import com.matching.project.entity.User;
import com.matching.project.repository.NotificationRepository;
import com.matching.project.repository.PositionRepository;
import com.matching.project.repository.UserRepository;
import com.matching.project.service.JwtTokenService;
import com.matching.project.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

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
public class NotificationControllerTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenService jwtTokenService;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    NotificationService notificationService;

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
                .email_auth(true)
                .imageNo(null)
                .position(null)
                .build();

        return userRepository.save(user);
    }

    String getToken(User user) {
        return jwtTokenService.createToken(TokenDto.builder().email(user.getEmail()).build());
    }

    List<Notification> saveNotification(User user1, User user2) {
        Notification n1 = notificationService.sendNotification(Type.NOTICE, null, "t1", "c1");
        Notification n2 = notificationService.sendNotification(Type.PROJECT_PARTICIPATION_REFUSE, user2.getEmail(), "t2", "c2");
        Notification n3 = notificationService.sendNotification(Type.PROJECT_PARTICIPATION_SUCCESS,  user1.getEmail(), "t3", "c3");
        return List.of(n1,n2,n3);
    }

    @Nested
    @DisplayName("공지 알림 전송 (관리자)")
    class notificationSend {

        @DisplayName("성공")
        @Test
        void success() throws Exception {
            //given
            User user = saveAdmin();
            String token = getToken(user);

            NotificationSendRequestDto requestDto = NotificationSendRequestDto.builder()
                    .title("title")
                    .content("content")
                    .build();

            //when
            ResultActions resultActions = mvc.perform(post("/v1/notification")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(new ObjectMapper().writeValueAsString(requestDto)));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isOk());
            List<Notification> notificationList = notificationRepository.findAll();
            assertThat(notificationList.get(0).getTitle()).isEqualTo(requestDto.getTitle());
            assertThat(notificationList.get(0).getContent()).isEqualTo(requestDto.getContent());
            assertThat(notificationList.get(0).getType().toString()).isEqualTo(Type.NOTICE.toString());
            assertThat(notificationList.get(0).getUser()).isNull();
        }

        @DisplayName("실패 : 로그인 하지 않는 경우")
        @Test
        void fail1() throws Exception {
            //given

            NotificationSendRequestDto requestDto = NotificationSendRequestDto.builder()
                    .title("title")
                    .content("content")
                    .build();

            //when
            ResultActions resultActions = mvc.perform(post("/v1/notification")
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

            NotificationSendRequestDto requestDto = NotificationSendRequestDto.builder()
                    .title("title")
                    .content("content")
                    .build();

            //when
            ResultActions resultActions = mvc.perform(post("/v1/notification")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(new ObjectMapper().writeValueAsString(requestDto)));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("알림 목록 조회")
    class notificationList {

        @DisplayName("성공")
        @Test
        void success() throws Exception {
            //given
            User user1 = saveUser();
            User user2 = saveAdmin();
            String token = getToken(user1);
            List<Notification> n = saveNotification(user1, user2);

            //when
            ResultActions resultActions = mvc.perform(get("/v1/notification?size=1")
                    .header("Authorization", "Bearer " + token));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content.[0].type").value("PROJECT_PARTICIPATION_SUCCESS"))
                    .andExpect(jsonPath("$.data.content.[0].title").value("t3"))
                    .andExpect(jsonPath("$.data.last").value(false));

        }

        @DisplayName("실패 : 로그인 하지 않는 경우")
        @Test
        void fail1() throws Exception {
            //given
            User user1 = saveUser();
            User user2 = saveAdmin();
            List<Notification> n = saveNotification(user1, user2);

            //when
            ResultActions resultActions = mvc.perform(get("/v1/notification"));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("알림 상세 조회")
    class notificationInfo {

        @DisplayName("성공")
        @Test
        void success() throws Exception {
            //given
            User user1 = saveUser();
            User user2 = saveAdmin();
            String token = getToken(user1);
            List<Notification> n = saveNotification(user1, user2);

            //when
            ResultActions resultActions = mvc.perform(get("/v1/notification/" + n.get(2).getNo())
                    .header("Authorization", "Bearer " + token));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.type").value("PROJECT_PARTICIPATION_SUCCESS"))
                    .andExpect(jsonPath("$.data.title").value("t3"))
                    .andExpect(jsonPath("$.data.content").value("c3"));
        }

        @DisplayName("실패 : 로그인 하지 않는 경우")
        @Test
        void fail1() throws Exception {
            //given
            User user1 = saveUser();
            User user2 = saveAdmin();
            List<Notification> n = saveNotification(user1, user2);

            //when
            ResultActions resultActions = mvc.perform(get("/v1/notification/" + n.get(2)));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }
}