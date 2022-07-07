package com.matching.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.matching.project.config.SecurityConfig;
import com.matching.project.dto.common.TokenDto;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Position;
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
    ProjectUserRepository projectUserRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    BookMarkRepository bookMarkRepository;

    // 프로젝트, 유저 저장
    void saveProject(){
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
                .image(null)
                .userPosition(null)
                .build();

        userRepository.save(user1);

        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        // 0 ~ 2까지 모집중이고 삭제 안당한 프로젝트
        // 3 ~ 5번쨰 프로젝트는 모집완료된 프로젝트
        // 6번째 프로젝트는 모집완료 되었고 삭제된 프로젝트
        // 7번째 프로젝트는 모집중이고 삭제된 프로젝트
        for (int i = 0 ; i < 10 ; i++) {

            System.out.println("index : " + i);
            boolean state = true;
            boolean delete = false;
            String deleteReason = null;
            if (i > 3 && i < 7) {
                state = false;
            }
            if (i > 5) {
                delete = true;
                deleteReason = "testDeleteReason" + i;
            }
            Project project = Project.builder()
                    .name("testName" + i)
                    .createUserName("user")
                    .createDate(createDate)
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
                    .image(null)
                    .build();
            projectRepository.save(project);
        }
    }

    @Test
    void 프로젝트_등록_테스트() throws Exception {
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
                .image(null)
                .userPosition(null)
                .build();

        userRepository.save(user1);
        String token = jwtTokenService.createToken(new TokenDto(1L, "wkemrm12@naver.com"));

        // given

        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        List<String> technicalStack = new ArrayList<>();
        technicalStack.add("SPRING");
        technicalStack.add("JAVA");
        ProjectPositionDto projectPositionDto = new ProjectPositionDto(Position.BACKEND, technicalStack);
        List<ProjectPositionDto> projectPositionDtoList = new ArrayList<>();
        projectPositionDtoList.add(projectPositionDto);

        ProjectRegisterRequestDto content = ProjectRegisterRequestDto.builder()
                .name("testName")
                .profile(null)
                .createDate(createDate)
                .startDate(startDate)
                .endDate(endDate)
                .introduction("testIntroduction")
                .maxPeople(10)
                .projectPosition(projectPositionDtoList)
                .build();

        // then
        mvc.perform(post("/v1/project").contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(content)))
                .andDo(print())
                .andExpect(header().string("Content-type", "application/json"))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.data.name").value("testName"))
                .andExpect(jsonPath("$.data.createUser").value("userName1"))
                .andExpect(jsonPath("$.data.profile").isEmpty())
                .andExpect(jsonPath("$.data.createDate").value(createDate.toString()))
                .andExpect(jsonPath("$.data.startDate").value(startDate.toString()))
                .andExpect(jsonPath("$.data.endDate").value(endDate.toString()))
                .andExpect(jsonPath("$.data.state").value(true))
                .andExpect(jsonPath("$.data.introduction").value("testIntroduction"))
                .andExpect(jsonPath("$.data.maxPeople").value(10))
                .andExpect(jsonPath("$.data.currentPeople").value(1))
                .andExpect(jsonPath("$.data.projectPosition[0].position").value(Position.BACKEND.toString()))
                .andExpect(jsonPath("$.data.projectPosition[0].technicalStack[0]").value("SPRING"))
                .andExpect(jsonPath("$.data.projectPosition[0].technicalStack[1]").value("JAVA"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void 비로그인_모집중_프로젝트_조회_테스트() throws Exception {
        saveProject();

        mvc.perform(get("/v1/project/recruitment").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(header().string("Content-type", "application/json"))
                .andExpect(jsonPath("$.data[0].name").value("testName3"))
                .andExpect(jsonPath("$.data[0].profile").isEmpty())
                .andExpect(jsonPath("$.data[0].maxPeople").value(10))
                .andExpect(jsonPath("$.data[0].currentPeople").value(4))
                .andExpect(jsonPath("$.data[0].viewCount").value(10))
                .andExpect(jsonPath("$.data[0].commentCount").value(10))
                .andExpect(jsonPath("$.data[0].register").value("user"))

                .andExpect(jsonPath("$.data[1].name").value("testName2"))
                .andExpect(jsonPath("$.data[1].profile").isEmpty())
                .andExpect(jsonPath("$.data[1].maxPeople").value(10))
                .andExpect(jsonPath("$.data[1].currentPeople").value(4))
                .andExpect(jsonPath("$.data[1].viewCount").value(10))
                .andExpect(jsonPath("$.data[1].commentCount").value(10))
                .andExpect(jsonPath("$.data[1].register").value("user"))

                .andExpect(jsonPath("$.data[2].name").value("testName1"))
                .andExpect(jsonPath("$.data[2].profile").isEmpty())
                .andExpect(jsonPath("$.data[2].maxPeople").value(10))
                .andExpect(jsonPath("$.data[2].currentPeople").value(4))
                .andExpect(jsonPath("$.data[2].viewCount").value(10))
                .andExpect(jsonPath("$.data[2].commentCount").value(10))
                .andExpect(jsonPath("$.data[2].register").value("user"))

                .andExpect(jsonPath("$.data[3].name").value("testName0"))
                .andExpect(jsonPath("$.data[3].profile").isEmpty())
                .andExpect(jsonPath("$.data[3].maxPeople").value(10))
                .andExpect(jsonPath("$.data[3].currentPeople").value(4))
                .andExpect(jsonPath("$.data[3].viewCount").value(10))
                .andExpect(jsonPath("$.data[3].commentCount").value(10))
                .andExpect(jsonPath("$.data[3].register").value("user"))
                .andExpect(status().isOk());
    }

    @Test
    public void 로그인_모집중_프로젝트_조회_테스트() throws Exception {
        saveProject();
        String token = jwtTokenService.createToken(new TokenDto(1L, "wkemrm12@naver.com"));

        mvc.perform(get("/v1/project/login/recruitment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(header().string("Content-type", "application/json"))
                .andExpect(jsonPath("$.data[0].name").value("testName3"))
                .andExpect(jsonPath("$.data[0].profile").isEmpty())
                .andExpect(jsonPath("$.data[0].maxPeople").value(10))
                .andExpect(jsonPath("$.data[0].currentPeople").value(4))
                .andExpect(jsonPath("$.data[0].bookMark").value(false))
                .andExpect(jsonPath("$.data[0].viewCount").value(10))
                .andExpect(jsonPath("$.data[0].commentCount").value(10))
                .andExpect(jsonPath("$.data[0].register").value("user"))

                .andExpect(jsonPath("$.data[1].name").value("testName2"))
                .andExpect(jsonPath("$.data[1].profile").isEmpty())
                .andExpect(jsonPath("$.data[1].maxPeople").value(10))
                .andExpect(jsonPath("$.data[1].currentPeople").value(4))
                .andExpect(jsonPath("$.data[1].bookMark").value(false))
                .andExpect(jsonPath("$.data[1].viewCount").value(10))
                .andExpect(jsonPath("$.data[1].commentCount").value(10))
                .andExpect(jsonPath("$.data[1].register").value("user"))

                .andExpect(jsonPath("$.data[2].name").value("testName1"))
                .andExpect(jsonPath("$.data[2].profile").isEmpty())
                .andExpect(jsonPath("$.data[2].maxPeople").value(10))
                .andExpect(jsonPath("$.data[2].currentPeople").value(4))
                .andExpect(jsonPath("$.data[2].bookMark").value(false))
                .andExpect(jsonPath("$.data[2].viewCount").value(10))
                .andExpect(jsonPath("$.data[2].commentCount").value(10))
                .andExpect(jsonPath("$.data[2].register").value("user"))

                .andExpect(jsonPath("$.data[3].name").value("testName0"))
                .andExpect(jsonPath("$.data[3].profile").isEmpty())
                .andExpect(jsonPath("$.data[3].maxPeople").value(10))
                .andExpect(jsonPath("$.data[3].currentPeople").value(4))
                .andExpect(jsonPath("$.data[3].bookMark").value(false))
                .andExpect(jsonPath("$.data[3].viewCount").value(10))
                .andExpect(jsonPath("$.data[3].commentCount").value(10))
                .andExpect(jsonPath("$.data[3].register").value("user"))
                .andExpect(status().isOk());
    }

    @Test
    public void 비로그인_상세_프로젝트_조회_테스트() throws Exception {
        // given
        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        // 프로젝트에 참가중인 유저
        User user1 = User.builder()
                .name("testUserName1")
                .sex('M')
                .email("testEmail1")
                .password("testPassword")
                .github("testGithub")
                .selfIntroduction("testSelfIntroduction")
                .block(false)
                .blockReason(null)
                .oauthCategory(OAuth.NORMAL)
                .permission(Role.ROLE_USER)
                .image(null)
                .userPosition(null)
                .build();

        User user2 = User.builder()
                .name("testUserName2")
                .sex('M')
                .email("testEmail2")
                .password("testPassword")
                .github("testGithub")
                .selfIntroduction("testSelfIntroduction")
                .block(false)
                .blockReason(null)
                .oauthCategory(OAuth.NORMAL)
                .permission(Role.ROLE_USER)
                .image(null)
                .userPosition(null)
                .build();
        userRepository.save(user1);
        userRepository.save(user2);

        // 프로젝트
        Project project1 = Project.builder()
                .name("testProjectName1")
                .createUserName("testCreateUserName1")
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
                .image(null)
                .build();
        Project projectSave1 = projectRepository.save(project1);

        // 프로젝트에서 구하고있는 포지션
        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .position(Position.FRONTEND)
                .state(false)
                .build();
        projectPosition1.setProject(project1);
        ProjectPosition projectPositionSave1 = projectPositionRepository.save(projectPosition1);

        ProjectPosition projectPosition2 = ProjectPosition.builder()
                .position(Position.BACKEND)
                .state(false)
                .build();
        projectPosition2.setProject(project1);
        ProjectPosition projectPositionSave2 = projectPositionRepository.save(projectPosition2);

        ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
                .name("javaScript")
                .build();
        projectTechnicalStack1.setProjectPosition(projectPosition1);
        ProjectTechnicalStack projectTechnicalStackSave1 = projectTechnicalStackRepository.save(projectTechnicalStack1);

        ProjectTechnicalStack projectTechnicalStack2 = ProjectTechnicalStack.builder()
                .name("java")
                .build();
        projectTechnicalStack2.setProjectPosition(projectPosition2);
        ProjectTechnicalStack projectTechnicalStackSave2 = projectTechnicalStackRepository.save(projectTechnicalStack2);

        ProjectUser projectUser1 = ProjectUser.builder()
                .projectPosition(Position.FRONTEND)
                .user(user1)
                .creator(true)
                .project(project1).build();
        ProjectUser projectUser2 = ProjectUser.builder()
                .projectPosition(Position.BACKEND)
                .user(user2)
                .creator(false)
                .project(project1).build();

        ProjectUser projectUserSave1 = projectUserRepository.save(projectUser1);
        ProjectUser projectUserSave2 = projectUserRepository.save(projectUser2);

        // 프로젝트 댓글
        Comment comment1 = Comment.builder()
                .user(user1)
                .project(project1)
                .content("testComment1")
                .build();

        Comment comment2 = Comment.builder()
                .user(user1)
                .project(project1)
                .content("testComment2")
                .build();

        Comment commentSave1 = commentRepository.save(comment1);
        Comment commentSave2 = commentRepository.save(comment2);
        
        mvc.perform(get("/v1/project/" + projectSave1.getNo())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.data.name").value(project1.getName()))
                .andExpect(jsonPath("$.data.profile").isEmpty())
                .andExpect(jsonPath("$.data.createDate").value(projectSave1.getCreateDate().toString()))
                .andExpect(jsonPath("$.data.startDate").value(projectSave1.getStartDate().toString()))
                .andExpect(jsonPath("$.data.endDate").value(projectSave1.getEndDate().toString()))
                .andExpect(jsonPath("$.data.state").value(projectSave1.isState()))
                .andExpect(jsonPath("$.data.introduction").value(projectSave1.getIntroduction()))
                .andExpect(jsonPath("$.data.maxPeople").value(projectSave1.getMaxPeople()))
                .andExpect(jsonPath("$.data.bookmark").value(false))
                .andExpect(jsonPath("$.data.register").value(projectSave1.getCreateUserName()))

                .andExpect(jsonPath("$.data.userSimpleInfoDtoList[0].no").value(projectUserSave1.getUser().getNo()))
                .andExpect(jsonPath("$.data.userSimpleInfoDtoList[0].profile").isEmpty())
                .andExpect(jsonPath("$.data.userSimpleInfoDtoList[0].projectPosition").value(projectUserSave1.getProjectPosition().toString()))
                .andExpect(jsonPath("$.data.userSimpleInfoDtoList[0].creator").value(projectUserSave1.isCreator()))

                .andExpect(jsonPath("$.data.userSimpleInfoDtoList[1].no").value(projectUserSave2.getUser().getNo()))
                .andExpect(jsonPath("$.data.userSimpleInfoDtoList[1].profile").isEmpty())
                .andExpect(jsonPath("$.data.userSimpleInfoDtoList[1].projectPosition").value(projectUserSave2.getProjectPosition().toString()))
                .andExpect(jsonPath("$.data.userSimpleInfoDtoList[1].creator").value(projectUserSave2.isCreator()))

                .andExpect(jsonPath("$.data.projectPosition[0].position").value(projectPositionSave1.getPosition().toString()))
                .andExpect(jsonPath("$.data.projectPosition[0].technicalStack[0]").value(projectTechnicalStackSave1.getName()))

                .andExpect(jsonPath("$.data.projectPosition[1].position").value(projectPositionSave2.getPosition().toString()))
                .andExpect(jsonPath("$.data.projectPosition[1].technicalStack[0]").value(projectTechnicalStackSave2.getName()))


                .andExpect(jsonPath("$.data.commentDtoList[0].no").value(commentSave1.getNo()))
                .andExpect(jsonPath("$.data.commentDtoList[0].registrant").value(commentSave1.getUser().getName()))
                .andExpect(jsonPath("$.data.commentDtoList[0].content").value(commentSave1.getContent()))

                .andExpect(jsonPath("$.data.commentDtoList[1].no").value(commentSave2.getNo()))
                .andExpect(jsonPath("$.data.commentDtoList[1].registrant").value(commentSave2.getUser().getName()))
                .andExpect(jsonPath("$.data.commentDtoList[1].content").value(commentSave2.getContent()))

                .andExpect(status().isOk());
    }

    @Test
    public void 로그인_상세_프로젝트_조회_테스트() throws Exception {
        // given
        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        // 프로젝트에 참가중인 유저
        User user1 = User.builder()
                .name("testUserName1")
                .sex('M')
                .email("wkemrm12@naver.com")
                .password("testPassword")
                .github("testGithub")
                .selfIntroduction("testSelfIntroduction")
                .block(false)
                .blockReason(null)
                .oauthCategory(OAuth.NORMAL)
                .permission(Role.ROLE_USER)
                .image(null)
                .userPosition(null)
                .build();

        User user2 = User.builder()
                .name("testUserName2")
                .sex('M')
                .email("testEmail2")
                .password("testPassword")
                .github("testGithub")
                .selfIntroduction("testSelfIntroduction")
                .block(false)
                .blockReason(null)
                .oauthCategory(OAuth.NORMAL)
                .permission(Role.ROLE_USER)
                .image(null)
                .userPosition(null)
                .build();
        userRepository.save(user1);
        userRepository.save(user2);

        // 프로젝트
        Project project1 = Project.builder()
                .name("testProjectName1")
                .createUserName("testCreateUserName1")
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
                .image(null)
                .build();
        Project projectSave1 = projectRepository.save(project1);

        // 프로젝트에서 구하고있는 포지션
        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .position(Position.FRONTEND)
                .state(false)
                .build();
        projectPosition1.setProject(project1);
        ProjectPosition projectPositionSave1 = projectPositionRepository.save(projectPosition1);

        ProjectPosition projectPosition2 = ProjectPosition.builder()
                .position(Position.BACKEND)
                .state(false)
                .build();
        projectPosition2.setProject(project1);
        ProjectPosition projectPositionSave2 = projectPositionRepository.save(projectPosition2);

        ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
                .name("javaScript")
                .build();
        projectTechnicalStack1.setProjectPosition(projectPosition1);
        ProjectTechnicalStack projectTechnicalStackSave1 = projectTechnicalStackRepository.save(projectTechnicalStack1);

        ProjectTechnicalStack projectTechnicalStack2 = ProjectTechnicalStack.builder()
                .name("java")
                .build();
        projectTechnicalStack2.setProjectPosition(projectPosition2);
        ProjectTechnicalStack projectTechnicalStackSave2 = projectTechnicalStackRepository.save(projectTechnicalStack2);

        ProjectUser projectUser1 = ProjectUser.builder()
                .projectPosition(Position.FRONTEND)
                .user(user1)
                .creator(true)
                .project(project1).build();
        ProjectUser projectUser2 = ProjectUser.builder()
                .projectPosition(Position.BACKEND)
                .user(user2)
                .creator(false)
                .project(project1).build();

        ProjectUser projectUserSave1 = projectUserRepository.save(projectUser1);
        ProjectUser projectUserSave2 = projectUserRepository.save(projectUser2);

        // 프로젝트 댓글
        Comment comment1 = Comment.builder()
                .user(user1)
                .project(project1)
                .content("testComment1")
                .build();

        Comment comment2 = Comment.builder()
                .user(user1)
                .project(project1)
                .content("testComment2")
                .build();

        Comment commentSave1 = commentRepository.save(comment1);
        Comment commentSave2 = commentRepository.save(comment2);

        BookMark bookMark1 = BookMark.builder()
                .user(user1)
                .project(project1)
                .build();
        bookMarkRepository.save(bookMark1);

        String token = jwtTokenService.createToken(new TokenDto(1L, "wkemrm12@naver.com"));
        mvc.perform(get("/v1/project/" + projectSave1.getNo())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(jsonPath("$.data.name").value(project1.getName()))
                .andExpect(jsonPath("$.data.profile").isEmpty())
                .andExpect(jsonPath("$.data.createDate").value(projectSave1.getCreateDate().toString()))
                .andExpect(jsonPath("$.data.startDate").value(projectSave1.getStartDate().toString()))
                .andExpect(jsonPath("$.data.endDate").value(projectSave1.getEndDate().toString()))
                .andExpect(jsonPath("$.data.state").value(projectSave1.isState()))
                .andExpect(jsonPath("$.data.introduction").value(projectSave1.getIntroduction()))
                .andExpect(jsonPath("$.data.maxPeople").value(projectSave1.getMaxPeople()))
                .andExpect(jsonPath("$.data.bookmark").value(true))
                .andExpect(jsonPath("$.data.register").value(projectSave1.getCreateUserName()))

                .andExpect(jsonPath("$.data.userSimpleInfoDtoList[0].no").value(projectUserSave1.getUser().getNo()))
                .andExpect(jsonPath("$.data.userSimpleInfoDtoList[0].profile").isEmpty())
                .andExpect(jsonPath("$.data.userSimpleInfoDtoList[0].projectPosition").value(projectUserSave1.getProjectPosition().toString()))
                .andExpect(jsonPath("$.data.userSimpleInfoDtoList[0].creator").value(projectUserSave1.isCreator()))

                .andExpect(jsonPath("$.data.userSimpleInfoDtoList[1].no").value(projectUserSave2.getUser().getNo()))
                .andExpect(jsonPath("$.data.userSimpleInfoDtoList[1].profile").isEmpty())
                .andExpect(jsonPath("$.data.userSimpleInfoDtoList[1].projectPosition").value(projectUserSave2.getProjectPosition().toString()))
                .andExpect(jsonPath("$.data.userSimpleInfoDtoList[1].creator").value(projectUserSave2.isCreator()))

                .andExpect(jsonPath("$.data.projectPosition[0].position").value(projectPositionSave1.getPosition().toString()))
                .andExpect(jsonPath("$.data.projectPosition[0].technicalStack[0]").value(projectTechnicalStackSave1.getName()))

                .andExpect(jsonPath("$.data.projectPosition[1].position").value(projectPositionSave2.getPosition().toString()))
                .andExpect(jsonPath("$.data.projectPosition[1].technicalStack[0]").value(projectTechnicalStackSave2.getName()))


                .andExpect(jsonPath("$.data.commentDtoList[0].no").value(commentSave1.getNo()))
                .andExpect(jsonPath("$.data.commentDtoList[0].registrant").value(commentSave1.getUser().getName()))
                .andExpect(jsonPath("$.data.commentDtoList[0].content").value(commentSave1.getContent()))

                .andExpect(jsonPath("$.data.commentDtoList[1].no").value(commentSave2.getNo()))
                .andExpect(jsonPath("$.data.commentDtoList[1].registrant").value(commentSave2.getUser().getName()))
                .andExpect(jsonPath("$.data.commentDtoList[1].content").value(commentSave2.getContent()))

                .andExpect(status().isOk());
    }
}