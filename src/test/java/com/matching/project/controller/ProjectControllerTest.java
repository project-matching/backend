package com.matching.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.matching.project.config.SecurityConfig;
import com.matching.project.dto.enumerate.Position;
import com.matching.project.dto.project.ProjectPositionDto;
import com.matching.project.dto.project.ProjectRegisterRequestDto;
import com.matching.project.dto.project.ProjectRegisterResponseDto;
import com.matching.project.oauth.CustomOAuth2UserService;
import com.matching.project.service.ProjectService;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {
    @Autowired
    MockMvc mvc;

    @Test
    void 프로젝트_등록_테스트() throws Exception {
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
                        .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(content)))
                .andDo(print())
                .andExpect(header().string("Content-type", "application/json"))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.data.name").value("testName"))
                .andExpect(jsonPath("$.data.profile").isEmpty())
                .andExpect(jsonPath("$.data.createDate").value(createDate.toString()))
                .andExpect(jsonPath("$.data.startDate").value(startDate.toString()))
                .andExpect(jsonPath("$.data.endDate").value(endDate.toString()))
                .andExpect(jsonPath("$.data.state").value(true))
                .andExpect(jsonPath("$.data.introduction").value("testIntroduction"))
                .andExpect(jsonPath("$.data.maxPeople").value(10))
                .andExpect(jsonPath("$.data.projectPosition[0].position").value(Position.BACKEND.toString()))
                .andExpect(jsonPath("$.data.projectPosition[0].technicalStack[0]").value("SPRING"))
                .andExpect(jsonPath("$.data.projectPosition[0].technicalStack[1]").value("JAVA"))
                .andExpect(status().isOk());
    }
}