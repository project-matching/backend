package com.matching.project.service;

import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.project.ProjectParticipateRequestDto;
import com.matching.project.dto.projectparticipate.ProjectParticipateFormResponseDto;
import com.matching.project.entity.*;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import com.matching.project.repository.*;
import org.assertj.core.api.BDDAssumptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProjectParticipateRequestServiceImplTest {
    @Mock
    ProjectRepository projectRepository;

    @Mock
    ProjectPositionRepository projectPositionRepository;

    @Mock
    ProjectParticipateRequestRepository projectParticipateRequestRepository;

    @Mock
    TechnicalStackRepository technicalStackRepository;

    @Mock
    ParticipateRequestTechnicalStackRepository participateRequestTechnicalStackRepository;

    @InjectMocks
    ProjectParticipateRequestServiceImpl projectParticipateRequestService;

    @Test
    public void 프로젝트_참가_신청_등록_테스트() {
        // given
        
        LocalDateTime createDate = LocalDateTime.now();
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);
        
        // 유저 세팅
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

        // 프로젝트 세팅
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
        
        // 포지션 세팅
        Position position1 = Position.builder()
                .no(1L)
                .name("testPosition1")
                .build();
        
        // 프로젝트 포지션 세팅
        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .no(1L)
                .project(project1)
                .position(position1)
                .user(null)
                .creator(false)
                .build();
        
        // 프로젝트 참가 신청 세팅
        ProjectParticipateRequest projectParticipateRequest1 = ProjectParticipateRequest.builder()
                .no(1L)
                .projectPosition(projectPosition1)
                .user(user1)
                .motive("testMotive1")
                .github("testGitHub1")
                .build();
        
        // 기술스택 세팅
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
        
        // 참여 신청 기술 스택 세팅
        ParticipateRequestTechnicalStack participateRequestTechnicalStack1 = ParticipateRequestTechnicalStack.builder()
                .no(1L)
                .projectParticipateRequest(projectParticipateRequest1)
                .technicalStack(technicalStack1)
                .build();

        ParticipateRequestTechnicalStack participateRequestTechnicalStack2 = ParticipateRequestTechnicalStack.builder()
                .no(2L)
                .projectParticipateRequest(projectParticipateRequest1)
                .technicalStack(technicalStack2)
                .build();

        given(projectPositionRepository.findById(any())).willReturn(Optional.of(projectPosition1));
        given(projectParticipateRequestRepository.save(any())).willReturn(projectParticipateRequest1);
        given(technicalStackRepository.findByNameIn(any())).willReturn(technicalStackList);
        given(participateRequestTechnicalStackRepository.save(any())).willReturn(participateRequestTechnicalStack1)
                .willReturn(participateRequestTechnicalStack2);

        // when
        List<String> technicalStackRequestList = new ArrayList<>();
        technicalStackRequestList.add("testTechnicalStack1");
        technicalStackRequestList.add("testTechnicalStack2");

        ProjectParticipateRequestDto projectParticipateRequestDto = new ProjectParticipateRequestDto(1L, technicalStackRequestList, "testGitHub1", "testMotive");

        Boolean result = null;
        
        // 권한 추가
        UserDetails userDetails = user1;
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        try {
            result = projectParticipateRequestService.projectParticipateRequestRegister(projectParticipateRequestDto);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // then
        verify(projectPositionRepository).findById(any());
        verify(projectParticipateRequestRepository).save(any());
        verify(technicalStackRepository).findByNameIn(any());
        verify(participateRequestTechnicalStackRepository, times(2)).save(any());

        assertEquals(result, true);
    }

    @Test
    public void 프로젝트_참가_신청_등록_잘못된_기술스택_요청_테스트() {
        // given

        LocalDateTime createDate = LocalDateTime.now();
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        // 유저 세팅
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

        // 프로젝트 세팅
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

        // 포지션 세팅
        Position position1 = Position.builder()
                .no(1L)
                .name("testPosition1")
                .build();

        // 프로젝트 포지션 세팅
        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .no(1L)
                .project(project1)
                .position(position1)
                .user(null)
                .creator(false)
                .build();

        // 프로젝트 참가 신청 세팅
        ProjectParticipateRequest projectParticipateRequest1 = ProjectParticipateRequest.builder()
                .no(1L)
                .projectPosition(projectPosition1)
                .user(user1)
                .motive("testMotive1")
                .github("testGitHub1")
                .build();

        // 기술스택 세팅
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

        // 참여 신청 기술 스택 세팅
        ParticipateRequestTechnicalStack participateRequestTechnicalStack1 = ParticipateRequestTechnicalStack.builder()
                .no(1L)
                .projectParticipateRequest(projectParticipateRequest1)
                .technicalStack(technicalStack1)
                .build();

        ParticipateRequestTechnicalStack participateRequestTechnicalStack2 = ParticipateRequestTechnicalStack.builder()
                .no(2L)
                .projectParticipateRequest(projectParticipateRequest1)
                .technicalStack(technicalStack2)
                .build();

        given(projectPositionRepository.findById(any())).willReturn(Optional.of(projectPosition1));
        given(projectParticipateRequestRepository.save(any())).willReturn(projectParticipateRequest1);
        given(technicalStackRepository.findByNameIn(any())).willReturn(technicalStackList);

        // when
        List<String> technicalStackRequestList = new ArrayList<>();
        technicalStackRequestList.add("testTechnicalStack3");
        technicalStackRequestList.add("testTechnicalStack4");

        ProjectParticipateRequestDto projectParticipateRequestDto = new ProjectParticipateRequestDto(1L, technicalStackRequestList, "testGitHub1", "testMotive");

        // 권한 추가
        UserDetails userDetails = user1;
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        CustomException e = Assertions.assertThrows(CustomException.class, () -> {
            projectParticipateRequestService.projectParticipateRequestRegister(projectParticipateRequestDto);
        });

        // then
        verify(projectPositionRepository).findById(any());
        verify(projectParticipateRequestRepository).save(any());
        verify(technicalStackRepository).findByNameIn(any());

        assertEquals(e.getErrorCode().getHttpStatus(), ErrorCode.TECHNICAL_STACK_NOT_FOUND.getHttpStatus());
        assertEquals(e.getErrorCode().getDetail(),ErrorCode.TECHNICAL_STACK_NOT_FOUND.getDetail());
    }

    @Test
    public void 프로젝트_참가_신청_등록_유저_존재_테스트() {
        // given
        LocalDateTime createDate = LocalDateTime.now();
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        // 유저 세팅
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

        // 프로젝트 세팅
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

        // 포지션 세팅
        Position position1 = Position.builder()
                .no(1L)
                .name("testPosition1")
                .build();

        // 프로젝트 포지션 세팅
        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .no(1L)
                .project(project1)
                .position(position1)
                .user(user1)
                .creator(false)
                .build();

        // 프로젝트 참가 신청 세팅
        ProjectParticipateRequest projectParticipateRequest1 = ProjectParticipateRequest.builder()
                .no(1L)
                .projectPosition(projectPosition1)
                .user(user1)
                .motive("testMotive1")
                .github("testGitHub1")
                .build();

        // 기술스택 세팅
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

        // 참여 신청 기술 스택 세팅
        ParticipateRequestTechnicalStack participateRequestTechnicalStack1 = ParticipateRequestTechnicalStack.builder()
                .no(1L)
                .projectParticipateRequest(projectParticipateRequest1)
                .technicalStack(technicalStack1)
                .build();

        ParticipateRequestTechnicalStack participateRequestTechnicalStack2 = ParticipateRequestTechnicalStack.builder()
                .no(2L)
                .projectParticipateRequest(projectParticipateRequest1)
                .technicalStack(technicalStack2)
                .build();

        given(projectPositionRepository.findById(any())).willReturn(Optional.of(projectPosition1));

        // when
        List<String> technicalStackRequestList = new ArrayList<>();
        technicalStackRequestList.add("testTechnicalStack3");
        technicalStackRequestList.add("testTechnicalStack4");

        ProjectParticipateRequestDto projectParticipateRequestDto = new ProjectParticipateRequestDto(1L, technicalStackRequestList, "testGitHub1", "testMotive");

        // 권한 추가
        UserDetails userDetails = user1;
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        CustomException e = Assertions.assertThrows(CustomException.class, () -> {
            projectParticipateRequestService.projectParticipateRequestRegister(projectParticipateRequestDto);
        });

        // then
        assertEquals(e.getErrorCode().getHttpStatus(), ErrorCode.PROJECT_POSITION_EXISTENCE_USER.getHttpStatus());
        assertEquals(e.getErrorCode().getDetail(),ErrorCode.PROJECT_POSITION_EXISTENCE_USER.getDetail());
    }

    @Test
    public void 프로젝트_신청_폼_조회_테스트() throws Exception {
        // given
        LocalDateTime createDate = LocalDateTime.now();
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        // 유저 세팅
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

        // 프로젝트 세팅
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
                .user(user1)
                .commentCount(0)
                .build();

        List<ProjectParticipateFormResponseDto> projectParticipateFormResponseDtoList = new ArrayList<>();
        ProjectParticipateFormResponseDto projectParticipateFormResponseDto = new ProjectParticipateFormResponseDto(1L, user1.getName(), "testPositionName1", "testMotive");
        List<String> technicalStackList = new ArrayList<>();
        technicalStackList.add("testTechnicalStack1");
        technicalStackList.add("testTechnicalStack2");
        projectParticipateFormResponseDto.setTechnicalStackList(technicalStackList);
        projectParticipateFormResponseDtoList.add(projectParticipateFormResponseDto);

        Pageable pageable = PageRequest.of(0, 5, Sort.by("no").descending());
        int start = (int)pageable.getOffset();
        int end = (start + pageable.getPageSize()) > projectParticipateFormResponseDtoList.size() ? projectParticipateFormResponseDtoList.size() : (start + pageable.getPageSize());
        Page<ProjectParticipateFormResponseDto> projectParticipateFormResponseDtoPage = new PageImpl<>(projectParticipateFormResponseDtoList.subList(start, end), pageable, projectParticipateFormResponseDtoList.size());

        given(projectRepository.findProjectWithUserUsingFetchJoinByProjectNo(any())).willReturn(project1);
        given(projectParticipateRequestRepository.findProjectParticipateRequestByProjectNo(any(), any())).willReturn(projectParticipateFormResponseDtoPage);

        // when
        // 권한 추가
        UserDetails userDetails = user1;
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Page<ProjectParticipateFormResponseDto> result = null;
        try {
            result = projectParticipateRequestService.findProjectParticipateManagementForm(project1.getNo(), pageable);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // then
        verify(projectRepository).findProjectWithUserUsingFetchJoinByProjectNo(any());
        verify(projectParticipateRequestRepository).findProjectParticipateRequestByProjectNo(any(), any());

        List<ProjectParticipateFormResponseDto> content = result.getContent();

        assertEquals(content.get(0).getProjectParticipateNo(), projectParticipateFormResponseDtoList.get(0).getProjectParticipateNo());
        assertEquals(content.get(0).getTechnicalStackList().get(0), projectParticipateFormResponseDtoList.get(0).getTechnicalStackList().get(0));
        assertEquals(content.get(0).getTechnicalStackList().get(1), projectParticipateFormResponseDtoList.get(0).getTechnicalStackList().get(1));
        assertEquals(content.get(0).getPositionName(), projectParticipateFormResponseDtoList.get(0).getPositionName());
        assertEquals(content.get(0).getUserName(), projectParticipateFormResponseDtoList.get(0).getUserName());
        assertEquals(content.get(0).getMotive(), projectParticipateFormResponseDtoList.get(0).getMotive());
    }

    @Test
    public void 프로젝트_신청_폼_조회_내가_만든_프로젝트_아닌_경우_테스트() throws Exception {
        // given
        LocalDateTime createDate = LocalDateTime.now();
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        // 유저 세팅
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

        // 프로젝트 세팅
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

        given(projectRepository.findProjectWithUserUsingFetchJoinByProjectNo(any())).willReturn(project1);

        // when
        // 권한 추가
        UserDetails userDetails = user1;
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Pageable pageable = PageRequest.of(0, 5, Sort.by("no").descending());

        CustomException e = Assertions.assertThrows(CustomException.class, () -> {
            projectParticipateRequestService.findProjectParticipateManagementForm(project1.getNo(), pageable);
        });

        // then
        verify(projectRepository).findProjectWithUserUsingFetchJoinByProjectNo(any());

        assertEquals(e.getErrorCode().getHttpStatus(), ErrorCode.PROJECT_NOT_REGISTER_USER.getHttpStatus());
        assertEquals(e.getErrorCode().getDetail(),ErrorCode.PROJECT_NOT_REGISTER_USER.getDetail());
    }

    @Test
    public void 프로젝트_참가_신청_수락_테스트() throws Exception {
        // given
        // 유저 세팅
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

        User user2 = User.builder()
                .no(2L)
                .name("testUser2")
                .sex("M")
                .email("testEmail2")
                .password("testPassword2")
                .github("testGithub2")
                .block(false)
                .blockReason(null)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(false)
                .imageNo(0L)
                .position(null)
                .build();

        // 프로젝트 세팅
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
                .user(user1)
                .commentCount(0)
                .build();
        // 프로젝트 포지션 세팅
        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .no(1L)
                .project(project1)
                .position(null)
                .user(user1)
                .creator(false)
                .build();
        // 프로젝트 참가 신청
        ProjectParticipateRequest projectParticipateRequest1 = ProjectParticipateRequest.builder()
                .no(1L)
                .user(user2)
                .projectPosition(projectPosition1)
                .motive("testMotive1")
                .github("testGitHub1")
                .build();

        given(projectParticipateRequestRepository.findProjectPositionAndUserAndProjectFetchJoinByNo(any())).willReturn(projectParticipateRequest1);
        given(projectParticipateRequestRepository.deleteByNo(any())).willReturn(projectParticipateRequest1.getNo());
        given(projectPositionRepository.findById(any())).willReturn(Optional.ofNullable(projectPosition1));

        // when
        // 권한 추가
        UserDetails userDetails = user1;
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        boolean result = false;
        try {
            result = projectParticipateRequestService.permitProjectParticipate(projectParticipateRequest1.getNo());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // then
        verify(projectParticipateRequestRepository).findProjectPositionAndUserAndProjectFetchJoinByNo(any());
        verify(participateRequestTechnicalStackRepository).deleteByProjectParticipateNo(any());
        verify(projectParticipateRequestRepository).deleteByNo(any());
        verify(projectPositionRepository).findById(any());

        assertEquals(result, true);
    }

    @Test
    public void 프로젝트_참가_신청_수락_내가_만든_프로젝트_아닌_경우_테스트() throws Exception {
        // given
        // 유저 세팅
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

        User user2 = User.builder()
                .no(2L)
                .name("testUser2")
                .sex("M")
                .email("testEmail2")
                .password("testPassword2")
                .github("testGithub2")
                .block(false)
                .blockReason(null)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(false)
                .imageNo(0L)
                .position(null)
                .build();

        // 프로젝트 세팅
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
        // 프로젝트 포지션 세팅
        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .no(1L)
                .project(project1)
                .position(null)
                .user(user1)
                .creator(false)
                .build();
        // 프로젝트 참가 신청
        ProjectParticipateRequest projectParticipateRequest1 = ProjectParticipateRequest.builder()
                .no(1L)
                .user(user2)
                .projectPosition(projectPosition1)
                .motive("testMotive1")
                .github("testGitHub1")
                .build();

        given(projectParticipateRequestRepository.findProjectPositionAndUserAndProjectFetchJoinByNo(any())).willReturn(projectParticipateRequest1);

        // when
        // 권한 추가
        UserDetails userDetails = user1;
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        CustomException e = Assertions.assertThrows(CustomException.class, () -> {
            projectParticipateRequestService.permitProjectParticipate(projectParticipateRequest1.getNo());
        });

        verify(projectParticipateRequestRepository).findProjectPositionAndUserAndProjectFetchJoinByNo(any());

        assertEquals(e.getErrorCode().getHttpStatus(), ErrorCode.PROJECT_NOT_REGISTER_USER.getHttpStatus());
        assertEquals(e.getErrorCode().getDetail(),ErrorCode.PROJECT_NOT_REGISTER_USER.getDetail());
    }

    @Test
    public void 프로젝트_참가_신청_거절_테스트() throws Exception {
        // given
        // 유저 세팅
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

        User user2 = User.builder()
                .no(2L)
                .name("testUser2")
                .sex("M")
                .email("testEmail2")
                .password("testPassword2")
                .github("testGithub2")
                .block(false)
                .blockReason(null)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(false)
                .imageNo(0L)
                .position(null)
                .build();

        // 프로젝트 세팅
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
                .user(user1)
                .commentCount(0)
                .build();
        // 프로젝트 포지션 세팅
        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .no(1L)
                .project(project1)
                .position(null)
                .user(user1)
                .creator(false)
                .build();
        // 프로젝트 참가 신청
        ProjectParticipateRequest projectParticipateRequest1 = ProjectParticipateRequest.builder()
                .no(1L)
                .user(user2)
                .projectPosition(projectPosition1)
                .motive("testMotive1")
                .github("testGitHub1")
                .build();

        given(projectParticipateRequestRepository.findProjectPositionAndUserAndProjectFetchJoinByNo(any())).willReturn(projectParticipateRequest1);
        given(projectParticipateRequestRepository.deleteByNo(any())).willReturn(projectParticipateRequest1.getNo());

        // when
        // 권한 추가
        UserDetails userDetails = user1;
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        boolean result = false;
        try {
            result = projectParticipateRequestService.refusalProjectParticipate(projectParticipateRequest1.getNo(), "testReason");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // then
        verify(projectParticipateRequestRepository).findProjectPositionAndUserAndProjectFetchJoinByNo(any());
        verify(participateRequestTechnicalStackRepository).deleteByProjectParticipateNo(any());
        verify(projectParticipateRequestRepository).deleteByNo(any());

        assertEquals(result, true);
    }

    @Test
    public void 프로젝트_참가_신청_거절_내가_만든_프로젝트_아닌_경우_테스트() throws Exception {
        // given
        // 유저 세팅
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

        User user2 = User.builder()
                .no(2L)
                .name("testUser2")
                .sex("M")
                .email("testEmail2")
                .password("testPassword2")
                .github("testGithub2")
                .block(false)
                .blockReason(null)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(false)
                .imageNo(0L)
                .position(null)
                .build();

        // 프로젝트 세팅
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
        // 프로젝트 포지션 세팅
        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .no(1L)
                .project(project1)
                .position(null)
                .user(user1)
                .creator(false)
                .build();
        // 프로젝트 참가 신청
        ProjectParticipateRequest projectParticipateRequest1 = ProjectParticipateRequest.builder()
                .no(1L)
                .user(user2)
                .projectPosition(projectPosition1)
                .motive("testMotive1")
                .github("testGitHub1")
                .build();

        given(projectParticipateRequestRepository.findProjectPositionAndUserAndProjectFetchJoinByNo(any())).willReturn(projectParticipateRequest1);

        // when
        // 권한 추가
        UserDetails userDetails = user1;
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        CustomException e = Assertions.assertThrows(CustomException.class, () -> {
            projectParticipateRequestService.refusalProjectParticipate(projectParticipateRequest1.getNo(), "testReson");
        });

        verify(projectParticipateRequestRepository).findProjectPositionAndUserAndProjectFetchJoinByNo(any());

        assertEquals(e.getErrorCode().getHttpStatus(), ErrorCode.PROJECT_NOT_REGISTER_USER.getHttpStatus());
        assertEquals(e.getErrorCode().getDetail(),ErrorCode.PROJECT_NOT_REGISTER_USER.getDetail());
    }
}