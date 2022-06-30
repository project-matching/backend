package com.matching.project.service;

import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Position;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.project.ProjectPositionDto;
import com.matching.project.dto.project.ProjectRegisterRequestDto;
import com.matching.project.dto.project.ProjectRegisterResponseDto;
import com.matching.project.dto.project.ProjectSimpleDto;
import com.matching.project.entity.*;
import com.matching.project.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {
    @Mock
    ProjectPositionRepository projectPositionRepository;

    @Mock
    ProjectTechnicalStackRepository projectTechnicalStackRepository;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    BookMarkRepository bookMarkRepository;

    @InjectMocks
    ProjectServiceImpl projectService;

    @Test
    public void 프로젝트_등록_성공_테스트() {
        // given

        // 객체 생성
        String testName = "testName";
        String testIntroduction = "testIntroduction";
        Integer testMaxPeople = 10;

        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        List<String> technicalStack = new ArrayList<>();
        technicalStack.add("SPRING");
        technicalStack.add("JAVA");
        ProjectPositionDto projectPositionDto = new ProjectPositionDto(Position.BACKEND, technicalStack);
        List<ProjectPositionDto> projectPositionDtoList = new ArrayList<>();
        projectPositionDtoList.add(projectPositionDto);

        ProjectRegisterRequestDto projectRegisterRequestDto = ProjectRegisterRequestDto.builder()
                .name(testName)
                .profile(null)
                .createDate(createDate)
                .startDate(startDate)
                .endDate(endDate)
                .introduction(testIntroduction)
                .maxPeople(testMaxPeople)
                .projectPosition(projectPositionDtoList)
                .build();

        Project project = Project.of(projectRegisterRequestDto);
        ProjectPosition projectPosition1 = ProjectPosition.of(projectRegisterRequestDto.getProjectPosition().get(0));
        ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.of(projectRegisterRequestDto.getProjectPosition().get(0).getTechnicalStack().get(0));
        ProjectTechnicalStack projectTechnicalStack2 = ProjectTechnicalStack.of(projectRegisterRequestDto.getProjectPosition().get(0).getTechnicalStack().get(1));

        projectPosition1.getProjectTechnicalStack().add(projectTechnicalStack1);
        projectPosition1.getProjectTechnicalStack().add(projectTechnicalStack2);
        project.getProjectPosition().add(projectPosition1);

        given(projectRepository.save(any(Project.class))).willReturn(project);
        given(projectPositionRepository.save(any(ProjectPosition.class))).willReturn(projectPosition1);
        given(projectTechnicalStackRepository.save(any(ProjectTechnicalStack.class))).willReturn(projectTechnicalStack1);

        ProjectRegisterResponseDto projectRegisterResponseDto = null;

        //when
        try {
            projectRegisterResponseDto = projectService.projectRegister(projectRegisterRequestDto);
        } catch (Exception e) {

        }

        // then
        verify(projectRepository).save(any());
        verify(projectPositionRepository).save(any());
        verify(projectTechnicalStackRepository, times(2)).save(any());

        assertEquals(projectRegisterResponseDto.getName(), testName);
        assertEquals(projectRegisterResponseDto.getCreateDate(), createDate);
        assertEquals(projectRegisterResponseDto.getStartDate(), startDate);
        assertEquals(projectRegisterResponseDto.getEndDate(), endDate);
        assertEquals(projectRegisterResponseDto.isState(), true);
        assertEquals(projectRegisterResponseDto.getIntroduction(), testIntroduction);
        assertEquals(projectRegisterResponseDto.getMaxPeople(), testMaxPeople);
        assertEquals(projectRegisterResponseDto.getProjectPosition().get(0).getPosition(), Position.BACKEND);
        assertEquals(projectRegisterResponseDto.getProjectPosition().get(0).getTechnicalStack().get(0), "SPRING");
        assertEquals(projectRegisterResponseDto.getProjectPosition().get(0).getTechnicalStack().get(1), "JAVA");
    }

    @Test
    public void 프로젝트_모집중_목록_조회_성공_테스트() {
        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);
        
        // 유저 객체
        User user = User.builder()
                .no(1L)
                .name("testName")
                .sex('M')
                .email("testEmail")
                .github("testGithub")
                .selfIntroduction("testSelfIntroduction")
                .block(false)
                .blockReason(null)
                .oauthCategory(OAuth.NORMAL)
                .permission(Role.ROLE_USER)
                .image(null)
                .userPosition(null)
                .build();
        
        // 프로젝트 객체
        Project project1 = Project.builder()
                .no(1L)
                .name("testName1")
                .createUserName("user1")
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

        Project project2 = Project.builder()
                .no(2L)
                .name("testName2")
                .createUserName("user1")
                .createDate(createDate)
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
                .image(null)
                .build();

        List<Project> projectList = new ArrayList<>();
        projectList.add(project2);
        projectList.add(project1);


        // List to Page
        Pageable pageable = PageRequest.of(0, 4, Sort.by("no").descending());
        int start = (int)pageable.getOffset();
        int end = (start + pageable.getPageSize()) > projectList.size() ? projectList.size() : (start + pageable.getPageSize());
        Page<Project> projectPage = new PageImpl<>(projectList.subList(start, end), pageable, projectList.size());

        List<BookMark> bookMarkList = new ArrayList<>();

        BookMark bookMark = BookMark.builder()
                .no(1L)
                .user(user)
                .project(project1)
                .build();
        bookMarkList.add(bookMark);

        given(bookMarkRepository.findByUserNo(any(Long.class))).willReturn(bookMarkList);
        given(projectRepository.findByStateProjectPage(any(Boolean.class), any(Boolean.class), any(Pageable.class))).willReturn(projectPage);

        List<ProjectSimpleDto> projectSimpleDtoList = null;

        try {
            projectSimpleDtoList = projectService.projectRecruitingList(pageable);
        } catch (Exception e) {

        }

        verify(bookMarkRepository, times(1)).findByUserNo(any(Long.class));
        verify(projectRepository, times(1)).findByStateProjectPage(any(Boolean.class), any(Boolean.class), any(Pageable.class));

        assertEquals(projectSimpleDtoList.size(), 2);
        assertEquals(projectSimpleDtoList.get(0).getNo(), 2L);
        assertEquals(projectSimpleDtoList.get(0).getName(), "testName2");
        assertEquals(projectSimpleDtoList.get(0).getProfile(), null);
        assertEquals(projectSimpleDtoList.get(0).isBookmark(), false);
        assertEquals(projectSimpleDtoList.get(0).getMaxPeople(), 10);
        assertEquals(projectSimpleDtoList.get(0).getCurrentPeople(), 4);
        assertEquals(projectSimpleDtoList.get(0).getViewCount(), 10);
        assertEquals(projectSimpleDtoList.get(0).getCommentCount(), 10);
        assertEquals(projectSimpleDtoList.get(0).getRegister(), "user1");

        assertEquals(projectSimpleDtoList.get(1).getNo(), 1L);
        assertEquals(projectSimpleDtoList.get(1).getName(), "testName1");
        assertEquals(projectSimpleDtoList.get(1).getProfile(), null);
        assertEquals(projectSimpleDtoList.get(1).isBookmark(), true);
        assertEquals(projectSimpleDtoList.get(1).getMaxPeople(), 10);
        assertEquals(projectSimpleDtoList.get(1).getCurrentPeople(), 4);
        assertEquals(projectSimpleDtoList.get(1).getViewCount(), 10);
        assertEquals(projectSimpleDtoList.get(1).getCommentCount(), 10);
        assertEquals(projectSimpleDtoList.get(1).getRegister(), "user1");
    }
}