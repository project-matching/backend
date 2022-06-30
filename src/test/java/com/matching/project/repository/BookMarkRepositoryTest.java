package com.matching.project.repository;

import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.entity.BookMark;
import com.matching.project.entity.Project;
import com.matching.project.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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
                .name("testName1")
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
                .name("testName2")
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
                .name("testName1")
                .createDate(createDate)
                .startDate(startDate)
                .endDate(endDate)
                .state(true)
                .introduction("testIntroduction1")
                .maxPeople(10)
                .delete(false)
                .deleteReason(null)
                .viewCount(10)
                .commentCount(10)
                .image(null)
                .build();

        Project project2 = Project.builder()
                .name("testName2")
                .createDate(createDate)
                .startDate(startDate)
                .endDate(endDate)
                .state(false)
                .introduction("testIntroduction2")
                .maxPeople(10)
                .delete(false)
                .deleteReason(null)
                .viewCount(10)
                .commentCount(10)
                .image(null)
                .build();

        User saveUser1 = userRepository.save(user1);
        User saveUser2 = userRepository.save(user2);
        Project saveProject1 = projectRepository.save(project1);
        Project saveProject2 = projectRepository.save(project2);

        BookMark bookMark1 = BookMark.builder()
                .user(saveUser1)
                .project(saveProject1)
                .build();

        BookMark bookMark2 = BookMark.builder()
                .user(saveUser1)
                .project(saveProject2)
                .build();

        BookMark bookMark3 = BookMark.builder()
                .user(saveUser2)
                .project(saveProject1)
                .build();

        BookMark bookMark4 = BookMark.builder()
                .user(saveUser2)
                .project(saveProject2)
                .build();

        bookMarkRepository.save(bookMark1);
        bookMarkRepository.save(bookMark2);
        bookMarkRepository.save(bookMark3);
        bookMarkRepository.save(bookMark4);

        // when
        List<BookMark> findBookMarkList = bookMarkRepository.findByUserNo(saveUser1.getNo());

        // then
        assertEquals(findBookMarkList.size(), 2);

        assertEquals(findBookMarkList.get(0).getUser().getNo(), saveUser1.getNo());
        assertEquals(findBookMarkList.get(0).getUser().getName(), saveUser1.getName());
        assertEquals(findBookMarkList.get(0).getUser().getSex(), saveUser1.getSex());
        assertEquals(findBookMarkList.get(0).getUser().getEmail(), saveUser1.getEmail());
        assertEquals(findBookMarkList.get(0).getUser().getPassword(), saveUser1.getPassword());
        assertEquals(findBookMarkList.get(0).getUser().getGithub(), saveUser1.getGithub());
        assertEquals(findBookMarkList.get(0).getUser().getSelfIntroduction(), saveUser1.getSelfIntroduction());
        assertEquals(findBookMarkList.get(0).getUser().isBlock(), saveUser1.isBlock());
        assertEquals(findBookMarkList.get(0).getUser().getBlockReason(), saveUser1.getBlockReason());
        assertEquals(findBookMarkList.get(0).getUser().getPermission(), saveUser1.getPermission());
        assertEquals(findBookMarkList.get(0).getUser().getOauthCategory(), saveUser1.getOauthCategory());
        assertEquals(findBookMarkList.get(0).getUser().getImage(), saveUser1.getImage());
        assertEquals(findBookMarkList.get(0).getUser().getUserPosition(), saveUser1.getUserPosition());

        assertEquals(findBookMarkList.get(0).getProject().getNo(), saveProject1.getNo());
        assertEquals(findBookMarkList.get(0).getProject().getName(), saveProject1.getName());
        assertEquals(findBookMarkList.get(0).getProject().getStartDate(), saveProject1.getStartDate());
        assertEquals(findBookMarkList.get(0).getProject().getEndDate(), saveProject1.getEndDate());
        assertEquals(findBookMarkList.get(0).getProject().isState(), saveProject1.isState());
        assertEquals(findBookMarkList.get(0).getProject().getIntroduction(), saveProject1.getIntroduction());
        assertEquals(findBookMarkList.get(0).getProject().getMaxPeople(), saveProject1.getMaxPeople());
        assertEquals(findBookMarkList.get(0).getProject().isDelete(), saveProject1.isDelete());
        assertEquals(findBookMarkList.get(0).getProject().getDeleteReason(), saveProject1.getDeleteReason());
        assertEquals(findBookMarkList.get(0).getProject().getViewCount(), saveProject1.getViewCount());
        assertEquals(findBookMarkList.get(0).getProject().getCommentCount(), saveProject1.getCommentCount());
        assertEquals(findBookMarkList.get(0).getProject().getImage(), saveProject1.getImage());
    }
}