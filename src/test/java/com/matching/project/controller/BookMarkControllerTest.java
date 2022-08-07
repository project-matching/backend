package com.matching.project.controller;

import com.matching.project.dto.common.TokenDto;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.entity.*;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookMarkControllerTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    BookMarkRepository bookMarkRepository;

    @Autowired
    JwtTokenService jwtTokenService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    ProjectPositionRepository projectPositionRepository;

    @Autowired
    TechnicalStackRepository technicalStackRepository;

    @Autowired
    ProjectTechnicalStackRepository projectTechnicalStackRepository;

    @Autowired
    ImageRepository imageRepository;

    @Nested
    @DisplayName("즐겨찾기 추가")
    class bookMarkRegister {
        @Test
        @DisplayName("성공")
        public void success() throws Exception {
            // given
            LocalDateTime createDate = LocalDateTime.now();
            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            // 유저 세팅
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
            User saveUser1 = userRepository.save(user1);

            // 프로젝트 세팅
            Project project1 = Project.builder()
                    .name("testName1")
                    .createUserName("user1")
                    .createDate(createDate.plusDays(1))
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
            Project saveProject1 = projectRepository.save(project1);

            // when
            String token = jwtTokenService.createToken(new TokenDto(saveUser1.getEmail()));

            ResultActions resultActions = mvc.perform(post("/v1/bookmark/" + saveProject1.getNo()).contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token));

            // then
            resultActions.andDo(print())
                    .andExpect(header().string("Content-type", "application/json"))
                    .andExpect(jsonPath("$.data").value(true))
                    .andExpect(status().isOk());

            List<BookMark> bookMarkList = bookMarkRepository.findAll();

            assertEquals(bookMarkList.size(), 1);
            assertEquals(bookMarkList.get(0).getUser(), saveUser1);
            assertEquals(bookMarkList.get(0).getProject(), saveProject1);
        }

        @Test
        @DisplayName("실패 : 비로그인 유저")
        public void fail1() throws Exception {
            // when
            ResultActions resultActions = mvc.perform(post("/v1/bookmark/1").contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("즐겨찾기 프로젝트 조회")
    class bookMarkList {
        @Test
        @DisplayName("성공")
        public void success() throws Exception {
            // given
            LocalDateTime createDate = LocalDateTime.now();
            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            // 유저 세팅
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
            User saveUser1 = userRepository.save(user1);

            // 프로젝트 세팅
            Project project1 = Project.builder()
                    .name("testName1")
                    .createUserName("user1")
                    .createDate(createDate.plusDays(1))
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

            Project project2 = Project.builder()
                    .name("testName2")
                    .createUserName("user2")
                    .createDate(createDate.plusDays(1))
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction2")
                    .maxPeople(10)
                    .currentPeople(4)
                    .delete(false)
                    .deleteReason(null)
                    .viewCount(10)
                    .commentCount(10)
                    .build();
            Project saveProject1 = projectRepository.save(project1);
            Project saveProject2 = projectRepository.save(project2);

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
                    .state(false)
                    .project(saveProject1)
                    .position(savePosition1)
                    .creator(false)
                    .build();

            ProjectPosition projectPosition2 = ProjectPosition.builder()
                    .state(false)
                    .project(saveProject1)
                    .position(savePosition2)
                    .creator(false)
                    .build();

            ProjectPosition projectPosition3 = ProjectPosition.builder()
                    .state(false)
                    .project(saveProject2)
                    .position(savePosition1)
                    .creator(false)
                    .build();

            ProjectPosition projectPosition4 = ProjectPosition.builder()
                    .state(false)
                    .project(saveProject2)
                    .position(savePosition2)
                    .creator(false)
                    .build();
            ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);
            ProjectPosition saveProjectPosition2 = projectPositionRepository.save(projectPosition2);
            projectPositionRepository.save(projectPosition3);
            projectPositionRepository.save(projectPosition4);

            // 이미지 세팅
            Image image1 = Image.builder()
                    .physicalName("testPhysicalName1")
                    .logicalName("testLogicalName1")
                    .url("testUrl1")
                    .build();
            Image saveImage1 = imageRepository.save(image1);

            // 기술 스택 세팅
            TechnicalStack technicalStack1 = TechnicalStack.builder()
                    .name("testTechnicalStack1")
                    .imageNo(saveImage1.getNo())
                    .build();

            TechnicalStack technicalStack2 = TechnicalStack.builder()
                    .name("testTechnicalStack2")
                    .imageNo(saveImage1.getNo())
                    .build();
            TechnicalStack saveTechnicalStack1 = technicalStackRepository.save(technicalStack1);
            TechnicalStack saveTechnicalStack2 = technicalStackRepository.save(technicalStack1);

            // 프로젝트 기술 스택 세팅
            ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
                    .project(saveProject1)
                    .technicalStack(saveTechnicalStack1)
                    .build();
            ProjectTechnicalStack projectTechnicalStack2 = ProjectTechnicalStack.builder()
                    .project(saveProject1)
                    .technicalStack(saveTechnicalStack2)
                    .build();
            ProjectTechnicalStack projectTechnicalStack3 = ProjectTechnicalStack.builder()
                    .project(saveProject2)
                    .technicalStack(saveTechnicalStack1)
                    .build();
            ProjectTechnicalStack projectTechnicalStack4 = ProjectTechnicalStack.builder()
                    .project(saveProject2)
                    .technicalStack(saveTechnicalStack2)
                    .build();
            ProjectTechnicalStack saveProjectTechnicalStack1 = projectTechnicalStackRepository.save(projectTechnicalStack1);
            ProjectTechnicalStack saveProjectTechnicalStack2 = projectTechnicalStackRepository.save(projectTechnicalStack2);
            ProjectTechnicalStack saveProjectTechnicalStack3 = projectTechnicalStackRepository.save(projectTechnicalStack3);
            ProjectTechnicalStack saveProjectTechnicalStack4 = projectTechnicalStackRepository.save(projectTechnicalStack4);

            // 북마크 세팅
            BookMark bookMark1 = BookMark.builder()
                    .user(saveUser1)
                    .project(project1)
                    .build();
            bookMarkRepository.save(bookMark1);

            // when
            String token = jwtTokenService.createToken(new TokenDto(saveUser1.getEmail()));

            ResultActions resultActions = mvc.perform(get("/v1/bookmark?page=0&size=5&sortBy=createDate,desc")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token));

            // then
            resultActions
                    .andDo(print())
                    .andExpect(header().string("Content-type", "application/json"))
                    .andExpect(jsonPath("$.data.length()").value(1))
                    .andExpect(jsonPath("$.data[0].name").value(saveProject1.getName()))
                    .andExpect(jsonPath("$.data[0].maxPeople").value(saveProject1.getMaxPeople()))
                    .andExpect(jsonPath("$.data[0].currentPeople").value(saveProject1.getCurrentPeople()))
                    .andExpect(jsonPath("$.data[0].viewCount").value(saveProject1.getViewCount()))
                    .andExpect(jsonPath("$.data[0].register").value(saveProject1.getCreateUserName()))
                    .andExpect(jsonPath("$.data[0].bookMark").value(true))

                    .andExpect(jsonPath("$.data[0].projectSimplePositionDtoList[0].projectNo").value(saveProjectPosition1.getProject().getNo()))
                    .andExpect(jsonPath("$.data[0].projectSimplePositionDtoList[0].positionNo").value(saveProjectPosition1.getPosition().getNo()))
                    .andExpect(jsonPath("$.data[0].projectSimplePositionDtoList[0].positionName").value(saveProjectPosition1.getPosition().getName()))
                    .andExpect(jsonPath("$.data[0].projectSimplePositionDtoList[1].projectNo").value(saveProjectPosition2.getProject().getNo()))
                    .andExpect(jsonPath("$.data[0].projectSimplePositionDtoList[1].positionNo").value(saveProjectPosition2.getPosition().getNo()))
                    .andExpect(jsonPath("$.data[0].projectSimplePositionDtoList[1].positionName").value(saveProjectPosition2.getPosition().getName()))

                    .andExpect(jsonPath("$.data[0].projectSimpleTechnicalStackDtoList[0].projectNo").value(saveProjectTechnicalStack1.getProject().getNo()))
                    .andExpect(jsonPath("$.data[0].projectSimpleTechnicalStackDtoList[0].image").value(saveImage1.getLogicalName()))
                    .andExpect(jsonPath("$.data[0].projectSimpleTechnicalStackDtoList[0].technicalStackName").value(saveProjectTechnicalStack1.getTechnicalStack().getName()))
                    .andExpect(jsonPath("$.data[0].projectSimpleTechnicalStackDtoList[1].projectNo").value(saveProjectTechnicalStack2.getProject().getNo()))
                    .andExpect(jsonPath("$.data[0].projectSimpleTechnicalStackDtoList[1].image").value(saveImage1.getLogicalName()))
                    .andExpect(jsonPath("$.data[0].projectSimpleTechnicalStackDtoList[1].technicalStackName").value(saveProjectTechnicalStack2.getTechnicalStack().getName()))

                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("실패 : 비로그인 유저")
        public void fail1() throws Exception {
            // when
            ResultActions resultActions = mvc.perform(get("/v1/bookmark?page=0&size=5&sortBy=createDate,desc")
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("즐겨찾기 삭제")
    class bookMarkDelete {
        @Test
        @DisplayName("성공")
        public void success() throws Exception {
            // given
            LocalDateTime createDate = LocalDateTime.now();
            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            // 유저 세팅
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
            User saveUser1 = userRepository.save(user1);

            // 프로젝트 세팅
            Project project1 = Project.builder()
                    .name("testName1")
                    .createUserName("user1")
                    .createDate(createDate.plusDays(1))
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
            Project saveProject1 = projectRepository.save(project1);

            BookMark bookMark1 = BookMark.builder()
                    .user(saveUser1)
                    .project(saveProject1)
                    .build();

            bookMarkRepository.save(bookMark1);

            // when
            String token = jwtTokenService.createToken(new TokenDto(saveUser1.getEmail()));

            ResultActions resultActions = mvc.perform(delete("/v1/bookmark/" + saveProject1.getNo()).contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token));

            // then
            resultActions
                    .andDo(print())
                    .andExpect(header().string("Content-type", "application/json"))
                    .andExpect(jsonPath("$.data").value(true))
                    .andExpect(status().isOk());

            List<BookMark> bookMarkList = bookMarkRepository.findAll();
            assertEquals(bookMarkList.size(), 0);
        }

        @Test
        @DisplayName("실패 : 비로그인 유저")
        public void fail1() throws Exception {
            // when
            ResultActions resultActions = mvc.perform(delete("/v1/bookmark/1").contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

}