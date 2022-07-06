package com.matching.project.repository;

import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.entity.Project;
import com.matching.project.entity.ProjectUser;
import com.matching.project.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class ProjectUserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    @Test
    public void 프로젝트_번호_검색() {
        // given
        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        User user1 = User.builder()
                .name("testUserName1")
                .sex('M')
                .email("testEmail1")
                .password("testPassword")
                .github("testGithub")
                .selfIntroduction("testSelfIntroduction")
                .block(false)
                .blockReason(null)
                .oauthCategory(OAuth.NORMAL)
                .permission(Role.ROLE_USER)
                .image(null)
                .userPosition(null)
                .build();

        User user2 = User.builder()
                .name("testUserName2")
                .sex('M')
                .email("testEmail2")
                .password("testPassword")
                .github("testGithub")
                .selfIntroduction("testSelfIntroduction")
                .block(false)
                .blockReason(null)
                .oauthCategory(OAuth.NORMAL)
                .permission(Role.ROLE_USER)
                .image(null)
                .userPosition(null)
                .build();

        Project project1 = Project.builder()
                .name("testProjectName1")
                .createUserName("testCreateUserName1")
                .createDate(createDate)
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
                .image(null)
                .build();

        Project project2 = Project.builder()
                .name("testProjectName2")
                .createUserName("testCreateUserName2")
                .createDate(createDate)
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
                .image(null)
                .build();

        // 유저 및 프로젝트 save
        User userSave1 = userRepository.save(user1);
        User userSave2 = userRepository.save(user2);
        Project projectSave1 = projectRepository.save(project1);
        Project projectSave2 = projectRepository.save(project2);

        ProjectUser projectUser1 = ProjectUser.builder()
                .creator(true)
                .user(userSave1)
                .project(projectSave1)
                .build();

        ProjectUser projectUser2 = ProjectUser.builder()
                .creator(false)
                .user(userSave2)
                .project(projectSave1)
                .build();

        ProjectUser projectUser3 = ProjectUser.builder()
                .creator(true)
                .user(userSave2)
                .project(projectSave2)
                .build();

        // ProjectUser save
        ProjectUser projectUserSave1 = projectUserRepository.save(projectUser1);
        ProjectUser projectUserSave2 = projectUserRepository.save(projectUser2);
        ProjectUser projectUserSave3 = projectUserRepository.save(projectUser3);


        // when
        List<ProjectUser> projectUserList = projectUserRepository.findByProjectNo(projectSave1.getNo());

        // then
        assertEquals(projectUserList.size(), 2);
        assertEquals(projectUserList.get(0).getNo(), projectUserSave1.getNo());
        assertEquals(projectUserList.get(0).isCreator(), projectUserSave1.isCreator());
        assertEquals(projectUserList.get(0).getUser(), projectUserSave1.getUser());
        assertEquals(projectUserList.get(0).getProject(), projectUserSave1.getProject());

        assertEquals(projectUserList.get(1).getNo(), projectUserSave2.getNo());
        assertEquals(projectUserList.get(1).isCreator(), projectUserSave2.isCreator());
        assertEquals(projectUserList.get(1).getUser(), projectUserSave2.getUser());
        assertEquals(projectUserList.get(1).getProject(), projectUserSave2.getProject());
    }
}