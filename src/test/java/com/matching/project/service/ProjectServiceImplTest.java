package com.matching.project.service;

import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.project.*;
import com.matching.project.dto.projectposition.ProjectPositionAddDto;
import com.matching.project.dto.projectposition.ProjectPositionRegisterDto;
import com.matching.project.dto.user.ProjectRegisterUserDto;
import com.matching.project.entity.*;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import com.matching.project.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {
    @Mock
    ProjectPositionRepository projectPositionRepository;

    @Mock
    ProjectTechnicalStackRepository projectTechnicalStackRepository;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    PositionRepository positionRepository;

    @Mock
    TechnicalStackRepository technicalStackRepository;

    @Mock
    BookMarkRepository bookMarkRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    ProjectParticipateRequestRepository projectParticipateRequestRepository;

    @Mock
    ParticipateRequestTechnicalStackRepository participateRequestTechnicalStackRepository;

    @Mock
    EntityManager entityManager;

    @InjectMocks
    ProjectServiceImpl projectService;

    @Test
    public void 프로젝트_등록_폼_테스트() {
        // given
        Position position1 = new Position(1L, "testPosition1");
        Position position2 = new Position(2L, "testPosition2");
        List<Position> positionList = new ArrayList<>();
        positionList.add(position1);
        positionList.add(position2);

        TechnicalStack technicalStack1 = new TechnicalStack(1L, "testTechnicalStack1", null);
        TechnicalStack technicalStack2 = new TechnicalStack(2L, "testTechnicalStack2", null);
        List<TechnicalStack> technicalStackList = new ArrayList<>();
        technicalStackList.add(technicalStack1);
        technicalStackList.add(technicalStack2);

        given(positionRepository.findAll()).willReturn(positionList);
        given(technicalStackRepository.findAll()).willReturn(technicalStackList);

        // when
        ProjectRegisterFormResponseDto projectRegisterForm = null;
        try {
            projectRegisterForm = projectService.findProjectRegisterForm();
        } catch(Exception e) {
            e.printStackTrace();
        }

        verify(positionRepository, times(1)).findAll();
        verify(technicalStackRepository, times(1)).findAll();

        // then
        assertEquals(projectRegisterForm.getPositionRegisterFormDtoList().get(0).getPositionNo(), position1.getNo());
        assertEquals(projectRegisterForm.getPositionRegisterFormDtoList().get(0).getPositionName(), position1.getName());
        assertEquals(projectRegisterForm.getPositionRegisterFormDtoList().get(1).getPositionNo(), position2.getNo());
        assertEquals(projectRegisterForm.getPositionRegisterFormDtoList().get(1).getPositionName(), position2.getName());

        assertEquals(projectRegisterForm.getTechnicalStackRegisterFormDtoList().get(0).getTechnicalStackNo(), technicalStack1.getNo());
        assertEquals(projectRegisterForm.getTechnicalStackRegisterFormDtoList().get(0).getTechnicalStackName(), technicalStack1.getName());
        assertEquals(projectRegisterForm.getTechnicalStackRegisterFormDtoList().get(1).getTechnicalStackNo(), technicalStack2.getNo());
        assertEquals(projectRegisterForm.getTechnicalStackRegisterFormDtoList().get(1).getTechnicalStackName(), technicalStack2.getName());
    }

    @Test
    public void 프로젝트_등록_성공_테스트() {
        // given
        LocalDateTime createDate = LocalDateTime.now();
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        Project project1 = Project.builder()
                .no(1L)
                .name("testProject1")
                .createUserName("user1")
                .createDate(createDate)
                .startDate(startDate)
                .endDate(endDate)
                .state(true)
                .introduction("testIntroduction1")
                .maxPeople(10)
                .currentPeople(1)
                .delete(false)
                .deleteReason(null)
                .viewCount(0)
                .commentCount(0)
                .build();

        List<Position> positionList = new ArrayList<>();
        Position position1 = Position.builder()
                .no(1L)
                .name("testPosition1")
                .build();
        Position position2 = Position.builder()
                .no(2L)
                .name("testPosition2")
                .build();
        positionList.add(position1);
        positionList.add(position2);

        User user1 = User.builder()
                .no(1L)
                .name("testUser1")
                .sex("M")
                .email("testEmail1")
                .password("testPassword1")
                .github("testGithub1")
                .block(false)
                .blockReason(null)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(false)
                .imageNo(0L)
                .position(position1)
                .build();

        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .no(1L)
                .state(true)
                .project(project1)
                .position(position1)
                .user(user1)
                .creator(true)
                .build();

        ProjectPosition projectPosition2 = ProjectPosition.builder()
                .no(2L)
                .state(false)
                .project(project1)
                .position(position2)
                .user(null)
                .creator(false)
                .build();

        List<TechnicalStack> technicalStackList = new ArrayList<>();
        TechnicalStack technicalStack1 = TechnicalStack.builder()
                .no(1L)
                .name("testTechnicalStack1")
                .build();

        TechnicalStack technicalStack2 = TechnicalStack.builder()
                .no(2L)
                .name("testTechnicalStack2")
                .build();
        technicalStackList.add(technicalStack1);
        technicalStackList.add(technicalStack2);

        ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
                .no(1L)
                .technicalStack(technicalStack1)
                .project(project1)
                .build();

        ProjectTechnicalStack projectTechnicalStack2 = ProjectTechnicalStack.builder()
                .no(2L)
                .technicalStack(technicalStack2)
                .project(project1)
                .build();

        given(projectRepository.save(any())).willReturn(project1);
        given(positionRepository.findByNoIn(any())).willReturn(positionList);
        given(projectPositionRepository.save(any())).willReturn(projectPosition1).willReturn(projectPosition2);
        given(technicalStackRepository.findByNoIn(any())).willReturn(technicalStackList);
        given(projectTechnicalStackRepository.save(any())).willReturn(projectTechnicalStack1).willReturn(projectTechnicalStack2);

        // when
        UserDetails userDetails = user1;
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        List<ProjectPositionRegisterDto> projectPositionRegisterDtoList = new ArrayList<>();
        projectPositionRegisterDtoList.add(new ProjectPositionRegisterDto(1L, new ProjectRegisterUserDto(1L)));
        projectPositionRegisterDtoList.add(new ProjectPositionRegisterDto(2L, null));

        List<Long> projectTechnicalStackList = new ArrayList<>();
        projectTechnicalStackList.add(1L);
        projectTechnicalStackList.add(2L);

        ProjectRegisterRequestDto projectRegisterRequestDto = ProjectRegisterRequestDto.builder()
                .name("testProject1")
                .startDate(startDate)
                .endDate(endDate)
                .introduction("testIntroduction1")
                .projectPositionRegisterDtoList(projectPositionRegisterDtoList)
                .projectTechnicalStackList(projectTechnicalStackList)
                .build();

        Long projectNo = null;
        try {
            projectNo = projectService.projectRegister(projectRegisterRequestDto);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // then
        verify(projectRepository,times(1)).save(any());
        verify(positionRepository, times(1)).findByNoIn(any());
        verify(projectPositionRepository, times(2)).save(any());
        verify(technicalStackRepository, times(1)).findByNoIn(any());
        verify(projectTechnicalStackRepository, times(2)).save(any());

        assertEquals(projectNo, project1.getNo());
    }
//
    @Test
    public void 비로그인_프로젝트_목록_조회_성공_테스트() {
        // 프로젝트 객체
        List<ProjectSimpleDto> projectSimpleDtoList = new ArrayList<>();
        ProjectSimpleDto projectSimpleDto1 = ProjectSimpleDto.builder()
                .projectNo(1L)
                .name("testName1")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .register("user1")
                .bookMark(false)
                .build();
        
        // ProjectSimplePositionDto 객체
        ProjectSimplePositionDto positionName1 = ProjectSimplePositionDto.builder()
                .projectNo(1L)
                .positionNo(1L)
                .positionName("testPositionName1")
                .build();

        ProjectSimplePositionDto positionName2 = ProjectSimplePositionDto.builder()
                .projectNo(1L)
                .positionNo(2L)
                .positionName("testPositionName2")
                .build();
        List<ProjectSimplePositionDto> projectSimplePositionDtoList1 = new ArrayList<>();
        projectSimplePositionDtoList1.add(positionName1);
        projectSimplePositionDtoList1.add(positionName2);
        projectSimpleDto1.setProjectSimplePositionDtoList(projectSimplePositionDtoList1);
        
        // ProjectSimpleTechnicalStackDto 객체
        ProjectSimpleTechnicalStackDto technicalStackName1 = ProjectSimpleTechnicalStackDto.builder()
                .projectNo(1L)
                .image("testImage1")
                .technicalStackName("testTechnicalStackName1")
                .build();

        ProjectSimpleTechnicalStackDto technicalStackName2 = ProjectSimpleTechnicalStackDto.builder()
                .projectNo(1L)
                .image("testImage2")
                .technicalStackName("testTechnicalStackName2")
                .build();

        List<ProjectSimpleTechnicalStackDto> projectSimpleTechnicalStackDtoList1 = new ArrayList<>();
        projectSimpleTechnicalStackDtoList1.add(technicalStackName1);
        projectSimpleTechnicalStackDtoList1.add(technicalStackName2);
        projectSimpleDto1.setProjectSimpleTechnicalStackDtoList(projectSimpleTechnicalStackDtoList1);

        // projectSimpleDtoList 세팅
        projectSimpleDtoList.add(projectSimpleDto1);

        ProjectSimpleDto projectSimpleDto2 = ProjectSimpleDto.builder()
                .projectNo(2L)
                .name("testName2")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .register("user1")
                .bookMark(false)
                .build();
        
        // ProjectSimplePositionDto 객체
        ProjectSimplePositionDto positionName3 = ProjectSimplePositionDto.builder()
                .projectNo(2L)
                .positionNo(1L)
                .positionName("testPositionName3")
                .build();

        ProjectSimplePositionDto positionName4 = ProjectSimplePositionDto.builder()
                .projectNo(2L)
                .positionNo(2L)
                .positionName("testPositionName4")
                .build();
        List<ProjectSimplePositionDto> projectSimplePositionDtoList2 = new ArrayList<>();
        projectSimplePositionDtoList2.add(positionName3);
        projectSimplePositionDtoList2.add(positionName4);
        projectSimpleDto2.setProjectSimplePositionDtoList(projectSimplePositionDtoList2);
        
        // ProjectSimpleTechnicalStackDto 객체
        ProjectSimpleTechnicalStackDto technicalStackName3 = ProjectSimpleTechnicalStackDto.builder()
                .projectNo(2L)
                .image("testImage3")
                .technicalStackName("testTechnicalStackName3")
                .build();

        ProjectSimpleTechnicalStackDto technicalStackName4 = ProjectSimpleTechnicalStackDto.builder()
                .projectNo(2L)
                .image("testImage4")
                .technicalStackName("testTechnicalStackName4")
                .build();

        List<ProjectSimpleTechnicalStackDto> projectSimpleTechnicalStackDtoList2 = new ArrayList<>();
        projectSimpleTechnicalStackDtoList2.add(technicalStackName3);
        projectSimpleTechnicalStackDtoList2.add(technicalStackName4);
        projectSimpleDto2.setProjectSimpleTechnicalStackDtoList(projectSimpleTechnicalStackDtoList2);

        // projectSimpleDtoList 세팅
        projectSimpleDtoList.add(projectSimpleDto2);


        // List to Page
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createDate").descending());
        int start = (int)pageable.getOffset();
        int end = (start + pageable.getPageSize()) > projectSimpleDtoList.size() ? projectSimpleDtoList.size() : (start + pageable.getPageSize());
        Page<ProjectSimpleDto> projectPage = new PageImpl<>(projectSimpleDtoList.subList(start, end), pageable, projectSimpleDtoList.size());

        given(projectRepository.findProjectByStatusAndDelete(any(Pageable.class), any(Boolean.class), any(Boolean.class), any())).willReturn(projectPage);


        List<ProjectSimpleDto> result = null;

        Authentication auth = new AnonymousAuthenticationToken("key", "principle", Arrays.asList(new SimpleGrantedAuthority(Role.ROLE_ANONYMOUS.toString())));
        SecurityContextHolder.getContext().setAuthentication(auth);
        try {
            result = projectService.findProjectList(true, false, null, pageable);
        } catch (Exception e) {

        }

        verify(projectRepository, times(1)).findProjectByStatusAndDelete(any(Pageable.class), any(Boolean.class), any(Boolean.class), any());

        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getProjectNo(), projectSimpleDto1.getProjectNo());
        assertEquals(result.get(0).getName(), projectSimpleDto1.getName());
        assertEquals(result.get(0).getMaxPeople(), projectSimpleDto1.getMaxPeople());
        assertEquals(result.get(0).getCurrentPeople(), projectSimpleDto1.getCurrentPeople());
        assertEquals(result.get(0).getViewCount(), projectSimpleDto1.getViewCount());
        assertEquals(result.get(0).getRegister(), projectSimpleDto1.getRegister());
        assertEquals(result.get(0).isBookMark(), projectSimpleDto1.isBookMark());

        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(0).getProjectNo(), positionName1.getProjectNo());
        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(0).getPositionNo(), positionName1.getPositionNo());
        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(0).getPositionName(), positionName1.getPositionName());
        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(1).getProjectNo(), positionName2.getProjectNo());
        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(1).getPositionNo(), positionName2.getPositionNo());
        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(1).getPositionName(), positionName2.getPositionName());

        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), technicalStackName1.getProjectNo());
        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getImage(), technicalStackName1.getImage());
        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), technicalStackName1.getTechnicalStackName());
        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), technicalStackName2.getProjectNo());
        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getImage(), technicalStackName2.getImage());
        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), technicalStackName2.getTechnicalStackName());

        assertEquals(result.get(1).getProjectNo(), projectSimpleDto2.getProjectNo());
        assertEquals(result.get(1).getName(), projectSimpleDto2.getName());
        assertEquals(result.get(1).getMaxPeople(), projectSimpleDto2.getMaxPeople());
        assertEquals(result.get(1).getCurrentPeople(), projectSimpleDto2.getCurrentPeople());
        assertEquals(result.get(1).getViewCount(), projectSimpleDto2.getViewCount());
        assertEquals(result.get(1).getRegister(), projectSimpleDto2.getRegister());
        assertEquals(result.get(1).isBookMark(), projectSimpleDto2.isBookMark());

        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(0).getProjectNo(), positionName3.getProjectNo());
        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(0).getPositionNo(), positionName3.getPositionNo());
        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(0).getPositionName(), positionName3.getPositionName());
        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(1).getProjectNo(), positionName4.getProjectNo());
        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(1).getPositionNo(), positionName4.getPositionNo());
        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(1).getPositionName(), positionName4.getPositionName());

        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), technicalStackName3.getProjectNo());
        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getImage(), technicalStackName3.getImage());
        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), technicalStackName3.getTechnicalStackName());
        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), technicalStackName4.getProjectNo());
        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getImage(), technicalStackName4.getImage());
        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), technicalStackName4.getTechnicalStackName());
    }

    @Test
    public void 로그인_프로젝트_목록_조회_성공_테스트() {
        LocalDateTime createDate = LocalDateTime.now();
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        // 유저 객체
        User user1 = User.builder()
                .no(1L)
                .name("testUser1")
                .sex("M")
                .email("testEmail1")
                .password("testPassword1")
                .github("testGithub1")
                .block(false)
                .blockReason(null)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(false)
                .imageNo(0L)
                .position(null)
                .build();

        // 프로젝트 객체
        List<ProjectSimpleDto> projectSimpleDtoList = new ArrayList<>();
        ProjectSimpleDto projectSimpleDto1 = ProjectSimpleDto.builder()
                .projectNo(1L)
                .name("testName1")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .register("testUser1")
                .bookMark(false)
                .build();

        // ProjectSimplePositionDto 객체
        ProjectSimplePositionDto positionName1 = ProjectSimplePositionDto.builder()
                .projectNo(1L)
                .positionNo(1L)
                .positionName("testPositionName1")
                .build();

        ProjectSimplePositionDto positionName2 = ProjectSimplePositionDto.builder()
                .projectNo(1L)
                .positionNo(2L)
                .positionName("testPositionName2")
                .build();
        List<ProjectSimplePositionDto> projectSimplePositionDtoList1 = new ArrayList<>();
        projectSimplePositionDtoList1.add(positionName1);
        projectSimplePositionDtoList1.add(positionName2);
        projectSimpleDto1.setProjectSimplePositionDtoList(projectSimplePositionDtoList1);

        // ProjectSimpleTechnicalStackDto 객체
        ProjectSimpleTechnicalStackDto technicalStackName1 = ProjectSimpleTechnicalStackDto.builder()
                .projectNo(1L)
                .image("testImage1")
                .technicalStackName("testTechnicalStackName1")
                .build();

        ProjectSimpleTechnicalStackDto technicalStackName2 = ProjectSimpleTechnicalStackDto.builder()
                .projectNo(1L)
                .image("testImage2")
                .technicalStackName("testTechnicalStackName2")
                .build();

        List<ProjectSimpleTechnicalStackDto> projectSimpleTechnicalStackDtoList1 = new ArrayList<>();
        projectSimpleTechnicalStackDtoList1.add(technicalStackName1);
        projectSimpleTechnicalStackDtoList1.add(technicalStackName2);
        projectSimpleDto1.setProjectSimpleTechnicalStackDtoList(projectSimpleTechnicalStackDtoList1);

        // projectSimpleDtoList 세팅
        projectSimpleDtoList.add(projectSimpleDto1);

        ProjectSimpleDto projectSimpleDto2 = ProjectSimpleDto.builder()
                .projectNo(2L)
                .name("testName2")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .register("testUser1")
                .bookMark(false)
                .build();

        // ProjectSimplePositionDto 객체
        ProjectSimplePositionDto positionName3 = ProjectSimplePositionDto.builder()
                .projectNo(2L)
                .positionNo(1L)
                .positionName("testPositionName3")
                .build();

        ProjectSimplePositionDto positionName4 = ProjectSimplePositionDto.builder()
                .projectNo(2L)
                .positionNo(2L)
                .positionName("testPositionName4")
                .build();
        List<ProjectSimplePositionDto> projectSimplePositionDtoList2 = new ArrayList<>();
        projectSimplePositionDtoList2.add(positionName3);
        projectSimplePositionDtoList2.add(positionName4);
        projectSimpleDto2.setProjectSimplePositionDtoList(projectSimplePositionDtoList2);

        // ProjectSimpleTechnicalStackDto 객체
        ProjectSimpleTechnicalStackDto technicalStackName3 = ProjectSimpleTechnicalStackDto.builder()
                .projectNo(2L)
                .image("testImage3")
                .technicalStackName("testTechnicalStackName3")
                .build();

        ProjectSimpleTechnicalStackDto technicalStackName4 = ProjectSimpleTechnicalStackDto.builder()
                .projectNo(2L)
                .image("testImage4")
                .technicalStackName("testTechnicalStackName4")
                .build();

        List<ProjectSimpleTechnicalStackDto> projectSimpleTechnicalStackDtoList2 = new ArrayList<>();
        projectSimpleTechnicalStackDtoList2.add(technicalStackName3);
        projectSimpleTechnicalStackDtoList2.add(technicalStackName4);
        projectSimpleDto2.setProjectSimpleTechnicalStackDtoList(projectSimpleTechnicalStackDtoList2);

        // projectSimpleDtoList 세팅
        projectSimpleDtoList.add(projectSimpleDto2);
        
        // bookMark 세팅
        List<BookMark> bookMarkList = new ArrayList<>();
        Project project1 = Project.builder()
                .no(1L)
                .name("testName1")
                .createUserName("testUser1")
                .createDate(createDate)
                .startDate(startDate)
                .endDate(endDate)
                .state(true)
                .introduction("testIntroduction1")
                .maxPeople(10)
                .currentPeople(4)
                .deleteReason(null)
                .viewCount(10)
                .commentCount(10)
                .build();
        BookMark bookMark1 = BookMark.builder()
                .no(1L)
                .user(user1)
                .project(project1)
                .build();
        bookMarkList.add(bookMark1);

        // List to Page
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createDate").descending());
        int start = (int)pageable.getOffset();
        int end = (start + pageable.getPageSize()) > projectSimpleDtoList.size() ? projectSimpleDtoList.size() : (start + pageable.getPageSize());
        Page<ProjectSimpleDto> projectPage = new PageImpl<>(projectSimpleDtoList.subList(start, end), pageable, projectSimpleDtoList.size());

        given(projectRepository.findProjectByStatusAndDelete(any(Pageable.class), any(Boolean.class), any(Boolean.class), any())).willReturn(projectPage);
        given(bookMarkRepository.findByUserNo(any())).willReturn(bookMarkList);

        List<ProjectSimpleDto> result = null;

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user1, "", user1.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        try {
            result = projectService.findProjectList(true, false, null, pageable);
        } catch (Exception e) {
            e.printStackTrace();
        }

        verify(projectRepository, times(1)).findProjectByStatusAndDelete(any(Pageable.class), any(Boolean.class), any(Boolean.class), any());
        verify(bookMarkRepository, times(1)).findByUserNo(any());

        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getProjectNo(), projectSimpleDto1.getProjectNo());
        assertEquals(result.get(0).getName(), projectSimpleDto1.getName());
        assertEquals(result.get(0).getMaxPeople(), projectSimpleDto1.getMaxPeople());
        assertEquals(result.get(0).getCurrentPeople(), projectSimpleDto1.getCurrentPeople());
        assertEquals(result.get(0).getViewCount(), projectSimpleDto1.getViewCount());
        assertEquals(result.get(0).getRegister(), projectSimpleDto1.getRegister());
        assertEquals(result.get(0).isBookMark(), true);

        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(0).getProjectNo(), positionName1.getProjectNo());
        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(0).getPositionNo(), positionName1.getPositionNo());
        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(0).getPositionName(), positionName1.getPositionName());
        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(1).getProjectNo(), positionName2.getProjectNo());
        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(1).getPositionNo(), positionName2.getPositionNo());
        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(1).getPositionName(), positionName2.getPositionName());

        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), technicalStackName1.getProjectNo());
        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getImage(), technicalStackName1.getImage());
        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), technicalStackName1.getTechnicalStackName());
        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), technicalStackName2.getProjectNo());
        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getImage(), technicalStackName2.getImage());
        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), technicalStackName2.getTechnicalStackName());

        assertEquals(result.get(1).getProjectNo(), projectSimpleDto2.getProjectNo());
        assertEquals(result.get(1).getName(), projectSimpleDto2.getName());
        assertEquals(result.get(1).getMaxPeople(), projectSimpleDto2.getMaxPeople());
        assertEquals(result.get(1).getCurrentPeople(), projectSimpleDto2.getCurrentPeople());
        assertEquals(result.get(1).getViewCount(), projectSimpleDto2.getViewCount());
        assertEquals(result.get(1).getRegister(), projectSimpleDto2.getRegister());
        assertEquals(result.get(1).isBookMark(), false);

        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(0).getProjectNo(), positionName3.getProjectNo());
        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(0).getPositionNo(), positionName3.getPositionNo());
        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(0).getPositionName(), positionName3.getPositionName());
        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(1).getProjectNo(), positionName4.getProjectNo());
        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(1).getPositionNo(), positionName4.getPositionNo());
        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(1).getPositionName(), positionName4.getPositionName());

        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), technicalStackName3.getProjectNo());
        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getImage(), technicalStackName3.getImage());
        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), technicalStackName3.getTechnicalStackName());
        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), technicalStackName4.getProjectNo());
        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getImage(), technicalStackName4.getImage());
        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), technicalStackName4.getTechnicalStackName());
    }

    @Test
    public void 유저가_만든_목록_조회_성공_테스트() {
        LocalDateTime createDate = LocalDateTime.now();
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        // 유저 객체
        User user1 = User.builder()
                .no(1L)
                .name("testUser1")
                .sex("M")
                .email("testEmail1")
                .password("testPassword1")
                .github("testGithub1")
                .block(false)
                .blockReason(null)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(false)
                .imageNo(0L)
                .position(null)
                .build();

        // 프로젝트 객체
        List<ProjectSimpleDto> projectSimpleDtoList = new ArrayList<>();
        ProjectSimpleDto projectSimpleDto1 = ProjectSimpleDto.builder()
                .projectNo(1L)
                .name("testName1")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .register("testUser1")
                .bookMark(false)
                .build();

        // ProjectSimplePositionDto 객체
        ProjectSimplePositionDto positionName1 = ProjectSimplePositionDto.builder()
                .projectNo(1L)
                .positionNo(1L)
                .positionName("testPositionName1")
                .build();

        ProjectSimplePositionDto positionName2 = ProjectSimplePositionDto.builder()
                .projectNo(1L)
                .positionNo(2L)
                .positionName("testPositionName2")
                .build();
        List<ProjectSimplePositionDto> projectSimplePositionDtoList1 = new ArrayList<>();
        projectSimplePositionDtoList1.add(positionName1);
        projectSimplePositionDtoList1.add(positionName2);
        projectSimpleDto1.setProjectSimplePositionDtoList(projectSimplePositionDtoList1);

        // ProjectSimpleTechnicalStackDto 객체
        ProjectSimpleTechnicalStackDto technicalStackName1 = ProjectSimpleTechnicalStackDto.builder()
                .projectNo(1L)
                .image("testImage1")
                .technicalStackName("testTechnicalStackName1")
                .build();

        ProjectSimpleTechnicalStackDto technicalStackName2 = ProjectSimpleTechnicalStackDto.builder()
                .projectNo(1L)
                .image("testImage2")
                .technicalStackName("testTechnicalStackName2")
                .build();

        List<ProjectSimpleTechnicalStackDto> projectSimpleTechnicalStackDtoList1 = new ArrayList<>();
        projectSimpleTechnicalStackDtoList1.add(technicalStackName1);
        projectSimpleTechnicalStackDtoList1.add(technicalStackName2);
        projectSimpleDto1.setProjectSimpleTechnicalStackDtoList(projectSimpleTechnicalStackDtoList1);

        // projectSimpleDtoList 세팅
        projectSimpleDtoList.add(projectSimpleDto1);

        ProjectSimpleDto projectSimpleDto2 = ProjectSimpleDto.builder()
                .projectNo(2L)
                .name("testName2")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .register("testUser1")
                .bookMark(false)
                .build();

        // ProjectSimplePositionDto 객체
        ProjectSimplePositionDto positionName3 = ProjectSimplePositionDto.builder()
                .projectNo(2L)
                .positionNo(1L)
                .positionName("testPositionName3")
                .build();

        ProjectSimplePositionDto positionName4 = ProjectSimplePositionDto.builder()
                .projectNo(2L)
                .positionNo(2L)
                .positionName("testPositionName4")
                .build();
        List<ProjectSimplePositionDto> projectSimplePositionDtoList2 = new ArrayList<>();
        projectSimplePositionDtoList2.add(positionName3);
        projectSimplePositionDtoList2.add(positionName4);
        projectSimpleDto2.setProjectSimplePositionDtoList(projectSimplePositionDtoList2);

        // ProjectSimpleTechnicalStackDto 객체
        ProjectSimpleTechnicalStackDto technicalStackName3 = ProjectSimpleTechnicalStackDto.builder()
                .projectNo(2L)
                .image("testImage3")
                .technicalStackName("testTechnicalStackName3")
                .build();

        ProjectSimpleTechnicalStackDto technicalStackName4 = ProjectSimpleTechnicalStackDto.builder()
                .projectNo(2L)
                .image("testImage4")
                .technicalStackName("testTechnicalStackName4")
                .build();

        List<ProjectSimpleTechnicalStackDto> projectSimpleTechnicalStackDtoList2 = new ArrayList<>();
        projectSimpleTechnicalStackDtoList2.add(technicalStackName3);
        projectSimpleTechnicalStackDtoList2.add(technicalStackName4);
        projectSimpleDto2.setProjectSimpleTechnicalStackDtoList(projectSimpleTechnicalStackDtoList2);

        // projectSimpleDtoList 세팅
        projectSimpleDtoList.add(projectSimpleDto2);

        // bookMark 세팅
        List<BookMark> bookMarkList = new ArrayList<>();
        Project project1 = Project.builder()
                .no(1L)
                .name("testName1")
                .createUserName("testUser1")
                .createDate(createDate)
                .startDate(startDate)
                .endDate(endDate)
                .state(true)
                .introduction("testIntroduction1")
                .maxPeople(10)
                .currentPeople(4)
                .deleteReason(null)
                .viewCount(10)
                .commentCount(10)
                .build();
        BookMark bookMark1 = BookMark.builder()
                .no(1L)
                .user(user1)
                .project(project1)
                .build();
        bookMarkList.add(bookMark1);

        // List to Page
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createDate").descending());
        int start = (int)pageable.getOffset();
        int end = (start + pageable.getPageSize()) > projectSimpleDtoList.size() ? projectSimpleDtoList.size() : (start + pageable.getPageSize());
        Page<ProjectSimpleDto> projectPage = new PageImpl<>(projectSimpleDtoList.subList(start, end), pageable, projectSimpleDtoList.size());

        given(projectRepository.findUserProjectByDelete(any(Pageable.class), any(User.class), any(Boolean.class))).willReturn(projectPage);
        given(bookMarkRepository.findByUserNo(any())).willReturn(bookMarkList);

        List<ProjectSimpleDto> result = null;

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user1, "", user1.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        try {
            result = projectService.findUserProjectList(false, pageable);
        } catch (Exception e) {
            e.printStackTrace();
        }

        verify(projectRepository, times(1)).findUserProjectByDelete(any(Pageable.class), any(User.class), any(Boolean.class));
        verify(bookMarkRepository, times(1)).findByUserNo(any());

        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getProjectNo(), projectSimpleDto1.getProjectNo());
        assertEquals(result.get(0).getName(), projectSimpleDto1.getName());
        assertEquals(result.get(0).getMaxPeople(), projectSimpleDto1.getMaxPeople());
        assertEquals(result.get(0).getCurrentPeople(), projectSimpleDto1.getCurrentPeople());
        assertEquals(result.get(0).getViewCount(), projectSimpleDto1.getViewCount());
        assertEquals(result.get(0).getRegister(), user1.getName());
        assertEquals(result.get(0).isBookMark(), true);

        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(0).getProjectNo(), positionName1.getProjectNo());
        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(0).getPositionNo(), positionName1.getPositionNo());
        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(0).getPositionName(), positionName1.getPositionName());
        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(1).getProjectNo(), positionName2.getProjectNo());
        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(1).getPositionNo(), positionName2.getPositionNo());
        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(1).getPositionName(), positionName2.getPositionName());

        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), technicalStackName1.getProjectNo());
        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getImage(), technicalStackName1.getImage());
        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), technicalStackName1.getTechnicalStackName());
        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), technicalStackName2.getProjectNo());
        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getImage(), technicalStackName2.getImage());
        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), technicalStackName2.getTechnicalStackName());

        assertEquals(result.get(1).getProjectNo(), projectSimpleDto2.getProjectNo());
        assertEquals(result.get(1).getName(), projectSimpleDto2.getName());
        assertEquals(result.get(1).getMaxPeople(), projectSimpleDto2.getMaxPeople());
        assertEquals(result.get(1).getCurrentPeople(), projectSimpleDto2.getCurrentPeople());
        assertEquals(result.get(1).getViewCount(), projectSimpleDto2.getViewCount());
        assertEquals(result.get(1).getRegister(), user1.getName());
        assertEquals(result.get(1).isBookMark(), false);

        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(0).getProjectNo(), positionName3.getProjectNo());
        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(0).getPositionNo(), positionName3.getPositionNo());
        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(0).getPositionName(), positionName3.getPositionName());
        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(1).getProjectNo(), positionName4.getProjectNo());
        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(1).getPositionNo(), positionName4.getPositionNo());
        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(1).getPositionName(), positionName4.getPositionName());

        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), technicalStackName3.getProjectNo());
        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getImage(), technicalStackName3.getImage());
        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), technicalStackName3.getTechnicalStackName());
        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), technicalStackName4.getProjectNo());
        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getImage(), technicalStackName4.getImage());
        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), technicalStackName4.getTechnicalStackName());
    }

    @Test
    public void 참여중인_프로젝트_목록_조회_성공_테스트() {
        LocalDateTime createDate = LocalDateTime.now();
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        // 유저 객체
        User user1 = User.builder()
                .no(1L)
                .name("testUser1")
                .sex("M")
                .email("testEmail1")
                .password("testPassword1")
                .github("testGithub1")
                .block(false)
                .blockReason(null)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(false)
                .imageNo(0L)
                .position(null)
                .build();

        // 프로젝트 객체
        List<ProjectSimpleDto> projectSimpleDtoList = new ArrayList<>();
        ProjectSimpleDto projectSimpleDto1 = ProjectSimpleDto.builder()
                .projectNo(1L)
                .name("testName1")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .register("testUser1")
                .bookMark(false)
                .build();

        // ProjectSimplePositionDto 객체
        ProjectSimplePositionDto positionName1 = ProjectSimplePositionDto.builder()
                .projectNo(1L)
                .positionNo(1L)
                .positionName("testPositionName1")
                .build();

        ProjectSimplePositionDto positionName2 = ProjectSimplePositionDto.builder()
                .projectNo(1L)
                .positionNo(2L)
                .positionName("testPositionName2")
                .build();
        List<ProjectSimplePositionDto> projectSimplePositionDtoList1 = new ArrayList<>();
        projectSimplePositionDtoList1.add(positionName1);
        projectSimplePositionDtoList1.add(positionName2);
        projectSimpleDto1.setProjectSimplePositionDtoList(projectSimplePositionDtoList1);

        // ProjectSimpleTechnicalStackDto 객체
        ProjectSimpleTechnicalStackDto technicalStackName1 = ProjectSimpleTechnicalStackDto.builder()
                .projectNo(1L)
                .image("testImage1")
                .technicalStackName("testTechnicalStackName1")
                .build();

        ProjectSimpleTechnicalStackDto technicalStackName2 = ProjectSimpleTechnicalStackDto.builder()
                .projectNo(1L)
                .image("testImage2")
                .technicalStackName("testTechnicalStackName2")
                .build();

        List<ProjectSimpleTechnicalStackDto> projectSimpleTechnicalStackDtoList1 = new ArrayList<>();
        projectSimpleTechnicalStackDtoList1.add(technicalStackName1);
        projectSimpleTechnicalStackDtoList1.add(technicalStackName2);
        projectSimpleDto1.setProjectSimpleTechnicalStackDtoList(projectSimpleTechnicalStackDtoList1);

        // projectSimpleDtoList 세팅
        projectSimpleDtoList.add(projectSimpleDto1);

        ProjectSimpleDto projectSimpleDto2 = ProjectSimpleDto.builder()
                .projectNo(2L)
                .name("testName2")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .register("testUser1")
                .bookMark(false)
                .build();

        // ProjectSimplePositionDto 객체
        ProjectSimplePositionDto positionName3 = ProjectSimplePositionDto.builder()
                .projectNo(2L)
                .positionNo(1L)
                .positionName("testPositionName3")
                .build();

        ProjectSimplePositionDto positionName4 = ProjectSimplePositionDto.builder()
                .projectNo(2L)
                .positionNo(2L)
                .positionName("testPositionName4")
                .build();
        List<ProjectSimplePositionDto> projectSimplePositionDtoList2 = new ArrayList<>();
        projectSimplePositionDtoList2.add(positionName3);
        projectSimplePositionDtoList2.add(positionName4);
        projectSimpleDto2.setProjectSimplePositionDtoList(projectSimplePositionDtoList2);

        // ProjectSimpleTechnicalStackDto 객체
        ProjectSimpleTechnicalStackDto technicalStackName3 = ProjectSimpleTechnicalStackDto.builder()
                .projectNo(2L)
                .image("testImage3")
                .technicalStackName("testTechnicalStackName3")
                .build();

        ProjectSimpleTechnicalStackDto technicalStackName4 = ProjectSimpleTechnicalStackDto.builder()
                .projectNo(2L)
                .image("testImage4")
                .technicalStackName("testTechnicalStackName4")
                .build();

        List<ProjectSimpleTechnicalStackDto> projectSimpleTechnicalStackDtoList2 = new ArrayList<>();
        projectSimpleTechnicalStackDtoList2.add(technicalStackName3);
        projectSimpleTechnicalStackDtoList2.add(technicalStackName4);
        projectSimpleDto2.setProjectSimpleTechnicalStackDtoList(projectSimpleTechnicalStackDtoList2);

        // projectSimpleDtoList 세팅
        projectSimpleDtoList.add(projectSimpleDto2);

        // bookMark 세팅
        List<BookMark> bookMarkList = new ArrayList<>();
        Project project1 = Project.builder()
                .no(1L)
                .name("testName1")
                .createUserName("testUser1")
                .createDate(createDate)
                .startDate(startDate)
                .endDate(endDate)
                .state(true)
                .introduction("testIntroduction1")
                .maxPeople(10)
                .currentPeople(4)
                .deleteReason(null)
                .viewCount(10)
                .commentCount(10)
                .build();
        BookMark bookMark1 = BookMark.builder()
                .no(1L)
                .user(user1)
                .project(project1)
                .build();
        bookMarkList.add(bookMark1);

        // List to Page
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createDate").descending());
        int start = (int)pageable.getOffset();
        int end = (start + pageable.getPageSize()) > projectSimpleDtoList.size() ? projectSimpleDtoList.size() : (start + pageable.getPageSize());
        Page<ProjectSimpleDto> projectPage = new PageImpl<>(projectSimpleDtoList.subList(start, end), pageable, projectSimpleDtoList.size());

        given(projectRepository.findParticipateProjectByDelete(any(Pageable.class), any(User.class), any(Boolean.class))).willReturn(projectPage);
        given(bookMarkRepository.findByUserNo(any())).willReturn(bookMarkList);

        List<ProjectSimpleDto> result = null;

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user1, "", user1.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        try {
            result = projectService.findParticipateProjectList(false, pageable);
        } catch (Exception e) {
            e.printStackTrace();
        }

        verify(projectRepository, times(1)).findParticipateProjectByDelete(any(Pageable.class), any(User.class), any(Boolean.class));
        verify(bookMarkRepository, times(1)).findByUserNo(any());

        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getProjectNo(), projectSimpleDto1.getProjectNo());
        assertEquals(result.get(0).getName(), projectSimpleDto1.getName());
        assertEquals(result.get(0).getMaxPeople(), projectSimpleDto1.getMaxPeople());
        assertEquals(result.get(0).getCurrentPeople(), projectSimpleDto1.getCurrentPeople());
        assertEquals(result.get(0).getViewCount(), projectSimpleDto1.getViewCount());
        assertEquals(result.get(0).getRegister(), user1.getName());
        assertEquals(result.get(0).isBookMark(), true);

        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(0).getProjectNo(), positionName1.getProjectNo());
        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(0).getPositionNo(), positionName1.getPositionNo());
        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(0).getPositionName(), positionName1.getPositionName());
        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(1).getProjectNo(), positionName2.getProjectNo());
        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(1).getPositionNo(), positionName2.getPositionNo());
        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(1).getPositionName(), positionName2.getPositionName());

        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), technicalStackName1.getProjectNo());
        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getImage(), technicalStackName1.getImage());
        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), technicalStackName1.getTechnicalStackName());
        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), technicalStackName2.getProjectNo());
        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getImage(), technicalStackName2.getImage());
        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), technicalStackName2.getTechnicalStackName());

        assertEquals(result.get(1).getProjectNo(), projectSimpleDto2.getProjectNo());
        assertEquals(result.get(1).getName(), projectSimpleDto2.getName());
        assertEquals(result.get(1).getMaxPeople(), projectSimpleDto2.getMaxPeople());
        assertEquals(result.get(1).getCurrentPeople(), projectSimpleDto2.getCurrentPeople());
        assertEquals(result.get(1).getViewCount(), projectSimpleDto2.getViewCount());
        assertEquals(result.get(1).getRegister(), user1.getName());
        assertEquals(result.get(1).isBookMark(), false);

        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(0).getProjectNo(), positionName3.getProjectNo());
        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(0).getPositionNo(), positionName3.getPositionNo());
        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(0).getPositionName(), positionName3.getPositionName());
        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(1).getProjectNo(), positionName4.getProjectNo());
        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(1).getPositionNo(), positionName4.getPositionNo());
        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(1).getPositionName(), positionName4.getPositionName());

        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), technicalStackName3.getProjectNo());
        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getImage(), technicalStackName3.getImage());
        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), technicalStackName3.getTechnicalStackName());
        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), technicalStackName4.getProjectNo());
        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getImage(), technicalStackName4.getImage());
        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), technicalStackName4.getTechnicalStackName());
    }

    @Test
    public void 신청중인_프로젝트_목록_조회_성공_테스트() {
        LocalDateTime createDate = LocalDateTime.now();
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        // 유저 객체
        User user1 = User.builder()
                .no(1L)
                .name("testUser1")
                .sex("M")
                .email("testEmail1")
                .password("testPassword1")
                .github("testGithub1")
                .block(false)
                .blockReason(null)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(false)
                .imageNo(0L)
                .position(null)
                .build();

        // 프로젝트 객체
        List<ProjectSimpleDto> projectSimpleDtoList = new ArrayList<>();
        ProjectSimpleDto projectSimpleDto1 = ProjectSimpleDto.builder()
                .projectNo(1L)
                .name("testName1")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .register("testUser1")
                .bookMark(false)
                .build();

        // ProjectSimplePositionDto 객체
        ProjectSimplePositionDto positionName1 = ProjectSimplePositionDto.builder()
                .projectNo(1L)
                .positionNo(1L)
                .positionName("testPositionName1")
                .build();

        ProjectSimplePositionDto positionName2 = ProjectSimplePositionDto.builder()
                .projectNo(1L)
                .positionNo(2L)
                .positionName("testPositionName2")
                .build();
        List<ProjectSimplePositionDto> projectSimplePositionDtoList1 = new ArrayList<>();
        projectSimplePositionDtoList1.add(positionName1);
        projectSimplePositionDtoList1.add(positionName2);
        projectSimpleDto1.setProjectSimplePositionDtoList(projectSimplePositionDtoList1);

        // ProjectSimpleTechnicalStackDto 객체
        ProjectSimpleTechnicalStackDto technicalStackName1 = ProjectSimpleTechnicalStackDto.builder()
                .projectNo(1L)
                .image("testImage1")
                .technicalStackName("testTechnicalStackName1")
                .build();

        ProjectSimpleTechnicalStackDto technicalStackName2 = ProjectSimpleTechnicalStackDto.builder()
                .projectNo(1L)
                .image("testImage2")
                .technicalStackName("testTechnicalStackName2")
                .build();

        List<ProjectSimpleTechnicalStackDto> projectSimpleTechnicalStackDtoList1 = new ArrayList<>();
        projectSimpleTechnicalStackDtoList1.add(technicalStackName1);
        projectSimpleTechnicalStackDtoList1.add(technicalStackName2);
        projectSimpleDto1.setProjectSimpleTechnicalStackDtoList(projectSimpleTechnicalStackDtoList1);

        // projectSimpleDtoList 세팅
        projectSimpleDtoList.add(projectSimpleDto1);

        ProjectSimpleDto projectSimpleDto2 = ProjectSimpleDto.builder()
                .projectNo(2L)
                .name("testName2")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .register("testUser1")
                .bookMark(false)
                .build();

        // ProjectSimplePositionDto 객체
        ProjectSimplePositionDto positionName3 = ProjectSimplePositionDto.builder()
                .projectNo(2L)
                .positionNo(1L)
                .positionName("testPositionName3")
                .build();

        ProjectSimplePositionDto positionName4 = ProjectSimplePositionDto.builder()
                .projectNo(2L)
                .positionNo(2L)
                .positionName("testPositionName4")
                .build();
        List<ProjectSimplePositionDto> projectSimplePositionDtoList2 = new ArrayList<>();
        projectSimplePositionDtoList2.add(positionName3);
        projectSimplePositionDtoList2.add(positionName4);
        projectSimpleDto2.setProjectSimplePositionDtoList(projectSimplePositionDtoList2);

        // ProjectSimpleTechnicalStackDto 객체
        ProjectSimpleTechnicalStackDto technicalStackName3 = ProjectSimpleTechnicalStackDto.builder()
                .projectNo(2L)
                .image("testImage3")
                .technicalStackName("testTechnicalStackName3")
                .build();

        ProjectSimpleTechnicalStackDto technicalStackName4 = ProjectSimpleTechnicalStackDto.builder()
                .projectNo(2L)
                .image("testImage4")
                .technicalStackName("testTechnicalStackName4")
                .build();

        List<ProjectSimpleTechnicalStackDto> projectSimpleTechnicalStackDtoList2 = new ArrayList<>();
        projectSimpleTechnicalStackDtoList2.add(technicalStackName3);
        projectSimpleTechnicalStackDtoList2.add(technicalStackName4);
        projectSimpleDto2.setProjectSimpleTechnicalStackDtoList(projectSimpleTechnicalStackDtoList2);

        // projectSimpleDtoList 세팅
        projectSimpleDtoList.add(projectSimpleDto2);

        // bookMark 세팅
        List<BookMark> bookMarkList = new ArrayList<>();
        Project project1 = Project.builder()
                .no(1L)
                .name("testName1")
                .createUserName("testUser1")
                .createDate(createDate)
                .startDate(startDate)
                .endDate(endDate)
                .state(true)
                .introduction("testIntroduction1")
                .maxPeople(10)
                .currentPeople(4)
                .deleteReason(null)
                .viewCount(10)
                .commentCount(10)
                .build();
        BookMark bookMark1 = BookMark.builder()
                .no(1L)
                .user(user1)
                .project(project1)
                .build();
        bookMarkList.add(bookMark1);

        // List to Page
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createDate").descending());
        int start = (int)pageable.getOffset();
        int end = (start + pageable.getPageSize()) > projectSimpleDtoList.size() ? projectSimpleDtoList.size() : (start + pageable.getPageSize());
        Page<ProjectSimpleDto> projectPage = new PageImpl<>(projectSimpleDtoList.subList(start, end), pageable, projectSimpleDtoList.size());

        given(projectRepository.findParticipateRequestProjectByDelete(any(Pageable.class), any(User.class), any(Boolean.class))).willReturn(projectPage);
        given(bookMarkRepository.findByUserNo(any())).willReturn(bookMarkList);

        List<ProjectSimpleDto> result = null;

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user1, "", user1.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        try {
            result = projectService.findParticipateRequestProjectList(false, pageable);
        } catch (Exception e) {
            e.printStackTrace();
        }

        verify(projectRepository, times(1)).findParticipateRequestProjectByDelete(any(Pageable.class), any(User.class), any(Boolean.class));
        verify(bookMarkRepository, times(1)).findByUserNo(any());

        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getProjectNo(), projectSimpleDto1.getProjectNo());
        assertEquals(result.get(0).getName(), projectSimpleDto1.getName());
        assertEquals(result.get(0).getMaxPeople(), projectSimpleDto1.getMaxPeople());
        assertEquals(result.get(0).getCurrentPeople(), projectSimpleDto1.getCurrentPeople());
        assertEquals(result.get(0).getViewCount(), projectSimpleDto1.getViewCount());
        assertEquals(result.get(0).getRegister(), user1.getName());
        assertEquals(result.get(0).isBookMark(), true);

        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(0).getProjectNo(), positionName1.getProjectNo());
        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(0).getPositionNo(), positionName1.getPositionNo());
        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(0).getPositionName(), positionName1.getPositionName());
        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(1).getProjectNo(), positionName2.getProjectNo());
        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(1).getPositionNo(), positionName2.getPositionNo());
        assertEquals(result.get(0).getProjectSimplePositionDtoList().get(1).getPositionName(), positionName2.getPositionName());

        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), technicalStackName1.getProjectNo());
        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getImage(), technicalStackName1.getImage());
        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), technicalStackName1.getTechnicalStackName());
        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), technicalStackName2.getProjectNo());
        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getImage(), technicalStackName2.getImage());
        assertEquals(result.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), technicalStackName2.getTechnicalStackName());

        assertEquals(result.get(1).getProjectNo(), projectSimpleDto2.getProjectNo());
        assertEquals(result.get(1).getName(), projectSimpleDto2.getName());
        assertEquals(result.get(1).getMaxPeople(), projectSimpleDto2.getMaxPeople());
        assertEquals(result.get(1).getCurrentPeople(), projectSimpleDto2.getCurrentPeople());
        assertEquals(result.get(1).getViewCount(), projectSimpleDto2.getViewCount());
        assertEquals(result.get(1).getRegister(), user1.getName());
        assertEquals(result.get(1).isBookMark(), false);

        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(0).getProjectNo(), positionName3.getProjectNo());
        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(0).getPositionNo(), positionName3.getPositionNo());
        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(0).getPositionName(), positionName3.getPositionName());
        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(1).getProjectNo(), positionName4.getProjectNo());
        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(1).getPositionNo(), positionName4.getPositionNo());
        assertEquals(result.get(1).getProjectSimplePositionDtoList().get(1).getPositionName(), positionName4.getPositionName());

        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), technicalStackName3.getProjectNo());
        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getImage(), technicalStackName3.getImage());
        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), technicalStackName3.getTechnicalStackName());
        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), technicalStackName4.getProjectNo());
        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getImage(), technicalStackName4.getImage());
        assertEquals(result.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), technicalStackName4.getTechnicalStackName());
    }

    @Nested
    @DisplayName("프로젝트 상세 조회 테스트")
    class testProjectInfo {
        @Nested
        @DisplayName("비로그인시 테스트")
        class testNotLogin {
            @Test
            @DisplayName("성공 테스트")
            public void testSuccess() {
                // given
                LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
                LocalDate startDate = LocalDate.of(2022, 06, 24);
                LocalDate endDate = LocalDate.of(2022, 06, 28);

                // 유저 객체
                User user1 = User.builder()
                        .no(1L)
                        .name("testUser1")
                        .sex("M")
                        .email("testEmail1")
                        .password("testPassword1")
                        .github("testGithub1")
                        .block(false)
                        .blockReason(null)
                        .permission(Role.ROLE_USER)
                        .oauthCategory(OAuth.NORMAL)
                        .email_auth(false)
                        .imageNo(0L)
                        .position(null)
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
                        .build();

                // 포지션 세팅
                Position position1 = Position.builder()
                        .no(1L)
                        .name("testPosition1")
                        .build();
                Position position2 = Position.builder()
                        .no(2L)
                        .name("testPosition2")
                        .build();

                List<ProjectPosition> projectPositionList = new ArrayList<>();
                ProjectPosition projectPosition1 = ProjectPosition.builder()
                        .no(1L)
                        .state(true)
                        .project(project1)
                        .position(position1)
                        .user(user1)
                        .creator(false)
                        .build();
                ProjectPosition projectPosition2 = ProjectPosition.builder()
                        .no(2L)
                        .state(false)
                        .project(project1)
                        .position(position2)
                        .user(null)
                        .creator(false)
                        .build();
                projectPositionList.add(projectPosition1);
                projectPositionList.add(projectPosition2);

                // 기술 스택 세팅
                TechnicalStack technicalStack1 = TechnicalStack.builder()
                        .no(1L)
                        .name("testTechnicalStack1")
                        .build();
                TechnicalStack technicalStack2 = TechnicalStack.builder()
                        .no(2L)
                        .name("testTechnicalStack2")
                        .build();

                List<ProjectTechnicalStack> projectTechnicalStackList = new ArrayList<>();
                ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
                        .no(1L)
                        .project(project1)
                        .technicalStack(technicalStack1)
                        .build();
                ProjectTechnicalStack projectTechnicalStack2 = ProjectTechnicalStack.builder()
                        .no(2L)
                        .project(project1)
                        .technicalStack(technicalStack2)
                        .build();
                projectTechnicalStackList.add(projectTechnicalStack1);
                projectTechnicalStackList.add(projectTechnicalStack2);

                given(projectRepository.findById(any())).willReturn(Optional.of(project1));
                given(projectPositionRepository.findProjectAndPositionAndUserUsingFetchJoinByProjectNo(any())).willReturn(projectPositionList);
                given(projectTechnicalStackRepository.findTechnicalStackAndProjectUsingFetchJoin(any())).willReturn(projectTechnicalStackList);

                // when
                Authentication auth = new AnonymousAuthenticationToken("key", "principle", Arrays.asList(new SimpleGrantedAuthority(Role.ROLE_ANONYMOUS.toString())));
                SecurityContextHolder.getContext().setAuthentication(auth);
                ProjectDto projectDto = null;

                try {
                    projectDto = projectService.getProjectDetail(project1.getNo());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // then
                verify(projectRepository).findById(any());
                verify(projectPositionRepository).findProjectAndPositionAndUserUsingFetchJoinByProjectNo(any());
                verify(projectTechnicalStackRepository).findTechnicalStackAndProjectUsingFetchJoin(any());

                assertEquals(projectDto.getProjectNo(), project1.getNo());
                assertEquals(projectDto.getName(), project1.getName());
                assertEquals(projectDto.getStartDate(), project1.getStartDate());
                assertEquals(projectDto.getEndDate(), project1.getEndDate());
                assertEquals(projectDto.isState(), project1.isState());
                assertEquals(projectDto.getIntroduction(), project1.getIntroduction());
                assertEquals(projectDto.getCurrentPeople(), project1.getCurrentPeople());
                assertEquals(projectDto.getMaxPeople(), project1.getMaxPeople());
                assertEquals(projectDto.isBookmark(), false);
                assertEquals(projectDto.isApplicationStatus(), false);

                assertEquals(projectDto.getProjectPositionDetailDtoList().get(0).getProjectPositionNo(), projectPosition1.getNo());
                assertEquals(projectDto.getProjectPositionDetailDtoList().get(0).getPositionName(), projectPosition1.getPosition().getName());
                assertEquals(projectDto.getProjectPositionDetailDtoList().get(0).getUserDto().getNo(), projectPosition1.getUser().getNo());
                assertEquals(projectDto.getProjectPositionDetailDtoList().get(0).getUserDto().getName(), projectPosition1.getUser().getName());
                assertEquals(projectDto.getProjectPositionDetailDtoList().get(0).getUserDto().isRegister(), projectPosition1.isCreator());

                assertEquals(projectDto.getProjectPositionDetailDtoList().get(1).getProjectPositionNo(), projectPosition2.getNo());
                assertEquals(projectDto.getProjectPositionDetailDtoList().get(1).getPositionName(), projectPosition2.getPosition().getName());
                assertEquals(projectDto.getProjectPositionDetailDtoList().get(1).getUserDto(), null);
            }
        }

        @Nested
        @DisplayName("로그인시 테스트")
        class testLogin {
            @Test
            @DisplayName("성공 테스트")
            public void testSuccess() {
                // given
                LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
                LocalDate startDate = LocalDate.of(2022, 06, 24);
                LocalDate endDate = LocalDate.of(2022, 06, 28);

                // 유저 객체
                User user1 = User.builder()
                        .no(1L)
                        .name("testUser1")
                        .sex("M")
                        .email("testEmail1")
                        .password("testPassword1")
                        .github("testGithub1")
                        .block(false)
                        .blockReason(null)
                        .permission(Role.ROLE_USER)
                        .oauthCategory(OAuth.NORMAL)
                        .email_auth(false)
                        .imageNo(0L)
                        .position(null)
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
                        .build();

                // 포지션 세팅
                Position position1 = Position.builder()
                        .no(1L)
                        .name("testPosition1")
                        .build();
                Position position2 = Position.builder()
                        .no(2L)
                        .name("testPosition2")
                        .build();

                List<ProjectPosition> projectPositionList = new ArrayList<>();
                ProjectPosition projectPosition1 = ProjectPosition.builder()
                        .no(1L)
                        .state(true)
                        .project(project1)
                        .position(position1)
                        .user(user1)
                        .creator(false)
                        .build();
                ProjectPosition projectPosition2 = ProjectPosition.builder()
                        .no(2L)
                        .state(false)
                        .project(project1)
                        .position(position2)
                        .user(null)
                        .creator(false)
                        .build();
                projectPositionList.add(projectPosition1);
                projectPositionList.add(projectPosition2);

                // 기술 스택 세팅
                TechnicalStack technicalStack1 = TechnicalStack.builder()
                        .no(1L)
                        .name("testTechnicalStack1")
                        .build();
                TechnicalStack technicalStack2 = TechnicalStack.builder()
                        .no(2L)
                        .name("testTechnicalStack2")
                        .build();

                List<ProjectTechnicalStack> projectTechnicalStackList = new ArrayList<>();
                ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
                        .no(1L)
                        .project(project1)
                        .technicalStack(technicalStack1)
                        .build();
                ProjectTechnicalStack projectTechnicalStack2 = ProjectTechnicalStack.builder()
                        .no(2L)
                        .project(project1)
                        .technicalStack(technicalStack2)
                        .build();
                projectTechnicalStackList.add(projectTechnicalStack1);
                projectTechnicalStackList.add(projectTechnicalStack2);

                given(projectRepository.findById(any())).willReturn(Optional.of(project1));
                given(projectPositionRepository.findProjectAndPositionAndUserUsingFetchJoinByProjectNo(any())).willReturn(projectPositionList);
                given(projectTechnicalStackRepository.findTechnicalStackAndProjectUsingFetchJoin(any())).willReturn(projectTechnicalStackList);
                given(bookMarkRepository.existBookMark(any(User.class), any(Project.class))).willReturn(true);
                // when
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user1, "", user1.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
                ProjectDto projectDto = null;

                try {
                    projectDto = projectService.getProjectDetail(project1.getNo());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // then
                verify(projectRepository).findById(any());
                verify(projectPositionRepository).findProjectAndPositionAndUserUsingFetchJoinByProjectNo(any());
                verify(projectTechnicalStackRepository).findTechnicalStackAndProjectUsingFetchJoin(any());

                assertEquals(projectDto.getProjectNo(), project1.getNo());
                assertEquals(projectDto.getName(), project1.getName());
                assertEquals(projectDto.getStartDate(), project1.getStartDate());
                assertEquals(projectDto.getEndDate(), project1.getEndDate());
                assertEquals(projectDto.isState(), project1.isState());
                assertEquals(projectDto.getIntroduction(), project1.getIntroduction());
                assertEquals(projectDto.getCurrentPeople(), project1.getCurrentPeople());
                assertEquals(projectDto.getMaxPeople(), project1.getMaxPeople());
                assertEquals(projectDto.isBookmark(), true);
                assertEquals(projectDto.isApplicationStatus(), true);

                assertEquals(projectDto.getProjectPositionDetailDtoList().get(0).getProjectPositionNo(), projectPosition1.getNo());
                assertEquals(projectDto.getProjectPositionDetailDtoList().get(0).getPositionName(), projectPosition1.getPosition().getName());
                assertEquals(projectDto.getProjectPositionDetailDtoList().get(0).getUserDto().getNo(), projectPosition1.getUser().getNo());
                assertEquals(projectDto.getProjectPositionDetailDtoList().get(0).getUserDto().getName(), projectPosition1.getUser().getName());
                assertEquals(projectDto.getProjectPositionDetailDtoList().get(0).getUserDto().isRegister(), projectPosition1.isCreator());

                assertEquals(projectDto.getProjectPositionDetailDtoList().get(1).getProjectPositionNo(), projectPosition2.getNo());
                assertEquals(projectDto.getProjectPositionDetailDtoList().get(1).getPositionName(), projectPosition2.getPosition().getName());
                assertEquals(projectDto.getProjectPositionDetailDtoList().get(1).getUserDto(), null);
            }
        }
    }

    @Test
    public void 프로젝트_수정_폼_조회_성공_테스트() {
        // given
        LocalDateTime createDate = LocalDateTime.now();
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        // 유저 객체
        User user1 = User.builder()
                .no(1L)
                .name("testUser1")
                .sex("M")
                .email("testEmail1")
                .password("testPassword1")
                .github("testGithub1")
                .block(false)
                .blockReason(null)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(false)
                .imageNo(0L)
                .position(null)
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
                .build();

        // 포지션 세팅
        List<Position> positionList = new ArrayList<>();
        Position position1 = Position.builder()
                .no(1L)
                .name("testPosition1")
                .build();
        Position position2 = Position.builder()
                .no(2L)
                .name("testPosition2")
                .build();
        positionList.add(position1);
        positionList.add(position2);
        
        // 프로젝트 포지션 세팅
        List<ProjectPosition> projectPositionList = new ArrayList<>();
        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .no(1L)
                .state(true)
                .project(project1)
                .position(position1)
                .user(user1)
                .creator(false)
                .build();
        ProjectPosition projectPosition2 = ProjectPosition.builder()
                .no(2L)
                .state(false)
                .project(project1)
                .position(position2)
                .user(null)
                .creator(false)
                .build();
        projectPositionList.add(projectPosition1);
        projectPositionList.add(projectPosition2);

        // 기술 스택 세팅
        List<TechnicalStack> technicalStackList = new ArrayList<>();
        TechnicalStack technicalStack1 = TechnicalStack.builder()
                .no(1L)
                .name("testTechnicalStack1")
                .build();
        TechnicalStack technicalStack2 = TechnicalStack.builder()
                .no(2L)
                .name("testTechnicalStack2")
                .build();
        technicalStackList.add(technicalStack1);
        technicalStackList.add(technicalStack2);
        
        // 프로젝트 기술 스택 세팅
        List<ProjectTechnicalStack> projectTechnicalStackList = new ArrayList<>();
        ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
                .no(1L)
                .project(project1)
                .technicalStack(technicalStack1)
                .build();
        ProjectTechnicalStack projectTechnicalStack2 = ProjectTechnicalStack.builder()
                .no(2L)
                .project(project1)
                .technicalStack(technicalStack2)
                .build();
        projectTechnicalStackList.add(projectTechnicalStack1);
        projectTechnicalStackList.add(projectTechnicalStack2);

        given(projectRepository.findById(any())).willReturn(Optional.of(project1));
        given(positionRepository.findAll()).willReturn(positionList);
        given(technicalStackRepository.findAll()).willReturn(technicalStackList);
        given(projectPositionRepository.findProjectAndPositionAndUserUsingFetchJoinByProjectNo(any())).willReturn(projectPositionList);
        given(projectTechnicalStackRepository.findTechnicalStackAndProjectUsingFetchJoin(any())).willReturn(projectTechnicalStackList);

        // when
        ProjectUpdateFormResponseDto projectUpdateFormResponseDto = null;
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user1, "", user1.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        try {
            projectUpdateFormResponseDto = projectService.getProjectUpdateForm(1L);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // then
        verify(projectRepository).findById(any());
        verify(positionRepository).findAll();
        verify(technicalStackRepository).findAll();
        verify(projectPositionRepository).findProjectAndPositionAndUserUsingFetchJoinByProjectNo(any());
        verify(projectTechnicalStackRepository).findTechnicalStackAndProjectUsingFetchJoin(any());
        
        // 프로젝트
        assertEquals(projectUpdateFormResponseDto.getProjectNo(), project1.getNo());
        assertEquals(projectUpdateFormResponseDto.getName(), project1.getName());
        assertEquals(projectUpdateFormResponseDto.isState(), project1.isState());
        assertEquals(projectUpdateFormResponseDto.getStartDate(), project1.getStartDate());
        assertEquals(projectUpdateFormResponseDto.getEndDate(), project1.getEndDate());
        assertEquals(projectUpdateFormResponseDto.getIntroduction(), project1.getIntroduction());
        
        // 포지션
        assertEquals(projectUpdateFormResponseDto.getPositionUpdateFormDtoList().get(0).getNo(), positionList.get(0).getNo());
        assertEquals(projectUpdateFormResponseDto.getPositionUpdateFormDtoList().get(0).getName(), positionList.get(0).getName());
        assertEquals(projectUpdateFormResponseDto.getPositionUpdateFormDtoList().get(1).getNo(), positionList.get(1).getNo());
        assertEquals(projectUpdateFormResponseDto.getPositionUpdateFormDtoList().get(1).getName(), positionList.get(1).getName());
        
        // 프로젝트 포지션
        assertEquals(projectUpdateFormResponseDto.getProjectPositionUpdateFormDtoList().get(0).getProjectPositionNo(), projectPositionList.get(0).getNo());
        assertEquals(projectUpdateFormResponseDto.getProjectPositionUpdateFormDtoList().get(0).getPositionNo(), projectPositionList.get(0).getPosition().getNo());
        assertEquals(projectUpdateFormResponseDto.getProjectPositionUpdateFormDtoList().get(0).getProjectPositionName(), projectPositionList.get(0).getPosition().getName());
        assertEquals(projectUpdateFormResponseDto.getProjectPositionUpdateFormDtoList().get(0).getProjectUpdateFormUserDto().getNo(), projectPositionList.get(0).getUser().getNo());
        assertEquals(projectUpdateFormResponseDto.getProjectPositionUpdateFormDtoList().get(1).getProjectPositionNo(), projectPositionList.get(1).getNo());
        assertEquals(projectUpdateFormResponseDto.getProjectPositionUpdateFormDtoList().get(1).getPositionNo(), projectPositionList.get(1).getPosition().getNo());
        assertEquals(projectUpdateFormResponseDto.getProjectPositionUpdateFormDtoList().get(1).getProjectPositionName(), projectPositionList.get(1).getPosition().getName());
        assertEquals(projectUpdateFormResponseDto.getProjectPositionUpdateFormDtoList().get(1).getProjectUpdateFormUserDto(), null);
        
        // 기술스택
        assertEquals(projectUpdateFormResponseDto.getTechnicalStackUpdateFormDtoList().get(0).getNo(), technicalStackList.get(0).getNo());
        assertEquals(projectUpdateFormResponseDto.getTechnicalStackUpdateFormDtoList().get(0).getName(), technicalStackList.get(0).getName());
        assertEquals(projectUpdateFormResponseDto.getTechnicalStackUpdateFormDtoList().get(1).getNo(), technicalStackList.get(1).getNo());
        assertEquals(projectUpdateFormResponseDto.getTechnicalStackUpdateFormDtoList().get(1).getName(), technicalStackList.get(1).getName());
        
        // 프로젝트 기술스택
        assertEquals(projectUpdateFormResponseDto.getProjectTechnicalStackList().get(0), projectTechnicalStackList.get(0).getTechnicalStack().getName());
        assertEquals(projectUpdateFormResponseDto.getProjectTechnicalStackList().get(1), projectTechnicalStackList.get(1).getTechnicalStack().getName());
    }

    @Test
    public void 프로젝트_수정_폼_조회_실패_테스트() {
        // given
        given(projectRepository.findById(any())).willThrow(new CustomException(ErrorCode.PROJECT_NO_SUCH_ELEMENT_EXCEPTION));

        // when
        CustomException e = Assertions.assertThrows(CustomException.class, () -> {
            projectService.getProjectUpdateForm(1L);
        });

        // then
        assertEquals(e.getErrorCode().getHttpStatus(),ErrorCode.PROJECT_NO_SUCH_ELEMENT_EXCEPTION.getHttpStatus());
        assertEquals(e.getErrorCode().getDetail(),ErrorCode.PROJECT_NO_SUCH_ELEMENT_EXCEPTION.getDetail());
    }

    @Nested
    @DisplayName("프로젝트 업데이트 테스트")
    class testProjectUpdate {
        @Test
        @DisplayName("성공 테스트")
        public void testSuccess() {
            // given
            LocalDateTime createDate = LocalDateTime.now();
            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

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
                    .build();

            // 포지션 세팅
            List<Position> positionList = new ArrayList<>();
            Position position1 = Position.builder()
                    .no(1L)
                    .name("testPosition1")
                    .build();
            positionList.add(position1);

            // 프로젝트 포지션 세팅
            ProjectPosition projectPosition1 = ProjectPosition.builder()
                    .no(1L)
                    .state(false)
                    .project(project1)
                    .position(position1)
                    .user(null)
                    .creator(false)
                    .build();
            ProjectPosition projectPosition2 = ProjectPosition.builder()
                    .no(2L)
                    .state(false)
                    .project(project1)
                    .position(position1)
                    .user(null)
                    .creator(false)
                    .build();

            // 기술 스택 세팅
            List<TechnicalStack> technicalStackList = new ArrayList<>();
            TechnicalStack technicalStack1 = TechnicalStack.builder()
                    .no(1L)
                    .name("testTechnicalStack1")
                    .build();
            TechnicalStack technicalStack2 = TechnicalStack.builder()
                    .no(2L)
                    .name("testTechnicalStack2")
                    .build();
            technicalStackList.add(technicalStack1);
            technicalStackList.add(technicalStack2);

            // 프로젝트 기술 스택 세팅
            ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
                    .project(project1)
                    .technicalStack(technicalStack1).build();
            ProjectTechnicalStack projectTechnicalStack2 = ProjectTechnicalStack.builder()
                    .project(project1)
                    .technicalStack(technicalStack1).build();

            given(projectRepository.existUserProjectByUser(any(Long.class), any(Long.class))).willReturn(true);
            given(projectRepository.findById(any())).willReturn(Optional.of(project1));
            given(positionRepository.findByNoIn(any())).willReturn(positionList);
            given(projectPositionRepository.saveAndFlush(any())).willReturn(projectPosition1).willReturn(projectPosition2);
            given(technicalStackRepository.findByNoIn(any())).willReturn(technicalStackList);
            given(projectTechnicalStackRepository.saveAndFlush(any())).willReturn(projectTechnicalStack1).willReturn(projectTechnicalStack2);

            // when
            Long result = null;
            List<ProjectPositionAddDto> projectPositionAddDtoList = new ArrayList<>();
            ProjectPositionAddDto projectPositionAddDto = new ProjectPositionAddDto(1L, 2);
            projectPositionAddDtoList.add(projectPositionAddDto);

            String startDateRequest = "2022-08-03";
            String endDateRequest = "2022-08-04";

            List<Long> technicalStackNoList = new ArrayList<>();
            technicalStackNoList.add(1L);
            technicalStackNoList.add(2L);

            ProjectUpdateRequestDto projectUpdateRequestDto = new ProjectUpdateRequestDto("testName", projectPositionAddDtoList, null, startDateRequest, endDateRequest, technicalStackNoList, "testIntroduction");
            try {
                result = projectService.projectUpdate(1L, projectUpdateRequestDto);
            } catch (Exception e) {
                e.printStackTrace();
            }

            assertEquals(result, project1.getNo());
        }
    }

    @Nested
    @DisplayName("프로젝트 삭제 테스트")
    class testProjectDelete {
        @Test
        @DisplayName("성공 테스트")
        public void testSuccess() throws Exception {
            // given
            // 유저 객체
            User user1 = User.builder()
                    .no(1L)
                    .name("testUser1")
                    .sex("M")
                    .email("testEmail1")
                    .password("testPassword1")
                    .github("testGithub1")
                    .block(false)
                    .blockReason(null)
                    .permission(Role.ROLE_USER)
                    .oauthCategory(OAuth.NORMAL)
                    .email_auth(false)
                    .imageNo(0L)
                    .position(null)
                    .build();

            given(projectRepository.existUserProjectByUser(any(Long.class), any(Long.class))).willReturn(true);

            // when
            boolean result = false;
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user1, "", user1.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
            try {
                result = projectService.projectDelete(1L);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // then
            verify(projectRepository).existUserProjectByUser(any(Long.class), any(Long.class));
            verify(participateRequestTechnicalStackRepository).deleteByProjectNo(any());
            verify(projectParticipateRequestRepository).deleteByProjectNo(any());
            verify(projectPositionRepository).deleteByProjectNo(any());
            verify(projectTechnicalStackRepository).deleteByProjectNo(any());
            verify(bookMarkRepository).deleteByProjectNo(any());
            verify(commentRepository).deleteByProjectNo(any());
            verify(projectRepository).deleteById(any());

            assertEquals(result, true);
        }
    }
}
