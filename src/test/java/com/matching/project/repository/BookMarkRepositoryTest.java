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
    UserRepository userRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    BookMarkRepository bookMarkRepository;

    @Test
    public void 북마크_유저_조회_테스트() {
        // given
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        // 유저 세팅
        User user1 = User.builder()
                .name("userName1")
                .sex("M")
                .email("wkemrm12@naver.com")
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

        // 프로젝트 세팅
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
                .commentCount(10)
                .build();
        Project saveProject1 = projectRepository.save(project1);

        // 북마크 세팅
        BookMark bookMark1 = BookMark.builder()
                .user(saveUser1)
                .project(saveProject1)
                .build();

        BookMark saveBookMark1 = bookMarkRepository.save(bookMark1);

        // when
        List<BookMark> bookMarkList = bookMarkRepository.findByUserNo(saveUser1.getNo()).get();

        // then
        assertEquals(bookMarkList.get(0).getNo(), saveBookMark1.getNo());
        assertEquals(bookMarkList.get(0).getProject().getNo(), saveProject1.getNo());
        assertEquals(bookMarkList.get(0).getUser().getNo(), saveUser1.getNo());
    }

    @Test
    public void 북마크_존재_테스트() {
        // given
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        // 유저 세팅
        User user1 = User.builder()
                .name("userName1")
                .sex("M")
                .email("wkemrm12@naver.com")
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

        // 프로젝트 세팅
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
                .commentCount(10)
                .build();
        Project saveProject1 = projectRepository.save(project1);

        // 북마크 세팅
        BookMark bookMark1 = BookMark.builder()
                .user(saveUser1)
                .project(saveProject1)
                .build();

        bookMarkRepository.save(bookMark1);

        // when
        boolean result = bookMarkRepository.existBookMark(saveUser1, saveProject1);

        // then
        assertEquals(result, true);
    }

    @Test
    public void 북마크_삭제_테스트() {
        // given
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        // 유저 세팅
        User user1 = User.builder()
                .name("userName1")
                .sex("M")
                .email("wkemrm12@naver.com")
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
        
        // 프로젝트 세팅
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
                .commentCount(10)
                .build();
        Project saveProject1 = projectRepository.save(project1);
        
        // 북마크 세팅
        BookMark bookMark1 = BookMark.builder()
                .user(saveUser1)
                .project(saveProject1)
                .build();

        BookMark saveBookMark1 = bookMarkRepository.save(bookMark1);

        // when
        bookMarkRepository.deleteByProjectNo(saveProject1.getNo());

        // then
        List<BookMark> bookMarkList = bookMarkRepository.findAll();

        assertEquals(bookMarkList.isEmpty(), true);
    }
}