package com.matching.project.repository;

import com.matching.project.config.QuerydslConfiguration;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.entity.Comment;
import com.matching.project.entity.Project;
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
class CommentRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    public void 프로젝트_번호_검색() {
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

        Project project2 = Project.builder()
                .name("testName2")
                .createUserName("user1")
                .createDate(createDate)
                .startDate(startDate)
                .endDate(endDate)
                .state(true)
                .introduction("testIntroduction2")
                .maxPeople(10)
                .currentPeople(4)
                .delete(true)
                .deleteReason(null)
                .viewCount(10)
                .commentCount(10)
                .build();

        Project saveProject1 = projectRepository.save(project1);
        Project saveProject2 = projectRepository.save(project2);

        Comment comment1 = Comment.builder()
                .user(saveUser1)
                .project(saveProject1)
                .content("testContent1")
                .build();
        Comment comment2 = Comment.builder()
                .user(saveUser1)
                .project(saveProject2)
                .content("testContent1")
                .build();
        Comment comment3 = Comment.builder()
                .user(saveUser1)
                .project(saveProject2)
                .content("testContent1")
                .build();
        Comment saveComment1 = commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);

        // when
        List<Comment> commentList = commentRepository.findByProjectNo(project1);

        //then
        assertEquals(commentList.size(), 1);
        assertEquals(commentList.get(0).getProject(), saveProject1);
        assertEquals(commentList.get(0).getUser(), saveUser1);
        assertEquals(commentList.get(0).getContent(), saveComment1.getContent());
    }
}