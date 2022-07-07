package com.matching.project.repository;

import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.entity.Comment;
import com.matching.project.entity.Project;
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

        User userSave1 = userRepository.save(user1);
        User userSave2 = userRepository.save(user2);
        Project projectSave1 = projectRepository.save(project1);

        Comment comment1 = Comment.builder()
                .content("comment1")
                .user(userSave1)
                .project(projectSave1)
                .build();
        Comment comment2 = Comment.builder()
                .content("comment2")
                .user(userSave1)
                .project(projectSave1)
                .build();
        Comment comment3 = Comment.builder()
                .content("comment3")
                .user(userSave2)
                .project(projectSave1)
                .build();

        Comment commentSave1 = commentRepository.save(comment1);
        Comment commentSave2 = commentRepository.save(comment2);
        Comment commentSave3 = commentRepository.save(comment3);

        // when
        List<Comment> commentList = commentRepository.findByProjectNo(projectSave1.getNo());

        // then
        assertEquals(commentList.size(), 3);
        assertEquals(commentList.get(0).getNo(), commentSave1.getNo());
        assertEquals(commentList.get(0).getContent(), commentSave1.getContent());
        assertEquals(commentList.get(0).getUser(), commentSave1.getUser());
        assertEquals(commentList.get(0).getProject(), commentSave1.getProject());

        assertEquals(commentList.get(1).getNo(), commentSave2.getNo());
        assertEquals(commentList.get(1).getContent(), commentSave2.getContent());
        assertEquals(commentList.get(1).getUser(), commentSave2.getUser());
        assertEquals(commentList.get(1).getProject(), commentSave2.getProject());

        assertEquals(commentList.get(2).getNo(), commentSave3.getNo());
        assertEquals(commentList.get(2).getContent(), commentSave3.getContent());
        assertEquals(commentList.get(2).getUser(), commentSave3.getUser());
        assertEquals(commentList.get(2).getProject(), commentSave3.getProject());
    }
}