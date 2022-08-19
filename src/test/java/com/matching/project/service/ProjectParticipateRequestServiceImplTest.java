package com.matching.project.service;

import com.matching.project.dto.SliceDto;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.project.ProjectParticipateRequestDto;
import com.matching.project.dto.projectparticipate.ProjectParticipateFormResponseDto;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
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

    @Mock
    NotificationService notificationService;

    @InjectMocks
    ProjectParticipateRequestServiceImpl projectParticipateRequestService;

    @Nested
    @DisplayName("프로젝트 참가 신청 등록")
    class testProjectParticipateRequestRegister {
        @Test
        @DisplayName("성공")
        public void success() {
            // given
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
            Project project1 = Project.builder()
                    .no(1L)
                    .name("testProject1")
                    .createUserName("user1")
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(1)
                    .viewCount(0)
                    .commentCount(0)
                    .user(user2)
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
            given(technicalStackRepository.findByNameIn(any())).willReturn(Optional.of(technicalStackList));
            given(participateRequestTechnicalStackRepository.save(any())).willReturn(participateRequestTechnicalStack1)
                    .willReturn(participateRequestTechnicalStack2);
            given(projectRepository.findProjectWithUserUsingFetchJoinByProjectNo(any())).willReturn(Optional.of(project1));

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
            verify(projectRepository, times(1)).findProjectWithUserUsingFetchJoinByProjectNo(any());
            verify(notificationService, times(1)).sendNotification(any(), any(), any(), any());

            assertEquals(result, true);
        }

        @Test
        @DisplayName("실패 : 잘못된 기술스택 요청")
        public void fail1() {
            // given
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
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(1)
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
            given(technicalStackRepository.findByNameIn(any())).willReturn(Optional.of(technicalStackList));

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

            assertEquals(e.getErrorCode().getHttpStatus(), ErrorCode.NOT_FIND_TECHNICAL_STACK_EXCEPTION.getHttpStatus());
            assertEquals(e.getErrorCode().getDetail(),ErrorCode.NOT_FIND_TECHNICAL_STACK_EXCEPTION.getDetail());
        }

        @Test
        @DisplayName("실패 : 해당 프로젝트 포지션에 유저가 존재하는 경우")
        public void fail2() {
            // given
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
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(1)
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
    }

    @Nested
    @DisplayName("프로젝트 신청 폼 조회")
    class testFindProjectParticipateManagementForm {
        @Test
        @DisplayName("성공")
        public void success() throws Exception {
            // given
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
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(1)
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

            given(projectRepository.findProjectWithUserUsingFetchJoinByProjectNo(any())).willReturn(Optional.of(project1));
            given(projectParticipateRequestRepository.findProjectParticipateRequestByProjectNo(any(), any(), any())).willReturn(Optional.of(projectParticipateFormResponseDtoPage));

            // when
            // 권한 추가
            UserDetails userDetails = user1;
            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            SliceDto<ProjectParticipateFormResponseDto> result = null;
            try {
                result = projectParticipateRequestService.findProjectParticipateManagementForm(project1.getNo(), Long.MAX_VALUE, pageable);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // then
            verify(projectRepository).findProjectWithUserUsingFetchJoinByProjectNo(any());
            verify(projectParticipateRequestRepository).findProjectParticipateRequestByProjectNo(any(), any(), any());

            assertEquals(result.getContent().get(0).getProjectParticipateNo(), projectParticipateFormResponseDtoList.get(0).getProjectParticipateNo());
            assertEquals(result.getContent().get(0).getTechnicalStackList().get(0), projectParticipateFormResponseDtoList.get(0).getTechnicalStackList().get(0));
            assertEquals(result.getContent().get(0).getTechnicalStackList().get(1), projectParticipateFormResponseDtoList.get(0).getTechnicalStackList().get(1));
            assertEquals(result.getContent().get(0).getPositionName(), projectParticipateFormResponseDtoList.get(0).getPositionName());
            assertEquals(result.getContent().get(0).getUserName(), projectParticipateFormResponseDtoList.get(0).getUserName());
            assertEquals(result.getContent().get(0).getMotive(), projectParticipateFormResponseDtoList.get(0).getMotive());

            assertEquals(result.isLast(), true);
        }

        @Test
        @DisplayName("실패 : 내가 만든 프로젝트가 아닌 경우")
        public void fail1() {
            // given
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
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(1)
                    .viewCount(0)
                    .commentCount(0)
                    .build();

            given(projectRepository.findProjectWithUserUsingFetchJoinByProjectNo(any())).willReturn(Optional.of(project1));

            // when
            // 권한 추가
            UserDetails userDetails = user1;
            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            Pageable pageable = PageRequest.of(0, 5, Sort.by("no").descending());

            CustomException e = Assertions.assertThrows(CustomException.class, () -> {
                projectParticipateRequestService.findProjectParticipateManagementForm(project1.getNo(), Long.MAX_VALUE, pageable);
            });

            // then
            verify(projectRepository).findProjectWithUserUsingFetchJoinByProjectNo(any());

            assertEquals(e.getErrorCode().getHttpStatus(), ErrorCode.PROJECT_NOT_REGISTER_USER.getHttpStatus());
            assertEquals(e.getErrorCode().getDetail(),ErrorCode.PROJECT_NOT_REGISTER_USER.getDetail());
        }
    }
    
    @Nested
    @DisplayName("프로젝트 참가 신청 수락")
    class testPermitProjectParticipate {
        @Test
        @DisplayName("성공")
        public void success() throws Exception {
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
            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            Project project1 = Project.builder()
                    .no(1L)
                    .name("testProject1")
                    .createUserName("user1")
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(1)
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

            given(projectParticipateRequestRepository.findProjectPositionAndUserAndProjectFetchJoinByNo(any())).willReturn(Optional.of(projectParticipateRequest1));
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
        @DisplayName("실패 : 내가 만든 프로젝트가 아닌 경우")
        public void fail1() throws Exception {
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
            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            Project project1 = Project.builder()
                    .no(1L)
                    .name("testProject1")
                    .createUserName("user1")
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(1)
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

            given(projectParticipateRequestRepository.findProjectPositionAndUserAndProjectFetchJoinByNo(any())).willReturn(Optional.of(projectParticipateRequest1));

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
    }

    @Nested
    @DisplayName("프로젝트 참가 신청 거절")
    class testRefusalProjectParticipate {
        @Test
        @DisplayName("성공")
        public void success() throws Exception {
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
            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            Project project1 = Project.builder()
                    .no(1L)
                    .name("testProject1")
                    .createUserName("user1")
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(1)
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

            given(projectParticipateRequestRepository.findProjectPositionAndUserAndProjectFetchJoinByNo(any())).willReturn(Optional.of(projectParticipateRequest1));
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
        @DisplayName("실패 : 내가 만든 프로젝트가 아닌 경우")
        public void fail1() throws Exception {
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
            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            Project project1 = Project.builder()
                    .no(1L)
                    .name("testProject1")
                    .createUserName("user1")
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(1)
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

            given(projectParticipateRequestRepository.findProjectPositionAndUserAndProjectFetchJoinByNo(any())).willReturn(Optional.of(projectParticipateRequest1));

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
}