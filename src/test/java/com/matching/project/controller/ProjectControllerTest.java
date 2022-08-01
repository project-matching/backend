package com.matching.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.matching.project.config.SecurityConfig;
import com.matching.project.dto.ResponseDto;
import com.matching.project.dto.common.TokenDto;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.project.ProjectPositionDto;
import com.matching.project.dto.project.ProjectRegisterRequestDto;
import com.matching.project.dto.project.ProjectSearchRequestDto;
import com.matching.project.dto.projectposition.ProjectPositionRegisterDto;
import com.matching.project.dto.user.ProjectRegisterUserDto;
import com.matching.project.entity.*;
import com.matching.project.entity.Image;
import com.matching.project.oauth.CustomOAuth2UserService;
import com.matching.project.repository.*;
import com.matching.project.service.JwtTokenService;
import com.matching.project.service.ProjectService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProjectControllerTest {
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
    ImageRepository imageRepository;

    @Autowired
    TechnicalStackRepository technicalStackRepository;

    @Autowired
    BookMarkRepository bookMarkRepository;

    @Autowired
    ProjectParticipateRequestRepository projectParticipateRequestRepository;

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

    /**
     * 0 ~ 2 까지 모집중이면서 삭제 안된 프로젝트
     * 3 모집중이면서 삭제된 프로젝트
     */
    List<Project> saveRecruitmentProject(){
        List<Project> projectList = new ArrayList<>();

        LocalDateTime createDate = LocalDateTime.now();
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        for (int i = 0 ; i < 4 ; i++) {
            boolean state = true;
            boolean delete = false;
            String deleteReason = null;

            if (i == 3) {
                delete = true;
                deleteReason = "testDeleteReason" + i;
            }

            Project project = Project.builder()
                    .name("testName" + i)
                    .createUserName("user")
                    .createDate(createDate.plusDays(i))
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(state)
                    .introduction("testIntroduction" + i)
                    .maxPeople(10)
                    .currentPeople(4)
                    .delete(delete)
                    .deleteReason(deleteReason)
                    .viewCount(10)
                    .commentCount(10)
                    .build();
            projectList.add(projectRepository.save(project));
        }
        return projectList;
    }
    /**
     * 4 ~ 6 까지 모집완료이면서 삭제 안된 프로젝트
     * 7 모집완료이면서 삭제된 프로젝트
     */
    List<Project> saveRecruitmentCompleteProject(){
        List<Project> projectList = new ArrayList<>();

        LocalDateTime createDate = LocalDateTime.now();
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        for (int i = 4 ; i < 8 ; i++) {
            boolean state = false;
            boolean delete = false;
            String deleteReason = null;

            if (i == 7) {
                delete = true;
                deleteReason = "testDeleteReason" + i;
            }

            Project project = Project.builder()
                    .name("testName" + i)
                    .createUserName("user")
                    .createDate(createDate.plusDays(i))
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(state)
                    .introduction("testIntroduction" + i)
                    .maxPeople(10)
                    .currentPeople(4)
                    .delete(delete)
                    .deleteReason(deleteReason)
                    .viewCount(10)
                    .commentCount(10)
                    .build();
            projectList.add(projectRepository.save(project));
        }
        return projectList;
    }

    /**
     * 8 ~ 9 까지 내가 만든 프로젝트이면서 삭제 안된 프로젝트
     * 10 내가 만든 프로젝트이면서 삭제된 프로젝트
     */
    List<Project> saveCreateSelfProject(User user) {
        List<Project> projectList = new ArrayList<>();

        LocalDateTime createDate = LocalDateTime.now();
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        for (int i = 8 ; i < 11 ; i++) {
            boolean state = false;
            boolean delete = false;
            String deleteReason = null;

            if (i == 10) {
                delete = true;
                deleteReason = "testDeleteReason" + i;
            }

            Project project = Project.builder()
                    .name("testName" + i)
                    .createUserName("userName1")
                    .createDate(createDate.plusDays(i))
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(state)
                    .introduction("testIntroduction" + i)
                    .maxPeople(10)
                    .currentPeople(4)
                    .delete(delete)
                    .deleteReason(deleteReason)
                    .viewCount(10)
                    .commentCount(10)
                    .user(user)
                    .build();
            projectList.add(projectRepository.save(project));
        }
        return projectList;
    }

    BookMark saveBookMark(User user, Project project) {
        BookMark bookMark = BookMark.builder()
                .user(user)
                .project(project)
                .build();
        return bookMarkRepository.save(bookMark);
    }

    @Test
    void 프로젝트_등록_폼_테스트() throws Exception {
        // given
        User saveUser = saveUser();

        Position position1 = Position.builder()
                .name("testPosition1")
                .build();
        Position position2 = Position.builder()
                .name("testPosition2")
                .build();

        positionRepository.save(position1);
        positionRepository.save(position2);


        TechnicalStack technicalStack1 = TechnicalStack.builder()
                .name("testTechnicalStack1")
                .build();
        TechnicalStack technicalStack2 = TechnicalStack.builder()
                .name("testTechnicalStack2")
                .build();

        technicalStackRepository.save(technicalStack1);
        technicalStackRepository.save(technicalStack2);

        // then
        String token = jwtTokenService.createToken(new TokenDto(saveUser.getEmail()));

        mvc.perform(get("/v1/project/create").contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(header().string("Content-type", "application/json"))

                .andExpect(jsonPath("$.data.positionRegisterFormDtoList[0].positionNo").value(position1.getNo()))
                .andExpect(jsonPath("$.data.positionRegisterFormDtoList[0].positionName").value(position1.getName()))
                .andExpect(jsonPath("$.data.positionRegisterFormDtoList[1].positionNo").value(position2.getNo()))
                .andExpect(jsonPath("$.data.positionRegisterFormDtoList[1].positionName").value(position2.getName()))

                .andExpect(jsonPath("$.data.technicalStackRegisterFormDtoList[0].technicalStackNo").value(technicalStack1.getNo()))
                .andExpect(jsonPath("$.data.technicalStackRegisterFormDtoList[0].technicalStackName").value(technicalStack1.getName()))
                .andExpect(jsonPath("$.data.technicalStackRegisterFormDtoList[1].technicalStackNo").value(technicalStack2.getNo()))
                .andExpect(jsonPath("$.data.technicalStackRegisterFormDtoList[1].technicalStackName").value(technicalStack2.getName()))
                .andExpect(status().isOk());
    }

    @Test
    void 프로젝트_등록_테스트() throws Exception {
        // given
        User saveUser = saveUser();

        Position position1 = Position.builder()
                .name("testPosition1")
                .build();
        Position position2 = Position.builder()
                .name("testPosition2")
                .build();

        Position savePosition1 = positionRepository.save(position1);
        Position savePosition2 = positionRepository.save(position2);


        TechnicalStack technicalStack1 = TechnicalStack.builder()
                .name("testTechnicalStack1")
                .build();
        TechnicalStack technicalStack2 = TechnicalStack.builder()
                .name("testTechnicalStack2")
                .build();

        TechnicalStack saveTechnicalStack1 = technicalStackRepository.save(technicalStack1);
        TechnicalStack saveTechnicalStack2 = technicalStackRepository.save(technicalStack2);


        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        List<ProjectPositionRegisterDto> projectPositionRegisterDtoList = new ArrayList<>();
        projectPositionRegisterDtoList.add(new ProjectPositionRegisterDto(savePosition1.getNo(), new ProjectRegisterUserDto(saveUser.getNo())));
        projectPositionRegisterDtoList.add(new ProjectPositionRegisterDto(savePosition2.getNo(), null));

        List<Long> projectTechnicalStackList = new ArrayList<>();
        projectTechnicalStackList.add(saveTechnicalStack1.getNo());
        projectTechnicalStackList.add(saveTechnicalStack2.getNo());

        ProjectRegisterRequestDto projectRegisterRequestDto = ProjectRegisterRequestDto.builder()
                .name("testName")
                .startDate(startDate)
                .endDate(endDate)
                .introduction("testIntroduction1")
                .projectPositionRegisterDtoList(projectPositionRegisterDtoList)
                .projectTechnicalStackList(projectTechnicalStackList)
                .build();

        // then
        String token = jwtTokenService.createToken(new TokenDto(saveUser.getEmail()));

        MvcResult result = mvc.perform(post("/v1/project").contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(projectRegisterRequestDto)))
                .andDo(print())
                .andExpect(header().string("Content-type", "application/json"))
                .andExpect(status().isOk())
                .andReturn();

        Type type = new TypeToken<ResponseDto<Long>>() {}.getType();
        ResponseDto<Long> responseDto = new Gson().fromJson(result.getResponse().getContentAsString(), type);

        Project project = projectRepository.findById((Long)responseDto.getData()).get();
        assertEquals(project.getNo(), responseDto.getData());
        assertEquals(project.getName(), projectRegisterRequestDto.getName());
        assertEquals(project.getCreateUserName(), saveUser.getName());
        assertEquals(project.getStartDate(), projectRegisterRequestDto.getStartDate());
        assertEquals(project.getEndDate(), projectRegisterRequestDto.getEndDate());
        assertEquals(project.isState(), true);
        assertEquals(project.getIntroduction(), projectRegisterRequestDto.getIntroduction());
        assertEquals(project.getMaxPeople(), projectRegisterRequestDto.getProjectPositionRegisterDtoList().size());
        assertEquals(project.isDelete(), false);
        assertEquals(project.getDeleteReason(), null);
        assertEquals(project.getViewCount(), 0);
        assertEquals(project.getCommentCount(), 0);
        assertEquals(project.getUser(), saveUser);

        assertEquals(project.getProjectPositionList().get(0).getPosition(), savePosition1);
        assertEquals(project.getProjectPositionList().get(0).getProject(), project);
        assertEquals(project.getProjectPositionList().get(0).getUser(), saveUser);
        assertEquals(project.getProjectPositionList().get(1).getPosition(), savePosition2);
        assertEquals(project.getProjectPositionList().get(1).getProject(), project);
        assertEquals(project.getProjectPositionList().get(1).getUser(), null);

        assertEquals(project.getProjectTechnicalStackList().get(0).getTechnicalStack(), saveTechnicalStack1);
        assertEquals(project.getProjectTechnicalStackList().get(0).getProject(), project);
        assertEquals(project.getProjectTechnicalStackList().get(1).getTechnicalStack(), saveTechnicalStack2);
        assertEquals(project.getProjectTechnicalStackList().get(1).getProject(), project);
    }

    @Test
    void 프로젝트_등록_VALIDATION_테스트() throws Exception {
        // given
        User saveUser = saveUser();
        List<ProjectPositionRegisterDto> projectPositionRegisterDtoList = new ArrayList<>();
        projectPositionRegisterDtoList.add(new ProjectPositionRegisterDto(null, null));

        ProjectRegisterRequestDto projectRegisterRequestDto = ProjectRegisterRequestDto.builder()
                .name(null)
                .startDate(null)
                .endDate(null)
                .introduction(null)
                .projectPositionRegisterDtoList(projectPositionRegisterDtoList)
                .projectTechnicalStackList(null)
                .build();

        // then
        String token = jwtTokenService.createToken(new TokenDto(saveUser.getEmail()));

        mvc.perform(post("/v1/project").contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(projectRegisterRequestDto)))
                .andDo(print())
                .andExpect(header().string("Content-type", "application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();
    }
//
    @Test
    public void 비로그인_모집중_프로젝트_조회_테스트() throws Exception {
        User saveUser = saveUser();
        List<Project> saveRecruitmentProject = saveRecruitmentProject();
        saveRecruitmentCompleteProject();

        // 포지션 세팅
        Position position1 = Position.builder()
                .name("testPosition1")
                .build();
        Position savePosition1 = positionRepository.save(position1);
        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .project(saveRecruitmentProject.get(0))
                .position(savePosition1)
                .build();
        ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);

        // 이미지 세팅
        Image image1 = Image.builder()
                .logicalName("testLogicalName1")
                .physicalName("testPhysicalName1")
                .url("testUrl1")
                .build();
        Image saveImage1 = imageRepository.save(image1);

        // 기술스택 세팅
        TechnicalStack technicalStack1 = TechnicalStack.builder()
                .imageNo(saveImage1.getNo())
                .name("testTechnicalStack1")
                .build();
        technicalStackRepository.save(technicalStack1);

        ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
                .technicalStack(technicalStack1)
                .project(saveRecruitmentProject.get(0))
                .build();

        ProjectTechnicalStack saveProjectTechnicalStack1 = projectTechnicalStackRepository.save(projectTechnicalStack1);

        // 0번째 1번째는 북마크
        saveBookMark(saveUser, saveRecruitmentProject.get(0));
        saveBookMark(saveUser, saveRecruitmentProject.get(1));

        // then
        mvc.perform(get("/v1/project/recruitment?page=0&size=5&sortBy=createDate,desc").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(header().string("Content-type", "application/json"))
                .andExpect(jsonPath("$.data.content.length()").value(3))
                .andExpect(jsonPath("$.data.content[0].name").value("testName2"))
                .andExpect(jsonPath("$.data.content[0].maxPeople").value(10))
                .andExpect(jsonPath("$.data.content[0].currentPeople").value(4))
                .andExpect(jsonPath("$.data.content[0].viewCount").value(10))
                .andExpect(jsonPath("$.data.content[0].register").value("user"))
                .andExpect(jsonPath("$.data.content[0].bookMark").value(false))

                .andExpect(jsonPath("$.data.content[1].name").value("testName1"))
                .andExpect(jsonPath("$.data.content[1].maxPeople").value(10))
                .andExpect(jsonPath("$.data.content[1].currentPeople").value(4))
                .andExpect(jsonPath("$.data.content[1].viewCount").value(10))
                .andExpect(jsonPath("$.data.content[1].register").value("user"))
                .andExpect(jsonPath("$.data.content[1].bookMark").value(false))

                .andExpect(jsonPath("$.data.content[2].name").value("testName0"))
                .andExpect(jsonPath("$.data.content[2].maxPeople").value(10))
                .andExpect(jsonPath("$.data.content[2].currentPeople").value(4))
                .andExpect(jsonPath("$.data.content[2].viewCount").value(10))
                .andExpect(jsonPath("$.data.content[2].register").value("user"))
                .andExpect(jsonPath("$.data.content[2].bookMark").value(false))
                .andExpect(jsonPath("$.data.content[2].projectSimplePositionDtoList[0].projectNo").value(saveProjectPosition1.getProject().getNo()))
                .andExpect(jsonPath("$.data.content[2].projectSimplePositionDtoList[0].positionNo").value(saveProjectPosition1.getPosition().getNo()))
                .andExpect(jsonPath("$.data.content[2].projectSimplePositionDtoList[0].positionName").value(saveProjectPosition1.getPosition().getName()))
                .andExpect(jsonPath("$.data.content[2].projectSimpleTechnicalStackDtoList[0].projectNo").value(saveProjectTechnicalStack1.getProject().getNo()))
                .andExpect(jsonPath("$.data.content[2].projectSimpleTechnicalStackDtoList[0].image").value(saveImage1.getLogicalName()))
                .andExpect(jsonPath("$.data.content[2].projectSimpleTechnicalStackDtoList[0].technicalStackName").value(saveProjectTechnicalStack1.getTechnicalStack().getName()))

                // paging
                .andExpect(jsonPath("$.data.pageable.pageSize").value(5))
                .andExpect(jsonPath("$.data.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.data.pageable.offset").value(0))
                .andExpect(jsonPath("$.data.totalElements").value(3))
                .andExpect(status().isOk());
    }

    @Test
    public void 로그인_모집중_프로젝트_조회_테스트() throws Exception {
        User saveUser = saveUser();
        List<Project> saveRecruitmentProject = saveRecruitmentProject();
        saveRecruitmentCompleteProject();

        // 포지션 세팅
        Position position1 = Position.builder()
                .name("testPosition1")
                .build();
        Position savePosition1 = positionRepository.save(position1);
        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .project(saveRecruitmentProject.get(0))
                .position(savePosition1)
                .build();
        ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);

        // 이미지 세팅
        Image image1 = Image.builder()
                .logicalName("testLogicalName1")
                .physicalName("testPhysicalName1")
                .url("testUrl1")
                .build();
        Image saveImage1 = imageRepository.save(image1);

        // 기술스택 세팅
        TechnicalStack technicalStack1 = TechnicalStack.builder()
                .imageNo(saveImage1.getNo())
                .name("testTechnicalStack1")
                .build();
        technicalStackRepository.save(technicalStack1);

        ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
                .technicalStack(technicalStack1)
                .project(saveRecruitmentProject.get(0))
                .build();

        ProjectTechnicalStack saveProjectTechnicalStack1 = projectTechnicalStackRepository.save(projectTechnicalStack1);

        // 0번째 1번째는 북마크
        saveBookMark(saveUser, saveRecruitmentProject.get(0));
        saveBookMark(saveUser, saveRecruitmentProject.get(1));

        // then
        String token = jwtTokenService.createToken(new TokenDto(saveUser.getEmail()));

        mvc.perform(get("/v1/project/recruitment?page=0&size=5&sortBy=createDate,desc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(header().string("Content-type", "application/json"))
                .andExpect(jsonPath("$.data.content.length()").value(3))
                .andExpect(jsonPath("$.data.content[0].name").value("testName2"))
                .andExpect(jsonPath("$.data.content[0].maxPeople").value(10))
                .andExpect(jsonPath("$.data.content[0].currentPeople").value(4))
                .andExpect(jsonPath("$.data.content[0].viewCount").value(10))
                .andExpect(jsonPath("$.data.content[0].register").value("user"))
                .andExpect(jsonPath("$.data.content[0].bookMark").value(false))

                .andExpect(jsonPath("$.data.content[1].name").value("testName1"))
                .andExpect(jsonPath("$.data.content[1].maxPeople").value(10))
                .andExpect(jsonPath("$.data.content[1].currentPeople").value(4))
                .andExpect(jsonPath("$.data.content[1].viewCount").value(10))
                .andExpect(jsonPath("$.data.content[1].register").value("user"))
                .andExpect(jsonPath("$.data.content[1].bookMark").value(true))

                .andExpect(jsonPath("$.data.content[2].name").value("testName0"))
                .andExpect(jsonPath("$.data.content[2].maxPeople").value(10))
                .andExpect(jsonPath("$.data.content[2].currentPeople").value(4))
                .andExpect(jsonPath("$.data.content[2].viewCount").value(10))
                .andExpect(jsonPath("$.data.content[2].register").value("user"))
                .andExpect(jsonPath("$.data.content[2].bookMark").value(true))
                .andExpect(jsonPath("$.data.content[2].projectSimplePositionDtoList[0].projectNo").value(saveProjectPosition1.getProject().getNo()))
                .andExpect(jsonPath("$.data.content[2].projectSimplePositionDtoList[0].positionNo").value(saveProjectPosition1.getPosition().getNo()))
                .andExpect(jsonPath("$.data.content[2].projectSimplePositionDtoList[0].positionName").value(saveProjectPosition1.getPosition().getName()))
                .andExpect(jsonPath("$.data.content[2].projectSimpleTechnicalStackDtoList[0].projectNo").value(saveProjectTechnicalStack1.getProject().getNo()))
                .andExpect(jsonPath("$.data.content[2].projectSimpleTechnicalStackDtoList[0].image").value(saveImage1.getLogicalName()))
                .andExpect(jsonPath("$.data.content[2].projectSimpleTechnicalStackDtoList[0].technicalStackName").value(saveProjectTechnicalStack1.getTechnicalStack().getName()))

                // paging
                .andExpect(jsonPath("$.data.pageable.pageSize").value(5))
                .andExpect(jsonPath("$.data.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.data.pageable.offset").value(0))
                .andExpect(jsonPath("$.data.totalElements").value(3))
                .andExpect(status().isOk());
    }

    @Test
    public void 비로그인_모집완료_프로젝트_조회_테스트() throws Exception {
        // given
        User saveUser = saveUser();
        saveRecruitmentProject();
        List<Project> saveRecruitmentCompleteProject = saveRecruitmentCompleteProject();

        // 포지션 세팅
        Position position1 = Position.builder()
                .name("testPosition1")
                .build();
        Position savePosition1 = positionRepository.save(position1);
        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .project(saveRecruitmentCompleteProject.get(0))
                .position(savePosition1)
                .build();
        ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);

        // 이미지 세팅
        Image image1 = Image.builder()
                .logicalName("testLogicalName1")
                .physicalName("testPhysicalName1")
                .url("testUrl1")
                .build();
        Image saveImage1 = imageRepository.save(image1);

        // 기술스택 세팅
        TechnicalStack technicalStack1 = TechnicalStack.builder()
                .imageNo(saveImage1.getNo())
                .name("testTechnicalStack1")
                .build();
        technicalStackRepository.save(technicalStack1);

        ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
                .technicalStack(technicalStack1)
                .project(saveRecruitmentCompleteProject.get(0))
                .build();

        ProjectTechnicalStack saveProjectTechnicalStack1 = projectTechnicalStackRepository.save(projectTechnicalStack1);

        // 4번째 5번째는 북마크
        saveBookMark(saveUser, saveRecruitmentCompleteProject.get(0));
        saveBookMark(saveUser, saveRecruitmentCompleteProject.get(1));

        // then
        mvc.perform(get("/v1/project/recruitment/complete?page=0&size=5&sortBy=createDate,desc").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(header().string("Content-type", "application/json"))
                .andExpect(jsonPath("$.data.content.length()").value(3))
                .andExpect(jsonPath("$.data.content[0].name").value("testName6"))
                .andExpect(jsonPath("$.data.content[0].maxPeople").value(10))
                .andExpect(jsonPath("$.data.content[0].currentPeople").value(4))
                .andExpect(jsonPath("$.data.content[0].viewCount").value(10))
                .andExpect(jsonPath("$.data.content[0].register").value("user"))
                .andExpect(jsonPath("$.data.content[0].bookMark").value(false))

                .andExpect(jsonPath("$.data.content[1].name").value("testName5"))
                .andExpect(jsonPath("$.data.content[1].maxPeople").value(10))
                .andExpect(jsonPath("$.data.content[1].currentPeople").value(4))
                .andExpect(jsonPath("$.data.content[1].viewCount").value(10))
                .andExpect(jsonPath("$.data.content[1].register").value("user"))
                .andExpect(jsonPath("$.data.content[1].bookMark").value(false))

                .andExpect(jsonPath("$.data.content[2].name").value("testName4"))
                .andExpect(jsonPath("$.data.content[2].maxPeople").value(10))
                .andExpect(jsonPath("$.data.content[2].currentPeople").value(4))
                .andExpect(jsonPath("$.data.content[2].viewCount").value(10))
                .andExpect(jsonPath("$.data.content[2].register").value("user"))
                .andExpect(jsonPath("$.data.content[2].bookMark").value(false))
                .andExpect(jsonPath("$.data.content[2].projectSimplePositionDtoList[0].projectNo").value(saveProjectPosition1.getProject().getNo()))
                .andExpect(jsonPath("$.data.content[2].projectSimplePositionDtoList[0].positionNo").value(saveProjectPosition1.getPosition().getNo()))
                .andExpect(jsonPath("$.data.content[2].projectSimplePositionDtoList[0].positionName").value(saveProjectPosition1.getPosition().getName()))
                .andExpect(jsonPath("$.data.content[2].projectSimpleTechnicalStackDtoList[0].projectNo").value(saveProjectTechnicalStack1.getProject().getNo()))
                .andExpect(jsonPath("$.data.content[2].projectSimpleTechnicalStackDtoList[0].image").value(saveImage1.getLogicalName()))
                .andExpect(jsonPath("$.data.content[2].projectSimpleTechnicalStackDtoList[0].technicalStackName").value(saveProjectTechnicalStack1.getTechnicalStack().getName()))

                // paging
                .andExpect(jsonPath("$.data.pageable.pageSize").value(5))
                .andExpect(jsonPath("$.data.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.data.pageable.offset").value(0))
                .andExpect(jsonPath("$.data.totalElements").value(3))
                .andExpect(status().isOk());
    }

    @Test
    public void 로그인_모집완료_프로젝트_조회_테스트() throws Exception {
        // given
        User saveUser = saveUser();
        saveRecruitmentProject();
        List<Project> saveRecruitmentCompleteProject = saveRecruitmentCompleteProject();

        // 포지션 세팅
        Position position1 = Position.builder()
                .name("testPosition1")
                .build();
        Position savePosition1 = positionRepository.save(position1);
        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .project(saveRecruitmentCompleteProject.get(0))
                .position(savePosition1)
                .build();
        ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);

        // 이미지 세팅
        Image image1 = Image.builder()
                .logicalName("testLogicalName1")
                .physicalName("testPhysicalName1")
                .url("testUrl1")
                .build();
        Image saveImage1 = imageRepository.save(image1);

        // 기술스택 세팅
        TechnicalStack technicalStack1 = TechnicalStack.builder()
                .imageNo(saveImage1.getNo())
                .name("testTechnicalStack1")
                .build();
        technicalStackRepository.save(technicalStack1);

        ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
                .technicalStack(technicalStack1)
                .project(saveRecruitmentCompleteProject.get(0))
                .build();

        ProjectTechnicalStack saveProjectTechnicalStack1 = projectTechnicalStackRepository.save(projectTechnicalStack1);

        // 4번째 5번째는 북마크
        saveBookMark(saveUser, saveRecruitmentCompleteProject.get(0));
        saveBookMark(saveUser, saveRecruitmentCompleteProject.get(1));

        // then
        String token = jwtTokenService.createToken(new TokenDto(saveUser.getEmail()));

        mvc.perform(get("/v1/project/recruitment/complete?page=0&size=5&sortBy=createDate,desc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(header().string("Content-type", "application/json"))
                .andExpect(jsonPath("$.data.content.length()").value(3))
                .andExpect(jsonPath("$.data.content[0].name").value("testName6"))
                .andExpect(jsonPath("$.data.content[0].maxPeople").value(10))
                .andExpect(jsonPath("$.data.content[0].currentPeople").value(4))
                .andExpect(jsonPath("$.data.content[0].viewCount").value(10))
                .andExpect(jsonPath("$.data.content[0].register").value("user"))
                .andExpect(jsonPath("$.data.content[0].bookMark").value(false))

                .andExpect(jsonPath("$.data.content[1].name").value("testName5"))
                .andExpect(jsonPath("$.data.content[1].maxPeople").value(10))
                .andExpect(jsonPath("$.data.content[1].currentPeople").value(4))
                .andExpect(jsonPath("$.data.content[1].viewCount").value(10))
                .andExpect(jsonPath("$.data.content[1].register").value("user"))
                .andExpect(jsonPath("$.data.content[1].bookMark").value(true))

                .andExpect(jsonPath("$.data.content[2].name").value("testName4"))
                .andExpect(jsonPath("$.data.content[2].maxPeople").value(10))
                .andExpect(jsonPath("$.data.content[2].currentPeople").value(4))
                .andExpect(jsonPath("$.data.content[2].viewCount").value(10))
                .andExpect(jsonPath("$.data.content[2].register").value("user"))
                .andExpect(jsonPath("$.data.content[2].bookMark").value(true))
                .andExpect(jsonPath("$.data.content[2].projectSimplePositionDtoList[0].projectNo").value(saveProjectPosition1.getProject().getNo()))
                .andExpect(jsonPath("$.data.content[2].projectSimplePositionDtoList[0].positionNo").value(saveProjectPosition1.getPosition().getNo()))
                .andExpect(jsonPath("$.data.content[2].projectSimplePositionDtoList[0].positionName").value(saveProjectPosition1.getPosition().getName()))
                .andExpect(jsonPath("$.data.content[2].projectSimpleTechnicalStackDtoList[0].projectNo").value(saveProjectTechnicalStack1.getProject().getNo()))
                .andExpect(jsonPath("$.data.content[2].projectSimpleTechnicalStackDtoList[0].image").value(saveImage1.getLogicalName()))
                .andExpect(jsonPath("$.data.content[2].projectSimpleTechnicalStackDtoList[0].technicalStackName").value(saveProjectTechnicalStack1.getTechnicalStack().getName()))

                // paging
                .andExpect(jsonPath("$.data.pageable.pageSize").value(5))
                .andExpect(jsonPath("$.data.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.data.pageable.offset").value(0))
                .andExpect(jsonPath("$.data.totalElements").value(3))
                .andExpect(status().isOk());
    }

    @Test
    public void 내가_만든_프로젝트_조회_테스트() throws Exception {
        // given
        User saveUser = saveUser();
        saveRecruitmentProject();
        saveRecruitmentCompleteProject();
        List<Project> saveCreateSelfProject = saveCreateSelfProject(saveUser);

        // 포지션 세팅
        Position position1 = Position.builder()
                .name("testPosition1")
                .build();
        Position savePosition1 = positionRepository.save(position1);
        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .project(saveCreateSelfProject.get(0))
                .position(savePosition1)
                .build();
        ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);

        // 이미지 세팅
        Image image1 = Image.builder()
                .logicalName("testLogicalName1")
                .physicalName("testPhysicalName1")
                .url("testUrl1")
                .build();
        Image saveImage1 = imageRepository.save(image1);

        // 기술스택 세팅
        TechnicalStack technicalStack1 = TechnicalStack.builder()
                .imageNo(saveImage1.getNo())
                .name("testTechnicalStack1")
                .build();
        technicalStackRepository.save(technicalStack1);

        ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
                .technicalStack(technicalStack1)
                .project(saveCreateSelfProject.get(0))
                .build();

        ProjectTechnicalStack saveProjectTechnicalStack1 = projectTechnicalStackRepository.save(projectTechnicalStack1);

        // 8번째는 북마크
        saveBookMark(saveUser, saveCreateSelfProject.get(0));

        // then
        String token = jwtTokenService.createToken(new TokenDto(saveUser.getEmail()));

        mvc.perform(get("/v1/project/create/self?page=0&size=5&sortBy=createDate,desc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(header().string("Content-type", "application/json"))
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].name").value("testName9"))
                .andExpect(jsonPath("$.data.content[0].maxPeople").value(10))
                .andExpect(jsonPath("$.data.content[0].currentPeople").value(4))
                .andExpect(jsonPath("$.data.content[0].viewCount").value(10))
                .andExpect(jsonPath("$.data.content[0].register").value(saveUser.getName()))
                .andExpect(jsonPath("$.data.content[0].bookMark").value(false))

                .andExpect(jsonPath("$.data.content[1].name").value("testName8"))
                .andExpect(jsonPath("$.data.content[1].maxPeople").value(10))
                .andExpect(jsonPath("$.data.content[1].currentPeople").value(4))
                .andExpect(jsonPath("$.data.content[1].viewCount").value(10))
                .andExpect(jsonPath("$.data.content[1].register").value(saveUser.getName()))
                .andExpect(jsonPath("$.data.content[1].bookMark").value(true))

                .andExpect(jsonPath("$.data.content[1].projectSimplePositionDtoList[0].projectNo").value(saveProjectPosition1.getProject().getNo()))
                .andExpect(jsonPath("$.data.content[1].projectSimplePositionDtoList[0].positionNo").value(saveProjectPosition1.getPosition().getNo()))
                .andExpect(jsonPath("$.data.content[1].projectSimplePositionDtoList[0].positionName").value(saveProjectPosition1.getPosition().getName()))
                .andExpect(jsonPath("$.data.content[1].projectSimpleTechnicalStackDtoList[0].projectNo").value(saveProjectTechnicalStack1.getProject().getNo()))
                .andExpect(jsonPath("$.data.content[1].projectSimpleTechnicalStackDtoList[0].image").value(saveImage1.getLogicalName()))
                .andExpect(jsonPath("$.data.content[1].projectSimpleTechnicalStackDtoList[0].technicalStackName").value(saveProjectTechnicalStack1.getTechnicalStack().getName()))

                // paging
                .andExpect(jsonPath("$.data.pageable.pageSize").value(5))
                .andExpect(jsonPath("$.data.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.data.pageable.offset").value(0))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(status().isOk());
    }

    @Test
    public void 참여중인_프로젝트_조회_테스트() throws Exception {
        // given
        User saveUser = saveUser();
        List<Project> saveRecruitmentProjectList = saveRecruitmentProject();
        List<Project> saveRecruitmentCompleteProjectList = saveRecruitmentCompleteProject();

        // 포지션 세팅
        Position position1 = Position.builder()
                .name("testPosition1")
                .build();
        Position savePosition1 = positionRepository.save(position1);
        
        // 프로젝트 포지션 세팅
        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .project(saveRecruitmentProjectList.get(0))
                .position(savePosition1)
                .user(saveUser)
                .build();
        ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);

        ProjectPosition projectPosition2 = ProjectPosition.builder()
                .project(saveRecruitmentCompleteProjectList.get(0))
                .position(savePosition1)
                .user(saveUser)
                .build();
        ProjectPosition saveProjectPosition2 = projectPositionRepository.save(projectPosition2);

        // 이미지 세팅
        Image image1 = Image.builder()
                .logicalName("testLogicalName1")
                .physicalName("testPhysicalName1")
                .url("testUrl1")
                .build();
        Image saveImage1 = imageRepository.save(image1);

        // 기술스택 세팅
        TechnicalStack technicalStack1 = TechnicalStack.builder()
                .imageNo(saveImage1.getNo())
                .name("testTechnicalStack1")
                .build();
        technicalStackRepository.save(technicalStack1);
        
        // 프로젝트 기술스택 세팅
        ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
                .technicalStack(technicalStack1)
                .project(saveRecruitmentProjectList.get(0))
                .build();

        ProjectTechnicalStack saveProjectTechnicalStack1 = projectTechnicalStackRepository.save(projectTechnicalStack1);

        ProjectTechnicalStack projectTechnicalStack2 = ProjectTechnicalStack.builder()
                .technicalStack(technicalStack1)
                .project(saveRecruitmentCompleteProjectList.get(0))
                .build();

        ProjectTechnicalStack saveProjectTechnicalStack2 = projectTechnicalStackRepository.save(projectTechnicalStack2);

        // 북마크
        saveBookMark(saveUser, saveRecruitmentProjectList.get(0));

        // then
        String token = jwtTokenService.createToken(new TokenDto(saveUser.getEmail()));

        mvc.perform(get("/v1/project/participate?page=0&size=5&sortBy=createDate,desc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(header().string("Content-type", "application/json"))
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].name").value("testName4"))
                .andExpect(jsonPath("$.data.content[0].maxPeople").value(10))
                .andExpect(jsonPath("$.data.content[0].currentPeople").value(4))
                .andExpect(jsonPath("$.data.content[0].viewCount").value(10))
                .andExpect(jsonPath("$.data.content[0].register").value("user"))
                .andExpect(jsonPath("$.data.content[0].bookMark").value(false))
                .andExpect(jsonPath("$.data.content[0].projectSimplePositionDtoList[0].projectNo").value(saveProjectPosition2.getProject().getNo()))
                .andExpect(jsonPath("$.data.content[0].projectSimplePositionDtoList[0].positionNo").value(saveProjectPosition2.getPosition().getNo()))
                .andExpect(jsonPath("$.data.content[0].projectSimplePositionDtoList[0].positionName").value(saveProjectPosition2.getPosition().getName()))
                .andExpect(jsonPath("$.data.content[0].projectSimpleTechnicalStackDtoList[0].projectNo").value(saveProjectTechnicalStack2.getProject().getNo()))
                .andExpect(jsonPath("$.data.content[0].projectSimpleTechnicalStackDtoList[0].image").value(saveImage1.getLogicalName()))
                .andExpect(jsonPath("$.data.content[0].projectSimpleTechnicalStackDtoList[0].technicalStackName").value(saveProjectTechnicalStack2.getTechnicalStack().getName()))

                .andExpect(jsonPath("$.data.content[1].name").value("testName0"))
                .andExpect(jsonPath("$.data.content[1].maxPeople").value(10))
                .andExpect(jsonPath("$.data.content[1].currentPeople").value(4))
                .andExpect(jsonPath("$.data.content[1].viewCount").value(10))
                .andExpect(jsonPath("$.data.content[1].register").value("user"))
                .andExpect(jsonPath("$.data.content[1].bookMark").value(true))
                .andExpect(jsonPath("$.data.content[1].projectSimplePositionDtoList[0].projectNo").value(saveProjectPosition1.getProject().getNo()))
                .andExpect(jsonPath("$.data.content[1].projectSimplePositionDtoList[0].positionNo").value(saveProjectPosition1.getPosition().getNo()))
                .andExpect(jsonPath("$.data.content[1].projectSimplePositionDtoList[0].positionName").value(saveProjectPosition1.getPosition().getName()))
                .andExpect(jsonPath("$.data.content[1].projectSimpleTechnicalStackDtoList[0].projectNo").value(saveProjectTechnicalStack1.getProject().getNo()))
                .andExpect(jsonPath("$.data.content[1].projectSimpleTechnicalStackDtoList[0].image").value(saveImage1.getLogicalName()))
                .andExpect(jsonPath("$.data.content[1].projectSimpleTechnicalStackDtoList[0].technicalStackName").value(saveProjectTechnicalStack1.getTechnicalStack().getName()))

                // paging
                .andExpect(jsonPath("$.data.pageable.pageSize").value(5))
                .andExpect(jsonPath("$.data.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.data.pageable.offset").value(0))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(status().isOk());
    }

    @Test
    public void 신청중인_프로젝트_조회_테스트() throws Exception {
        // given
        User saveUser = saveUser();
        List<Project> saveRecruitmentProjectList = saveRecruitmentProject();
        List<Project> saveRecruitmentCompleteProjectList = saveRecruitmentCompleteProject();

        // 포지션 세팅
        Position position1 = Position.builder()
                .name("testPosition1")
                .build();
        Position savePosition1 = positionRepository.save(position1);

        // 프로젝트 포지션 세팅
        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .project(saveRecruitmentProjectList.get(0))
                .position(savePosition1)
                .user(null)
                .build();
        ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);

        ProjectPosition projectPosition2 = ProjectPosition.builder()
                .project(saveRecruitmentCompleteProjectList.get(0))
                .position(savePosition1)
                .user(null)
                .build();
        ProjectPosition saveProjectPosition2 = projectPositionRepository.save(projectPosition2);

        // 이미지 세팅
        Image image1 = Image.builder()
                .logicalName("testLogicalName1")
                .physicalName("testPhysicalName1")
                .url("testUrl1")
                .build();
        Image saveImage1 = imageRepository.save(image1);

        // 기술스택 세팅
        TechnicalStack technicalStack1 = TechnicalStack.builder()
                .imageNo(saveImage1.getNo())
                .name("testTechnicalStack1")
                .build();
        technicalStackRepository.save(technicalStack1);

        // 프로젝트 기술스택 세팅
        ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
                .technicalStack(technicalStack1)
                .project(saveRecruitmentProjectList.get(0))
                .build();

        ProjectTechnicalStack saveProjectTechnicalStack1 = projectTechnicalStackRepository.save(projectTechnicalStack1);

        ProjectTechnicalStack projectTechnicalStack2 = ProjectTechnicalStack.builder()
                .technicalStack(technicalStack1)
                .project(saveRecruitmentCompleteProjectList.get(0))
                .build();

        ProjectTechnicalStack saveProjectTechnicalStack2 = projectTechnicalStackRepository.save(projectTechnicalStack2);

        // 북마크
        saveBookMark(saveUser, saveRecruitmentProjectList.get(0));
        
        // 프로젝트 신청
        ProjectParticipateRequest projectParticipateRequest1 = ProjectParticipateRequest
                .builder()
                .user(saveUser)
                .projectPosition(saveProjectPosition1)
                .build();
        projectParticipateRequestRepository.save(projectParticipateRequest1);

        ProjectParticipateRequest projectParticipateRequest2 = ProjectParticipateRequest
                .builder()
                .user(saveUser)
                .projectPosition(saveProjectPosition2)
                .build();
        projectParticipateRequestRepository.save(projectParticipateRequest2);

        // then
        String token = jwtTokenService.createToken(new TokenDto(saveUser.getEmail()));

        mvc.perform(get("/v1/project/application?page=0&size=5&sortBy=createDate,desc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(header().string("Content-type", "application/json"))
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].name").value("testName4"))
                .andExpect(jsonPath("$.data.content[0].maxPeople").value(10))
                .andExpect(jsonPath("$.data.content[0].currentPeople").value(4))
                .andExpect(jsonPath("$.data.content[0].viewCount").value(10))
                .andExpect(jsonPath("$.data.content[0].register").value("user"))
                .andExpect(jsonPath("$.data.content[0].bookMark").value(false))
                .andExpect(jsonPath("$.data.content[0].projectSimplePositionDtoList[0].projectNo").value(saveProjectPosition2.getProject().getNo()))
                .andExpect(jsonPath("$.data.content[0].projectSimplePositionDtoList[0].positionNo").value(saveProjectPosition2.getPosition().getNo()))
                .andExpect(jsonPath("$.data.content[0].projectSimplePositionDtoList[0].positionName").value(saveProjectPosition2.getPosition().getName()))
                .andExpect(jsonPath("$.data.content[0].projectSimpleTechnicalStackDtoList[0].projectNo").value(saveProjectTechnicalStack2.getProject().getNo()))
                .andExpect(jsonPath("$.data.content[0].projectSimpleTechnicalStackDtoList[0].image").value(saveImage1.getLogicalName()))
                .andExpect(jsonPath("$.data.content[0].projectSimpleTechnicalStackDtoList[0].technicalStackName").value(saveProjectTechnicalStack2.getTechnicalStack().getName()))

                .andExpect(jsonPath("$.data.content[1].name").value("testName0"))
                .andExpect(jsonPath("$.data.content[1].maxPeople").value(10))
                .andExpect(jsonPath("$.data.content[1].currentPeople").value(4))
                .andExpect(jsonPath("$.data.content[1].viewCount").value(10))
                .andExpect(jsonPath("$.data.content[1].register").value("user"))
                .andExpect(jsonPath("$.data.content[1].bookMark").value(true))
                .andExpect(jsonPath("$.data.content[1].projectSimplePositionDtoList[0].projectNo").value(saveProjectPosition1.getProject().getNo()))
                .andExpect(jsonPath("$.data.content[1].projectSimplePositionDtoList[0].positionNo").value(saveProjectPosition1.getPosition().getNo()))
                .andExpect(jsonPath("$.data.content[1].projectSimplePositionDtoList[0].positionName").value(saveProjectPosition1.getPosition().getName()))
                .andExpect(jsonPath("$.data.content[1].projectSimpleTechnicalStackDtoList[0].projectNo").value(saveProjectTechnicalStack1.getProject().getNo()))
                .andExpect(jsonPath("$.data.content[1].projectSimpleTechnicalStackDtoList[0].image").value(saveImage1.getLogicalName()))
                .andExpect(jsonPath("$.data.content[1].projectSimpleTechnicalStackDtoList[0].technicalStackName").value(saveProjectTechnicalStack1.getTechnicalStack().getName()))

                // paging
                .andExpect(jsonPath("$.data.pageable.pageSize").value(5))
                .andExpect(jsonPath("$.data.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.data.pageable.offset").value(0))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(status().isOk());
    }
}