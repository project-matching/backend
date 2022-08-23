package com.matching.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.matching.project.dto.token.TokenClaimsDto;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.enumerate.Type;
import com.matching.project.dto.project.ProjectParticipateRequestDto;
import com.matching.project.dto.projectparticipate.ProjectParticipateRefusalRequestDto;
import com.matching.project.entity.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProjectParticipateControllerTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ProjectPositionRepository projectPositionRepository;

    @Autowired
    JwtTokenService jwtTokenService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    TechnicalStackRepository technicalStackRepository;

    @Autowired
    ProjectParticipateRequestRepository projectParticipateRequestRepository;

    @Autowired
    ParticipateRequestTechnicalStackRepository participateRequestTechnicalStackRepository;

    @Autowired
    NotificationRepository notificationRepository;

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

    User saveProjectUser() {
        User user1 = User.builder()
                .name("userName2")
                .sex("M")
                .email("wkemrm123@naver.com")
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
    @DisplayName("프로젝트 참가 신청")
    class projectParticipateRequest {
        @Test
        @DisplayName("성공")
        public void success() throws Exception {
            // given
            // 유저 세팅
            User saveUser = saveUser();
            User saveProjectUser = saveProjectUser();
            // 프로젝트 세팅
            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            Project project = Project.builder()
                    .name("testName1")
                    .createUserName("user")
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(4)
                    .viewCount(10)
                    .commentCount(10)
                    .user(saveProjectUser)
                    .build();
            Project saveProject1 = projectRepository.save(project);

            // 포지션 세팅
            Position position1 = Position.builder()
                    .name("testPosition1")
                    .build();
            Position position2 = Position.builder()
                    .name("testPosition2")
                    .build();

            Position savePosition1 = positionRepository.save(position1);
            Position savePosition2 = positionRepository.save(position2);

            // 프로젝트 포지션 세팅
            ProjectPosition projectPosition1 = ProjectPosition.builder()
                    .project(saveProject1)
                    .position(savePosition1)
                    .user(null)
                    .state(false)
                    .creator(false)
                    .build();

            ProjectPosition projectPosition2 = ProjectPosition.builder()
                    .project(saveProject1)
                    .position(savePosition2)
                    .user(null)
                    .state(false)
                    .creator(false)
                    .build();
            ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);
            projectPositionRepository.save(projectPosition2);

            // 기술스택 세팅
            TechnicalStack technicalStack1 = TechnicalStack.builder()
                    .name("testTechnicalStack1")
                    .build();
            TechnicalStack technicalStack2 = TechnicalStack.builder()
                    .name("testTechnicalStack2")
                    .build();

            technicalStackRepository.save(technicalStack1);
            technicalStackRepository.save(technicalStack2);

            // when
            List<String> technicalStackRequestList = new ArrayList<>();
            technicalStackRequestList.add("testTechnicalStack1");
            technicalStackRequestList.add("testTechnicalStack2");

            ProjectParticipateRequestDto projectParticipateRequestDto = new ProjectParticipateRequestDto(saveProjectPosition1.getNo(), technicalStackRequestList, "testGitHub1", "testMotive");

            String token = jwtTokenService.createToken(new TokenClaimsDto(saveUser.getEmail())).getAccess();

            ResultActions resultActions = mvc.perform(post("/v1/participate").contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(projectParticipateRequestDto)));

            // then
            resultActions
                    .andDo(print())
                    .andExpect(header().string("Content-type", "application/json"))
                    .andExpect(jsonPath("$.data").value(true))
                    .andExpect(status().isOk())
                    .andReturn();

            // DB 검증
            List<ProjectParticipateRequest> projectParticipateRequestList = projectParticipateRequestRepository.findAll();
            List<ParticipateRequestTechnicalStack> participateRequestTechnicalStackList = participateRequestTechnicalStackRepository.findAll();
            List<Notification> notificationList = notificationRepository.findAll();

            assertEquals(projectParticipateRequestList.get(0).getProjectPosition().getNo(), saveProjectPosition1.getNo());
            assertEquals(projectParticipateRequestList.get(0).getUser().getNo(), saveUser.getNo());
            assertEquals(projectParticipateRequestList.get(0).getGithub(), projectParticipateRequestDto.getGitHub());
            assertEquals(projectParticipateRequestList.get(0).getMotive(), projectParticipateRequestDto.getMotive());

            assertEquals(participateRequestTechnicalStackList.get(0).getProjectParticipateRequest().getNo(), projectParticipateRequestList.get(0).getNo());
            assertEquals(participateRequestTechnicalStackList.get(0).getTechnicalStack().getName(), projectParticipateRequestDto.getTechnicalStackList().get(0));
            assertEquals(participateRequestTechnicalStackList.get(1).getTechnicalStack().getName(), projectParticipateRequestDto.getTechnicalStackList().get(1));

            assertEquals(notificationList.size(), 1);
            assertEquals(notificationList.get(0).getType(), Type.PROJECT_PARTICIPATION_REQUEST);
            assertEquals(notificationList.get(0).getTitle(), "[프로젝트 참가 신청] " + saveProject1.getName());
            assertEquals(notificationList.get(0).getContent(), saveUser.getName() + "님이 " + saveProject1.getName() + "에 참가 신청했습니다.");
        }

        @Test
        @DisplayName("실패 : 비로그인 유저")
        public void fail1() throws Exception {
            // given
            ResultActions resultActions = mvc.perform(post("/v1/participate").contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
        
        @Test
        @DisplayName("실패 : 이미 유저가 존재할 경우")
        public void fail2() throws Exception {
            // given
            // 유저 세팅
            User saveUser = saveUser();

            // 프로젝트 세팅
            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            Project project = Project.builder()
                    .name("testName1")
                    .createUserName("user")
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(4)
                    .viewCount(10)
                    .commentCount(10)
                    .build();
            Project saveProject1 = projectRepository.save(project);

            // 포지션 세팅
            Position position1 = Position.builder()
                    .name("testPosition1")
                    .build();
            Position position2 = Position.builder()
                    .name("testPosition2")
                    .build();

            Position savePosition1 = positionRepository.save(position1);
            Position savePosition2 = positionRepository.save(position2);

            // 프로젝트 포지션 세팅
            ProjectPosition projectPosition1 = ProjectPosition.builder()
                    .project(saveProject1)
                    .position(savePosition1)
                    .user(saveUser)
                    .state(false)
                    .creator(false)
                    .build();

            ProjectPosition projectPosition2 = ProjectPosition.builder()
                    .project(saveProject1)
                    .position(savePosition2)
                    .user(null)
                    .state(false)
                    .creator(false)
                    .build();
            ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);
            projectPositionRepository.save(projectPosition2);

            // 기술스택 세팅
            TechnicalStack technicalStack1 = TechnicalStack.builder()
                    .name("testTechnicalStack1")
                    .build();
            TechnicalStack technicalStack2 = TechnicalStack.builder()
                    .name("testTechnicalStack2")
                    .build();

            technicalStackRepository.save(technicalStack1);
            technicalStackRepository.save(technicalStack2);

            // when
            List<String> technicalStackRequestList = new ArrayList<>();
            technicalStackRequestList.add("testTechnicalStack1");
            technicalStackRequestList.add("testTechnicalStack2");

            ProjectParticipateRequestDto projectParticipateRequestDto = new ProjectParticipateRequestDto(saveProjectPosition1.getNo(), technicalStackRequestList, "testGitHub1", "testMotive");

            String token = jwtTokenService.createToken(new TokenClaimsDto(saveUser.getEmail())).getAccess();

            ResultActions resultActions = mvc.perform(post("/v1/participate").contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(projectParticipateRequestDto)));

            // then
            resultActions
                    .andDo(print())
                    .andExpect(header().string("Content-type", "application/json"))
                    .andExpect(jsonPath("$.error.status").value(ErrorCode.PROJECT_POSITION_EXISTENCE_USER.getHttpStatus().value()))
                    .andExpect(jsonPath("$.error.error").value(ErrorCode.PROJECT_POSITION_EXISTENCE_USER.getHttpStatus().name()))
                    .andExpect(jsonPath("$.error.message[0]").value(ErrorCode.PROJECT_POSITION_EXISTENCE_USER.getDetail()))
                    .andExpect(status().is5xxServerError())
                    .andReturn();

            // DB 검증
            List<ProjectParticipateRequest> projectParticipateRequestList = projectParticipateRequestRepository.findAll();
            List<ParticipateRequestTechnicalStack> participateRequestTechnicalStackList = participateRequestTechnicalStackRepository.findAll();

            assertEquals(projectParticipateRequestList.size(), 0);
            assertEquals(participateRequestTechnicalStackList.size(), 0);
        }
    }

    @Nested
    @DisplayName("프로젝트 신청 관리 페이지 폼")
    class projectParticipateManagementForm {
        @Test
        @DisplayName("성공 : projectParticipateRequestNo를 안줬을때")
        public void success1() throws Exception {
            // given
            // 유저 세팅
            User user1 = User.builder()
                    .name("userName1")
                    .sex("M")
                    .email("wkemrm1@naver.com")
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
            User saveUser1 = userRepository.saveAndFlush(user1);

            User user2 = User.builder()
                    .name("userName2")
                    .sex("M")
                    .email("wkemrm2@naver.com")
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
            User saveUser2 = userRepository.saveAndFlush(user2);

            User user3 = User.builder()
                    .name("userName3")
                    .sex("M")
                    .email("wkemrm3@naver.com")
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
            User saveUser3 = userRepository.saveAndFlush(user3);

            // 프로젝트 세팅
            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            Project project = Project.builder()
                    .name("testName1")
                    .createUserName("user")
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(4)
                    .viewCount(10)
                    .commentCount(10)
                    .user(saveUser1)
                    .build();
            Project saveProject1 = projectRepository.saveAndFlush(project);

            // 포지션 세팅
            Position position1 = Position.builder()
                    .name("testPosition1")
                    .build();
            Position position2 = Position.builder()
                    .name("testPosition2")
                    .build();

            Position savePosition1 = positionRepository.saveAndFlush(position1);
            Position savePosition2 = positionRepository.saveAndFlush(position2);

            // 프로젝트 포지션 세팅
            ProjectPosition projectPosition1 = ProjectPosition.builder()
                    .project(saveProject1)
                    .position(savePosition1)
                    .user(null)
                    .state(false)
                    .creator(false)
                    .build();

            ProjectPosition projectPosition2 = ProjectPosition.builder()
                    .project(saveProject1)
                    .position(savePosition2)
                    .user(null)
                    .state(false)
                    .creator(false)
                    .build();
            ProjectPosition saveProjectPosition1 = projectPositionRepository.saveAndFlush(projectPosition1);
            ProjectPosition saveProjectPosition2 = projectPositionRepository.saveAndFlush(projectPosition2);

            // 기술스택 세팅
            TechnicalStack technicalStack1 = TechnicalStack.builder()
                    .name("testTechnicalStack1")
                    .build();
            TechnicalStack technicalStack2 = TechnicalStack.builder()
                    .name("testTechnicalStack2")
                    .build();

            technicalStackRepository.saveAndFlush(technicalStack1);
            technicalStackRepository.saveAndFlush(technicalStack2);

            // 프로젝트 신청 세팅
            ProjectParticipateRequest projectParticipateRequest1 = ProjectParticipateRequest.builder()
                    .projectPosition(saveProjectPosition1)
                    .user(saveUser2)
                    .github("testGitHub1")
                    .motive("testMotive1")
                    .build();

            ProjectParticipateRequest projectParticipateRequest2 = ProjectParticipateRequest.builder()
                    .projectPosition(saveProjectPosition2)
                    .user(saveUser3)
                    .github("testGitHub2")
                    .motive("testMotive2")
                    .build();

            ProjectParticipateRequest saveProjectParticipateRequest1 = projectParticipateRequestRepository.saveAndFlush(projectParticipateRequest1);
            ProjectParticipateRequest saveProjectParticipateRequest2 = projectParticipateRequestRepository.saveAndFlush(projectParticipateRequest2);

            // 프로젝트 신청 기술 스택 세팅
            ParticipateRequestTechnicalStack participateRequestTechnicalStack1 = ParticipateRequestTechnicalStack.builder()
                    .technicalStack(technicalStack1)
                    .projectParticipateRequest(saveProjectParticipateRequest1)
                    .build();

            ParticipateRequestTechnicalStack participateRequestTechnicalStack2 = ParticipateRequestTechnicalStack.builder()
                    .technicalStack(technicalStack2)
                    .projectParticipateRequest(saveProjectParticipateRequest1)
                    .build();

            ParticipateRequestTechnicalStack saveParticipateRequestTechnicalStack1 = participateRequestTechnicalStackRepository.saveAndFlush(participateRequestTechnicalStack1);
            ParticipateRequestTechnicalStack saveParticipateRequestTechnicalStack2 = participateRequestTechnicalStackRepository.saveAndFlush(participateRequestTechnicalStack2);

            // when
            String token = jwtTokenService.createToken(new TokenClaimsDto(saveUser1.getEmail())).getAccess();

            ResultActions resultActions = mvc.perform(get("/v1/participate/" + saveProject1.getNo() + "?size=5&sortBy=createdDate,desc").contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token));

            // then
            resultActions
                    .andDo(print())
                    .andExpect(header().string("Content-type", "application/json"))
                    .andExpect(jsonPath("$.data.content[0].projectParticipateNo").value(saveProjectParticipateRequest2.getNo()))
                    .andExpect(jsonPath("$.data.content[0].userName").value(saveProjectParticipateRequest2.getUser().getName()))
                    .andExpect(jsonPath("$.data.content[0].positionName").value(saveProjectParticipateRequest2.getProjectPosition().getPosition().getName()))
                    .andExpect(jsonPath("$.data.content[0].motive").value(saveProjectParticipateRequest2.getMotive()))
                    .andExpect(jsonPath("$.data.content[0].technicalStackList").isEmpty())

                    .andExpect(jsonPath("$.data.content[1].projectParticipateNo").value(saveProjectParticipateRequest1.getNo()))
                    .andExpect(jsonPath("$.data.content[1].userName").value(saveProjectParticipateRequest1.getUser().getName()))
                    .andExpect(jsonPath("$.data.content[1].positionName").value(saveProjectParticipateRequest1.getProjectPosition().getPosition().getName()))
                    .andExpect(jsonPath("$.data.content[1].motive").value(saveProjectParticipateRequest1.getMotive()))
                    .andExpect(jsonPath("$.data.content[1].technicalStackList[0]").value(saveParticipateRequestTechnicalStack1.getTechnicalStack().getName()))
                    .andExpect(jsonPath("$.data.content[1].technicalStackList[1]").value(saveParticipateRequestTechnicalStack2.getTechnicalStack().getName()))

                    .andExpect(jsonPath("$.data.last").value(true))

                    .andExpect(status().isOk())
                    .andReturn();
        }

        @Test
        @DisplayName("성공 : projectParticipateRequestNo를 줬을때")
        public void success2() throws Exception {
            // given
            // 유저 세팅
            User user1 = User.builder()
                    .name("userName1")
                    .sex("M")
                    .email("wkemrm1@naver.com")
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
            User saveUser1 = userRepository.save(user1);

            User user2 = User.builder()
                    .name("userName2")
                    .sex("M")
                    .email("wkemrm2@naver.com")
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
            User saveUser2 = userRepository.save(user2);

            User user3 = User.builder()
                    .name("userName3")
                    .sex("M")
                    .email("wkemrm3@naver.com")
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
            User saveUser3 = userRepository.save(user3);

            // 프로젝트 세팅
            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            Project project = Project.builder()
                    .name("testName1")
                    .createUserName("user")
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(4)
                    .viewCount(10)
                    .commentCount(10)
                    .user(saveUser1)
                    .build();
            Project saveProject1 = projectRepository.save(project);

            // 포지션 세팅
            Position position1 = Position.builder()
                    .name("testPosition1")
                    .build();
            Position position2 = Position.builder()
                    .name("testPosition2")
                    .build();

            Position savePosition1 = positionRepository.save(position1);
            Position savePosition2 = positionRepository.save(position2);

            // 프로젝트 포지션 세팅
            ProjectPosition projectPosition1 = ProjectPosition.builder()
                    .project(saveProject1)
                    .position(savePosition1)
                    .user(null)
                    .state(false)
                    .creator(false)
                    .build();

            ProjectPosition projectPosition2 = ProjectPosition.builder()
                    .project(saveProject1)
                    .position(savePosition2)
                    .user(null)
                    .state(false)
                    .creator(false)
                    .build();
            ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);
            ProjectPosition saveProjectPosition2 = projectPositionRepository.save(projectPosition2);

            // 기술스택 세팅
            TechnicalStack technicalStack1 = TechnicalStack.builder()
                    .name("testTechnicalStack1")
                    .build();
            TechnicalStack technicalStack2 = TechnicalStack.builder()
                    .name("testTechnicalStack2")
                    .build();

            technicalStackRepository.save(technicalStack1);
            technicalStackRepository.save(technicalStack2);

            // 프로젝트 신청 세팅
            ProjectParticipateRequest projectParticipateRequest1 = ProjectParticipateRequest.builder()
                    .projectPosition(saveProjectPosition1)
                    .user(saveUser2)
                    .github("testGitHub1")
                    .motive("testMotive1")
                    .build();

            ProjectParticipateRequest projectParticipateRequest2 = ProjectParticipateRequest.builder()
                    .projectPosition(saveProjectPosition2)
                    .user(saveUser3)
                    .github("testGitHub2")
                    .motive("testMotive2")
                    .build();

            ProjectParticipateRequest saveProjectParticipateRequest1 = projectParticipateRequestRepository.save(projectParticipateRequest1);
            ProjectParticipateRequest saveProjectParticipateRequest2 = projectParticipateRequestRepository.save(projectParticipateRequest2);

            // 프로젝트 신청 기술 스택 세팅
            ParticipateRequestTechnicalStack participateRequestTechnicalStack1 = ParticipateRequestTechnicalStack.builder()
                    .technicalStack(technicalStack1)
                    .projectParticipateRequest(saveProjectParticipateRequest1)
                    .build();

            ParticipateRequestTechnicalStack participateRequestTechnicalStack2 = ParticipateRequestTechnicalStack.builder()
                    .technicalStack(technicalStack2)
                    .projectParticipateRequest(saveProjectParticipateRequest1)
                    .build();

            ParticipateRequestTechnicalStack saveParticipateRequestTechnicalStack1 = participateRequestTechnicalStackRepository.save(participateRequestTechnicalStack1);
            ParticipateRequestTechnicalStack saveParticipateRequestTechnicalStack2 = participateRequestTechnicalStackRepository.save(participateRequestTechnicalStack2);

            // when
            String token = jwtTokenService.createToken(new TokenClaimsDto(saveUser1.getEmail())).getAccess();

            ResultActions resultActions = mvc.perform(get("/v1/participate/" + saveProject1.getNo() + "?size=5&sortBy=createdDate,desc&projectParticipateRequestNo=" + saveProjectParticipateRequest2.getNo())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token));

            // then
            resultActions
                    .andDo(print())
                    .andExpect(header().string("Content-type", "application/json"))
                    .andExpect(jsonPath("$.data.content[0].projectParticipateNo").value(saveProjectParticipateRequest1.getNo()))
                    .andExpect(jsonPath("$.data.content[0].userName").value(saveProjectParticipateRequest1.getUser().getName()))
                    .andExpect(jsonPath("$.data.content[0].positionName").value(saveProjectParticipateRequest1.getProjectPosition().getPosition().getName()))
                    .andExpect(jsonPath("$.data.content[0].motive").value(saveProjectParticipateRequest1.getMotive()))
                    .andExpect(jsonPath("$.data.content[0].technicalStackList[0]").value(saveParticipateRequestTechnicalStack1.getTechnicalStack().getName()))
                    .andExpect(jsonPath("$.data.content[0].technicalStackList[1]").value(saveParticipateRequestTechnicalStack2.getTechnicalStack().getName()))

                    .andExpect(jsonPath("$.data.last").value(true))

                    .andExpect(status().isOk())
                    .andReturn();
        }

        @Test
        @DisplayName("실패 : 비로그인 유저")
        public void fail1() throws Exception {
            // when
            ResultActions resultActions = mvc.perform(get("/v1/participate/1?&size=5&sortBy=createdDate,desc").contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("실패 : 내가 만든 프로젝트가 아닌 경우")
        public void fail2() throws Exception {
            // given
            // 유저 세팅
            User user1 = User.builder()
                    .name("userName1")
                    .sex("M")
                    .email("wkemrm1@naver.com")
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
            User saveUser1 = userRepository.save(user1);

            User user2 = User.builder()
                    .name("userName2")
                    .sex("M")
                    .email("wkemrm2@naver.com")
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
            User saveUser2 = userRepository.save(user2);

            User user3 = User.builder()
                    .name("userName3")
                    .sex("M")
                    .email("wkemrm3@naver.com")
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
            User saveUser3 = userRepository.save(user3);

            // 프로젝트 세팅
            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            Project project = Project.builder()
                    .name("testName1")
                    .createUserName("user")
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(4)
                    .viewCount(10)
                    .commentCount(10)
                    .user(saveUser1)
                    .build();
            Project saveProject1 = projectRepository.save(project);

            // 포지션 세팅
            Position position1 = Position.builder()
                    .name("testPosition1")
                    .build();
            Position position2 = Position.builder()
                    .name("testPosition2")
                    .build();

            Position savePosition1 = positionRepository.save(position1);
            Position savePosition2 = positionRepository.save(position2);

            // 프로젝트 포지션 세팅
            ProjectPosition projectPosition1 = ProjectPosition.builder()
                    .project(saveProject1)
                    .position(savePosition1)
                    .user(null)
                    .state(false)
                    .creator(false)
                    .build();

            ProjectPosition projectPosition2 = ProjectPosition.builder()
                    .project(saveProject1)
                    .position(savePosition2)
                    .user(null)
                    .state(false)
                    .creator(false)
                    .build();
            ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);
            ProjectPosition saveProjectPosition2 = projectPositionRepository.save(projectPosition2);

            // 기술스택 세팅
            TechnicalStack technicalStack1 = TechnicalStack.builder()
                    .name("testTechnicalStack1")
                    .build();
            TechnicalStack technicalStack2 = TechnicalStack.builder()
                    .name("testTechnicalStack2")
                    .build();

            technicalStackRepository.save(technicalStack1);
            technicalStackRepository.save(technicalStack2);

            // 프로젝트 신청 세팅
            ProjectParticipateRequest projectParticipateRequest1 = ProjectParticipateRequest.builder()
                    .projectPosition(saveProjectPosition1)
                    .user(saveUser2)
                    .github("testGitHub1")
                    .motive("testMotive1")
                    .build();

            ProjectParticipateRequest projectParticipateRequest2 = ProjectParticipateRequest.builder()
                    .projectPosition(saveProjectPosition2)
                    .user(saveUser3)
                    .github("testGitHub2")
                    .motive("testMotive2")
                    .build();

            ProjectParticipateRequest saveProjectParticipateRequest1 = projectParticipateRequestRepository.save(projectParticipateRequest1);
            projectParticipateRequestRepository.save(projectParticipateRequest2);

            // 프로젝트 신청 기술 스택 세팅
            ParticipateRequestTechnicalStack participateRequestTechnicalStack1 = ParticipateRequestTechnicalStack.builder()
                    .technicalStack(technicalStack1)
                    .projectParticipateRequest(saveProjectParticipateRequest1)
                    .build();

            ParticipateRequestTechnicalStack participateRequestTechnicalStack2 = ParticipateRequestTechnicalStack.builder()
                    .technicalStack(technicalStack2)
                    .projectParticipateRequest(saveProjectParticipateRequest1)
                    .build();

            participateRequestTechnicalStackRepository.save(participateRequestTechnicalStack1);
            participateRequestTechnicalStackRepository.save(participateRequestTechnicalStack2);

            // when
            String token = jwtTokenService.createToken(new TokenClaimsDto(saveUser2.getEmail())).getAccess();

            ResultActions resultActions = mvc.perform(get("/v1/participate/" + saveProject1.getNo() + "?size=5&sortBy=createdDate,desc").contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token));

            // then
            resultActions
                    .andDo(print())
                    .andExpect(header().string("Content-type", "application/json"))
                    .andExpect(jsonPath("$.error.status").value(ErrorCode.PROJECT_NOT_REGISTER_USER.getHttpStatus().value()))
                    .andExpect(jsonPath("$.error.error").value(ErrorCode.PROJECT_NOT_REGISTER_USER.getHttpStatus().name()))
                    .andExpect(jsonPath("$.error.message[0]").value(ErrorCode.PROJECT_NOT_REGISTER_USER.getDetail()))
                    .andExpect(status().is5xxServerError())
                    .andReturn();
        }
    }

    @Nested
    @DisplayName("프로젝트 참가 신청 수락")
    class projectParticipatePermit {
        @Test
        @DisplayName("성공")
        public void success() throws Exception {
            // given
            // 유저 세팅
            User user1 = User.builder()
                    .name("userName1")
                    .sex("M")
                    .email("wkemrm1@naver.com")
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
            User saveUser1 = userRepository.save(user1);

            User user2 = User.builder()
                    .name("userName2")
                    .sex("M")
                    .email("wkemrm2@naver.com")
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
            User saveUser2 = userRepository.save(user2);

            // 프로젝트 세팅
            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            Project project = Project.builder()
                    .name("testName1")
                    .createUserName("user")
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(4)
                    .viewCount(10)
                    .commentCount(10)
                    .user(saveUser1)
                    .build();
            Project saveProject1 = projectRepository.save(project);

            // 포지션 세팅
            Position position1 = Position.builder()
                    .name("testPosition1")
                    .build();
            Position position2 = Position.builder()
                    .name("testPosition2")
                    .build();

            Position savePosition1 = positionRepository.save(position1);
            Position savePosition2 = positionRepository.save(position2);

            // 프로젝트 포지션 세팅
            ProjectPosition projectPosition1 = ProjectPosition.builder()
                    .project(saveProject1)
                    .position(savePosition1)
                    .user(null)
                    .state(false)
                    .creator(false)
                    .build();

            ProjectPosition projectPosition2 = ProjectPosition.builder()
                    .project(saveProject1)
                    .position(savePosition2)
                    .user(null)
                    .state(false)
                    .creator(false)
                    .build();
            ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);
            ProjectPosition saveProjectPosition2 = projectPositionRepository.save(projectPosition2);

            // 기술스택 세팅
            TechnicalStack technicalStack1 = TechnicalStack.builder()
                    .name("testTechnicalStack1")
                    .build();
            TechnicalStack technicalStack2 = TechnicalStack.builder()
                    .name("testTechnicalStack2")
                    .build();

            technicalStackRepository.save(technicalStack1);
            technicalStackRepository.save(technicalStack2);

            // 프로젝트 신청 세팅
            ProjectParticipateRequest projectParticipateRequest1 = ProjectParticipateRequest.builder()
                    .projectPosition(saveProjectPosition1)
                    .user(saveUser2)
                    .github("testGitHub1")
                    .motive("testMotive1")
                    .build();

            ProjectParticipateRequest saveProjectParticipateRequest1 = projectParticipateRequestRepository.save(projectParticipateRequest1);

            // 프로젝트 신청 기술 스택 세팅
            ParticipateRequestTechnicalStack participateRequestTechnicalStack1 = ParticipateRequestTechnicalStack.builder()
                    .technicalStack(technicalStack1)
                    .projectParticipateRequest(saveProjectParticipateRequest1)
                    .build();

            ParticipateRequestTechnicalStack participateRequestTechnicalStack2 = ParticipateRequestTechnicalStack.builder()
                    .technicalStack(technicalStack2)
                    .projectParticipateRequest(saveProjectParticipateRequest1)
                    .build();

            participateRequestTechnicalStackRepository.save(participateRequestTechnicalStack1);
            participateRequestTechnicalStackRepository.save(participateRequestTechnicalStack2);

            // when
            ProjectPosition beforeProjectPosition = projectPositionRepository.findById(saveProjectParticipateRequest1.getProjectPosition().getNo()).get();
            assertEquals(beforeProjectPosition.getUser(), null);
            String token = jwtTokenService.createToken(new TokenClaimsDto(saveUser1.getEmail())).getAccess();


            ResultActions resultActions = mvc.perform(post("/v1/participate/" + saveProjectParticipateRequest1.getNo() + "/permit").contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token));

            // then
            resultActions
                    .andDo(print())
                    .andExpect(header().string("Content-type", "application/json"))
                    .andExpect(jsonPath("$.data").value(true))
                    .andExpect(status().isOk())
                    .andReturn();

            List<ParticipateRequestTechnicalStack> participateRequestTechnicalStackRepositoryAll = participateRequestTechnicalStackRepository.findAll();
            Optional<ProjectParticipateRequest> projectParticipateRequest = projectParticipateRequestRepository.findById(saveProjectParticipateRequest1.getNo());
            ProjectPosition afterProjectPosition = projectPositionRepository.findById(saveProjectParticipateRequest1.getProjectPosition().getNo()).get();
            List<Notification> notificationList = notificationRepository.findAll();

            assertEquals(afterProjectPosition.getUser().getNo(), saveUser2.getNo());
            assertEquals(afterProjectPosition.getUser().getName(), saveUser2.getName());
            assertEquals(afterProjectPosition.getUser().getSex(), saveUser2.getSex());
            assertEquals(afterProjectPosition.getUser().getEmail(), saveUser2.getEmail());
            assertEquals(afterProjectPosition.getUser().getPassword(), saveUser2.getPassword());
            assertEquals(afterProjectPosition.getUser().getGithub(), saveUser2.getGithub());
            assertEquals(afterProjectPosition.getUser().getSelfIntroduction(), saveUser2.getSelfIntroduction());
            assertEquals(afterProjectPosition.getUser().isBlock(), saveUser2.isBlock());
            assertEquals(afterProjectPosition.getUser().getBlockReason(), saveUser2.getBlockReason());
            assertEquals(afterProjectPosition.getUser().getPermission(), saveUser2.getPermission());
            assertEquals(afterProjectPosition.getUser().getOauthCategory(), saveUser2.getOauthCategory());
            assertEquals(afterProjectPosition.getUser().isEmail_auth(), saveUser2.isEmail_auth());
            assertEquals(afterProjectPosition.getUser().getImageNo(), saveUser2.getImageNo());
            assertEquals(afterProjectPosition.getUser().getPosition(), saveUser2.getPosition());

            assertEquals(notificationList.size(), 1);
            assertEquals(notificationList.get(0).getType(), Type.PROJECT_PARTICIPATION_SUCCESS);
            assertEquals(notificationList.get(0).getTitle(), "[프로젝트 신청 수락] " + saveProject1.getName());
            assertEquals(notificationList.get(0).getContent(), saveProject1.getName() + " 프로젝트에 참가 완료되었습니다.");

            assertEquals(projectParticipateRequest.isEmpty(), true);
            assertEquals(participateRequestTechnicalStackRepositoryAll.size(), 0);
        }

        @Test
        @DisplayName("실패 : 비로그인 유저")
        public void fail1() throws Exception {
            // when
            ResultActions resultActions = mvc.perform(post("/v1/participate/1/permit").contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
        
        @Test
        @DisplayName("실패 : 내가 만든 프로젝트가 아닌 경우")
        public void fail2() throws Exception {
            // given
            // 유저 세팅
            User user1 = User.builder()
                    .name("userName1")
                    .sex("M")
                    .email("wkemrm1@naver.com")
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
            User saveUser1 = userRepository.save(user1);

            User user2 = User.builder()
                    .name("userName2")
                    .sex("M")
                    .email("wkemrm2@naver.com")
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
            User saveUser2 = userRepository.save(user2);

            // 프로젝트 세팅
            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            Project project = Project.builder()
                    .name("testName1")
                    .createUserName("user")
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(4)
                    .viewCount(10)
                    .commentCount(10)
                    .user(saveUser1)
                    .build();
            Project saveProject1 = projectRepository.save(project);

            // 포지션 세팅
            Position position1 = Position.builder()
                    .name("testPosition1")
                    .build();
            Position position2 = Position.builder()
                    .name("testPosition2")
                    .build();

            Position savePosition1 = positionRepository.save(position1);
            Position savePosition2 = positionRepository.save(position2);

            // 프로젝트 포지션 세팅
            ProjectPosition projectPosition1 = ProjectPosition.builder()
                    .project(saveProject1)
                    .position(savePosition1)
                    .user(null)
                    .state(false)
                    .creator(false)
                    .build();

            ProjectPosition projectPosition2 = ProjectPosition.builder()
                    .project(saveProject1)
                    .position(savePosition2)
                    .user(null)
                    .state(false)
                    .creator(false)
                    .build();
            ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);
            ProjectPosition saveProjectPosition2 = projectPositionRepository.save(projectPosition2);

            // 기술스택 세팅
            TechnicalStack technicalStack1 = TechnicalStack.builder()
                    .name("testTechnicalStack1")
                    .build();
            TechnicalStack technicalStack2 = TechnicalStack.builder()
                    .name("testTechnicalStack2")
                    .build();

            technicalStackRepository.save(technicalStack1);
            technicalStackRepository.save(technicalStack2);

            // 프로젝트 신청 세팅
            ProjectParticipateRequest projectParticipateRequest1 = ProjectParticipateRequest.builder()
                    .projectPosition(saveProjectPosition1)
                    .user(saveUser2)
                    .github("testGitHub1")
                    .motive("testMotive1")
                    .build();

            ProjectParticipateRequest saveProjectParticipateRequest1 = projectParticipateRequestRepository.save(projectParticipateRequest1);

            // 프로젝트 신청 기술 스택 세팅
            ParticipateRequestTechnicalStack participateRequestTechnicalStack1 = ParticipateRequestTechnicalStack.builder()
                    .technicalStack(technicalStack1)
                    .projectParticipateRequest(saveProjectParticipateRequest1)
                    .build();

            ParticipateRequestTechnicalStack participateRequestTechnicalStack2 = ParticipateRequestTechnicalStack.builder()
                    .technicalStack(technicalStack2)
                    .projectParticipateRequest(saveProjectParticipateRequest1)
                    .build();

            participateRequestTechnicalStackRepository.save(participateRequestTechnicalStack1);
            participateRequestTechnicalStackRepository.save(participateRequestTechnicalStack2);

            // when
            String token = jwtTokenService.createToken(new TokenClaimsDto(saveUser2.getEmail())).getAccess();

            ResultActions resultActions = mvc.perform(post("/v1/participate/" + saveProjectParticipateRequest1.getNo() + "/permit").contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token));

            // then
            resultActions
                    .andDo(print())
                    .andExpect(header().string("Content-type", "application/json"))
                    .andExpect(jsonPath("$.error.status").value(ErrorCode.PROJECT_NOT_REGISTER_USER.getHttpStatus().value()))
                    .andExpect(jsonPath("$.error.error").value(ErrorCode.PROJECT_NOT_REGISTER_USER.getHttpStatus().name()))
                    .andExpect(jsonPath("$.error.message[0]").value(ErrorCode.PROJECT_NOT_REGISTER_USER.getDetail()))
                    .andExpect(status().is5xxServerError())
                    .andReturn();
        }
    }

    @Nested
    @DisplayName("프로젝트 참가 신청 거절")
    class projectParticipateRefusal {
        @Test
        @DisplayName("성공")
        public void success() throws Exception {
            // given
            // 유저 세팅
            User user1 = User.builder()
                    .name("userName1")
                    .sex("M")
                    .email("wkemrm1@naver.com")
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
            User saveUser1 = userRepository.save(user1);

            User user2 = User.builder()
                    .name("userName2")
                    .sex("M")
                    .email("wkemrm2@naver.com")
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
            User saveUser2 = userRepository.save(user2);

            // 프로젝트 세팅
            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            Project project = Project.builder()
                    .name("testName1")
                    .createUserName("user")
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(4)
                    .viewCount(10)
                    .commentCount(10)
                    .user(saveUser1)
                    .build();
            Project saveProject1 = projectRepository.save(project);

            // 포지션 세팅
            Position position1 = Position.builder()
                    .name("testPosition1")
                    .build();
            Position position2 = Position.builder()
                    .name("testPosition2")
                    .build();

            Position savePosition1 = positionRepository.save(position1);
            Position savePosition2 = positionRepository.save(position2);

            // 프로젝트 포지션 세팅
            ProjectPosition projectPosition1 = ProjectPosition.builder()
                    .project(saveProject1)
                    .position(savePosition1)
                    .user(null)
                    .state(false)
                    .creator(false)
                    .build();

            ProjectPosition projectPosition2 = ProjectPosition.builder()
                    .project(saveProject1)
                    .position(savePosition2)
                    .user(null)
                    .state(false)
                    .creator(false)
                    .build();
            ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);
            ProjectPosition saveProjectPosition2 = projectPositionRepository.save(projectPosition2);

            // 기술스택 세팅
            TechnicalStack technicalStack1 = TechnicalStack.builder()
                    .name("testTechnicalStack1")
                    .build();
            TechnicalStack technicalStack2 = TechnicalStack.builder()
                    .name("testTechnicalStack2")
                    .build();

            technicalStackRepository.save(technicalStack1);
            technicalStackRepository.save(technicalStack2);

            // 프로젝트 신청 세팅
            ProjectParticipateRequest projectParticipateRequest1 = ProjectParticipateRequest.builder()
                    .projectPosition(saveProjectPosition1)
                    .user(saveUser2)
                    .github("testGitHub1")
                    .motive("testMotive1")
                    .build();

            ProjectParticipateRequest saveProjectParticipateRequest1 = projectParticipateRequestRepository.save(projectParticipateRequest1);

            // 프로젝트 신청 기술 스택 세팅
            ParticipateRequestTechnicalStack participateRequestTechnicalStack1 = ParticipateRequestTechnicalStack.builder()
                    .technicalStack(technicalStack1)
                    .projectParticipateRequest(saveProjectParticipateRequest1)
                    .build();

            ParticipateRequestTechnicalStack participateRequestTechnicalStack2 = ParticipateRequestTechnicalStack.builder()
                    .technicalStack(technicalStack2)
                    .projectParticipateRequest(saveProjectParticipateRequest1)
                    .build();

            participateRequestTechnicalStackRepository.save(participateRequestTechnicalStack1);
            participateRequestTechnicalStackRepository.save(participateRequestTechnicalStack2);

            // when
            String token = jwtTokenService.createToken(new TokenClaimsDto(saveUser1.getEmail())).getAccess();
            ProjectParticipateRefusalRequestDto projectParticipateRefusalRequestDto = new ProjectParticipateRefusalRequestDto("testReason");
            ResultActions resultActions = mvc.perform(post("/v1/participate/" + saveProjectParticipateRequest1.getNo() + "/refusal").contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(projectParticipateRefusalRequestDto)));

            // then
            resultActions
                    .andDo(print())
                    .andExpect(header().string("Content-type", "application/json"))
                    .andExpect(jsonPath("$.data").value(true))
                    .andExpect(status().isOk())
                    .andReturn();

            List<ParticipateRequestTechnicalStack> participateRequestTechnicalStackRepositoryAll = participateRequestTechnicalStackRepository.findAll();
            Optional<ProjectParticipateRequest> projectParticipateRequest = projectParticipateRequestRepository.findById(saveProjectParticipateRequest1.getNo());
            List<Notification> notificationList = notificationRepository.findAll();

            assertEquals(notificationList.size(), 1);
            assertEquals(notificationList.get(0).getType(), Type.PROJECT_PARTICIPATION_REFUSE);
            assertEquals(notificationList.get(0).getTitle(), "[프로젝트 신청 거절] " + saveProject1.getName());
            assertEquals(notificationList.get(0).getContent(), saveProject1.getName() + " 프로젝트에 참가 신청이 거절되었습니다.\n"
                    + "거부사유 : " + projectParticipateRefusalRequestDto.getReason());

            assertEquals(projectParticipateRequest.isEmpty(), true);
            assertEquals(participateRequestTechnicalStackRepositoryAll.size(), 0);
        }

        @Test
        @DisplayName("실패 : 비로그인 유저")
        public void fail1() throws Exception {
            // when
            ResultActions resultActions = mvc.perform(post("/v1/participate/1/refusal").contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("실패 : 내가 만든 프로젝트가 아닌 경우")
        public void fail2() throws Exception {
            // given
            // 유저 세팅
            User user1 = User.builder()
                    .name("userName1")
                    .sex("M")
                    .email("wkemrm1@naver.com")
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
            User saveUser1 = userRepository.save(user1);

            User user2 = User.builder()
                    .name("userName2")
                    .sex("M")
                    .email("wkemrm2@naver.com")
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
            User saveUser2 = userRepository.save(user2);

            // 프로젝트 세팅
            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            Project project = Project.builder()
                    .name("testName1")
                    .createUserName("user")
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(4)
                    .viewCount(10)
                    .commentCount(10)
                    .user(saveUser1)
                    .build();
            Project saveProject1 = projectRepository.save(project);

            // 포지션 세팅
            Position position1 = Position.builder()
                    .name("testPosition1")
                    .build();
            Position position2 = Position.builder()
                    .name("testPosition2")
                    .build();

            Position savePosition1 = positionRepository.save(position1);
            Position savePosition2 = positionRepository.save(position2);

            // 프로젝트 포지션 세팅
            ProjectPosition projectPosition1 = ProjectPosition.builder()
                    .project(saveProject1)
                    .position(savePosition1)
                    .user(null)
                    .state(false)
                    .creator(false)
                    .build();

            ProjectPosition projectPosition2 = ProjectPosition.builder()
                    .project(saveProject1)
                    .position(savePosition2)
                    .user(null)
                    .state(false)
                    .creator(false)
                    .build();
            ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);
            ProjectPosition saveProjectPosition2 = projectPositionRepository.save(projectPosition2);

            // 기술스택 세팅
            TechnicalStack technicalStack1 = TechnicalStack.builder()
                    .name("testTechnicalStack1")
                    .build();
            TechnicalStack technicalStack2 = TechnicalStack.builder()
                    .name("testTechnicalStack2")
                    .build();

            technicalStackRepository.save(technicalStack1);
            technicalStackRepository.save(technicalStack2);

            // 프로젝트 신청 세팅
            ProjectParticipateRequest projectParticipateRequest1 = ProjectParticipateRequest.builder()
                    .projectPosition(saveProjectPosition1)
                    .user(saveUser2)
                    .github("testGitHub1")
                    .motive("testMotive1")
                    .build();

            ProjectParticipateRequest saveProjectParticipateRequest1 = projectParticipateRequestRepository.save(projectParticipateRequest1);

            // 프로젝트 신청 기술 스택 세팅
            ParticipateRequestTechnicalStack participateRequestTechnicalStack1 = ParticipateRequestTechnicalStack.builder()
                    .technicalStack(technicalStack1)
                    .projectParticipateRequest(saveProjectParticipateRequest1)
                    .build();

            ParticipateRequestTechnicalStack participateRequestTechnicalStack2 = ParticipateRequestTechnicalStack.builder()
                    .technicalStack(technicalStack2)
                    .projectParticipateRequest(saveProjectParticipateRequest1)
                    .build();

            participateRequestTechnicalStackRepository.save(participateRequestTechnicalStack1);
            participateRequestTechnicalStackRepository.save(participateRequestTechnicalStack2);

            // when
            String token = jwtTokenService.createToken(new TokenClaimsDto(saveUser2.getEmail())).getAccess();

            ResultActions resultActions = mvc.perform(post("/v1/participate/" + saveProjectParticipateRequest1.getNo() + "/refusal").contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString("testReason")));

            // then
            resultActions
                    .andDo(print())
                    .andExpect(header().string("Content-type", "application/json"))
                    .andExpect(jsonPath("$.error.status").value(ErrorCode.PROJECT_NOT_REGISTER_USER.getHttpStatus().value()))
                    .andExpect(jsonPath("$.error.error").value(ErrorCode.PROJECT_NOT_REGISTER_USER.getHttpStatus().name()))
                    .andExpect(jsonPath("$.error.message[0]").value(ErrorCode.PROJECT_NOT_REGISTER_USER.getDetail()))
                    .andExpect(status().is5xxServerError())
                    .andReturn();
        }
    }
}