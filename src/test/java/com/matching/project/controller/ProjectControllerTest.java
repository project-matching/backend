package com.matching.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.matching.project.config.SecurityConfig;
import com.matching.project.dto.common.TokenDto;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.project.ProjectPositionDto;
import com.matching.project.dto.project.ProjectRegisterRequestDto;
import com.matching.project.dto.project.ProjectRegisterResponseDto;
import com.matching.project.entity.*;
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
import org.springframework.transaction.annotation.Transactional;

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
    TechnicalStackRepository technicalStackRepository;

    @Autowired
    BookMarkRepository bookMarkRepository;

    // 프로젝트, 유저 저장
    User saveUser() {
        User user1 = User.builder()
                .name("userName1")
                .sex('M')
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

        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
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

        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
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

    BookMark saveBookMark(User user, Project project) {
        BookMark bookMark = BookMark.builder()
                .user(user)
                .project(project)
                .build();
        return bookMarkRepository.save(bookMark);
    }

    @Test
    void 프로젝트_등록_테스트() throws Exception {
        // given
        User user1 = User.builder()
                .name("userName1")
                .sex('M')
                .email("wkemrm12@naver.com")
                .password("testPassword")
                .github("testGithub")
                .selfIntroduction("testSelfIntroduction")
                .block(false)
                .blockReason(null)
                .oauthCategory(OAuth.NORMAL)
                .permission(Role.ROLE_USER)
                .build();

        userRepository.save(user1);
        String token = jwtTokenService.createToken(new TokenDto(1L, "wkemrm12@naver.com"));

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


        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        List<ProjectPositionDto> projectPositionDtoList = new ArrayList<>();
        ProjectPositionDto projectPositionDto1 = ProjectPositionDto.builder()
                .name("testPosition1")
                .state(true)
                .build();
        ProjectPositionDto projectPositionDto2 = ProjectPositionDto.builder()
                .name("testPosition2")
                .state(false)
                .build();
        projectPositionDtoList.add(projectPositionDto1);
        projectPositionDtoList.add(projectPositionDto2);

        List<String> projectTechnicalStack = new ArrayList<>();
        projectTechnicalStack.add("testTechnicalStack1");
        projectTechnicalStack.add("testTechnicalStack2");

        ProjectRegisterRequestDto projectRegisterRequestDto = ProjectRegisterRequestDto.builder()
                .name("testName")
                .profile(null)
                .createDate(createDate)
                .startDate(startDate)
                .endDate(endDate)
                .introduction("testIntroduction1")
                .maxPeople(10)
                .build();

        projectRegisterRequestDto.setProjectPositionDtoList(projectPositionDtoList);
        projectRegisterRequestDto.setProjectTechnicalStack(projectTechnicalStack);

        // then
        mvc.perform(post("/v1/project").contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(projectRegisterRequestDto)))
                .andDo(print())
                .andExpect(header().string("Content-type", "application/json"))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.data.name").value(projectRegisterRequestDto.getName()))
                .andExpect(jsonPath("$.data.createUser").value(user1.getName()))
                .andExpect(jsonPath("$.data.profile").isEmpty())
                .andExpect(jsonPath("$.data.createDate").value(projectRegisterRequestDto.getCreateDate().toString()))
                .andExpect(jsonPath("$.data.startDate").value(projectRegisterRequestDto.getStartDate().toString()))
                .andExpect(jsonPath("$.data.endDate").value(projectRegisterRequestDto.getEndDate().toString()))
                .andExpect(jsonPath("$.data.state").value(true))
                .andExpect(jsonPath("$.data.introduction").value(projectRegisterRequestDto.getIntroduction()))
                .andExpect(jsonPath("$.data.maxPeople").value(projectRegisterRequestDto.getMaxPeople()))
                .andExpect(jsonPath("$.data.currentPeople").value(1))
                .andExpect(jsonPath("$.data.viewCount").value(0))
                .andExpect(jsonPath("$.data.commentCount").value(0))

                .andExpect(jsonPath("$.data.projectPositionDtoList[0].name").value(projectPositionDto1.getName()))
                .andExpect(jsonPath("$.data.projectPositionDtoList[0].state").value(projectPositionDto1.isState()))
                .andExpect(jsonPath("$.data.projectPositionDtoList[1].name").value(projectPositionDto2.getName()))
                .andExpect(jsonPath("$.data.projectPositionDtoList[1].state").value(projectPositionDto2.isState()))

                .andExpect(jsonPath("$.data.projectTechnicalStack[0]").value(technicalStack1.getName()))
                .andExpect(jsonPath("$.data.projectTechnicalStack[1]").value(technicalStack2.getName()))
                .andExpect(status().isOk());

        List<Project> projectList = projectRepository.findAll();
        List<ProjectPosition> projectPositionList = projectPositionRepository.findAll();
        List<ProjectTechnicalStack> projectTechnicalStackList = projectTechnicalStackRepository.findAll();
        assertEquals(projectList.size(), 1);
        assertEquals(projectPositionList.size(), projectPositionDtoList.size());
        assertEquals(projectTechnicalStackList.size(), projectTechnicalStack.size());
    }
    
    @Test
    public void 비로그인_모집중_프로젝트_조회_테스트() throws Exception {
        User saveUser = saveUser();
        List<Project> saveRecruitmentProject = saveRecruitmentProject();
        saveRecruitmentCompleteProject();
        // 0번째 1번째는 북마크
        saveBookMark(saveUser, saveRecruitmentProject.get(0));
        saveBookMark(saveUser, saveRecruitmentProject.get(1));

        mvc.perform(get("/v1/project/recruitment").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(header().string("Content-type", "application/json"))
                .andExpect(jsonPath("$.data[0].name").value("testName2"))
                .andExpect(jsonPath("$.data[0].profile").isEmpty())
                .andExpect(jsonPath("$.data[0].maxPeople").value(10))
                .andExpect(jsonPath("$.data[0].currentPeople").value(4))
                .andExpect(jsonPath("$.data[0].viewCount").value(10))
                .andExpect(jsonPath("$.data[0].commentCount").value(10))
                .andExpect(jsonPath("$.data[0].register").value("user"))
                .andExpect(jsonPath("$.data[0].bookMark").value(false))

                .andExpect(jsonPath("$.data[1].name").value("testName1"))
                .andExpect(jsonPath("$.data[1].profile").isEmpty())
                .andExpect(jsonPath("$.data[1].maxPeople").value(10))
                .andExpect(jsonPath("$.data[1].currentPeople").value(4))
                .andExpect(jsonPath("$.data[1].viewCount").value(10))
                .andExpect(jsonPath("$.data[1].commentCount").value(10))
                .andExpect(jsonPath("$.data[1].register").value("user"))
                .andExpect(jsonPath("$.data[1].bookMark").value(false))

                .andExpect(jsonPath("$.data[2].name").value("testName0"))
                .andExpect(jsonPath("$.data[2].profile").isEmpty())
                .andExpect(jsonPath("$.data[2].maxPeople").value(10))
                .andExpect(jsonPath("$.data[2].currentPeople").value(4))
                .andExpect(jsonPath("$.data[2].viewCount").value(10))
                .andExpect(jsonPath("$.data[2].commentCount").value(10))
                .andExpect(jsonPath("$.data[2].register").value("user"))
                .andExpect(jsonPath("$.data[2].bookMark").value(false))

                .andExpect(status().isOk());
    }

    @Test
    public void 로그인_모집중_프로젝트_조회_테스트() throws Exception {
        User saveUser = saveUser();
        List<Project> saveRecruitmentProject = saveRecruitmentProject();
        saveRecruitmentCompleteProject();
        // 0번째 1번째는 북마크
        saveBookMark(saveUser, saveRecruitmentProject.get(0));
        saveBookMark(saveUser, saveRecruitmentProject.get(1));

        String token = jwtTokenService.createToken(new TokenDto(1L, "wkemrm12@naver.com"));

        mvc.perform(get("/v1/project/recruitment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(header().string("Content-type", "application/json"))
                .andExpect(jsonPath("$.data[0].name").value("testName2"))
                .andExpect(jsonPath("$.data[0].profile").isEmpty())
                .andExpect(jsonPath("$.data[0].maxPeople").value(10))
                .andExpect(jsonPath("$.data[0].currentPeople").value(4))
                .andExpect(jsonPath("$.data[0].viewCount").value(10))
                .andExpect(jsonPath("$.data[0].commentCount").value(10))
                .andExpect(jsonPath("$.data[0].register").value("user"))
                .andExpect(jsonPath("$.data[0].bookMark").value(false))

                .andExpect(jsonPath("$.data[1].name").value("testName1"))
                .andExpect(jsonPath("$.data[1].profile").isEmpty())
                .andExpect(jsonPath("$.data[1].maxPeople").value(10))
                .andExpect(jsonPath("$.data[1].currentPeople").value(4))
                .andExpect(jsonPath("$.data[1].viewCount").value(10))
                .andExpect(jsonPath("$.data[1].commentCount").value(10))
                .andExpect(jsonPath("$.data[1].register").value("user"))
                .andExpect(jsonPath("$.data[1].bookMark").value(true))

                .andExpect(jsonPath("$.data[2].name").value("testName0"))
                .andExpect(jsonPath("$.data[2].profile").isEmpty())
                .andExpect(jsonPath("$.data[2].maxPeople").value(10))
                .andExpect(jsonPath("$.data[2].currentPeople").value(4))
                .andExpect(jsonPath("$.data[2].viewCount").value(10))
                .andExpect(jsonPath("$.data[2].commentCount").value(10))
                .andExpect(jsonPath("$.data[2].register").value("user"))
                .andExpect(jsonPath("$.data[2].bookMark").value(true))
                .andExpect(status().isOk());
    }

    @Test
    public void 비로그인_모집완료_프로젝트_조회_테스트() throws Exception {
        User saveUser = saveUser();
        saveRecruitmentProject();
        List<Project> saveRecruitmentCompleteProject = saveRecruitmentCompleteProject();
        // 4번째 5번째는 북마크
        saveBookMark(saveUser, saveRecruitmentCompleteProject.get(0));
        saveBookMark(saveUser, saveRecruitmentCompleteProject.get(1));

        mvc.perform(get("/v1/project/recruitment/complete").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(header().string("Content-type", "application/json"))
                .andExpect(jsonPath("$.data[0].name").value("testName6"))
                .andExpect(jsonPath("$.data[0].profile").isEmpty())
                .andExpect(jsonPath("$.data[0].maxPeople").value(10))
                .andExpect(jsonPath("$.data[0].currentPeople").value(4))
                .andExpect(jsonPath("$.data[0].viewCount").value(10))
                .andExpect(jsonPath("$.data[0].commentCount").value(10))
                .andExpect(jsonPath("$.data[0].register").value("user"))
                .andExpect(jsonPath("$.data[0].bookMark").value(false))

                .andExpect(jsonPath("$.data[1].name").value("testName5"))
                .andExpect(jsonPath("$.data[1].profile").isEmpty())
                .andExpect(jsonPath("$.data[1].maxPeople").value(10))
                .andExpect(jsonPath("$.data[1].currentPeople").value(4))
                .andExpect(jsonPath("$.data[1].viewCount").value(10))
                .andExpect(jsonPath("$.data[1].commentCount").value(10))
                .andExpect(jsonPath("$.data[1].register").value("user"))
                .andExpect(jsonPath("$.data[1].bookMark").value(false))

                .andExpect(jsonPath("$.data[2].name").value("testName4"))
                .andExpect(jsonPath("$.data[2].profile").isEmpty())
                .andExpect(jsonPath("$.data[2].maxPeople").value(10))
                .andExpect(jsonPath("$.data[2].currentPeople").value(4))
                .andExpect(jsonPath("$.data[2].viewCount").value(10))
                .andExpect(jsonPath("$.data[2].commentCount").value(10))
                .andExpect(jsonPath("$.data[2].register").value("user"))
                .andExpect(jsonPath("$.data[2].bookMark").value(false))

                .andExpect(status().isOk());
    }

    @Test
    public void 로그인_모집완료_프로젝트_조회_테스트() throws Exception {
        User saveUser = saveUser();
        saveRecruitmentProject();
        List<Project> saveRecruitmentCompleteProject = saveRecruitmentCompleteProject();
        // 4번째 5번째는 북마크
        saveBookMark(saveUser, saveRecruitmentCompleteProject.get(0));
        saveBookMark(saveUser, saveRecruitmentCompleteProject.get(1));
        String token = jwtTokenService.createToken(new TokenDto(1L, "wkemrm12@naver.com"));

        mvc.perform(get("/v1/project/recruitment/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(header().string("Content-type", "application/json"))
                .andExpect(jsonPath("$.data[0].name").value("testName6"))
                .andExpect(jsonPath("$.data[0].profile").isEmpty())
                .andExpect(jsonPath("$.data[0].maxPeople").value(10))
                .andExpect(jsonPath("$.data[0].currentPeople").value(4))
                .andExpect(jsonPath("$.data[0].viewCount").value(10))
                .andExpect(jsonPath("$.data[0].commentCount").value(10))
                .andExpect(jsonPath("$.data[0].register").value("user"))
                .andExpect(jsonPath("$.data[0].bookMark").value(false))

                .andExpect(jsonPath("$.data[1].name").value("testName5"))
                .andExpect(jsonPath("$.data[1].profile").isEmpty())
                .andExpect(jsonPath("$.data[1].maxPeople").value(10))
                .andExpect(jsonPath("$.data[1].currentPeople").value(4))
                .andExpect(jsonPath("$.data[1].viewCount").value(10))
                .andExpect(jsonPath("$.data[1].commentCount").value(10))
                .andExpect(jsonPath("$.data[1].register").value("user"))
                .andExpect(jsonPath("$.data[1].bookMark").value(true))

                .andExpect(jsonPath("$.data[2].name").value("testName4"))
                .andExpect(jsonPath("$.data[2].profile").isEmpty())
                .andExpect(jsonPath("$.data[2].maxPeople").value(10))
                .andExpect(jsonPath("$.data[2].currentPeople").value(4))
                .andExpect(jsonPath("$.data[2].viewCount").value(10))
                .andExpect(jsonPath("$.data[2].commentCount").value(10))
                .andExpect(jsonPath("$.data[2].register").value("user"))
                .andExpect(jsonPath("$.data[2].bookMark").value(true))
                .andExpect(status().isOk());
    }

    @Test
    public void 비로그인_상세_프로젝트_조회_테스트() throws Exception {
    }

    @Test
    public void 로그인_상세_프로젝트_조회_테스트() throws Exception {

    }
}