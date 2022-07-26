package com.matching.project.repository;

import com.matching.project.config.QuerydslConfiguration;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.entity.Position;
import com.matching.project.entity.Project;
import com.matching.project.entity.ProjectPosition;
import com.matching.project.entity.User;
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
class ProjectPositionRepositoryTest {
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private ProjectRepository projectRepository;
//
//    @Autowired
//    private PositionRepository positionRepository;
//
//    @Autowired
//    private ProjectPositionRepository projectPositionRepository;
//    @Test
//    public void 프로젝트_포지션_프로젝트_조회() {
//        // given
//        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
//        LocalDate startDate = LocalDate.of(2022, 06, 24);
//        LocalDate endDate = LocalDate.of(2022, 06, 28);
//
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
//        User saveUser1 = userRepository.save(user1);
//
//        Project project1 = Project.builder()
//                .name("testName1")
//                .createUserName("user1")
//                .createDate(createDate)
//                .startDate(startDate)
//                .endDate(endDate)
//                .state(true)
//                .introduction("testIntroduction1")
//                .maxPeople(10)
//                .currentPeople(4)
//                .delete(true)
//                .deleteReason(null)
//                .viewCount(10)
//                .commentCount(10)
//                .build();
//        Project saveProject1 = projectRepository.save(project1);
//
//        Position position1 = Position.builder()
//                .name("testPosition1")
//                .build();
//        Position position2 = Position.builder()
//                .name("testPosition2")
//                .build();
//        Position savePosition1 = positionRepository.save(position1);
//        Position savePosition2 = positionRepository.save(position2);
//
//        ProjectPosition projectPosition1 = ProjectPosition.builder()
//                .state(true)
//                .project(saveProject1)
//                .position(savePosition1)
//                .user(saveUser1)
//                .creator(false)
//                .build();
//        ProjectPosition projectPosition2 = ProjectPosition.builder()
//                .state(false)
//                .project(saveProject1)
//                .position(savePosition2)
//                .user(null)
//                .creator(false)
//                .build();
//        projectPositionRepository.save(projectPosition1);
//        projectPositionRepository.save(projectPosition2);
//
//        // when
//        List<ProjectPosition> projectPositionList = projectPositionRepository.findByProjectWithPositionAndProjectAndUserUsingLeftFetchJoin(project1);
//
//        // then
//        assertEquals(projectPositionList.get(0).getProject(), saveProject1);
//        assertEquals(projectPositionList.get(0).getPosition(), savePosition1);
//        assertEquals(projectPositionList.get(0).getUser(), saveUser1);
//        assertEquals(projectPositionList.get(0).isCreator(), false);
//        assertEquals(projectPositionList.get(0).isState(), true);
//
//        assertEquals(projectPositionList.get(1).getProject(), saveProject1);
//        assertEquals(projectPositionList.get(1).getPosition(), savePosition2);
//        assertEquals(projectPositionList.get(1).getUser(), null);
//        assertEquals(projectPositionList.get(1).isCreator(), false);
//        assertEquals(projectPositionList.get(1).isState(), false);
//    }

}