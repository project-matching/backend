package com.matching.project.service;

import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.entity.Position;
import com.matching.project.entity.Project;
import com.matching.project.entity.ProjectPosition;
import com.matching.project.entity.User;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import com.matching.project.repository.ProjectPositionRepository;
import com.matching.project.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.EntityManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProjectPositionServiceImplTest {
    @Mock
    ProjectPositionRepository projectPositionRepository;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    EntityManager entityManager;

    @Mock
    NotificationService notificationService;

    @InjectMocks
    ProjectPositionServiceImpl projectPositionService;

    @Nested
    @DisplayName("프로젝트 탈퇴")
    class testProjectPositionWithdraw {
        @Test
        @DisplayName("성공")
        public void success() {
            // given
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

            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            Project project1 = Project.builder()
                    .no(1L)
                    .name("testName1")
                    .createUserName("user1")
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(4)
                    .viewCount(10)
                    .commentCount(10)
                    .build();

            Position position1 = Position.builder()
                    .no(1L)
                    .name("testPosition1")
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
            projectPositionList.add(projectPosition1);

            given(projectPositionRepository.findUserAndProjectFetchJoinByProjectPositionNo(any())).willReturn(Optional.of(projectPosition1));
            given(projectPositionRepository.findProjectAndPositionAndUserUsingFetchJoinByProject(any())).willReturn(projectPositionList);

            // when
            Long result = null;

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user1, "", user1.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            try {
                result =  projectPositionService.projectPositionWithdraw(projectPosition1.getNo());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // then
            verify(projectPositionRepository).findUserAndProjectFetchJoinByProjectPositionNo(any());
            verify(projectPositionRepository).findProjectAndPositionAndUserUsingFetchJoinByProject(any());

            assertEquals(result, projectPosition1.getNo());
        }

        @Test
        @DisplayName("실패 : 프로젝트 포지션이 null일 경우")
        public void fail1() {
            // given
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

            given(projectPositionRepository.findUserAndProjectFetchJoinByProjectPositionNo(any())).willReturn(Optional.empty());

            // when
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user1, "", user1.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            CustomException customException = assertThrows(CustomException.class, () -> {
                projectPositionService.projectPositionWithdraw(1L);
            });

            // then
            assertEquals(customException.getErrorCode().getHttpStatus().name(), ErrorCode.PROJECT_POSITION_NO_SUCH_ELEMENT_EXCEPTION.getHttpStatus().name());
            assertEquals(customException.getErrorCode().getDetail(), ErrorCode.PROJECT_POSITION_NO_SUCH_ELEMENT_EXCEPTION.getDetail());
        }
        
        @Test
        @DisplayName("실패 : 프로젝트 포지션 유저가 null일 경우")
        public void fail2() {
            // given
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

            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            Project project1 = Project.builder()
                    .no(1L)
                    .name("testName1")
                    .createUserName("user1")
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(4)
                    .viewCount(10)
                    .commentCount(10)
                    .build();

            Position position1 = Position.builder()
                    .no(1L)
                    .name("testPosition1")
                    .build();

            ProjectPosition projectPosition1 = ProjectPosition.builder()
                    .no(1L)
                    .state(true)
                    .project(project1)
                    .position(position1)
                    .user(null)
                    .creator(false)
                    .build();

            given(projectPositionRepository.findUserAndProjectFetchJoinByProjectPositionNo(any())).willReturn(Optional.of(projectPosition1));

            // when
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user1, "", user1.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            CustomException customException = assertThrows(CustomException.class, () -> {
                projectPositionService.projectPositionWithdraw(projectPosition1.getNo());
            });

            // then
            assertEquals(customException.getErrorCode().getHttpStatus().name(), ErrorCode.PROJECT_POSITION_NOT_EXISTENCE_USER.getHttpStatus().name());
            assertEquals(customException.getErrorCode().getDetail(), ErrorCode.PROJECT_POSITION_NOT_EXISTENCE_USER.getDetail());
        }

        @Test
        @DisplayName("실패 : 프로젝트 포지션 유저가 다를 경우")
        public void fail3() {
            // given
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

            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            Project project1 = Project.builder()
                    .no(1L)
                    .name("testName1")
                    .createUserName("user1")
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(4)
                    .viewCount(10)
                    .commentCount(10)
                    .build();

            Position position1 = Position.builder()
                    .no(1L)
                    .name("testPosition1")
                    .build();

            ProjectPosition projectPosition1 = ProjectPosition.builder()
                    .no(1L)
                    .state(true)
                    .project(project1)
                    .position(position1)
                    .user(user2)
                    .creator(false)
                    .build();

            given(projectPositionRepository.findUserAndProjectFetchJoinByProjectPositionNo(any())).willReturn(Optional.of(projectPosition1));

            // when
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user1, "", user1.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            CustomException customException = assertThrows(CustomException.class, () -> {
                projectPositionService.projectPositionWithdraw(projectPosition1.getNo());
            });

            // then
            assertEquals(customException.getErrorCode().getHttpStatus().name(), ErrorCode.PROJECT_POSITION_NOT_EQUAL_USER.getHttpStatus().name());
            assertEquals(customException.getErrorCode().getDetail(), ErrorCode.PROJECT_POSITION_NOT_EQUAL_USER.getDetail());
        }
    }

    @Nested
    @DisplayName("프로젝트 추방")
    class testProjectPositionExpulsion {
        @Test
        @DisplayName("성공")
        public void success() {
            // given
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

            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            Project project1 = Project.builder()
                    .no(1L)
                    .name("testName1")
                    .createUserName("user1")
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(4)
                    .viewCount(10)
                    .commentCount(10)
                    .user(user1)
                    .build();

            Position position1 = Position.builder()
                    .no(1L)
                    .name("testPosition1")
                    .build();

            ProjectPosition projectPosition1 = ProjectPosition.builder()
                    .no(1L)
                    .state(true)
                    .project(project1)
                    .position(position1)
                    .user(user1)
                    .creator(false)
                    .build();

            given(projectPositionRepository.findUserAndProjectFetchJoinByProjectPositionNo(any())).willReturn(Optional.of(projectPosition1));
            given(projectRepository.existUserProjectByUser(any(Long.class), any(Long.class))).willReturn(true);

            // when
            Boolean result = null;

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user1, "", user1.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            try {
                result =  projectPositionService.projectPositionExpulsion(projectPosition1.getNo(), "testReason");
            } catch (Exception e) {
                e.printStackTrace();
            }

            assertEquals(result, true);
        }

        @Test
        @DisplayName("실패 : 프로젝트 포지션이 null일 경우")
        public void fail1() {
            // given
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

            given(projectPositionRepository.findUserAndProjectFetchJoinByProjectPositionNo(any())).willReturn(Optional.empty());

            // when
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user1, "", user1.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            CustomException customException = assertThrows(CustomException.class, () -> {
                projectPositionService.projectPositionExpulsion(1L, "testReason");
            });

            // then
            assertEquals(customException.getErrorCode().getHttpStatus().name(), ErrorCode.PROJECT_POSITION_NO_SUCH_ELEMENT_EXCEPTION.getHttpStatus().name());
            assertEquals(customException.getErrorCode().getDetail(), ErrorCode.PROJECT_POSITION_NO_SUCH_ELEMENT_EXCEPTION.getDetail());
        }

        @Test
        @DisplayName("실패 : 프로젝트 포지션 유저가 null일 경우")
        public void fail2() {
            // given
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

            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            Project project1 = Project.builder()
                    .no(1L)
                    .name("testName1")
                    .createUserName("user1")
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(4)
                    .viewCount(10)
                    .commentCount(10)
                    .user(user1)
                    .build();

            Position position1 = Position.builder()
                    .no(1L)
                    .name("testPosition1")
                    .build();

            ProjectPosition projectPosition1 = ProjectPosition.builder()
                    .no(1L)
                    .state(true)
                    .project(project1)
                    .position(position1)
                    .user(null)
                    .creator(false)
                    .build();

            given(projectPositionRepository.findUserAndProjectFetchJoinByProjectPositionNo(any())).willReturn(Optional.of(projectPosition1));
            given(projectRepository.existUserProjectByUser(any(Long.class), any(Long.class))).willReturn(true);

            // when
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user1, "", user1.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            CustomException customException = assertThrows(CustomException.class, () -> {
                projectPositionService.projectPositionExpulsion(projectPosition1.getNo(), "testReason");
            });

            // then
            assertEquals(customException.getErrorCode().getHttpStatus().name(), ErrorCode.PROJECT_POSITION_NOT_EXISTENCE_USER.getHttpStatus().name());
            assertEquals(customException.getErrorCode().getDetail(), ErrorCode.PROJECT_POSITION_NOT_EXISTENCE_USER.getDetail());
        }

        @Test
        @DisplayName("실패 : 프로젝트 포지션 유저가 null일 경우")
        public void fail3() {
            // given
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

            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            Project project1 = Project.builder()
                    .no(1L)
                    .name("testName1")
                    .createUserName("user1")
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(4)
                    .viewCount(10)
                    .commentCount(10)
                    .user(null)
                    .build();

            Position position1 = Position.builder()
                    .no(1L)
                    .name("testPosition1")
                    .build();

            ProjectPosition projectPosition1 = ProjectPosition.builder()
                    .no(1L)
                    .state(true)
                    .project(project1)
                    .position(position1)
                    .user(null)
                    .creator(false)
                    .build();

            given(projectPositionRepository.findUserAndProjectFetchJoinByProjectPositionNo(any())).willReturn(Optional.of(projectPosition1));
            given(projectRepository.existUserProjectByUser(any(Long.class), any(Long.class))).willReturn(false);

            // when
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user1, "", user1.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            CustomException customException = assertThrows(CustomException.class, () -> {
                projectPositionService.projectPositionExpulsion(projectPosition1.getNo(), "testReason");
            });

            // then
            assertEquals(customException.getErrorCode().getHttpStatus().name(), ErrorCode.PROJECT_NOT_REGISTER_USER.getHttpStatus().name());
            assertEquals(customException.getErrorCode().getDetail(), ErrorCode.PROJECT_NOT_REGISTER_USER.getDetail());
        }
    }
}