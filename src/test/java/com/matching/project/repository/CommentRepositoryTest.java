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