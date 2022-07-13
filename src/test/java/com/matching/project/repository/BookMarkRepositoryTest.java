package com.matching.project.repository;

import com.matching.project.config.QuerydslConfiguration;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.entity.BookMark;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@Import(QuerydslConfiguration.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class BookMarkRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private BookMarkRepository bookMarkRepository;

    @Test
    public void 북마크_유저_조회_조인() {
        // given
        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        User user1 = User.builder()
                .name("testUser1")
                .sex('M')
                .email("testEmail1@test.com")
                .password("testPassword1")
                .github("testGithub1")
                .selfIntroduction("testSelfIntroduction1")
                .block(false)
                .blockReason("testBlockReason1")
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(false)
                .imageNo(0L)
                .position(null)
                .build();

        User saveUser1 = userRepository.save(user1);

        Project project1 = Project.builder()
                .name("testProject1")
                .createUserName("testUser1")
                .createDate(createDate)
                .startDate(startDate)
                .endDate(endDate)
                .state(true)
                .introduction("testIntroduction1")
                .maxPeople(10)
                .currentPeople(4)
                .delete(false)
                .deleteReason(null)
                .imageNo(0L)
                .viewCount(0)
                .commentCount(0)
                .build();

        Project project2 = Project.builder()
                .name("testProject2")
                .createUserName("testUser1")
                .createDate(createDate)
                .startDate(startDate)
                .endDate(endDate)
                .state(true)
                .introduction("testIntroduction2")
                .maxPeople(10)
                .currentPeople(4)
                .delete(false)
                .deleteReason(null)
                .imageNo(0L)
                .viewCount(0)
                .commentCount(0)
                .build();

        Project saveProject1 = projectRepository.save(project1);
        Project saveProject2 = projectRepository.save(project2);

        BookMark bookMark1 = BookMark.builder()
                .user(user1)
                .project(project1)
                .build();

        BookMark bookMark2 = BookMark.builder()
                .user(user1)
                .project(project2)
                .build();

        bookMarkRepository.save(bookMark1);
        bookMarkRepository.save(bookMark2);

        // when
        List<BookMark> bookMarkList = bookMarkRepository.findByUserNo(saveUser1.getNo());

        // then
        assertEquals(bookMarkList.size(), 2);
        assertEquals(bookMarkList.get(0).getUser(), saveUser1);
        assertEquals(bookMarkList.get(0).getProject(), saveProject1);
        assertEquals(bookMarkList.get(1).getUser(), saveUser1);
        assertEquals(bookMarkList.get(1).getProject(), saveProject2);
    }

    @Test
    public void 북마크_존재_확인() {
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
        Project saveProject1 = projectRepository.save(project1);

        BookMark bookMark1 = BookMark.builder()
                .project(saveProject1)
                .user(saveUser1)
                .build();
        bookMarkRepository.save(bookMark1);

        // when
        boolean result = bookMarkRepository.existBookMark(saveUser1, saveProject1);

        // then
        assertEquals(result, true);
    }
}