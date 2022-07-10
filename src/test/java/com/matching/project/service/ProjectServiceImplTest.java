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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @InjectMocks
    ProjectServiceImpl projectService;

    @Test
    public void 프로젝트_등록_성공_테스트() {

        // given
        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
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
                .imageNo(0L)
                .viewCount(0)
                .commentCount(0)
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
        assertEquals(projectRegisterResponseDto.getCreateUser(), user1.getName());
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
    public void 비로그인_프로젝트_모집중_목록_조회_성공_테스트() {
//        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
//        LocalDate startDate = LocalDate.of(2022, 06, 24);
//        LocalDate endDate = LocalDate.of(2022, 06, 28);
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
//                .viewCount(10)
//                .commentCount(10)
//                .image(null)
//                .build();
//
//        Project project2 = Project.builder()
//                .no(2L)
//                .name("testName2")
//                .createUserName("user1")
//                .createDate(createDate)
//                .startDate(startDate)
//                .endDate(endDate)
//                .state(true)
//                .introduction("testIntroduction2")
//                .maxPeople(10)
//                .currentPeople(4)
//                .delete(false)
//                .deleteReason(null)
//                .viewCount(10)
//                .commentCount(10)
//                .image(null)
//                .build();
//
//        List<Project> projectList = new ArrayList<>();
//        projectList.add(project2);
//        projectList.add(project1);
//
//        // List to Page
//        Pageable pageable = PageRequest.of(0, 4, Sort.by("no").descending());
//        int start = (int)pageable.getOffset();
//        int end = (start + pageable.getPageSize()) > projectList.size() ? projectList.size() : (start + pageable.getPageSize());
//        Page<Project> projectPage = new PageImpl<>(projectList.subList(start, end), pageable, projectList.size());
//
//        given(projectRepository.findByStateProjectPage(any(Boolean.class), any(Boolean.class), any(Pageable.class))).willReturn(projectPage);
//
//        List<NoneLoginProjectSimpleDto> projectSimpleDtoList = null;
//
//        try {
//            projectSimpleDtoList = projectService.NoneLoginProjectRecruitingList(pageable);
//        } catch (Exception e) {
//
//        }
//
//        verify(projectRepository, times(1)).findByStateProjectPage(any(Boolean.class), any(Boolean.class), any(Pageable.class));
//
//        assertEquals(projectSimpleDtoList.size(), 2);
//        assertEquals(projectSimpleDtoList.get(0).getNo(), 2L);
//        assertEquals(projectSimpleDtoList.get(0).getName(), "testName2");
//        assertEquals(projectSimpleDtoList.get(0).getProfile(), null);
//        assertEquals(projectSimpleDtoList.get(0).getMaxPeople(), 10);
//        assertEquals(projectSimpleDtoList.get(0).getCurrentPeople(), 4);
//        assertEquals(projectSimpleDtoList.get(0).getViewCount(), 10);
//        assertEquals(projectSimpleDtoList.get(0).getCommentCount(), 10);
//        assertEquals(projectSimpleDtoList.get(0).getRegister(), "user1");
//
//        assertEquals(projectSimpleDtoList.get(1).getNo(), 1L);
//        assertEquals(projectSimpleDtoList.get(1).getName(), "testName1");
//        assertEquals(projectSimpleDtoList.get(1).getProfile(), null);
//        assertEquals(projectSimpleDtoList.get(1).getMaxPeople(), 10);
//        assertEquals(projectSimpleDtoList.get(1).getCurrentPeople(), 4);
//        assertEquals(projectSimpleDtoList.get(1).getViewCount(), 10);
//        assertEquals(projectSimpleDtoList.get(1).getCommentCount(), 10);
//        assertEquals(projectSimpleDtoList.get(1).getRegister(), "user1");
    }


    @Test
    public void 로그인_프로젝트_모집중_목록_조회_성공_테스트() {
//        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
//        LocalDate startDate = LocalDate.of(2022, 06, 24);
//        LocalDate endDate = LocalDate.of(2022, 06, 28);
//
//        // 유저 객체
//        User user = User.builder()
//                .no(1L)
//                .name("testName")
//                .sex('M')
//                .email("testEmail")
//                .github("testGithub")
//                .selfIntroduction("testSelfIntroduction")
//                .block(false)
//                .blockReason(null)
//                .oauthCategory(OAuth.NORMAL)
//                .permission(Role.ROLE_USER)
//                .image(null)
//                .userPosition(null)
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
//                .viewCount(10)
//                .commentCount(10)
//                .image(null)
//                .build();
//
//        Project project2 = Project.builder()
//                .no(2L)
//                .name("testName2")
//                .createUserName("user1")
//                .createDate(createDate)
//                .startDate(startDate)
//                .endDate(endDate)
//                .state(true)
//                .introduction("testIntroduction2")
//                .maxPeople(10)
//                .currentPeople(4)
//                .delete(false)
//                .deleteReason(null)
//                .viewCount(10)
//                .commentCount(10)
//                .image(null)
//                .build();
//
//        List<Project> projectList = new ArrayList<>();
//        projectList.add(project2);
//        projectList.add(project1);
//
//
//        // List to Page
//        Pageable pageable = PageRequest.of(0, 4, Sort.by("no").descending());
//        int start = (int)pageable.getOffset();
//        int end = (start + pageable.getPageSize()) > projectList.size() ? projectList.size() : (start + pageable.getPageSize());
//        Page<Project> projectPage = new PageImpl<>(projectList.subList(start, end), pageable, projectList.size());
//
//        List<BookMark> bookMarkList = new ArrayList<>();
//
//        BookMark bookMark = BookMark.builder()
//                .no(1L)
//                .user(user)
//                .project(project1)
//                .build();
//        bookMarkList.add(bookMark);
//
//        UserDetails userDetails = user;
//
//        // 로그인한 유저 securityContext set
//        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(auth);
//
//        given(bookMarkRepository.findByUserNo(any(Long.class))).willReturn(bookMarkList);
//        given(projectRepository.findByStateProjectPage(any(Boolean.class), any(Boolean.class), any(Pageable.class))).willReturn(projectPage);
//
//        List<LoginProjectSimpleDto> projectSimpleDtoList = null;
//
//        try {
//            projectSimpleDtoList = projectService.LoginProjectRecruitingList(pageable);
//        } catch (Exception e) {
//
//        }
//
//        verify(bookMarkRepository, times(1)).findByUserNo(any(Long.class));
//        verify(projectRepository, times(1)).findByStateProjectPage(any(Boolean.class), any(Boolean.class), any(Pageable.class));
//
//        assertEquals(projectSimpleDtoList.size(), 2);
//        assertEquals(projectSimpleDtoList.get(0).getNo(), 2L);
//        assertEquals(projectSimpleDtoList.get(0).getName(), "testName2");
//        assertEquals(projectSimpleDtoList.get(0).getProfile(), null);
//        assertEquals(projectSimpleDtoList.get(0).getMaxPeople(), 10);
//        assertEquals(projectSimpleDtoList.get(0).getCurrentPeople(), 4);
//        assertEquals(projectSimpleDtoList.get(0).isBookMark(), false);
//        assertEquals(projectSimpleDtoList.get(0).getViewCount(), 10);
//        assertEquals(projectSimpleDtoList.get(0).getCommentCount(), 10);
//        assertEquals(projectSimpleDtoList.get(0).getRegister(), "user1");
//
//        assertEquals(projectSimpleDtoList.get(1).getNo(), 1L);
//        assertEquals(projectSimpleDtoList.get(1).getName(), "testName1");
//        assertEquals(projectSimpleDtoList.get(1).getProfile(), null);
//        assertEquals(projectSimpleDtoList.get(1).getMaxPeople(), 10);
//        assertEquals(projectSimpleDtoList.get(1).getCurrentPeople(), 4);
//        assertEquals(projectSimpleDtoList.get(1).isBookMark(), true);
//        assertEquals(projectSimpleDtoList.get(1).getViewCount(), 10);
//        assertEquals(projectSimpleDtoList.get(1).getCommentCount(), 10);
//        assertEquals(projectSimpleDtoList.get(1).getRegister(), "user1");
    }

    @Test
    public void 비로그인_프로젝트_상세_조회_성공_테스트() {
        // given
//        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
//        LocalDate startDate = LocalDate.of(2022, 06, 24);
//        LocalDate endDate = LocalDate.of(2022, 06, 28);
//
//        // projectQueryDto 객체 생성
//        ProjectQueryDto projectQueryDto = new ProjectQueryDto(1L, "testProjectName1", "testCreateUserName1", createDate, startDate, endDate, true, "testIntroduction1", 10, 4, false, "deleteReasonTest", 10, 10);
//
//        // projectPositionDto 객체 생성
//        ProjectPositionQueryDto frontPosition = new ProjectPositionQueryDto(1L, Position.FRONTEND, false);
//        ProjectPositionQueryDto backPosition = new ProjectPositionQueryDto(2L, Position.BACKEND, false);
//
//        // ProjectTechnicalStackQueryDto 객체 생성
//        ProjectTechnicalStackQueryDto frontTechnicalStack1 = new ProjectTechnicalStackQueryDto(1L, 1L, "frontTechnicalStack1");
//        ProjectTechnicalStackQueryDto frontTechnicalStack2 = new ProjectTechnicalStackQueryDto(1L, 2L, "frontTechnicalStack2");
//        frontPosition.getProjectTechnicalStackList().add(frontTechnicalStack1);
//        frontPosition.getProjectTechnicalStackList().add(frontTechnicalStack2);
//
//        ProjectTechnicalStackQueryDto backTechnicalStack1 = new ProjectTechnicalStackQueryDto(1L, 3L, "backTechnicalStack1");
//        ProjectTechnicalStackQueryDto backTechnicalStack2 = new ProjectTechnicalStackQueryDto(1L, 4L, "backTechnicalStack2");
//        backPosition.getProjectTechnicalStackList().add(backTechnicalStack1);
//        backPosition.getProjectTechnicalStackList().add(backTechnicalStack2);
//
//        projectQueryDto.getProjectPositionList().add(frontPosition);
//        projectQueryDto.getProjectPositionList().add(backPosition);
//
//
//        // 프로젝트에 참가중인 유저
//        User user1 = User.builder()
//                .no(1L)
//                .name("testUserName1")
//                .sex('M')
//                .email("testEmail1")
//                .password("testPassword")
//                .github("testGithub")
//                .selfIntroduction("testSelfIntroduction")
//                .block(false)
//                .blockReason(null)
//                .oauthCategory(OAuth.NORMAL)
//                .permission(Role.ROLE_USER)
//                .image(null)
//                .userPosition(null)
//                .build();
//
//        User user2 = User.builder()
//                .no(2L)
//                .name("testUserName2")
//                .sex('M')
//                .email("testEmail2")
//                .password("testPassword")
//                .github("testGithub")
//                .selfIntroduction("testSelfIntroduction")
//                .block(false)
//                .blockReason(null)
//                .oauthCategory(OAuth.NORMAL)
//                .permission(Role.ROLE_USER)
//                .image(null)
//                .userPosition(null)
//                .build();
//
//        Project project1 = Project.builder()
//                .no(1L)
//                .name("testProjectName1")
//                .createUserName("testCreateUserName1")
//                .createDate(createDate)
//                .startDate(startDate)
//                .endDate(endDate)
//                .state(true)
//                .introduction("testIntroduction1")
//                .maxPeople(10)
//                .currentPeople(4)
//                .delete(false)
//                .deleteReason(null)
//                .viewCount(10)
//                .commentCount(10)
//                .image(null)
//                .build();
//
//        ProjectUser projectUser1 = ProjectUser.builder()
//                .no(1L)
//                .projectPosition(Position.FRONTEND)
//                .user(user1)
//                .creator(true)
//                .project(project1).build();
//        ProjectUser projectUser2 = ProjectUser.builder()
//                .no(2L)
//                .projectPosition(Position.BACKEND)
//                .user(user2)
//                .creator(false)
//                .project(project1).build();
//        List<ProjectUser> projectUserList = new ArrayList<>();
//        projectUserList.add(projectUser1);
//        projectUserList.add(projectUser2);
//
//        // 프로젝트 댓글
//        Comment comment1 = Comment.builder()
//                .no(1L)
//                .user(user1)
//                .project(project1)
//                .content("testComment1")
//                .build();
//
//        Comment comment2 = Comment.builder()
//                .no(2L)
//                .user(user1)
//                .project(project1)
//                .content("testComment2")
//                .build();
//
//        List<Comment> commentList = new ArrayList<>();
//        commentList.add(comment1);
//        commentList.add(comment2);
//
//        given(projectRepository.findDetailProject(any(Long.class))).willReturn(projectQueryDto);
//        given(projectUserRepository.findByProjectNo(any(Long.class))).willReturn(projectUserList);
//        given(commentRepository.findByProjectNo(any(Long.class))).willReturn(commentList);
//
//        // when
//        ProjectDto projectDetail = projectService.getProjectDetail(1L);
//
//        // then
//        assertEquals(projectDetail.getName(), project1.getName());
//        assertEquals(projectDetail.getProfile(), null);
//        assertEquals(projectDetail.getCreateDate(), project1.getCreateDate());
//        assertEquals(projectDetail.getStartDate(), project1.getStartDate());
//        assertEquals(projectDetail.getEndDate(), project1.getEndDate());
//        assertEquals(projectDetail.isState(), project1.isState());
//        assertEquals(projectDetail.getIntroduction(), project1.getIntroduction());
//        assertEquals(projectDetail.getMaxPeople(), project1.getMaxPeople());
//        assertEquals(projectDetail.isBookmark(), false);
//        assertEquals(projectDetail.getRegister(), project1.getCreateUserName());
//
//        assertEquals(projectDetail.getUserSimpleInfoDtoList().get(0).getNo(), user1.getNo());
//        assertEquals(projectDetail.getUserSimpleInfoDtoList().get(0).getProfile(), null);
//        assertEquals(projectDetail.getUserSimpleInfoDtoList().get(0).getName(), user1.getName());
//        assertEquals(projectDetail.getUserSimpleInfoDtoList().get(0).getProjectPosition(), projectUser1.getProjectPosition());
//        assertEquals(projectDetail.getUserSimpleInfoDtoList().get(0).isCreator(), projectUser1.isCreator());
//
//        assertEquals(projectDetail.getUserSimpleInfoDtoList().get(1).getNo(), user2.getNo());
//        assertEquals(projectDetail.getUserSimpleInfoDtoList().get(1).getProfile(), null);
//        assertEquals(projectDetail.getUserSimpleInfoDtoList().get(1).getName(), user2.getName());
//        assertEquals(projectDetail.getUserSimpleInfoDtoList().get(1).getProjectPosition(), projectUser2.getProjectPosition());
//        assertEquals(projectDetail.getUserSimpleInfoDtoList().get(1).isCreator(), projectUser2.isCreator());
//
//
//        assertEquals(projectDetail.getProjectPosition().get(0).getPosition(), frontPosition.getPosition());
//        assertEquals(projectDetail.getProjectPosition().get(0).getTechnicalStack().get(0), frontPosition.getProjectTechnicalStackList().get(0).getName());
//        assertEquals(projectDetail.getProjectPosition().get(0).getTechnicalStack().get(1), frontPosition.getProjectTechnicalStackList().get(1).getName());
//
//        assertEquals(projectDetail.getProjectPosition().get(1).getPosition(), backPosition.getPosition());
//        assertEquals(projectDetail.getProjectPosition().get(1).getTechnicalStack().get(0), backPosition.getProjectTechnicalStackList().get(0).getName());
//        assertEquals(projectDetail.getProjectPosition().get(1).getTechnicalStack().get(1), backPosition.getProjectTechnicalStackList().get(1).getName());
//
//
//        assertEquals(projectDetail.getCommentDtoList().get(0).getNo(), comment1.getNo());
//        assertEquals(projectDetail.getCommentDtoList().get(0).getRegistrant(), user1.getName());
//        assertEquals(projectDetail.getCommentDtoList().get(0).getContent(), comment1.getContent());
//
//        assertEquals(projectDetail.getCommentDtoList().get(1).getNo(), comment2.getNo());
//        assertEquals(projectDetail.getCommentDtoList().get(1).getRegistrant(), user1.getName());
//        assertEquals(projectDetail.getCommentDtoList().get(1).getContent(), comment2.getContent());

    }

    @Test
    public void 로그인_프로젝트_상세_조회_성공_테스트() {
//        // given
//        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
//        LocalDate startDate = LocalDate.of(2022, 06, 24);
//        LocalDate endDate = LocalDate.of(2022, 06, 28);
//
//        // projectQueryDto 객체 생성
//        ProjectQueryDto projectQueryDto = new ProjectQueryDto(1L, "testProjectName1", "testCreateUserName1", createDate, startDate, endDate, true, "testIntroduction1", 10, 4, false, "deleteReasonTest", 10, 10);
//
//        // projectPositionDto 객체 생성
//        ProjectPositionQueryDto frontPosition = new ProjectPositionQueryDto(1L, Position.FRONTEND, false);
//        ProjectPositionQueryDto backPosition = new ProjectPositionQueryDto(2L, Position.BACKEND, false);
//
//        // ProjectTechnicalStackQueryDto 객체 생성
//        ProjectTechnicalStackQueryDto frontTechnicalStack1 = new ProjectTechnicalStackQueryDto(1L, 1L, "frontTechnicalStack1");
//        ProjectTechnicalStackQueryDto frontTechnicalStack2 = new ProjectTechnicalStackQueryDto(1L, 2L, "frontTechnicalStack2");
//        frontPosition.getProjectTechnicalStackList().add(frontTechnicalStack1);
//        frontPosition.getProjectTechnicalStackList().add(frontTechnicalStack2);
//
//        ProjectTechnicalStackQueryDto backTechnicalStack1 = new ProjectTechnicalStackQueryDto(1L, 3L, "backTechnicalStack1");
//        ProjectTechnicalStackQueryDto backTechnicalStack2 = new ProjectTechnicalStackQueryDto(1L, 4L, "backTechnicalStack2");
//        backPosition.getProjectTechnicalStackList().add(backTechnicalStack1);
//        backPosition.getProjectTechnicalStackList().add(backTechnicalStack2);
//
//        projectQueryDto.getProjectPositionList().add(frontPosition);
//        projectQueryDto.getProjectPositionList().add(backPosition);
//
//
//        // 프로젝트에 참가중인 유저
//        User user1 = User.builder()
//                .no(1L)
//                .name("testUserName1")
//                .sex('M')
//                .email("testEmail1")
//                .password("testPassword")
//                .github("testGithub")
//                .selfIntroduction("testSelfIntroduction")
//                .block(false)
//                .blockReason(null)
//                .oauthCategory(OAuth.NORMAL)
//                .permission(Role.ROLE_USER)
//                .image(null)
//                .userPosition(null)
//                .build();
//
//        User user2 = User.builder()
//                .no(2L)
//                .name("testUserName2")
//                .sex('M')
//                .email("testEmail2")
//                .password("testPassword")
//                .github("testGithub")
//                .selfIntroduction("testSelfIntroduction")
//                .block(false)
//                .blockReason(null)
//                .oauthCategory(OAuth.NORMAL)
//                .permission(Role.ROLE_USER)
//                .image(null)
//                .userPosition(null)
//                .build();
//
//        Project project1 = Project.builder()
//                .no(1L)
//                .name("testProjectName1")
//                .createUserName("testCreateUserName1")
//                .createDate(createDate)
//                .startDate(startDate)
//                .endDate(endDate)
//                .state(true)
//                .introduction("testIntroduction1")
//                .maxPeople(10)
//                .currentPeople(4)
//                .delete(false)
//                .deleteReason(null)
//                .viewCount(10)
//                .commentCount(10)
//                .image(null)
//                .build();
//
//        ProjectUser projectUser1 = ProjectUser.builder()
//                .no(1L)
//                .projectPosition(Position.FRONTEND)
//                .user(user1)
//                .creator(true)
//                .project(project1).build();
//        ProjectUser projectUser2 = ProjectUser.builder()
//                .no(2L)
//                .projectPosition(Position.BACKEND)
//                .user(user2)
//                .creator(false)
//                .project(project1).build();
//        List<ProjectUser> projectUserList = new ArrayList<>();
//        projectUserList.add(projectUser1);
//        projectUserList.add(projectUser2);
//
//        // 프로젝트 댓글
//        Comment comment1 = Comment.builder()
//                .no(1L)
//                .user(user1)
//                .project(project1)
//                .content("testComment1")
//                .build();
//
//        Comment comment2 = Comment.builder()
//                .no(2L)
//                .user(user1)
//                .project(project1)
//                .content("testComment2")
//                .build();
//
//        List<Comment> commentList = new ArrayList<>();
//        commentList.add(comment1);
//        commentList.add(comment2);
//
//        given(projectRepository.findDetailProject(any(Long.class))).willReturn(projectQueryDto);
//        given(projectUserRepository.findByProjectNo(any(Long.class))).willReturn(projectUserList);
//        given(commentRepository.findByProjectNo(any(Long.class))).willReturn(commentList);
//        given(bookMarkRepository.existBookMark(any(Long.class), any(Long.class))).willReturn(true);
//
//        // when
//        UserDetails userDetails = user1;
//        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(auth);
//        ProjectDto projectDetail = projectService.getProjectDetail(1L);
//
//        // then
//        assertEquals(projectDetail.getName(), project1.getName());
//        assertEquals(projectDetail.getProfile(), null);
//        assertEquals(projectDetail.getCreateDate(), project1.getCreateDate());
//        assertEquals(projectDetail.getStartDate(), project1.getStartDate());
//        assertEquals(projectDetail.getEndDate(), project1.getEndDate());
//        assertEquals(projectDetail.isState(), project1.isState());
//        assertEquals(projectDetail.getIntroduction(), project1.getIntroduction());
//        assertEquals(projectDetail.getMaxPeople(), project1.getMaxPeople());
//        assertEquals(projectDetail.isBookmark(), true);
//        assertEquals(projectDetail.getRegister(), project1.getCreateUserName());
//
//        assertEquals(projectDetail.getUserSimpleInfoDtoList().get(0).getNo(), user1.getNo());
//        assertEquals(projectDetail.getUserSimpleInfoDtoList().get(0).getProfile(), null);
//        assertEquals(projectDetail.getUserSimpleInfoDtoList().get(0).getName(), user1.getName());
//        assertEquals(projectDetail.getUserSimpleInfoDtoList().get(0).getProjectPosition(), projectUser1.getProjectPosition());
//        assertEquals(projectDetail.getUserSimpleInfoDtoList().get(0).isCreator(), projectUser1.isCreator());
//
//        assertEquals(projectDetail.getUserSimpleInfoDtoList().get(1).getNo(), user2.getNo());
//        assertEquals(projectDetail.getUserSimpleInfoDtoList().get(1).getProfile(), null);
//        assertEquals(projectDetail.getUserSimpleInfoDtoList().get(1).getName(), user2.getName());
//        assertEquals(projectDetail.getUserSimpleInfoDtoList().get(1).getProjectPosition(), projectUser2.getProjectPosition());
//        assertEquals(projectDetail.getUserSimpleInfoDtoList().get(1).isCreator(), projectUser2.isCreator());
//
//
//        assertEquals(projectDetail.getProjectPosition().get(0).getPosition(), frontPosition.getPosition());
//        assertEquals(projectDetail.getProjectPosition().get(0).getTechnicalStack().get(0), frontPosition.getProjectTechnicalStackList().get(0).getName());
//        assertEquals(projectDetail.getProjectPosition().get(0).getTechnicalStack().get(1), frontPosition.getProjectTechnicalStackList().get(1).getName());
//
//        assertEquals(projectDetail.getProjectPosition().get(1).getPosition(), backPosition.getPosition());
//        assertEquals(projectDetail.getProjectPosition().get(1).getTechnicalStack().get(0), backPosition.getProjectTechnicalStackList().get(0).getName());
//        assertEquals(projectDetail.getProjectPosition().get(1).getTechnicalStack().get(1), backPosition.getProjectTechnicalStackList().get(1).getName());
//
//
//        assertEquals(projectDetail.getCommentDtoList().get(0).getNo(), comment1.getNo());
//        assertEquals(projectDetail.getCommentDtoList().get(0).getRegistrant(), user1.getName());
//        assertEquals(projectDetail.getCommentDtoList().get(0).getContent(), comment1.getContent());
//
//        assertEquals(projectDetail.getCommentDtoList().get(1).getNo(), comment2.getNo());
//        assertEquals(projectDetail.getCommentDtoList().get(1).getRegistrant(), user1.getName());
//        assertEquals(projectDetail.getCommentDtoList().get(1).getContent(), comment2.getContent());

    }
}
