package com.matching.project.repository;

import com.matching.project.config.QuerydslConfiguration;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@Import(QuerydslConfiguration.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class ParticipateRequestTechnicalStackRepositoryTest {
    @Autowired
    ParticipateRequestTechnicalStackRepository participateRequestTechnicalStackRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ProjectParticipateRequestRepository projectParticipateRequestRepository;

    @Autowired
    ProjectPositionRepository projectPositionRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    TechnicalStackRepository technicalStackRepository;

    @Test
    public void 신청_기술스택_삭제_테스트() {
        // given
        LocalDateTime createDate = LocalDateTime.now();
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
                .createDate(createDate.plusDays(1))
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
        
        // 기술 스택 세팅
        TechnicalStack technicalStack1 = TechnicalStack.builder()
                .name("testTechnicalStack1")
                .build();
        TechnicalStack technicalStack2 = TechnicalStack.builder()
                .name("testTechnicalStack2")
                .build();
        TechnicalStack technicalStack3 = TechnicalStack.builder()
                .name("testTechnicalStack3")
                .build();
        TechnicalStack saveTechnicalStack1 = technicalStackRepository.save(technicalStack1);
        TechnicalStack saveTechnicalStack2 = technicalStackRepository.save(technicalStack2);
        TechnicalStack saveTechnicalStack3 = technicalStackRepository.save(technicalStack3);
        
        // 신청 기술 스택 세팅
        ParticipateRequestTechnicalStack participateRequestTechnicalStack1 = ParticipateRequestTechnicalStack.builder()
                .projectParticipateRequest(saveProjectParticipateRequest1)
                .technicalStack(saveTechnicalStack1)
                .build();
        ParticipateRequestTechnicalStack participateRequestTechnicalStack2 = ParticipateRequestTechnicalStack.builder()
                .projectParticipateRequest(saveProjectParticipateRequest1)
                .technicalStack(saveTechnicalStack2)
                .build();
        ParticipateRequestTechnicalStack participateRequestTechnicalStack3 = ParticipateRequestTechnicalStack.builder()
                .projectParticipateRequest(saveProjectParticipateRequest1)
                .technicalStack(saveTechnicalStack3)
                .build();

        ParticipateRequestTechnicalStack saveParticipateRequestTechnicalStack1 = participateRequestTechnicalStackRepository.save(participateRequestTechnicalStack1);
        ParticipateRequestTechnicalStack saveParticipateRequestTechnicalStack2 = participateRequestTechnicalStackRepository.save(participateRequestTechnicalStack2);
        ParticipateRequestTechnicalStack saveParticipateRequestTechnicalStack3 = participateRequestTechnicalStackRepository.save(participateRequestTechnicalStack3);

        // when
        try {
            participateRequestTechnicalStackRepository.deleteByProjectParticipateNo(saveProjectParticipateRequest1.getNo());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // then
        List<ParticipateRequestTechnicalStack> participateRequestTechnicalStackList = participateRequestTechnicalStackRepository.findAll();
        assertEquals(participateRequestTechnicalStackList.size(), 0);
    }
}