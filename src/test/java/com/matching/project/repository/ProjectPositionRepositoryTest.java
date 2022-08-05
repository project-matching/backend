package com.matching.project.repository;

import com.matching.project.config.QuerydslConfiguration;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.entity.Position;
import com.matching.project.entity.Project;
import com.matching.project.entity.ProjectPosition;
import com.matching.project.entity.User;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Import(QuerydslConfiguration.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class ProjectPositionRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private ProjectPositionRepository projectPositionRepository;

    @Test
    public void 프로젝트_포지션_프로젝트_조회() {
        // given
        LocalDateTime createDate = LocalDateTime.now();
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

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
        User saveUser1 = userRepository.save(user1);

        Project project1 = Project.builder()
                .name("testName1")
                .createUserName("user1")
                .createDate(createDate)
                .startDate(startDate)
                .endDate(endDate)
                .state(true)
                .introduction("testIntroduction1")
                .maxPeople(10)
                .currentPeople(4)
                .delete(true)
                .deleteReason(null)
                .viewCount(10)
                .commentCount(10)
                .build();
        Project saveProject1 = projectRepository.save(project1);

        Position position1 = Position.builder()
                .name("testPosition1")
                .build();
        Position position2 = Position.builder()
                .name("testPosition2")
                .build();
        Position savePosition1 = positionRepository.save(position1);
        Position savePosition2 = positionRepository.save(position2);

        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .state(true)
                .project(saveProject1)
                .position(savePosition1)
                .user(saveUser1)
                .creator(false)
                .build();
        ProjectPosition projectPosition2 = ProjectPosition.builder()
                .state(false)
                .project(saveProject1)
                .position(savePosition2)
                .user(null)
                .creator(false)
                .build();
        projectPositionRepository.save(projectPosition1);
        projectPositionRepository.save(projectPosition2);

        // when
        List<ProjectPosition> projectPositionList = projectPositionRepository.findProjectAndPositionAndUserUsingFetchJoinByProjectNo(project1);

        // then
        assertEquals(projectPositionList.get(0).getProject(), saveProject1);
        assertEquals(projectPositionList.get(0).getPosition(), savePosition1);
        assertEquals(projectPositionList.get(0).getUser(), saveUser1);
        assertEquals(projectPositionList.get(0).isCreator(), false);
        assertEquals(projectPositionList.get(0).isState(), true);

        assertEquals(projectPositionList.get(1).getProject(), saveProject1);
        assertEquals(projectPositionList.get(1).getPosition(), savePosition2);
        assertEquals(projectPositionList.get(1).getUser(), null);
        assertEquals(projectPositionList.get(1).isCreator(), false);
        assertEquals(projectPositionList.get(1).isState(), false);
    }

    @Test
    @DisplayName("프로젝트 포지션 IN 삭제")
    public void testDeleteByNoIn() {
        // given
        LocalDateTime createDate = LocalDateTime.now();
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        Project project1 = Project.builder()
                .name("testName1")
                .createUserName("user1")
                .createDate(createDate)
                .startDate(startDate)
                .endDate(endDate)
                .state(true)
                .introduction("testIntroduction1")
                .maxPeople(10)
                .currentPeople(4)
                .delete(true)
                .deleteReason(null)
                .viewCount(10)
                .commentCount(10)
                .build();
        Project saveProject1 = projectRepository.save(project1);

        Position position1 = Position.builder()
                .name("testPosition1")
                .build();
        Position position2 = Position.builder()
                .name("testPosition2")
                .build();
        Position savePosition1 = positionRepository.save(position1);
        Position savePosition2 = positionRepository.save(position2);

        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .state(true)
                .project(saveProject1)
                .position(savePosition1)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition projectPosition2 = ProjectPosition.builder()
                .state(false)
                .project(saveProject1)
                .position(savePosition2)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);
        ProjectPosition saveProjectPosition2 = projectPositionRepository.save(projectPosition2);

        // when
        List<ProjectPosition> beforeProjectPositionList = projectPositionRepository.findAll();
        assertEquals(beforeProjectPositionList.size(), 2);

        List<Long> nos = new ArrayList<>();
        nos.add(saveProjectPosition1.getNo());
        nos.add(saveProjectPosition2.getNo());
        projectPositionRepository.deleteByNoIn(nos);

        // then
        List<ProjectPosition> afterProjectPositionList = projectPositionRepository.findAll();
        assertEquals(afterProjectPositionList.size(), 0);
    }

    @Nested
    @DisplayName("프로젝트번호 조건 삭제")
    class testDeleteByProjectNo {
        @Test
        @DisplayName("성공 테스트")
        public void testSuccess() {
            // given
            LocalDateTime createDate = LocalDateTime.now();
            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            Project project1 = Project.builder()
                    .name("testName1")
                    .createUserName("user1")
                    .createDate(createDate)
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(4)
                    .delete(true)
                    .deleteReason(null)
                    .viewCount(10)
                    .commentCount(10)
                    .build();
            Project saveProject1 = projectRepository.save(project1);

            Position position1 = Position.builder()
                    .name("testPosition1")
                    .build();
            Position position2 = Position.builder()
                    .name("testPosition2")
                    .build();
            Position savePosition1 = positionRepository.save(position1);
            Position savePosition2 = positionRepository.save(position2);

            ProjectPosition projectPosition1 = ProjectPosition.builder()
                    .state(true)
                    .project(saveProject1)
                    .position(savePosition1)
                    .user(null)
                    .creator(false)
                    .build();
            ProjectPosition projectPosition2 = ProjectPosition.builder()
                    .state(false)
                    .project(saveProject1)
                    .position(savePosition2)
                    .user(null)
                    .creator(false)
                    .build();
            projectPositionRepository.save(projectPosition1);
            projectPositionRepository.save(projectPosition2);

            // when
            assertEquals(projectPositionRepository.findAll().size(), 2);

            projectPositionRepository.deleteByProjectNo(saveProject1.getNo());

            // then
            assertEquals(projectPositionRepository.findAll().size(), 0);
        }
    }

    @Nested
    @DisplayName("유저 조인 프로젝트포지션 번호 조건 조회")
    class testFindUserFetchJoinByProjectPositionNo {
        @Test
        @DisplayName("성공 테스트")
        public void testSuccess() {
            // given
            LocalDateTime createDate = LocalDateTime.now();
            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

            Project project1 = Project.builder()
                    .name("testName1")
                    .createUserName("user1")
                    .createDate(createDate)
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(4)
                    .delete(true)
                    .deleteReason(null)
                    .viewCount(10)
                    .commentCount(10)
                    .build();
            Project saveProject1 = projectRepository.save(project1);

            Position position1 = Position.builder()
                    .name("testPosition1")
                    .build();
            Position savePosition1 = positionRepository.save(position1);

            ProjectPosition projectPosition1 = ProjectPosition.builder()
                    .state(true)
                    .project(saveProject1)
                    .position(savePosition1)
                    .user(null)
                    .creator(false)
                    .build();
            ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);

            // when
            ProjectPosition projectPosition = projectPositionRepository.findUserFetchJoinByProjectPositionNo(saveProjectPosition1.getNo()).orElseThrow(() -> new CustomException(ErrorCode.PROJECT_POSITION_NO_SUCH_ELEMENT_EXCEPTION));

            // then
            assertEquals(projectPosition.getNo(), saveProjectPosition1.getNo());
            assertEquals(projectPosition.isState(), saveProjectPosition1.isState());
            assertEquals(projectPosition.getProject(), saveProjectPosition1.getProject());
            assertEquals(projectPosition.getPosition(), saveProjectPosition1.getPosition());
            assertEquals(projectPosition.getUser(), saveProjectPosition1.getUser());
            assertEquals(projectPosition.isCreator(), saveProjectPosition1.isCreator());
        }
    }

}