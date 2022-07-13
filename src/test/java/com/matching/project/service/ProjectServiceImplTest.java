package com.matching.project.service;

import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.project.*;
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
    public void 프로젝트_등록_성공_테스트() {

        // given
        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        User user1 = User.builder()
                .no(1L)
                .name("testUser1")
                .sex('M')
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

        Project project1 = Project.builder()
                .no(1L)
                .name("testProject1")
                .createUserName("testUser1")
                .createDate(createDate)
                .startDate(startDate)
                .endDate(endDate)
                .state(true)
                .introduction("testIntroduction1")
                .maxPeople(10)
                .currentPeople(1)
                .delete(false)
                .deleteReason(null)
                .imageNo(0L)
                .viewCount(0)
                .commentCount(0)
                .user(user1)
                .build();

        List<Position> positionList = new ArrayList<>();
        Position position1 = Position.builder()
                .no(1L)
                .name("testPosition1")
                .build();
        positionList.add(position1);

        Position position2 = Position.builder()
                .no(1L)
                .name("testPosition2")
                .build();
        positionList.add(position2);


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
        technicalStackList.add(technicalStack1);

        TechnicalStack technicalStack2 = TechnicalStack.builder()
                .no(2L)
                .name("testTechnicalStack2")
                .build();
        technicalStackList.add(technicalStack2);

        ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
                .no(1L)
                .technicalStack(technicalStack1)
                .project(project1)
                .build();

        ProjectTechnicalStack projectTechnicalStack2 = ProjectTechnicalStack.builder()
                .no(1L)
                .technicalStack(technicalStack2)
                .project(project1)
                .build();

        given(projectRepository.save(any())).willReturn(project1);
        given(positionRepository.findByNameIn(any())).willReturn(positionList);
        given(projectPositionRepository.save(any())).willReturn(projectPosition1).willReturn(projectPosition2);
        given(technicalStackRepository.findByNameIn(any())).willReturn(technicalStackList);
        given(projectTechnicalStackRepository.save(any())).willReturn(projectTechnicalStack1).willReturn(projectTechnicalStack2);


        // when
        UserDetails userDetails = user1;
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        ProjectRegisterRequestDto projectRegisterRequestDto = ProjectRegisterRequestDto.builder()
                .name("testProject1")
                .profile(null)
                .createDate(createDate)
                .startDate(startDate)
                .endDate(endDate)
                .introduction("testIntroduction1")
                .maxPeople(10)
                .build();

        List<ProjectPositionDto> projectPositionDtoList = new ArrayList<>();
        ProjectPositionDto projectPositionDto1 = ProjectPositionDto.builder()
                .name("testPosition1")
                .state(true)
                .build();
        projectPositionDtoList.add(projectPositionDto1);

        ProjectPositionDto projectPositionDto2 = ProjectPositionDto.builder()
                .name("testPosition2")
                .state(false)
                .build();
        projectPositionDtoList.add(projectPositionDto2);

        projectRegisterRequestDto.setProjectPositionDtoList(projectPositionDtoList);

        List<String> projectTechnicalStack = new ArrayList<>();
        projectTechnicalStack.add("testTechnicalStack1");
        projectTechnicalStack.add("testTechnicalStack2");
        projectRegisterRequestDto.setProjectTechnicalStack(projectTechnicalStack);

        ProjectRegisterResponseDto projectRegisterResponseDto = null;
        try {
            projectRegisterResponseDto = projectService.projectRegister(projectRegisterRequestDto);
        } catch (Exception e) {
            e.printStackTrace();
        }

        verify(projectRepository,times(1)).save(any());
        verify(positionRepository, times(1)).findByNameIn(any());
        verify(projectPositionRepository, times(2)).save(any());
        verify(technicalStackRepository, times(1)).findByNameIn(any());
        verify(projectTechnicalStackRepository, times(2)).save(any());

        assertEquals(projectRegisterResponseDto.getNo(), project1.getNo());
        assertEquals(projectRegisterResponseDto.getName(), project1.getName());
        assertEquals(projectRegisterResponseDto.getCreateUser(), project1.getCreateUserName());
        assertEquals(projectRegisterResponseDto.getProfile(), null);
        assertEquals(projectRegisterResponseDto.getCreateDate(), project1.getCreateDate());
        assertEquals(projectRegisterResponseDto.getStartDate(), project1.getStartDate());
        assertEquals(projectRegisterResponseDto.getEndDate(), project1.getEndDate());
        assertEquals(projectRegisterResponseDto.isState(), project1.isState());
        assertEquals(projectRegisterResponseDto.getIntroduction(), project1.getIntroduction());
        assertEquals(projectRegisterResponseDto.getMaxPeople(), project1.getMaxPeople());
        assertEquals(projectRegisterResponseDto.getCurrentPeople(), project1.getCurrentPeople());
        assertEquals(projectRegisterResponseDto.getViewCount(), project1.getViewCount());
        assertEquals(projectRegisterResponseDto.getCommentCount(), project1.getCommentCount());

        assertEquals(projectRegisterResponseDto.getProjectPositionDtoList().get(0).getName(), projectPosition1.getPosition().getName());
        assertEquals(projectRegisterResponseDto.getProjectPositionDtoList().get(0).isState(), projectPosition1.isState());
        assertEquals(projectRegisterResponseDto.getProjectPositionDtoList().get(1).getName(), projectPosition2.getPosition().getName());
        assertEquals(projectRegisterResponseDto.getProjectPositionDtoList().get(1).isState(), projectPosition2.isState());

        assertEquals(projectRegisterResponseDto.getProjectTechnicalStack().get(0), projectTechnicalStack1.getTechnicalStack().getName());
        assertEquals(projectRegisterResponseDto.getProjectTechnicalStack().get(1), projectTechnicalStack2.getTechnicalStack().getName());
    }

    @Test
    public void 비로그인_프로젝트_목록_조회_성공_테스트() {
        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
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
                .imageNo(0L)
                .viewCount(10)
                .commentCount(10)
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
                .imageNo(0L)
                .viewCount(10)
                .commentCount(10)
                .build();

        List<Project> projectList = new ArrayList<>();
        projectList.add(project2);
        projectList.add(project1);

        // List to Page
        Pageable pageable = PageRequest.of(0, 4, Sort.by("createDate").descending());
        int start = (int)pageable.getOffset();
        int end = (start + pageable.getPageSize()) > projectList.size() ? projectList.size() : (start + pageable.getPageSize());
        Page<Project> projectPage = new PageImpl<>(projectList.subList(start, end), pageable, projectList.size());

        given(projectRepository.findByStateProjectPage(any(Boolean.class), any(Boolean.class), any(Pageable.class))).willReturn(projectPage);

        List<ProjectSimpleDto> projectSimpleDtoList = null;

        Authentication auth = new AnonymousAuthenticationToken("key", "principle", Arrays.asList(new SimpleGrantedAuthority(Role.ROLE_ANONYMOUS.toString())));
        SecurityContextHolder.getContext().setAuthentication(auth);
        try {
            projectSimpleDtoList = projectService.findProjectList(true, false, pageable);
        } catch (Exception e) {

        }

        verify(projectRepository, times(1)).findByStateProjectPage(any(Boolean.class), any(Boolean.class), any(Pageable.class));

        assertEquals(projectSimpleDtoList.size(), 2);
        assertEquals(projectSimpleDtoList.get(0).getNo(), 2L);
        assertEquals(projectSimpleDtoList.get(0).getName(), "testName2");
        assertEquals(projectSimpleDtoList.get(0).getProfile(), null);
        assertEquals(projectSimpleDtoList.get(0).getMaxPeople(), 10);
        assertEquals(projectSimpleDtoList.get(0).getCurrentPeople(), 4);
        assertEquals(projectSimpleDtoList.get(0).getViewCount(), 10);
        assertEquals(projectSimpleDtoList.get(0).getCommentCount(), 10);
        assertEquals(projectSimpleDtoList.get(0).getRegister(), "user1");
        assertEquals(projectSimpleDtoList.get(0).isBookMark(), false);

        assertEquals(projectSimpleDtoList.get(1).getNo(), 1L);
        assertEquals(projectSimpleDtoList.get(1).getName(), "testName1");
        assertEquals(projectSimpleDtoList.get(1).getProfile(), null);
        assertEquals(projectSimpleDtoList.get(1).getMaxPeople(), 10);
        assertEquals(projectSimpleDtoList.get(1).getCurrentPeople(), 4);
        assertEquals(projectSimpleDtoList.get(1).getViewCount(), 10);
        assertEquals(projectSimpleDtoList.get(1).getCommentCount(), 10);
        assertEquals(projectSimpleDtoList.get(1).getRegister(), "user1");
        assertEquals(projectSimpleDtoList.get(1).isBookMark(), false);
    }

    @Test
    public void 로그인_프로젝트_목록_조회_성공_테스트() {
        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);
        
        // 유저 객체
        User user1 = User.builder()
                .no(1L)
                .name("testUser1")
                .sex('M')
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
                .imageNo(0L)
                .viewCount(10)
                .commentCount(10)
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
                .imageNo(0L)
                .viewCount(10)
                .commentCount(10)
                .build();

        List<Project> projectList = new ArrayList<>();
        projectList.add(project2);
        projectList.add(project1);

        List<BookMark> bookMarkList = new ArrayList<>();
        BookMark bookMark1 = BookMark.builder()
                .no(1L)
                .user(user1)
                .project(project1)
                .build();
        bookMarkList.add(bookMark1);

        // List to Page
        Pageable pageable = PageRequest.of(0, 4, Sort.by("createDate").descending());
        int start = (int)pageable.getOffset();
        int end = (start + pageable.getPageSize()) > projectList.size() ? projectList.size() : (start + pageable.getPageSize());
        Page<Project> projectPage = new PageImpl<>(projectList.subList(start, end), pageable, projectList.size());

        given(projectRepository.findByStateProjectPage(any(Boolean.class), any(Boolean.class), any(Pageable.class))).willReturn(projectPage);
        given(bookMarkRepository.findByUserNo(any())).willReturn(bookMarkList);

        List<ProjectSimpleDto> projectSimpleDtoList = null;

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user1, "", user1.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        try {
            projectSimpleDtoList = projectService.findProjectList(true, false, pageable);
        } catch (Exception e) {

        }

        verify(projectRepository, times(1)).findByStateProjectPage(any(Boolean.class), any(Boolean.class), any(Pageable.class));

        assertEquals(projectSimpleDtoList.size(), 2);
        assertEquals(projectSimpleDtoList.get(0).getNo(), 2L);
        assertEquals(projectSimpleDtoList.get(0).getName(), "testName2");
        assertEquals(projectSimpleDtoList.get(0).getProfile(), null);
        assertEquals(projectSimpleDtoList.get(0).getMaxPeople(), 10);
        assertEquals(projectSimpleDtoList.get(0).getCurrentPeople(), 4);
        assertEquals(projectSimpleDtoList.get(0).getViewCount(), 10);
        assertEquals(projectSimpleDtoList.get(0).getCommentCount(), 10);
        assertEquals(projectSimpleDtoList.get(0).getRegister(), "user1");
        assertEquals(projectSimpleDtoList.get(0).isBookMark(), false);

        assertEquals(projectSimpleDtoList.get(1).getNo(), 1L);
        assertEquals(projectSimpleDtoList.get(1).getName(), "testName1");
        assertEquals(projectSimpleDtoList.get(1).getProfile(), null);
        assertEquals(projectSimpleDtoList.get(1).getMaxPeople(), 10);
        assertEquals(projectSimpleDtoList.get(1).getCurrentPeople(), 4);
        assertEquals(projectSimpleDtoList.get(1).getViewCount(), 10);
        assertEquals(projectSimpleDtoList.get(1).getCommentCount(), 10);
        assertEquals(projectSimpleDtoList.get(1).getRegister(), "user1");
        assertEquals(projectSimpleDtoList.get(1).isBookMark(), true);
    }

    @Test
    public void 비로그인_프로젝트_상세_조회_성공_테스트() {
        // given
        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        // 유저 객체
        User user1 = User.builder()
                .no(1L)
                .name("testUser1")
                .sex('M')
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
                .imageNo(0L)
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
        
        // 댓글 세팅
        List<Comment> commentList = new ArrayList<>();
        Comment comment1 = Comment.builder()
                .no(1L)
                .user(user1)
                .project(project1)
                .content("testContent1")
                .build();
        Comment comment2 = Comment.builder()
                .no(2L)
                .user(user1)
                .project(project1)
                .content("testContent1")
                .build();
        commentList.add(comment1);
        commentList.add(comment2);
        
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
        given(projectPositionRepository.findByProjectWithPositionAndProjectAndUserUsingLeftFetchJoin(any())).willReturn(projectPositionList);
        given(commentRepository.findByProjectNo(any())).willReturn(commentList);
        given(projectTechnicalStackRepository.findByProjectWithTechnicalStackAndProjectUsingFetchJoin(any())).willReturn(projectTechnicalStackList);

        // when
        Authentication auth = new AnonymousAuthenticationToken("key", "principle", Arrays.asList(new SimpleGrantedAuthority(Role.ROLE_ANONYMOUS.toString())));
        SecurityContextHolder.getContext().setAuthentication(auth);
        ProjectDto projectDto = null;

        try {
            projectDto = projectService.getProjectDetail(1L);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // then
        verify(projectRepository).findById(any());
        verify(projectPositionRepository).findByProjectWithPositionAndProjectAndUserUsingLeftFetchJoin(any());
        verify(commentRepository).findByProjectNo(any());
        verify(projectTechnicalStackRepository).findByProjectWithTechnicalStackAndProjectUsingFetchJoin(any());

        assertEquals(projectDto.getName(), project1.getName());
        assertEquals(projectDto.getProfile(), null);
        assertEquals(projectDto.getCreateDate(), project1.getCreateDate());
        assertEquals(projectDto.getStartDate(), project1.getStartDate());
        assertEquals(projectDto.getEndDate(), project1.getEndDate());
        assertEquals(projectDto.isState(), project1.isState());
        assertEquals(projectDto.getIntroduction(), project1.getIntroduction());
        assertEquals(projectDto.getMaxPeople(), project1.getMaxPeople());
        assertEquals(projectDto.isBookmark(), false);
        assertEquals(projectDto.getRegister(), project1.getCreateUserName());
        assertEquals(projectDto.getTechnicalStack().get(0), technicalStack1.getName());
        assertEquals(projectDto.getTechnicalStack().get(1), technicalStack2.getName());

        assertEquals(projectDto.getProjectPositionDetailDtoList().get(0).getNo(), projectPosition1.getNo());
        assertEquals(projectDto.getProjectPositionDetailDtoList().get(0).getPositionName(), projectPosition1.getPosition().getName());
        assertEquals(projectDto.getProjectPositionDetailDtoList().get(0).getUserDetailDto().getNo(), projectPosition1.getUser().getNo());
        assertEquals(projectDto.getProjectPositionDetailDtoList().get(0).getUserDetailDto().getUserName(), projectPosition1.getUser().getName());
        assertEquals(projectDto.getProjectPositionDetailDtoList().get(0).isState(), projectPosition1.isState());

        assertEquals(projectDto.getProjectPositionDetailDtoList().get(1).getNo(), projectPosition2.getNo());
        assertEquals(projectDto.getProjectPositionDetailDtoList().get(1).getPositionName(), projectPosition2.getPosition().getName());
        assertEquals(projectDto.getProjectPositionDetailDtoList().get(1).getUserDetailDto(), null);
        assertEquals(projectDto.getProjectPositionDetailDtoList().get(1).isState(), projectPosition2.isState());

        assertEquals(projectDto.getCommentDtoList().get(0).getNo(), comment1.getNo());
        assertEquals(projectDto.getCommentDtoList().get(0).getRegistrant(), comment1.getUser().getName());
        assertEquals(projectDto.getCommentDtoList().get(0).getContent(), comment1.getContent());

        assertEquals(projectDto.getCommentDtoList().get(1).getNo(), comment2.getNo());
        assertEquals(projectDto.getCommentDtoList().get(1).getRegistrant(), comment2.getUser().getName());
        assertEquals(projectDto.getCommentDtoList().get(1).getContent(), comment2.getContent());

    }

    @Test
    public void 로그인_프로젝트_상세_조회_성공_테스트() {
        // given
        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        // 유저 객체
        User user1 = User.builder()
                .no(1L)
                .name("testUser1")
                .sex('M')
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
                .imageNo(0L)
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

        // 댓글 세팅
        List<Comment> commentList = new ArrayList<>();
        Comment comment1 = Comment.builder()
                .no(1L)
                .user(user1)
                .project(project1)
                .content("testContent1")
                .build();
        Comment comment2 = Comment.builder()
                .no(2L)
                .user(user1)
                .project(project1)
                .content("testContent1")
                .build();
        commentList.add(comment1);
        commentList.add(comment2);

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
        given(projectPositionRepository.findByProjectWithPositionAndProjectAndUserUsingLeftFetchJoin(any())).willReturn(projectPositionList);
        given(commentRepository.findByProjectNo(any())).willReturn(commentList);
        given(projectTechnicalStackRepository.findByProjectWithTechnicalStackAndProjectUsingFetchJoin(any())).willReturn(projectTechnicalStackList);
        given(bookMarkRepository.existBookMark(any(), any())).willReturn(true);

        // when
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user1, "", user1.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        ProjectDto projectDto = null;

        try {
            projectDto = projectService.getProjectDetail(1L);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // then
        verify(projectRepository).findById(any());
        verify(projectPositionRepository).findByProjectWithPositionAndProjectAndUserUsingLeftFetchJoin(any());
        verify(commentRepository).findByProjectNo(any());
        verify(projectTechnicalStackRepository).findByProjectWithTechnicalStackAndProjectUsingFetchJoin(any());
        verify(bookMarkRepository).existBookMark(any(), any());

        assertEquals(projectDto.getName(), project1.getName());
        assertEquals(projectDto.getProfile(), null);
        assertEquals(projectDto.getCreateDate(), project1.getCreateDate());
        assertEquals(projectDto.getStartDate(), project1.getStartDate());
        assertEquals(projectDto.getEndDate(), project1.getEndDate());
        assertEquals(projectDto.isState(), project1.isState());
        assertEquals(projectDto.getIntroduction(), project1.getIntroduction());
        assertEquals(projectDto.getMaxPeople(), project1.getMaxPeople());
        assertEquals(projectDto.isBookmark(), true);
        assertEquals(projectDto.getRegister(), project1.getCreateUserName());
        assertEquals(projectDto.getTechnicalStack().get(0), technicalStack1.getName());
        assertEquals(projectDto.getTechnicalStack().get(1), technicalStack2.getName());

        assertEquals(projectDto.getProjectPositionDetailDtoList().get(0).getNo(), projectPosition1.getNo());
        assertEquals(projectDto.getProjectPositionDetailDtoList().get(0).getPositionName(), projectPosition1.getPosition().getName());
        assertEquals(projectDto.getProjectPositionDetailDtoList().get(0).getUserDetailDto().getNo(), projectPosition1.getUser().getNo());
        assertEquals(projectDto.getProjectPositionDetailDtoList().get(0).getUserDetailDto().getUserName(), projectPosition1.getUser().getName());
        assertEquals(projectDto.getProjectPositionDetailDtoList().get(0).isState(), projectPosition1.isState());

        assertEquals(projectDto.getProjectPositionDetailDtoList().get(1).getNo(), projectPosition2.getNo());
        assertEquals(projectDto.getProjectPositionDetailDtoList().get(1).getPositionName(), projectPosition2.getPosition().getName());
        assertEquals(projectDto.getProjectPositionDetailDtoList().get(1).getUserDetailDto(), null);
        assertEquals(projectDto.getProjectPositionDetailDtoList().get(1).isState(), projectPosition2.isState());

        assertEquals(projectDto.getCommentDtoList().get(0).getNo(), comment1.getNo());
        assertEquals(projectDto.getCommentDtoList().get(0).getRegistrant(), comment1.getUser().getName());
        assertEquals(projectDto.getCommentDtoList().get(0).getContent(), comment1.getContent());

        assertEquals(projectDto.getCommentDtoList().get(1).getNo(), comment2.getNo());
        assertEquals(projectDto.getCommentDtoList().get(1).getRegistrant(), comment2.getUser().getName());
        assertEquals(projectDto.getCommentDtoList().get(1).getContent(), comment2.getContent());
    }

    @Test
    public void 프로젝트_수정_성공_테스트() {
        // given
        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        // 유저 객체
        User user1 = User.builder()
                .no(1L)
                .name("testUser1")
                .sex('M')
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
                .createUserName("testUser1")
                .createDate(createDate)
                .startDate(startDate)
                .endDate(endDate)
                .state(true)
                .introduction("testIntroduction1")
                .maxPeople(10)
                .currentPeople(4)
                .delete(false)
                .deleteReason(null)
                .imageNo(0L)
                .viewCount(10)
                .commentCount(10)
                .user(user1)
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
        Position position3 = Position.builder()
                .no(3L)
                .name("updatePosition1")
                .build();
        Position position4 = Position.builder()
                .no(4L)
                .name("updatePosition2")
                .build();
        positionList.add(position1);
        positionList.add(position2);
        positionList.add(position3);
        positionList.add(position4);

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
        TechnicalStack technicalStack3 = TechnicalStack.builder()
                .no(3L)
                .name("updateTechnicalStack1")
                .build();
        TechnicalStack technicalStack4 = TechnicalStack.builder()
                .no(4L)
                .name("updateTechnicalStack2")
                .build();
        technicalStackList.add(technicalStack1);
        technicalStackList.add(technicalStack2);
        technicalStackList.add(technicalStack3);
        technicalStackList.add(technicalStack4);

        given(projectRepository.findById(any())).willReturn(Optional.of(project1));
        given(projectRepository.existProject(any(), any())).willReturn(true);
        given(positionRepository.findByNameIn(any())).willReturn(positionList);
        given(technicalStackRepository.findByNameIn(any())).willReturn(technicalStackList);

        // when
        ProjectUpdateRequestDto projectUpdateRequestDto1 = ProjectUpdateRequestDto.builder()
                .name("updateName1")
                .startDate(startDate.plusDays(1))
                .endDate(endDate.plusDays(1))
                .introduction("updateIntroduction1")
                .maxPeople(4)
                .build();

        List<ProjectPositionUpdateDto> projectPositionDtoList = new ArrayList<>();
        UserUpdateDto userUpdateDto = new UserUpdateDto(1L, "testUser1");
        ProjectPositionUpdateDto projectPositionUpdateDto1 = ProjectPositionUpdateDto.builder()
                .no(1L)
                .name("testPosition1")
                .userUpdateDto(userUpdateDto)
                .build();
        ProjectPositionUpdateDto projectPositionUpdateDto2 = ProjectPositionUpdateDto.builder()
                .no(2L)
                .name("updatePosition2")
                .userUpdateDto(null)
                .build();
        projectPositionDtoList.add(projectPositionUpdateDto1);
        projectPositionDtoList.add(projectPositionUpdateDto2);

        List<String> projectTechnicalStack = new ArrayList<>();
        projectTechnicalStack.add("updateTechnicalStack1");
        projectTechnicalStack.add("updateTechnicalStack2");

        projectUpdateRequestDto1.setProjectPositionDtoList(projectPositionDtoList);
        projectUpdateRequestDto1.setProjectTechnicalStack(projectTechnicalStack);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user1, "", user1.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        ProjectUpdateResponseDto projectUpdateResponseDto = null;
        try {
            projectUpdateResponseDto = projectService.updateProject(project1.getNo(), projectUpdateRequestDto1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // then
        verify(projectRepository).findById(any());
        verify(projectRepository).existProject(any(), any());
        verify(positionRepository).findByNameIn(any());
        verify(projectPositionRepository).deleteByProjectAndUserIsNull(any());
        verify(projectPositionRepository, times(1)).save(any());
        verify(technicalStackRepository).findByNameIn(any());
        verify(projectTechnicalStackRepository).deleteByProject(any());
        verify(projectTechnicalStackRepository, times(2)).save(any());

        assertEquals(projectUpdateResponseDto.getNo(), project1.getNo());
        assertEquals(projectUpdateResponseDto.getName(), projectUpdateRequestDto1.getName());
        assertEquals(projectUpdateResponseDto.getCreateUser(), user1.getName());
        assertEquals(projectUpdateResponseDto.getProfile(), null);
        assertEquals(projectUpdateResponseDto.getCreateDate(), project1.getCreateDate());
        assertEquals(projectUpdateResponseDto.getStartDate(), projectUpdateRequestDto1.getStartDate());
        assertEquals(projectUpdateResponseDto.getEndDate(), projectUpdateRequestDto1.getEndDate());
        assertEquals(projectUpdateResponseDto.isState(), project1.isState());
        assertEquals(projectUpdateResponseDto.getIntroduction(), projectUpdateRequestDto1.getIntroduction());
        assertEquals(projectUpdateResponseDto.getMaxPeople(), projectUpdateRequestDto1.getMaxPeople());
        assertEquals(projectUpdateResponseDto.getCurrentPeople(), project1.getCurrentPeople());
        assertEquals(projectUpdateResponseDto.getViewCount(), project1.getViewCount());
        assertEquals(projectUpdateResponseDto.getCommentCount(), project1.getCommentCount());


        assertEquals(projectUpdateResponseDto.getProjectPositionDtoList().get(0).getNo(), projectPositionDtoList.get(0).getNo());
        assertEquals(projectUpdateResponseDto.getProjectPositionDtoList().get(0).getName(), projectPositionDtoList.get(0).getName());
        assertEquals(projectUpdateResponseDto.getProjectPositionDtoList().get(0).getUserUpdateDto(), userUpdateDto);
        assertEquals(projectUpdateResponseDto.getProjectPositionDtoList().get(1).getNo(), projectPositionDtoList.get(1).getNo());
        assertEquals(projectUpdateResponseDto.getProjectPositionDtoList().get(1).getName(), projectPositionDtoList.get(1).getName());
        assertEquals(projectUpdateResponseDto.getProjectPositionDtoList().get(1).getUserUpdateDto(), null);

        assertEquals(projectUpdateResponseDto.getProjectTechnicalStack().get(0), projectTechnicalStack.get(0));
        assertEquals(projectUpdateResponseDto.getProjectTechnicalStack().get(1), projectTechnicalStack.get(1));
    }
}
