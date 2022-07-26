package com.matching.project.service;

import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.project.*;
import com.matching.project.dto.projectposition.ProjectPositionRegisterDto;
import com.matching.project.dto.user.ProjectRegisterUserDto;
import com.matching.project.entity.*;
import com.matching.project.repository.*;
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
//    @Test
//    public void 비로그인_프로젝트_목록_조회_성공_테스트() {
//        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
//        LocalDate startDate = LocalDate.of(2022, 06, 24);
//        LocalDate endDate = LocalDate.of(2022, 06, 28);
//
//        // 프로젝트 객체
//        List<ProjectSimpleDto> projectSimpleDtoList = new ArrayList<>();
//        ProjectSimpleDto projectSimpleDto1 = ProjectSimpleDto.builder()
//                .no(1L)
//                .name("testName1")
//                .profile(null)
//                .maxPeople(10)
//                .currentPeople(4)
//                .viewCount(10)
//                .commentCount(10)
//                .register("user1")
//                .bookMark(false)
//                .build();
//
//        ProjectSimplePositionDto positionName1 = ProjectSimplePositionDto.builder()
//                .projectNo(1L)
//                .positionName("testPositionName1")
//                .image(null)
//                .state(true)
//                .build();
//
//        ProjectSimplePositionDto positionName2 = ProjectSimplePositionDto.builder()
//                .projectNo(2L)
//                .positionName("testPositionName2")
//                .image(null)
//                .state(true)
//                .build();
//        List<ProjectSimplePositionDto> projectSimplePositionDtoList1 = projectSimpleDto1.getProjectSimplePositionDtoList();
//        projectSimplePositionDtoList1.add(positionName1);
//        projectSimplePositionDtoList1.add(positionName2);
//
//        ProjectSimpleTechnicalStackDto technicalStackName1 = ProjectSimpleTechnicalStackDto.builder()
//                .projectNo(1L)
//                .technicalStackName("testTechnicalStackName1")
//                .build();
//
//        ProjectSimpleTechnicalStackDto technicalStackName2 = ProjectSimpleTechnicalStackDto.builder()
//                .projectNo(1L)
//                .technicalStackName("testTechnicalStackName2")
//                .build();
//
//        List<ProjectSimpleTechnicalStackDto> projectSimpleTechnicalStackDtoList1 = projectSimpleDto1.getProjectSimpleTechnicalStackDtoList();
//        projectSimpleTechnicalStackDtoList1.add(technicalStackName1);
//        projectSimpleTechnicalStackDtoList1.add(technicalStackName2);
//
//        projectSimpleDtoList.add(projectSimpleDto1);
//
//        ProjectSimpleDto projectSimpleDto2 = ProjectSimpleDto.builder()
//                .no(2L)
//                .name("testName2")
//                .profile(null)
//                .maxPeople(10)
//                .currentPeople(4)
//                .viewCount(10)
//                .commentCount(10)
//                .register("user1")
//                .bookMark(false)
//                .build();
//
//        ProjectSimplePositionDto positionName3 = ProjectSimplePositionDto.builder()
//                .projectNo(2L)
//                .positionName("testPositionName3")
//                .image(null)
//                .state(true)
//                .build();
//
//        ProjectSimplePositionDto positionName4 = ProjectSimplePositionDto.builder()
//                .projectNo(2L)
//                .positionName("testPositionName4")
//                .image(null)
//                .state(true)
//                .build();
//        List<ProjectSimplePositionDto> projectSimplePositionDtoList2 = projectSimpleDto2.getProjectSimplePositionDtoList();
//        projectSimplePositionDtoList2.add(positionName3);
//        projectSimplePositionDtoList2.add(positionName4);
//
//        ProjectSimpleTechnicalStackDto technicalStackName3 = ProjectSimpleTechnicalStackDto.builder()
//                .projectNo(2L)
//                .technicalStackName("testTechnicalStackName3")
//                .build();
//
//        ProjectSimpleTechnicalStackDto technicalStackName4 = ProjectSimpleTechnicalStackDto.builder()
//                .projectNo(2L)
//                .technicalStackName("testTechnicalStackName4")
//                .build();
//
//        List<ProjectSimpleTechnicalStackDto> projectSimpleTechnicalStackDtoList2 = projectSimpleDto2.getProjectSimpleTechnicalStackDtoList();
//        projectSimpleTechnicalStackDtoList2.add(technicalStackName3);
//        projectSimpleTechnicalStackDtoList2.add(technicalStackName4);
//
//        projectSimpleDtoList.add(projectSimpleDto2);
//
//
//        // List to Page
//        Pageable pageable = PageRequest.of(0, 4, Sort.by("createDate").descending());
//        int start = (int)pageable.getOffset();
//        int end = (start + pageable.getPageSize()) > projectSimpleDtoList.size() ? projectSimpleDtoList.size() : (start + pageable.getPageSize());
//        Page<ProjectSimpleDto> projectPage = new PageImpl<>(projectSimpleDtoList.subList(start, end), pageable, projectSimpleDtoList.size());
//
//        given(projectRepository.findProjectByStatus(any(Pageable.class), any(Boolean.class), any(Boolean.class), any())).willReturn(projectPage);
//
//
//        Page<ProjectSimpleDto> projectSimpleDtoPage = null;
//
//        Authentication auth = new AnonymousAuthenticationToken("key", "principle", Arrays.asList(new SimpleGrantedAuthority(Role.ROLE_ANONYMOUS.toString())));
//        SecurityContextHolder.getContext().setAuthentication(auth);
//        try {
//            projectSimpleDtoPage = projectService.findProjectList(true, false, null, pageable);
//        } catch (Exception e) {
//
//        }
//        List<ProjectSimpleDto> content = projectSimpleDtoPage.getContent();
//
//        verify(projectRepository, times(1)).findProjectByStatus(any(Pageable.class), any(Boolean.class), any(Boolean.class), any());
//
//        assertEquals(content.size(), 2);
//        assertEquals(content.get(0).getNo(), projectSimpleDto1.getNo());
//        assertEquals(content.get(0).getName(), projectSimpleDto1.getName());
//        assertEquals(content.get(0).getProfile(), null);
//        assertEquals(content.get(0).getMaxPeople(), projectSimpleDto1.getMaxPeople());
//        assertEquals(content.get(0).getCurrentPeople(), projectSimpleDto1.getCurrentPeople());
//        assertEquals(content.get(0).getViewCount(), projectSimpleDto1.getViewCount());
//        assertEquals(content.get(0).getCommentCount(), projectSimpleDto1.getCommentCount());
//        assertEquals(content.get(0).getRegister(), projectSimpleDto1.getRegister());
//        assertEquals(content.get(0).isBookMark(), projectSimpleDto1.isBookMark());
//
//        assertEquals(content.get(0).getProjectSimplePositionDtoList().get(0).getProjectNo(), positionName1.getProjectNo());
//        assertEquals(content.get(0).getProjectSimplePositionDtoList().get(0).getPositionName(), positionName1.getPositionName());
//        assertEquals(content.get(0).getProjectSimplePositionDtoList().get(0).getImage(), positionName1.getImage());
//        assertEquals(content.get(0).getProjectSimplePositionDtoList().get(1).getProjectNo(), positionName2.getProjectNo());
//        assertEquals(content.get(0).getProjectSimplePositionDtoList().get(1).getPositionName(), positionName2.getPositionName());
//        assertEquals(content.get(0).getProjectSimplePositionDtoList().get(1).getImage(), positionName2.getImage());
//
//        assertEquals(content.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), technicalStackName1.getProjectNo());
//        assertEquals(content.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), technicalStackName1.getTechnicalStackName());
//        assertEquals(content.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), technicalStackName2.getProjectNo());
//        assertEquals(content.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), technicalStackName2.getTechnicalStackName());
//
//        assertEquals(content.get(1).getNo(), projectSimpleDto2.getNo());
//        assertEquals(content.get(1).getName(), projectSimpleDto2.getName());
//        assertEquals(content.get(1).getProfile(), null);
//        assertEquals(content.get(1).getMaxPeople(), projectSimpleDto2.getMaxPeople());
//        assertEquals(content.get(1).getCurrentPeople(), projectSimpleDto2.getCurrentPeople());
//        assertEquals(content.get(1).getViewCount(), projectSimpleDto2.getViewCount());
//        assertEquals(content.get(1).getCommentCount(), projectSimpleDto2.getCommentCount());
//        assertEquals(content.get(1).getRegister(), projectSimpleDto2.getRegister());
//        assertEquals(content.get(1).isBookMark(), projectSimpleDto2.isBookMark());
//
//        assertEquals(content.get(1).getProjectSimplePositionDtoList().get(0).getProjectNo(), positionName3.getProjectNo());
//        assertEquals(content.get(1).getProjectSimplePositionDtoList().get(0).getPositionName(), positionName3.getPositionName());
//        assertEquals(content.get(1).getProjectSimplePositionDtoList().get(0).getImage(), positionName3.getImage());
//        assertEquals(content.get(1).getProjectSimplePositionDtoList().get(1).getProjectNo(), positionName4.getProjectNo());
//        assertEquals(content.get(1).getProjectSimplePositionDtoList().get(1).getPositionName(), positionName4.getPositionName());
//        assertEquals(content.get(1).getProjectSimplePositionDtoList().get(1).getImage(), positionName4.getImage());
//
//        assertEquals(content.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), technicalStackName3.getProjectNo());
//        assertEquals(content.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), technicalStackName3.getTechnicalStackName());
//        assertEquals(content.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), technicalStackName4.getProjectNo());
//        assertEquals(content.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), technicalStackName4.getTechnicalStackName());
//    }
//
//    @Test
//    public void 로그인_프로젝트_목록_조회_성공_테스트() {
//        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
//        LocalDate startDate = LocalDate.of(2022, 06, 24);
//        LocalDate endDate = LocalDate.of(2022, 06, 28);
//
//        // 유저 객체
//        User user1 = User.builder()
//                .no(1L)
//                .name("testUser1")
//                .sex('M')
//                .email("testEmail1")
//                .password("testPassword1")
//                .github("testGithub1")
//                .block(false)
//                .blockReason(null)
//                .permission(Role.ROLE_USER)
//                .oauthCategory(OAuth.NORMAL)
//                .email_auth(false)
//                .imageNo(0L)
//                .position(null)
//                .build();
//
//        // 프로젝트 객체
//        List<ProjectSimpleDto> projectSimpleDtoList = new ArrayList<>();
//        ProjectSimpleDto projectSimpleDto1 = ProjectSimpleDto.builder()
//                .no(1L)
//                .name("testName1")
//                .profile(null)
//                .maxPeople(10)
//                .currentPeople(4)
//                .viewCount(10)
//                .commentCount(10)
//                .register("user1")
//                .bookMark(false)
//                .build();
//
//        ProjectSimplePositionDto positionName1 = ProjectSimplePositionDto.builder()
//                .projectNo(1L)
//                .positionName("testPositionName1")
//                .image(null)
//                .state(true)
//                .build();
//
//        ProjectSimplePositionDto positionName2 = ProjectSimplePositionDto.builder()
//                .projectNo(2L)
//                .positionName("testPositionName2")
//                .image(null)
//                .state(true)
//                .build();
//        List<ProjectSimplePositionDto> projectSimplePositionDtoList1 = projectSimpleDto1.getProjectSimplePositionDtoList();
//        projectSimplePositionDtoList1.add(positionName1);
//        projectSimplePositionDtoList1.add(positionName2);
//
//        ProjectSimpleTechnicalStackDto technicalStackName1 = ProjectSimpleTechnicalStackDto.builder()
//                .projectNo(1L)
//                .technicalStackName("testTechnicalStackName1")
//                .build();
//
//        ProjectSimpleTechnicalStackDto technicalStackName2 = ProjectSimpleTechnicalStackDto.builder()
//                .projectNo(1L)
//                .technicalStackName("testTechnicalStackName2")
//                .build();
//
//        List<ProjectSimpleTechnicalStackDto> projectSimpleTechnicalStackDtoList1 = projectSimpleDto1.getProjectSimpleTechnicalStackDtoList();
//        projectSimpleTechnicalStackDtoList1.add(technicalStackName1);
//        projectSimpleTechnicalStackDtoList1.add(technicalStackName2);
//
//        projectSimpleDtoList.add(projectSimpleDto1);
//
//        ProjectSimpleDto projectSimpleDto2 = ProjectSimpleDto.builder()
//                .no(2L)
//                .name("testName2")
//                .profile(null)
//                .maxPeople(10)
//                .currentPeople(4)
//                .viewCount(10)
//                .commentCount(10)
//                .register("user1")
//                .bookMark(false)
//                .build();
//
//        ProjectSimplePositionDto positionName3 = ProjectSimplePositionDto.builder()
//                .projectNo(2L)
//                .positionName("testPositionName3")
//                .image(null)
//                .state(true)
//                .build();
//
//        ProjectSimplePositionDto positionName4 = ProjectSimplePositionDto.builder()
//                .projectNo(2L)
//                .positionName("testPositionName4")
//                .image(null)
//                .state(true)
//                .build();
//        List<ProjectSimplePositionDto> projectSimplePositionDtoList2 = projectSimpleDto2.getProjectSimplePositionDtoList();
//        projectSimplePositionDtoList2.add(positionName3);
//        projectSimplePositionDtoList2.add(positionName4);
//
//        ProjectSimpleTechnicalStackDto technicalStackName3 = ProjectSimpleTechnicalStackDto.builder()
//                .projectNo(2L)
//                .technicalStackName("testTechnicalStackName3")
//                .build();
//
//        ProjectSimpleTechnicalStackDto technicalStackName4 = ProjectSimpleTechnicalStackDto.builder()
//                .projectNo(2L)
//                .technicalStackName("testTechnicalStackName4")
//                .build();
//
//        List<ProjectSimpleTechnicalStackDto> projectSimpleTechnicalStackDtoList2 = projectSimpleDto2.getProjectSimpleTechnicalStackDtoList();
//        projectSimpleTechnicalStackDtoList2.add(technicalStackName3);
//        projectSimpleTechnicalStackDtoList2.add(technicalStackName4);
//
//        projectSimpleDtoList.add(projectSimpleDto2);
//
//        List<BookMark> bookMarkList = new ArrayList<>();
//        Project project1 = Project.builder()
//                .no(1L)
//                .name("testName1")
//                .createUserName("user1")
//                .createDate(createDate)
//                .startDate(startDate)
//                .endDate(endDate)
//                .state(true)
//                .introduction("testIntroduction1")
//                .maxPeople(10)
//                .currentPeople(4)
//                .deleteReason(null)
//                .imageNo(0L)
//                .viewCount(10)
//                .commentCount(10)
//                .build();
//        BookMark bookMark1 = BookMark.builder()
//                .no(1L)
//                .user(user1)
//                .project(project1)
//                .build();
//        bookMarkList.add(bookMark1);
//
//        // List to Page
//        Pageable pageable = PageRequest.of(0, 4, Sort.by("createDate").descending());
//        int start = (int)pageable.getOffset();
//        int end = (start + pageable.getPageSize()) > projectSimpleDtoList.size() ? projectSimpleDtoList.size() : (start + pageable.getPageSize());
//        Page<ProjectSimpleDto> projectPage = new PageImpl<>(projectSimpleDtoList.subList(start, end), pageable, projectSimpleDtoList.size());
//
//        given(projectRepository.findProjectByStatus(any(Pageable.class), any(Boolean.class), any(Boolean.class), any())).willReturn(projectPage);
//        given(bookMarkRepository.findByUserNo(any())).willReturn(bookMarkList);
//
//        Page<ProjectSimpleDto> projectSimpleDtoPage = null;
//
//        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user1, "", user1.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(auth);
//        try {
//            projectSimpleDtoPage = projectService.findProjectList(true, false, null, pageable);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        verify(projectRepository, times(1)).findProjectByStatus(any(Pageable.class), any(Boolean.class), any(Boolean.class), any());
//        verify(bookMarkRepository, times(1)).findByUserNo(any());
//        List<ProjectSimpleDto> content = projectSimpleDtoPage.getContent();
//
//        assertEquals(content.size(), 2);
//        assertEquals(content.get(0).getNo(), projectSimpleDto1.getNo());
//        assertEquals(content.get(0).getName(), projectSimpleDto1.getName());
//        assertEquals(content.get(0).getProfile(), null);
//        assertEquals(content.get(0).getMaxPeople(), projectSimpleDto1.getMaxPeople());
//        assertEquals(content.get(0).getCurrentPeople(), projectSimpleDto1.getCurrentPeople());
//        assertEquals(content.get(0).getViewCount(), projectSimpleDto1.getViewCount());
//        assertEquals(content.get(0).getCommentCount(), projectSimpleDto1.getCommentCount());
//        assertEquals(content.get(0).getRegister(), projectSimpleDto1.getRegister());
//        assertEquals(content.get(0).isBookMark(), true);
//
//        assertEquals(content.get(0).getProjectSimplePositionDtoList().get(0).getProjectNo(), positionName1.getProjectNo());
//        assertEquals(content.get(0).getProjectSimplePositionDtoList().get(0).getPositionName(), positionName1.getPositionName());
//        assertEquals(content.get(0).getProjectSimplePositionDtoList().get(0).getImage(), positionName1.getImage());
//        assertEquals(content.get(0).getProjectSimplePositionDtoList().get(1).getProjectNo(), positionName2.getProjectNo());
//        assertEquals(content.get(0).getProjectSimplePositionDtoList().get(1).getPositionName(), positionName2.getPositionName());
//        assertEquals(content.get(0).getProjectSimplePositionDtoList().get(1).getImage(), positionName2.getImage());
//
//        assertEquals(content.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), technicalStackName1.getProjectNo());
//        assertEquals(content.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), technicalStackName1.getTechnicalStackName());
//        assertEquals(content.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), technicalStackName2.getProjectNo());
//        assertEquals(content.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), technicalStackName2.getTechnicalStackName());
//
//        assertEquals(content.get(1).getNo(), projectSimpleDto2.getNo());
//        assertEquals(content.get(1).getName(), projectSimpleDto2.getName());
//        assertEquals(content.get(1).getProfile(), null);
//        assertEquals(content.get(1).getMaxPeople(), projectSimpleDto2.getMaxPeople());
//        assertEquals(content.get(1).getCurrentPeople(), projectSimpleDto2.getCurrentPeople());
//        assertEquals(content.get(1).getViewCount(), projectSimpleDto2.getViewCount());
//        assertEquals(content.get(1).getCommentCount(), projectSimpleDto2.getCommentCount());
//        assertEquals(content.get(1).getRegister(), projectSimpleDto2.getRegister());
//        assertEquals(content.get(1).isBookMark(), false);
//
//        assertEquals(content.get(1).getProjectSimplePositionDtoList().get(0).getProjectNo(), positionName3.getProjectNo());
//        assertEquals(content.get(1).getProjectSimplePositionDtoList().get(0).getPositionName(), positionName3.getPositionName());
//        assertEquals(content.get(1).getProjectSimplePositionDtoList().get(0).getImage(), positionName3.getImage());
//        assertEquals(content.get(1).getProjectSimplePositionDtoList().get(1).getProjectNo(), positionName4.getProjectNo());
//        assertEquals(content.get(1).getProjectSimplePositionDtoList().get(1).getPositionName(), positionName4.getPositionName());
//        assertEquals(content.get(1).getProjectSimplePositionDtoList().get(1).getImage(), positionName4.getImage());
//
//        assertEquals(content.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), technicalStackName3.getProjectNo());
//        assertEquals(content.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), technicalStackName3.getTechnicalStackName());
//        assertEquals(content.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), technicalStackName4.getProjectNo());
//        assertEquals(content.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), technicalStackName4.getTechnicalStackName());
//    }
//
//    @Test
//    public void 비로그인_프로젝트_상세_조회_성공_테스트() {
//        // given
//        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
//        LocalDate startDate = LocalDate.of(2022, 06, 24);
//        LocalDate endDate = LocalDate.of(2022, 06, 28);
//
//        // 유저 객체
//        User user1 = User.builder()
//                .no(1L)
//                .name("testUser1")
//                .sex('M')
//                .email("testEmail1")
//                .password("testPassword1")
//                .github("testGithub1")
//                .block(false)
//                .blockReason(null)
//                .permission(Role.ROLE_USER)
//                .oauthCategory(OAuth.NORMAL)
//                .email_auth(false)
//                .imageNo(0L)
//                .position(null)
//                .build();
//
//        // 프로젝트 객체
//        Project project1 = Project.builder()
//                .no(1L)
//                .name("testName1")
//                .createUserName("user1")
//                .createDate(createDate)
//                .startDate(startDate)
//                .endDate(endDate)
//                .state(true)
//                .introduction("testIntroduction1")
//                .maxPeople(10)
//                .currentPeople(4)
//                .delete(false)
//                .deleteReason(null)
//                .imageNo(0L)
//                .viewCount(10)
//                .commentCount(10)
//                .build();
//
//        // 포지션 세팅
//        Position position1 = Position.builder()
//                .no(1L)
//                .name("testPosition1")
//                .build();
//        Position position2 = Position.builder()
//                .no(2L)
//                .name("testPosition2")
//                .build();
//
//        List<ProjectPosition> projectPositionList = new ArrayList<>();
//        ProjectPosition projectPosition1 = ProjectPosition.builder()
//                .no(1L)
//                .state(true)
//                .project(project1)
//                .position(position1)
//                .user(user1)
//                .creator(false)
//                .build();
//        ProjectPosition projectPosition2 = ProjectPosition.builder()
//                .no(2L)
//                .state(false)
//                .project(project1)
//                .position(position2)
//                .user(null)
//                .creator(false)
//                .build();
//        projectPositionList.add(projectPosition1);
//        projectPositionList.add(projectPosition2);
//
//        // 댓글 세팅
//        List<Comment> commentList = new ArrayList<>();
//        Comment comment1 = Comment.builder()
//                .no(1L)
//                .user(user1)
//                .project(project1)
//                .content("testContent1")
//                .build();
//        Comment comment2 = Comment.builder()
//                .no(2L)
//                .user(user1)
//                .project(project1)
//                .content("testContent1")
//                .build();
//        commentList.add(comment1);
//        commentList.add(comment2);
//
//        // 기술 스택 세팅
//        TechnicalStack technicalStack1 = TechnicalStack.builder()
//                .no(1L)
//                .name("testTechnicalStack1")
//                .build();
//        TechnicalStack technicalStack2 = TechnicalStack.builder()
//                .no(2L)
//                .name("testTechnicalStack2")
//                .build();
//
//        List<ProjectTechnicalStack> projectTechnicalStackList = new ArrayList<>();
//        ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
//                .no(1L)
//                .project(project1)
//                .technicalStack(technicalStack1)
//                .build();
//        ProjectTechnicalStack projectTechnicalStack2 = ProjectTechnicalStack.builder()
//                .no(2L)
//                .project(project1)
//                .technicalStack(technicalStack2)
//                .build();
//        projectTechnicalStackList.add(projectTechnicalStack1);
//        projectTechnicalStackList.add(projectTechnicalStack2);
//
//        given(projectRepository.findById(any())).willReturn(Optional.of(project1));
//        given(projectPositionRepository.findByProjectWithPositionAndProjectAndUserUsingLeftFetchJoin(any())).willReturn(projectPositionList);
//        given(commentRepository.findByProjectNo(any())).willReturn(commentList);
//        given(projectTechnicalStackRepository.findByProjectWithTechnicalStackAndProjectUsingFetchJoin(any())).willReturn(projectTechnicalStackList);
//
//        // when
//        Authentication auth = new AnonymousAuthenticationToken("key", "principle", Arrays.asList(new SimpleGrantedAuthority(Role.ROLE_ANONYMOUS.toString())));
//        SecurityContextHolder.getContext().setAuthentication(auth);
//        ProjectDto projectDto = null;
//
//        try {
//            projectDto = projectService.getProjectDetail(1L);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        // then
//        verify(projectRepository).findById(any());
//        verify(projectPositionRepository).findByProjectWithPositionAndProjectAndUserUsingLeftFetchJoin(any());
//        verify(commentRepository).findByProjectNo(any());
//        verify(projectTechnicalStackRepository).findByProjectWithTechnicalStackAndProjectUsingFetchJoin(any());
//
//        assertEquals(projectDto.getName(), project1.getName());
//        assertEquals(projectDto.getProfile(), null);
//        assertEquals(projectDto.getCreateDate(), project1.getCreateDate());
//        assertEquals(projectDto.getStartDate(), project1.getStartDate());
//        assertEquals(projectDto.getEndDate(), project1.getEndDate());
//        assertEquals(projectDto.isState(), project1.isState());
//        assertEquals(projectDto.getIntroduction(), project1.getIntroduction());
//        assertEquals(projectDto.getMaxPeople(), project1.getMaxPeople());
//        assertEquals(projectDto.isBookmark(), false);
//        assertEquals(projectDto.getRegister(), project1.getCreateUserName());
//        assertEquals(projectDto.getTechnicalStack().get(0), technicalStack1.getName());
//        assertEquals(projectDto.getTechnicalStack().get(1), technicalStack2.getName());
//
//        assertEquals(projectDto.getProjectPositionDetailDtoList().get(0).getPositionName(), projectPosition1.getPosition().getName());
//        assertEquals(projectDto.getProjectPositionDetailDtoList().get(0).getUserNo(), projectPosition1.getUser().getNo());
//        assertEquals(projectDto.getProjectPositionDetailDtoList().get(0).getUserName(), projectPosition1.getUser().getName());
//        assertEquals(projectDto.getProjectPositionDetailDtoList().get(0).isState(), projectPosition1.isState());
//
//        assertEquals(projectDto.getProjectPositionDetailDtoList().get(1).getPositionName(), projectPosition2.getPosition().getName());
//        assertEquals(projectDto.getProjectPositionDetailDtoList().get(1).getUserNo(), null);
//        assertEquals(projectDto.getProjectPositionDetailDtoList().get(1).getUserName(), null);
//        assertEquals(projectDto.getProjectPositionDetailDtoList().get(1).isState(), projectPosition2.isState());
//
//        assertEquals(projectDto.getCommentDtoList().get(0).getNo(), comment1.getNo());
//        assertEquals(projectDto.getCommentDtoList().get(0).getRegistrant(), comment1.getUser().getName());
//        assertEquals(projectDto.getCommentDtoList().get(0).getContent(), comment1.getContent());
//
//        assertEquals(projectDto.getCommentDtoList().get(1).getNo(), comment2.getNo());
//        assertEquals(projectDto.getCommentDtoList().get(1).getRegistrant(), comment2.getUser().getName());
//        assertEquals(projectDto.getCommentDtoList().get(1).getContent(), comment2.getContent());
//
//    }
//
//    @Test
//    public void 로그인_프로젝트_상세_조회_성공_테스트() {
//        // given
//        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
//        LocalDate startDate = LocalDate.of(2022, 06, 24);
//        LocalDate endDate = LocalDate.of(2022, 06, 28);
//
//        // 유저 객체
//        User user1 = User.builder()
//                .no(1L)
//                .name("testUser1")
//                .sex('M')
//                .email("testEmail1")
//                .password("testPassword1")
//                .github("testGithub1")
//                .block(false)
//                .blockReason(null)
//                .permission(Role.ROLE_USER)
//                .oauthCategory(OAuth.NORMAL)
//                .email_auth(false)
//                .imageNo(0L)
//                .position(null)
//                .build();
//
//        // 프로젝트 객체
//        Project project1 = Project.builder()
//                .no(1L)
//                .name("testName1")
//                .createUserName("user1")
//                .createDate(createDate)
//                .startDate(startDate)
//                .endDate(endDate)
//                .state(true)
//                .introduction("testIntroduction1")
//                .maxPeople(10)
//                .currentPeople(4)
//                .delete(false)
//                .deleteReason(null)
//                .imageNo(0L)
//                .viewCount(10)
//                .commentCount(10)
//                .build();
//
//        // 포지션 세팅
//        Position position1 = Position.builder()
//                .no(1L)
//                .name("testPosition1")
//                .build();
//        Position position2 = Position.builder()
//                .no(2L)
//                .name("testPosition2")
//                .build();
//
//        List<ProjectPosition> projectPositionList = new ArrayList<>();
//        ProjectPosition projectPosition1 = ProjectPosition.builder()
//                .no(1L)
//                .state(true)
//                .project(project1)
//                .position(position1)
//                .user(user1)
//                .creator(false)
//                .build();
//        ProjectPosition projectPosition2 = ProjectPosition.builder()
//                .no(2L)
//                .state(false)
//                .project(project1)
//                .position(position2)
//                .user(null)
//                .creator(false)
//                .build();
//        projectPositionList.add(projectPosition1);
//        projectPositionList.add(projectPosition2);
//
//        // 댓글 세팅
//        List<Comment> commentList = new ArrayList<>();
//        Comment comment1 = Comment.builder()
//                .no(1L)
//                .user(user1)
//                .project(project1)
//                .content("testContent1")
//                .build();
//        Comment comment2 = Comment.builder()
//                .no(2L)
//                .user(user1)
//                .project(project1)
//                .content("testContent1")
//                .build();
//        commentList.add(comment1);
//        commentList.add(comment2);
//
//        // 기술 스택 세팅
//        TechnicalStack technicalStack1 = TechnicalStack.builder()
//                .no(1L)
//                .name("testTechnicalStack1")
//                .build();
//        TechnicalStack technicalStack2 = TechnicalStack.builder()
//                .no(2L)
//                .name("testTechnicalStack2")
//                .build();
//
//        List<ProjectTechnicalStack> projectTechnicalStackList = new ArrayList<>();
//        ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
//                .no(1L)
//                .project(project1)
//                .technicalStack(technicalStack1)
//                .build();
//        ProjectTechnicalStack projectTechnicalStack2 = ProjectTechnicalStack.builder()
//                .no(2L)
//                .project(project1)
//                .technicalStack(technicalStack2)
//                .build();
//        projectTechnicalStackList.add(projectTechnicalStack1);
//        projectTechnicalStackList.add(projectTechnicalStack2);
//
//        given(projectRepository.findById(any())).willReturn(Optional.of(project1));
//        given(projectPositionRepository.findByProjectWithPositionAndProjectAndUserUsingLeftFetchJoin(any())).willReturn(projectPositionList);
//        given(commentRepository.findByProjectNo(any())).willReturn(commentList);
//        given(projectTechnicalStackRepository.findByProjectWithTechnicalStackAndProjectUsingFetchJoin(any())).willReturn(projectTechnicalStackList);
//        given(bookMarkRepository.existBookMark(any(), any())).willReturn(true);
//
//        // when
//        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user1, "", user1.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(auth);
//        ProjectDto projectDto = null;
//
//        try {
//            projectDto = projectService.getProjectDetail(1L);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        // then
//        verify(projectRepository).findById(any());
//        verify(projectPositionRepository).findByProjectWithPositionAndProjectAndUserUsingLeftFetchJoin(any());
//        verify(commentRepository).findByProjectNo(any());
//        verify(projectTechnicalStackRepository).findByProjectWithTechnicalStackAndProjectUsingFetchJoin(any());
//        verify(bookMarkRepository).existBookMark(any(), any());
//
//        assertEquals(projectDto.getName(), project1.getName());
//        assertEquals(projectDto.getProfile(), null);
//        assertEquals(projectDto.getCreateDate(), project1.getCreateDate());
//        assertEquals(projectDto.getStartDate(), project1.getStartDate());
//        assertEquals(projectDto.getEndDate(), project1.getEndDate());
//        assertEquals(projectDto.isState(), project1.isState());
//        assertEquals(projectDto.getIntroduction(), project1.getIntroduction());
//        assertEquals(projectDto.getMaxPeople(), project1.getMaxPeople());
//        assertEquals(projectDto.isBookmark(), true);
//        assertEquals(projectDto.getRegister(), project1.getCreateUserName());
//        assertEquals(projectDto.getTechnicalStack().get(0), technicalStack1.getName());
//        assertEquals(projectDto.getTechnicalStack().get(1), technicalStack2.getName());
//
//        assertEquals(projectDto.getProjectPositionDetailDtoList().get(0).getPositionName(), projectPosition1.getPosition().getName());
//        assertEquals(projectDto.getProjectPositionDetailDtoList().get(0).getUserNo(), projectPosition1.getUser().getNo());
//        assertEquals(projectDto.getProjectPositionDetailDtoList().get(0).getUserName(), projectPosition1.getUser().getName());
//        assertEquals(projectDto.getProjectPositionDetailDtoList().get(0).isState(), projectPosition1.isState());
//
//        assertEquals(projectDto.getProjectPositionDetailDtoList().get(1).getPositionName(), projectPosition2.getPosition().getName());
//        assertEquals(projectDto.getProjectPositionDetailDtoList().get(1).getUserNo(), null);
//        assertEquals(projectDto.getProjectPositionDetailDtoList().get(1).getUserName(), null);
//        assertEquals(projectDto.getProjectPositionDetailDtoList().get(1).isState(), projectPosition2.isState());
//
//        assertEquals(projectDto.getCommentDtoList().get(0).getNo(), comment1.getNo());
//        assertEquals(projectDto.getCommentDtoList().get(0).getRegistrant(), comment1.getUser().getName());
//        assertEquals(projectDto.getCommentDtoList().get(0).getContent(), comment1.getContent());
//
//        assertEquals(projectDto.getCommentDtoList().get(1).getNo(), comment2.getNo());
//        assertEquals(projectDto.getCommentDtoList().get(1).getRegistrant(), comment2.getUser().getName());
//        assertEquals(projectDto.getCommentDtoList().get(1).getContent(), comment2.getContent());
//    }
}
