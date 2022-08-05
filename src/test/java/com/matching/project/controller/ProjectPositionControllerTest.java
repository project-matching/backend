package com.matching.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matching.project.dto.common.TokenDto;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.entity.Position;
import com.matching.project.entity.Project;
import com.matching.project.entity.ProjectPosition;
import com.matching.project.entity.User;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import com.matching.project.repository.PositionRepository;
import com.matching.project.repository.ProjectPositionRepository;
import com.matching.project.repository.ProjectRepository;
import com.matching.project.repository.UserRepository;
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
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
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

    // 프로젝트, 유저 저장
    User saveUser() {
        User user1 = User.builder()
                .name("userName1")
                .sex("M")
                .email("wkemrm12@naver.com")
                .password("testPassword")
                .github("testGithub")
                .selfIntroduction("testSelfIntroduction")
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
    @DisplayName("프로젝트 탈퇴 테스트")
    class testProjectPositionWithdraw {
        @Test
        @DisplayName("성공 테스트")
        public void testSuccess() throws Exception {
            // given
            // 유저 세팅
            User saveUser = saveUser();

            LocalDateTime createDate = LocalDateTime.now();
            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);
            
            // 프로젝트 세팅
            Project project1 = Project.builder()
                    .name("testName1")
                    .createUserName("user1")
                    .createDate(createDate)
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(4)
                    .delete(true)
                    .deleteReason(null)
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
                    .user(saveUser)
                    .creator(false)
                    .build();
            ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);

            // when
            String token = jwtTokenService.createToken(new TokenDto(saveUser.getEmail()));
            ResultActions resultActions = mvc.perform(delete("/v1/projectposition/" + saveProjectPosition1.getNo() + "/withdrawal")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE));

            // then
            resultActions.andDo(print())
                    .andExpect(jsonPath("$.data").value(saveProjectPosition1.getNo()))
                    .andExpect(status().isOk());

            ProjectPosition projectPosition = projectPositionRepository.findUserFetchJoinByProjectPositionNo(saveProjectPosition1.getNo()).orElseThrow(() -> new CustomException(ErrorCode.PROJECT_POSITION_NO_SUCH_ELEMENT_EXCEPTION));
            assertEquals(projectPosition.getUser(), null);
        }

        @Test
        @DisplayName("실패 테스트 : 비로그인한 경우")
        public void testFailure() throws Exception {
            // given
            LocalDateTime createDate = LocalDateTime.now();
            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            // 프로젝트 세팅
            Project project1 = Project.builder()
                    .name("testName1")
                    .createUserName("user1")
                    .createDate(createDate)
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(4)
                    .delete(true)
                    .deleteReason(null)
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
                    .user(null)
                    .creator(false)
                    .build();
            ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);

            // when
            ResultActions resultActions = mvc.perform(delete("/v1/projectposition/" + saveProjectPosition1.getNo() + "/withdrawal")
                    .contentType(MediaType.APPLICATION_JSON_VALUE));

            // then
            resultActions.andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("프로젝트 추방 테스트")
    class testProjectPositionExpulsion {
        @Test
        @DisplayName("성공 테스트")
        public void testSuccess() throws Exception {
            // given
            // 유저 세팅
            User saveUser = saveUser();

            LocalDateTime createDate = LocalDateTime.now();
            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            // 프로젝트 세팅
            Project project1 = Project.builder()
                    .name("testName1")
                    .createUserName("user1")
                    .createDate(createDate)
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(4)
                    .delete(true)
                    .deleteReason(null)
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
                    .user(saveUser)
                    .creator(false)
                    .build();
            ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);

            // when
            String token = jwtTokenService.createToken(new TokenDto(saveUser.getEmail()));
            ResultActions resultActions = mvc.perform(delete("/v1/projectposition/" + saveProjectPosition1.getNo() + "/expulsion")
                    .header("Authorization", "Bearer " + token)
                    .content(new ObjectMapper().writeValueAsString("testReason"))
                    .contentType(MediaType.APPLICATION_JSON_VALUE));

            // then
            resultActions.andDo(print())
                    .andExpect(jsonPath("$.data").value(true))
                    .andExpect(status().isOk());

            ProjectPosition projectPosition = projectPositionRepository.findUserFetchJoinByProjectPositionNo(saveProjectPosition1.getNo()).orElseThrow(() -> new CustomException(ErrorCode.PROJECT_POSITION_NO_SUCH_ELEMENT_EXCEPTION));
            assertEquals(projectPosition.getUser(), null);
        }

        @Test
        @DisplayName("실패 테스트 : 비로그인한 경우")
        public void testFailure() throws Exception {
            // given
            LocalDateTime createDate = LocalDateTime.now();
            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            // 프로젝트 세팅
            Project project1 = Project.builder()
                    .name("testName1")
                    .createUserName("user1")
                    .createDate(createDate)
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(4)
                    .delete(true)
                    .deleteReason(null)
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
                    .user(null)
                    .creator(false)
                    .build();
            ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);

            // when
            ResultActions resultActions = mvc.perform(delete("/v1/projectposition/" + saveProjectPosition1.getNo() + "/expulsion")
                    .contentType(MediaType.APPLICATION_JSON_VALUE));

            // then
            resultActions.andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }
}