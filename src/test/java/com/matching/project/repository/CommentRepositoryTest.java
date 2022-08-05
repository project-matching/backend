package com.matching.project.repository;

import com.matching.project.config.QuerydslConfiguration;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.entity.Comment;
import com.matching.project.entity.Project;
import com.matching.project.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    User saveUser(String email) {
        User user = User.builder()
                .name("testUser")
                .sex("M")
                .email(email)
                .password(passwordEncoder.encode("test"))
                .github(null)
                .selfIntroduction(null)
                .block(false)
                .blockReason(null)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(true)
                .imageNo(null)
                .position(null)
                .build();
        return userRepository.save(user);
    }

    Project saveProject(User user) {
        Project project = Project.builder()
                .name("testProject")
                .createUserName(user.getName())
                .createDate(LocalDateTime.now())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(5))
                .state(true)
                .introduction(null)
                .maxPeople(4)
                .currentPeople(1)
                .delete(false)
                .deleteReason(null)
                .viewCount(0)
                .commentCount(0)
                .build();
        return projectRepository.save(project);
    }

    @Test
    void findByProjectNoUsingPaging() {
        //given
        User user1 = saveUser("test1@test.com");
        User user2 = saveUser("test2@test.com");
        User user3 = saveUser("test3@test.com");
        Project project = saveProject(user1);

        Comment comment1 = Comment.builder()
                .project(project)
                .content("testComment1")
                .user(user1)
                .build();
        commentRepository.save(comment1);

        Comment comment2 = Comment.builder()
                .project(project)
                .content("testComment2")
                .user(user2)
                .build();
        commentRepository.save(comment2);

        Comment comment3 = Comment.builder()
                .project(project)
                .content("testComment3")
                .user(user3)
                .build();
        commentRepository.save(comment3);

        Pageable pageable = PageRequest.of(0, 2);

        //then;
        List<Comment> commentList = commentRepository.findByProjectOrderByNoDescUsingPaging(project, pageable);

        //when
        assertThat(commentList.size()).isEqualTo(2);
        assertThat(commentList.get(0).getUser().getName()).isEqualTo(comment3.getUser().getName());
        assertThat(commentList.get(0).getContent()).isEqualTo(comment3.getContent());
        assertThat(commentList.get(1).getUser().getName()).isEqualTo(comment2.getUser().getName());
        assertThat(commentList.get(1).getContent()).isEqualTo(comment2.getContent());
    }

    @Nested
    @DisplayName("댓글 프로젝트 번호 조건 삭제")
    class testDeleteByProjectNo {
        @Test
        @DisplayName("성공 테스트")
        public void testSuccess() {
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
            User saveUser1 = userRepository.save(user1);
            
            // 프로젝트 세팅
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

            Comment content1 = Comment.builder()
                    .project(project1)
                    .user(saveUser1)
                    .content("testContent1")
                    .build();

            Comment content2 = Comment.builder()
                    .project(project1)
                    .user(saveUser1)
                    .content("testContent2")
                    .build();
            commentRepository.save(content1);
            commentRepository.save(content2);

            // when
            assertEquals(commentRepository.findAll().size(), 2);

            commentRepository.deleteByProjectNo(saveProject1.getNo());

            // then
            assertEquals(commentRepository.findAll().size(), 0);
        }
    }
}