package com.matching.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.matching.project.dto.ResponseDto;
import com.matching.project.dto.common.TokenDto;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.project.ProjectParticipateRequestDto;
import com.matching.project.entity.*;
import com.matching.project.error.ErrorCode;
import com.matching.project.repository.*;
import com.matching.project.service.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    ProjectTechnicalStackRepository projectTechnicalStackRepository;

    @Autowired
    JwtTokenService jwtTokenService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    TechnicalStackRepository technicalStackRepository;

    @Autowired
    ProjectParticipateRequestRepository projectParticipateRequestRepository;

    @Autowired
    ParticipateRequestTechnicalStackRepository participateRequestTechnicalStackRepository;

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

    @Test
    public void 프로젝트_참가_신청_테스트() throws Exception{
        // 유저 세팅
        User saveUser = saveUser();

        // 프로젝트 세팅
        LocalDateTime createDate = LocalDateTime.now();
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        Project project = Project.builder()
                    .name("testName1")
                    .createUserName("user")
                    .createDate(createDate)
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(4)
                    .delete(false)
                    .deleteReason(null)
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

        // then
        String token = jwtTokenService.createToken(new TokenDto(saveUser.getEmail()));

        mvc.perform(post("/v1/participate").contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(projectParticipateRequestDto)))
                .andDo(print())
                .andExpect(header().string("Content-type", "application/json"))
                .andExpect(jsonPath("$.data").value(true))
                .andExpect(status().isOk())
                .andReturn();
        
        // DB 검증
        List<ProjectParticipateRequest> projectParticipateRequestList = projectParticipateRequestRepository.findAll();
        List<ParticipateRequestTechnicalStack> participateRequestTechnicalStackList = participateRequestTechnicalStackRepository.findAll();

        assertEquals(projectParticipateRequestList.get(0).getProjectPosition(), saveProjectPosition1);
        assertEquals(projectParticipateRequestList.get(0).getUser(), saveUser);
        assertEquals(projectParticipateRequestList.get(0).getGithub(), projectParticipateRequestDto.getGitHub());
        assertEquals(projectParticipateRequestList.get(0).getMotive(), projectParticipateRequestDto.getMotive());

        assertEquals(participateRequestTechnicalStackList.get(0).getProjectParticipateRequest(), projectParticipateRequestList.get(0));
        assertEquals(participateRequestTechnicalStackList.get(0).getTechnicalStack().getName(), projectParticipateRequestDto.getTechnicalStackList().get(0));
        assertEquals(participateRequestTechnicalStackList.get(1).getTechnicalStack().getName(), projectParticipateRequestDto.getTechnicalStackList().get(1));
    }

    @Test
    public void 프로젝트_참가_신청_유저_존재_경우_실패_테스트() throws Exception{
        // 유저 세팅
        User saveUser = saveUser();

        // 프로젝트 세팅
        LocalDateTime createDate = LocalDateTime.now();
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        Project project = Project.builder()
                .name("testName1")
                .createUserName("user")
                .createDate(createDate)
                .startDate(startDate)
                .endDate(endDate)
                .state(true)
                .introduction("testIntroduction1")
                .maxPeople(10)
                .currentPeople(4)
                .delete(false)
                .deleteReason(null)
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

        // then
        String token = jwtTokenService.createToken(new TokenDto(saveUser.getEmail()));

        mvc.perform(post("/v1/participate").contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(projectParticipateRequestDto)))
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