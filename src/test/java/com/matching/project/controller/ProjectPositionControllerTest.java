package com.matching.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matching.project.config.EmbeddedRedisConfig;
import com.matching.project.dto.token.TokenClaimsDto;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.enumerate.Type;
import com.matching.project.entity.*;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import com.matching.project.repository.*;
import com.matching.project.service.JwtTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = EmbeddedRedisConfig.class)
@AutoConfigureMockMvc
@Transactional
class ProjectPositionControllerTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    ProjectPositionRepository projectPositionRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    JwtTokenService jwtTokenService;

    @Autowired
    NotificationRepository notificationRepository;

    // 프로젝트, 유저 저장
    User saveUser(Long no) {
        User user1 = User.builder()
                .name("userName" + no)
                .sex("M")
                .email("testEmail" + no)
                .password("testPassword" + no)
                .github("testGithub" + no)
                .selfIntroduction("testSelfIntroduction" + no)
                .block(false)
                .blockReason(null)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(false)
                .imageNo(0L)
                .position(null)
                .build();

        return userRepository.save(user1);
    }

    @Nested
    @DisplayName("프로젝트 탈퇴")
    class projectPositionWithdraw {
        @Test
        @DisplayName("성공")
        public void success() throws Exception {
            // given
            // 유저 세팅
            User saveUser1 = saveUser(1L);
            User saveUser2 = saveUser(2L);

            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);
            
            // 프로젝트 세팅
            Project project1 = Project.builder()
                    .name("testName1")
                    .createUserName("user1")
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(4)
                    .viewCount(10)
                    .commentCount(10)
                    .build();
            Project saveProject1 = projectRepository.save(project1);
            
            // 포지션 세팅
            Position position1 = Position.builder()
                    .name("testPosition1")
                    .build();
            Position savePosition1 = positionRepository.save(position1);
            
            // 프로젝트 포지션 세팅
            ProjectPosition projectPosition1 = ProjectPosition.builder()
                    .state(true)
                    .project(saveProject1)
                    .position(savePosition1)
                    .user(saveUser1)
                    .creator(false)
                    .build();

            ProjectPosition projectPosition2 = ProjectPosition.builder()
                    .state(true)
                    .project(saveProject1)
                    .position(savePosition1)
                    .user(saveUser2)
                    .creator(false)
                    .build();
            ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);
            ProjectPosition saveProjectPosition2 = projectPositionRepository.save(projectPosition2);

            // when
            String token = jwtTokenService.createToken(new TokenClaimsDto(saveUser1.getEmail())).getAccess();
            ResultActions resultActions = mvc.perform(delete("/v1/projectposition/" + saveProjectPosition1.getNo() + "/withdrawal")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE));

            // then
            resultActions.andDo(print())
                    .andExpect(jsonPath("$.data").value(saveProjectPosition1.getNo()))
                    .andExpect(status().isOk());

            ProjectPosition projectPosition = projectPositionRepository.findUserAndProjectFetchJoinByProjectPositionNo(saveProjectPosition1.getNo()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FIND_PROJECT_POSITION_EXCEPTION));
            List<Notification> notificationList = notificationRepository.findAll();
            assertEquals(projectPosition.getUser(), null);

            assertEquals(notificationList.size(), 1);
            assertEquals(notificationList.get(0).getType(), Type.PROJECT_POSITION_WITHDRAW);
            assertEquals(notificationList.get(0).getTitle(), "[프로젝트 탈퇴] " + saveProject1.getName());
            assertEquals(notificationList.get(0).getContent(), saveUser1.getName() + "이 " + saveProject1.getName() + " 프로젝트에서 탈퇴했습니다.\n"
                    + "현재 남은 참여자 : " + saveUser2.getName());
        }

        @Test
        @DisplayName("실패 : 비로그인 유저")
        public void fail1() throws Exception {
            // when
            ResultActions resultActions = mvc.perform(delete("/v1/projectposition/1/withdrawal")
                    .contentType(MediaType.APPLICATION_JSON_VALUE));

            // then
            resultActions.andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("프로젝트 추방")
    class projectPositionExpulsion {
        @Test
        @DisplayName("성공")
        public void success() throws Exception {
            // given
            // 유저 세팅
            User saveUser1 = saveUser(1L);

            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            // 프로젝트 세팅
            Project project1 = Project.builder()
                    .name("testName1")
                    .createUserName("user1")
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(4)
                    .viewCount(10)
                    .user(saveUser1)
                    .commentCount(10)
                    .build();
            Project saveProject1 = projectRepository.save(project1);

            // 포지션 세팅
            Position position1 = Position.builder()
                    .name("testPosition1")
                    .build();
            Position savePosition1 = positionRepository.save(position1);

            // 프로젝트 포지션 세팅
            ProjectPosition projectPosition1 = ProjectPosition.builder()
                    .state(true)
                    .project(saveProject1)
                    .position(savePosition1)
                    .user(saveUser1)
                    .creator(false)
                    .build();
            ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);

            // when
            String token = jwtTokenService.createToken(new TokenClaimsDto(saveUser1.getEmail())).getAccess();

            ResultActions resultActions = mvc.perform(delete("/v1/projectposition/" + saveProjectPosition1.getNo() + "/expulsion")
                    .header("Authorization", "Bearer " + token)
                    .content(new ObjectMapper().writeValueAsString("testReason"))
                    .contentType(MediaType.APPLICATION_JSON_VALUE));

            // then
            resultActions.andDo(print())
                    .andExpect(jsonPath("$.data").value(true))
                    .andExpect(status().isOk());

            ProjectPosition projectPosition = projectPositionRepository.findUserAndProjectFetchJoinByProjectPositionNo(saveProjectPosition1.getNo()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FIND_PROJECT_POSITION_EXCEPTION));
            List<Notification> notificationList = notificationRepository.findAll();
            assertEquals(projectPosition.getUser(), null);

            assertEquals(notificationList.size(), 1);
            assertEquals(notificationList.get(0).getType(), Type.PROJECT_POSITION_EXPULSION);
            assertEquals(notificationList.get(0).getTitle(), "[프로젝트 추방] " + saveProject1.getName());
            assertEquals(notificationList.get(0).getContent(), saveProject1.getName() +" 프로젝트에서 추방당하셨습니다.");
        }

        @Test
        @DisplayName("실패 : 비로그인 유저")
        public void fail1() throws Exception {
            // when
            ResultActions resultActions = mvc.perform(delete("/v1/projectposition/1/expulsion")
                    .contentType(MediaType.APPLICATION_JSON_VALUE));

            // then
            resultActions.andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }
}