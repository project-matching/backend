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
import com.matching.project.entity.Project;
import com.matching.project.entity.User;
import com.matching.project.oauth.CustomOAuth2UserService;
import com.matching.project.repository.ProjectRepository;
import com.matching.project.repository.UserRepository;
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
    JwtTokenService jwtTokenService;

    @Autowired
    UserRepository userRepository;

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
}