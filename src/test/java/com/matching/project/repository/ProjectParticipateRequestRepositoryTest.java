package com.matching.project.repository;

import com.matching.project.config.JpaConfig;
import com.matching.project.config.QuerydslConfiguration;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.projectparticipate.ProjectParticipateFormResponseDto;
import com.matching.project.entity.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@Import({QuerydslConfiguration.class, JpaConfig.class})
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class ProjectParticipateRequestRepositoryTest {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectPositionRepository projectPositionRepository;

    @Autowired
    private ProjectTechnicalStackRepository projectTechnicalStackRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private TechnicalStackRepository technicalStackRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectParticipateRequestRepository projectParticipateRequestRepository;

    @Autowired
    private ParticipateRequestTechnicalStackRepository participateRequestTechnicalStackRepository;

    @Test
    public void 프로젝트_신청_페이지_조회() {
        // given
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);
        
        // 유저 세팅
        User user1 = User.builder()
                .name("userName1")
                .sex("M")
                .email("wkemrm1@naver.com")
                .password("testPassword")
                .github("testGithub")
                .selfIntroduction("testSelfIntroduction")
                .block(false)
                .blockReason(null)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(false)
                .imageNo(0L)
                .position(null)
                .build();
        User user2 = User.builder()
                .name("userName2")
                .sex("M")
                .email("wkemrm2@naver.com")
                .password("testPassword")
                .github("testGithub")
                .selfIntroduction("testSelfIntroduction")
                .block(false)
                .blockReason(null)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(false)
                .imageNo(0L)
                .position(null)
                .build();
        User user3 = User.builder()
                .name("userName3")
                .sex("M")
                .email("wkemrm3@naver.com")
                .password("testPassword")
                .github("testGithub")
                .selfIntroduction("testSelfIntroduction")
                .block(false)
                .blockReason(null)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(false)
                .imageNo(0L)
                .position(null)
                .build();
        User saveUser1 = userRepository.save(user1);
        User saveUser2 = userRepository.save(user2);
        User saveUser3 = userRepository.save(user3);

        // 프로젝트 객체
        Project project1 = Project.builder()
                .name("testName1")
                .createUserName("user1")
                .startDate(startDate)
                .endDate(endDate)
                .state(true)
                .introduction("testIntroduction1")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .user(saveUser1)
                .commentCount(10)
                .build();
        Project saveProject1 = projectRepository.save(project1);
        
        // 포지션 세팅
        Position position1 = Position.builder()
                .name("testPosition1")
                .build();
        Position position2 = Position.builder()
                .name("testPosition2")
                .build();
        positionRepository.save(position1);
        positionRepository.save(position2);
        
        // 프로젝트 포지션 세팅
        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .state(true)
                .project(project1)
                .position(position1)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition projectPosition2 = ProjectPosition.builder()
                .state(false)
                .project(project1)
                .position(position2)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);
        ProjectPosition saveProjectPosition2 = projectPositionRepository.save(projectPosition2);
        
        // 참여 신청 세팅
        ProjectParticipateRequest projectParticipateRequest1 = ProjectParticipateRequest.builder()
                .user(saveUser1)
                .projectPosition(saveProjectPosition1)
                .motive("testMotive1")
                .github("testGitHub1")
                .build();
        ProjectParticipateRequest projectParticipateRequest2 = ProjectParticipateRequest.builder()
                .user(saveUser2)
                .projectPosition(saveProjectPosition1)
                .motive("testMotive2")
                .github("testGitHub2")
                .build();
        ProjectParticipateRequest projectParticipateRequest3 = ProjectParticipateRequest.builder()
                .user(saveUser3)
                .projectPosition(saveProjectPosition1)
                .motive("testMotive3")
                .github("testGitHub3")
                .build();
        ProjectParticipateRequest saveProjectParticipateRequest1 = projectParticipateRequestRepository.save(projectParticipateRequest1);
        ProjectParticipateRequest saveProjectParticipateRequest2 = projectParticipateRequestRepository.save(projectParticipateRequest2);
        ProjectParticipateRequest saveProjectParticipateRequest3 = projectParticipateRequestRepository.save(projectParticipateRequest3);
        
        // 기술스택 세팅
        TechnicalStack technicalStack1 = TechnicalStack.builder()
                .name("testTechnicalStack1")
                .build();
        TechnicalStack technicalStack2 = TechnicalStack.builder()
                .name("testTechnicalStack2")
                .build();
        TechnicalStack saveTechnicalStack1 = technicalStackRepository.save(technicalStack1);
        TechnicalStack saveTechnicalStack2 = technicalStackRepository.save(technicalStack1);

        // 참여 신청 기술 스택 세팅
        ParticipateRequestTechnicalStack participateRequestTechnicalStack1 = ParticipateRequestTechnicalStack.builder()
                .projectParticipateRequest(saveProjectParticipateRequest1)
                .technicalStack(saveTechnicalStack1)
                .build();
        ParticipateRequestTechnicalStack participateRequestTechnicalStack2 = ParticipateRequestTechnicalStack.builder()
                .projectParticipateRequest(saveProjectParticipateRequest2)
                .technicalStack(saveTechnicalStack1)
                .build();
        ParticipateRequestTechnicalStack participateRequestTechnicalStack3 = ParticipateRequestTechnicalStack.builder()
                .projectParticipateRequest(saveProjectParticipateRequest2)
                .technicalStack(saveTechnicalStack2)
                .build();
        ParticipateRequestTechnicalStack saveParticipateRequestTechnicalStack1 = participateRequestTechnicalStackRepository.save(participateRequestTechnicalStack1);
        ParticipateRequestTechnicalStack saveParticipateRequestTechnicalStack2 = participateRequestTechnicalStackRepository.save(participateRequestTechnicalStack2);
        ParticipateRequestTechnicalStack saveParticipateRequestTechnicalStack3 = participateRequestTechnicalStackRepository.save(participateRequestTechnicalStack3);

        // when
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdDate").descending());

        Slice<ProjectParticipateFormResponseDto> projectParticipateFormResponseDtoSlice = null;
        try {
            projectParticipateFormResponseDtoSlice = projectParticipateRequestRepository.findProjectParticipateRequestByProjectNo(saveProject1.getNo(), Long.MAX_VALUE, pageable);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<ProjectParticipateFormResponseDto> projectParticipateFormResponseDtoList = projectParticipateFormResponseDtoSlice.getContent();

        // then
        assertEquals(projectParticipateFormResponseDtoSlice.getNumber(), 0);
        assertEquals(projectParticipateFormResponseDtoSlice.getNumberOfElements(), 3);
        assertEquals(projectParticipateFormResponseDtoSlice.hasNext(), false);
        assertEquals(projectParticipateFormResponseDtoSlice.isFirst(), true);
        assertEquals(projectParticipateFormResponseDtoSlice.isLast(), true);
        assertEquals(projectParticipateFormResponseDtoSlice.hasContent(), true);

        assertEquals(projectParticipateFormResponseDtoList.size(), 3);
        assertEquals(projectParticipateFormResponseDtoList.get(0).getProjectParticipateNo(), saveProjectParticipateRequest3.getNo());
        assertEquals(projectParticipateFormResponseDtoList.get(0).getUserName(), saveProjectParticipateRequest3.getUser().getName());
        assertEquals(projectParticipateFormResponseDtoList.get(0).getPositionName(), saveProjectParticipateRequest3.getProjectPosition().getPosition().getName());
        assertEquals(projectParticipateFormResponseDtoList.get(0).getMotive(), saveProjectParticipateRequest3.getMotive());
        assertEquals(projectParticipateFormResponseDtoList.get(0).getTechnicalStackList(), null);

        assertEquals(projectParticipateFormResponseDtoList.get(1).getProjectParticipateNo(), saveProjectParticipateRequest2.getNo());
        assertEquals(projectParticipateFormResponseDtoList.get(1).getUserName(), saveProjectParticipateRequest2.getUser().getName());
        assertEquals(projectParticipateFormResponseDtoList.get(1).getPositionName(), saveProjectParticipateRequest2.getProjectPosition().getPosition().getName());
        assertEquals(projectParticipateFormResponseDtoList.get(1).getMotive(), saveProjectParticipateRequest2.getMotive());
        assertEquals(projectParticipateFormResponseDtoList.get(1).getTechnicalStackList().size(), 2);
        assertEquals(projectParticipateFormResponseDtoList.get(1).getTechnicalStackList().get(0), saveParticipateRequestTechnicalStack2.getTechnicalStack().getName());
        assertEquals(projectParticipateFormResponseDtoList.get(1).getTechnicalStackList().get(1), saveParticipateRequestTechnicalStack3.getTechnicalStack().getName());

        assertEquals(projectParticipateFormResponseDtoList.get(2).getProjectParticipateNo(), saveProjectParticipateRequest1.getNo());
        assertEquals(projectParticipateFormResponseDtoList.get(2).getUserName(), saveProjectParticipateRequest1.getUser().getName());
        assertEquals(projectParticipateFormResponseDtoList.get(2).getPositionName(), saveProjectParticipateRequest1.getProjectPosition().getPosition().getName());
        assertEquals(projectParticipateFormResponseDtoList.get(2).getMotive(), saveProjectParticipateRequest1.getMotive());
        assertEquals(projectParticipateFormResponseDtoList.get(2).getTechnicalStackList().size(), 1);
        assertEquals(projectParticipateFormResponseDtoList.get(2).getTechnicalStackList().get(0), saveParticipateRequestTechnicalStack1.getTechnicalStack().getName());
    }

    @Test
    public void 프로젝트_프로젝트포지션_유저_조인_조회() {
        // given
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        // 유저 세팅
        User user1 = User.builder()
                .name("userName1")
                .sex("M")
                .email("wkemrm1@naver.com")
                .password("testPassword")
                .github("testGithub")
                .selfIntroduction("testSelfIntroduction")
                .block(false)
                .blockReason(null)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(false)
                .imageNo(0L)
                .position(null)
                .build();

        User saveUser1 = userRepository.save(user1);

        // 프로젝트 객체
        Project project1 = Project.builder()
                .name("testName1")
                .createUserName("user1")
                .startDate(startDate)
                .endDate(endDate)
                .state(true)
                .introduction("testIntroduction1")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .user(saveUser1)
                .commentCount(10)
                .build();
        Project saveProject1 = projectRepository.save(project1);

        // 포지션 세팅
        Position position1 = Position.builder()
                .name("testPosition1")
                .build();
        Position position2 = Position.builder()
                .name("testPosition2")
                .build();
        positionRepository.save(position1);
        positionRepository.save(position2);

        // 프로젝트 포지션 세팅
        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .state(true)
                .project(project1)
                .position(position1)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition projectPosition2 = ProjectPosition.builder()
                .state(false)
                .project(project1)
                .position(position2)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);
        ProjectPosition saveProjectPosition2 = projectPositionRepository.save(projectPosition2);

        // 참여 신청 세팅
        ProjectParticipateRequest projectParticipateRequest1 = ProjectParticipateRequest.builder()
                .user(saveUser1)
                .projectPosition(saveProjectPosition1)
                .motive("testMotive1")
                .github("testGitHub1")
                .build();

        ProjectParticipateRequest saveProjectParticipateRequest1 = projectParticipateRequestRepository.save(projectParticipateRequest1);

        // when
        ProjectParticipateRequest projectParticipateRequest = null;
        try {
            projectParticipateRequest = projectParticipateRequestRepository.findProjectPositionAndUserAndProjectFetchJoinByNo(saveProjectParticipateRequest1.getNo());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // then
        assertEquals(projectParticipateRequest.getNo(), saveProjectParticipateRequest1.getNo());
        assertEquals(projectParticipateRequest.getProjectPosition(), saveProjectParticipateRequest1.getProjectPosition());
        assertEquals(projectParticipateRequest.getProjectPosition().getProject(), saveProjectParticipateRequest1.getProjectPosition().getProject());
        assertEquals(projectParticipateRequest.getUser(), saveProjectParticipateRequest1.getUser());
        assertEquals(projectParticipateRequest.getMotive(), saveProjectParticipateRequest1.getMotive());
        assertEquals(projectParticipateRequest.getGithub(), saveProjectParticipateRequest1.getGithub());
    }

    @Test
    public void 프로젝트_신청_삭제() {
        // given
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        // 유저 세팅
        User user1 = User.builder()
                .name("userName1")
                .sex("M")
                .email("wkemrm1@naver.com")
                .password("testPassword")
                .github("testGithub")
                .selfIntroduction("testSelfIntroduction")
                .block(false)
                .blockReason(null)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(false)
                .imageNo(0L)
                .position(null)
                .build();

        User saveUser1 = userRepository.save(user1);

        // 프로젝트 객체
        Project project1 = Project.builder()
                .name("testName1")
                .createUserName("user1")
                .startDate(startDate)
                .endDate(endDate)
                .state(true)
                .introduction("testIntroduction1")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .user(saveUser1)
                .commentCount(10)
                .build();
        Project saveProject1 = projectRepository.save(project1);

        // 포지션 세팅
        Position position1 = Position.builder()
                .name("testPosition1")
                .build();
        Position position2 = Position.builder()
                .name("testPosition2")
                .build();
        positionRepository.save(position1);
        positionRepository.save(position2);

        // 프로젝트 포지션 세팅
        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .state(true)
                .project(project1)
                .position(position1)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition projectPosition2 = ProjectPosition.builder()
                .state(false)
                .project(project1)
                .position(position2)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);
        ProjectPosition saveProjectPosition2 = projectPositionRepository.save(projectPosition2);

        // 참여 신청 세팅
        ProjectParticipateRequest projectParticipateRequest1 = ProjectParticipateRequest.builder()
                .user(saveUser1)
                .projectPosition(saveProjectPosition1)
                .motive("testMotive1")
                .github("testGitHub1")
                .build();

        ProjectParticipateRequest saveProjectParticipateRequest1 = projectParticipateRequestRepository.save(projectParticipateRequest1);

        // when
        try {
            projectParticipateRequestRepository.deleteByNo(saveProjectParticipateRequest1.getNo());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // then
        Optional<ProjectParticipateRequest> result = projectParticipateRequestRepository.findById(saveProjectParticipateRequest1.getNo());
        assertEquals(result.isEmpty(), true);
    }

    @Nested
    @DisplayName("프로젝트 포지션 번호 조건 삭제")
    class testDeleteByProjectPositionNo {
        @Test
        @DisplayName("성공 테스트")
        public void testSuccess() throws Exception {
            // given
            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            // 유저 세팅
            User user1 = User.builder()
                    .name("userName1")
                    .sex("M")
                    .email("wkemrm1@naver.com")
                    .password("testPassword")
                    .github("testGithub")
                    .selfIntroduction("testSelfIntroduction")
                    .block(false)
                    .blockReason(null)
                    .permission(Role.ROLE_USER)
                    .oauthCategory(OAuth.NORMAL)
                    .email_auth(false)
                    .imageNo(0L)
                    .position(null)
                    .build();

            User saveUser1 = userRepository.save(user1);

            // 프로젝트 객체
            Project project1 = Project.builder()
                    .name("testName1")
                    .createUserName("user1")
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(4)
                    .viewCount(10)
                    .user(saveUser1)
                    .commentCount(10)
                    .build();
            Project saveProject1 = projectRepository.save(project1);

            // 포지션 세팅
            Position position1 = Position.builder()
                    .name("testPosition1")
                    .build();
            Position position2 = Position.builder()
                    .name("testPosition2")
                    .build();
            positionRepository.save(position1);
            positionRepository.save(position2);

            // 프로젝트 포지션 세팅
            ProjectPosition projectPosition1 = ProjectPosition.builder()
                    .state(true)
                    .project(project1)
                    .position(position1)
                    .user(null)
                    .creator(false)
                    .build();
            ProjectPosition projectPosition2 = ProjectPosition.builder()
                    .state(false)
                    .project(project1)
                    .position(position2)
                    .user(null)
                    .creator(false)
                    .build();
            ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);
            ProjectPosition saveProjectPosition2 = projectPositionRepository.save(projectPosition2);

            // 참여 신청 세팅
            ProjectParticipateRequest projectParticipateRequest1 = ProjectParticipateRequest.builder()
                    .user(saveUser1)
                    .projectPosition(saveProjectPosition1)
                    .motive("testMotive1")
                    .github("testGitHub1")
                    .build();
            ProjectParticipateRequest projectParticipateRequest2 = ProjectParticipateRequest.builder()
                    .user(saveUser1)
                    .projectPosition(saveProjectPosition1)
                    .motive("testMotive2")
                    .github("testGitHub2")
                    .build();

            projectParticipateRequestRepository.save(projectParticipateRequest1);
            projectParticipateRequestRepository.save(projectParticipateRequest2);

            // when
            assertEquals(projectParticipateRequestRepository.findAll().size(), 2);

            projectParticipateRequestRepository.deleteByProjectNo(saveProject1.getNo());

            // then
            assertEquals(projectParticipateRequestRepository.findAll().size(), 0);
        }
    }
}