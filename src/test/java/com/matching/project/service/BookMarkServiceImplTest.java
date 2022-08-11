package com.matching.project.service;

import com.matching.project.dto.SliceDto;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.project.ProjectSimpleDto;
import com.matching.project.dto.project.ProjectSimplePositionDto;
import com.matching.project.dto.project.ProjectSimpleTechnicalStackDto;
import com.matching.project.entity.BookMark;
import com.matching.project.entity.Project;
import com.matching.project.entity.User;
import com.matching.project.repository.BookMarkRepository;
import com.matching.project.repository.ProjectRepository;
import com.matching.project.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookMarkServiceImplTest {
    @Mock
    ProjectRepository projectRepository;

    @Mock
    BookMarkRepository bookMarkRepository;

    @InjectMocks
    BookMarkServiceImpl bookMarkService;

    @Nested
    @DisplayName("즐겨찾기 등록")
    class testBookMarkRegister {
        @Test
        @DisplayName("성공")
        public void success() {
            // given
            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

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

            // 프로젝트 세팅
            Project project1 = Project.builder()
                    .no(1L)
                    .name("testName1")
                    .createUserName("testUser1")
                    .startDate(startDate)
                    .endDate(endDate)
                    .state(true)
                    .introduction("testIntroduction1")
                    .maxPeople(10)
                    .currentPeople(4)
                    .viewCount(10)
                    .commentCount(10)
                    .build();
            // 북마크 세팅
            BookMark bookMark1 = BookMark.builder()
                    .no(1L)
                    .user(user1)
                    .project(project1)
                    .build();

            given(projectRepository.findById(any())).willReturn(Optional.of(project1));
            given(bookMarkRepository.save(any())).willReturn(bookMark1);

            // when
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user1, "", user1.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            boolean result = false;
            try {
                result = bookMarkService.bookMarkRegister(project1.getNo());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // then
            verify(projectRepository).findById(any());
            verify(bookMarkRepository).save(any());

            assertEquals(result, true);
        }
    }

    @Nested
    @DisplayName("즐겨찾기 삭제")
    class testBookMarkDelete {
        @Test
        @DisplayName("성공")
        public void success() {
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

            // when
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user1, "", user1.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            boolean result = false;
            try {
                result = bookMarkService.bookMarkDelete(1L);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // then
            verify(bookMarkRepository).deleteByProjectNo(any());

            assertEquals(result, true);
        }
    }

    @Nested
    @DisplayName("즐겨찾기 프로젝트 조회")
    class testFindBookMarkProject {
        @Test
        @DisplayName("성공")
        public void success() {
            // 유저 객체
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

            // 프로젝트 객체
            List<ProjectSimpleDto> projectSimpleDtoList = new ArrayList<>();
            ProjectSimpleDto projectSimpleDto1 = ProjectSimpleDto.builder()
                    .projectNo(1L)
                    .name("testName1")
                    .maxPeople(10)
                    .currentPeople(4)
                    .viewCount(10)
                    .register("testUser1")
                    .bookMark(true)
                    .build();

            // ProjectSimplePositionDto 객체
            ProjectSimplePositionDto positionName1 = ProjectSimplePositionDto.builder()
                    .projectNo(1L)
                    .positionNo(1L)
                    .positionName("testPositionName1")
                    .build();

            ProjectSimplePositionDto positionName2 = ProjectSimplePositionDto.builder()
                    .projectNo(1L)
                    .positionNo(2L)
                    .positionName("testPositionName2")
                    .build();
            List<ProjectSimplePositionDto> projectSimplePositionDtoList1 = new ArrayList<>();
            projectSimplePositionDtoList1.add(positionName1);
            projectSimplePositionDtoList1.add(positionName2);
            projectSimpleDto1.setProjectSimplePositionDtoList(projectSimplePositionDtoList1);

            // ProjectSimpleTechnicalStackDto 객체
            ProjectSimpleTechnicalStackDto technicalStackName1 = ProjectSimpleTechnicalStackDto.builder()
                    .projectNo(1L)
                    .image("testImage1")
                    .technicalStackName("testTechnicalStackName1")
                    .build();

            ProjectSimpleTechnicalStackDto technicalStackName2 = ProjectSimpleTechnicalStackDto.builder()
                    .projectNo(1L)
                    .image("testImage2")
                    .technicalStackName("testTechnicalStackName2")
                    .build();

            List<ProjectSimpleTechnicalStackDto> projectSimpleTechnicalStackDtoList1 = new ArrayList<>();
            projectSimpleTechnicalStackDtoList1.add(technicalStackName1);
            projectSimpleTechnicalStackDtoList1.add(technicalStackName2);
            projectSimpleDto1.setProjectSimpleTechnicalStackDtoList(projectSimpleTechnicalStackDtoList1);

            // projectSimpleDtoList 세팅
            projectSimpleDtoList.add(projectSimpleDto1);

            ProjectSimpleDto projectSimpleDto2 = ProjectSimpleDto.builder()
                    .projectNo(2L)
                    .name("testName2")
                    .maxPeople(10)
                    .currentPeople(4)
                    .viewCount(10)
                    .register("testUser1")
                    .bookMark(true)
                    .build();

            // ProjectSimplePositionDto 객체
            ProjectSimplePositionDto positionName3 = ProjectSimplePositionDto.builder()
                    .projectNo(2L)
                    .positionNo(1L)
                    .positionName("testPositionName3")
                    .build();

            ProjectSimplePositionDto positionName4 = ProjectSimplePositionDto.builder()
                    .projectNo(2L)
                    .positionNo(2L)
                    .positionName("testPositionName4")
                    .build();
            List<ProjectSimplePositionDto> projectSimplePositionDtoList2 = new ArrayList<>();
            projectSimplePositionDtoList2.add(positionName3);
            projectSimplePositionDtoList2.add(positionName4);
            projectSimpleDto2.setProjectSimplePositionDtoList(projectSimplePositionDtoList2);

            // ProjectSimpleTechnicalStackDto 객체
            ProjectSimpleTechnicalStackDto technicalStackName3 = ProjectSimpleTechnicalStackDto.builder()
                    .projectNo(2L)
                    .image("testImage3")
                    .technicalStackName("testTechnicalStackName3")
                    .build();

            ProjectSimpleTechnicalStackDto technicalStackName4 = ProjectSimpleTechnicalStackDto.builder()
                    .projectNo(2L)
                    .image("testImage4")
                    .technicalStackName("testTechnicalStackName4")
                    .build();

            List<ProjectSimpleTechnicalStackDto> projectSimpleTechnicalStackDtoList2 = new ArrayList<>();
            projectSimpleTechnicalStackDtoList2.add(technicalStackName3);
            projectSimpleTechnicalStackDtoList2.add(technicalStackName4);
            projectSimpleDto2.setProjectSimpleTechnicalStackDtoList(projectSimpleTechnicalStackDtoList2);

            // projectSimpleDtoList 세팅
            projectSimpleDtoList.add(projectSimpleDto2);

            // List to Page
            Pageable pageable = PageRequest.of(0, 5, Sort.by("createDate").descending());
            int start = (int)pageable.getOffset();
            int end = (start + pageable.getPageSize()) > projectSimpleDtoList.size() ? projectSimpleDtoList.size() : (start + pageable.getPageSize());
            Page<ProjectSimpleDto> projectPage = new PageImpl<>(projectSimpleDtoList.subList(start, end), pageable, projectSimpleDtoList.size());

            given(projectRepository.findBookMarkProject(any(Pageable.class), any(Long.class), any(User.class))).willReturn(projectPage);

            SliceDto<ProjectSimpleDto> result = null;

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user1, "", user1.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
            try {
                result = bookMarkService.findBookMarkProject(Long.MAX_VALUE, pageable);
            } catch (Exception e) {
                e.printStackTrace();
            }

            verify(projectRepository).findBookMarkProject(any(Pageable.class), any(Long.class), any(User.class));

            assertEquals(result.getContent().size(), 2);
            assertEquals(result.getContent().get(0).getProjectNo(), projectSimpleDto1.getProjectNo());
            assertEquals(result.getContent().get(0).getName(), projectSimpleDto1.getName());
            assertEquals(result.getContent().get(0).getMaxPeople(), projectSimpleDto1.getMaxPeople());
            assertEquals(result.getContent().get(0).getCurrentPeople(), projectSimpleDto1.getCurrentPeople());
            assertEquals(result.getContent().get(0).getViewCount(), projectSimpleDto1.getViewCount());
            assertEquals(result.getContent().get(0).getRegister(), user1.getName());
            assertEquals(result.getContent().get(0).isBookMark(), true);

            assertEquals(result.getContent().get(0).getProjectSimplePositionDtoList().get(0).getProjectNo(), positionName1.getProjectNo());
            assertEquals(result.getContent().get(0).getProjectSimplePositionDtoList().get(0).getPositionNo(), positionName1.getPositionNo());
            assertEquals(result.getContent().get(0).getProjectSimplePositionDtoList().get(0).getPositionName(), positionName1.getPositionName());
            assertEquals(result.getContent().get(0).getProjectSimplePositionDtoList().get(1).getProjectNo(), positionName2.getProjectNo());
            assertEquals(result.getContent().get(0).getProjectSimplePositionDtoList().get(1).getPositionNo(), positionName2.getPositionNo());
            assertEquals(result.getContent().get(0).getProjectSimplePositionDtoList().get(1).getPositionName(), positionName2.getPositionName());

            assertEquals(result.getContent().get(0).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), technicalStackName1.getProjectNo());
            assertEquals(result.getContent().get(0).getProjectSimpleTechnicalStackDtoList().get(0).getImage(), technicalStackName1.getImage());
            assertEquals(result.getContent().get(0).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), technicalStackName1.getTechnicalStackName());
            assertEquals(result.getContent().get(0).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), technicalStackName2.getProjectNo());
            assertEquals(result.getContent().get(0).getProjectSimpleTechnicalStackDtoList().get(1).getImage(), technicalStackName2.getImage());
            assertEquals(result.getContent().get(0).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), technicalStackName2.getTechnicalStackName());

            assertEquals(result.getContent().get(1).getProjectNo(), projectSimpleDto2.getProjectNo());
            assertEquals(result.getContent().get(1).getName(), projectSimpleDto2.getName());
            assertEquals(result.getContent().get(1).getMaxPeople(), projectSimpleDto2.getMaxPeople());
            assertEquals(result.getContent().get(1).getCurrentPeople(), projectSimpleDto2.getCurrentPeople());
            assertEquals(result.getContent().get(1).getViewCount(), projectSimpleDto2.getViewCount());
            assertEquals(result.getContent().get(1).getRegister(), user1.getName());
            assertEquals(result.getContent().get(1).isBookMark(), true);

            assertEquals(result.getContent().get(1).getProjectSimplePositionDtoList().get(0).getProjectNo(), positionName3.getProjectNo());
            assertEquals(result.getContent().get(1).getProjectSimplePositionDtoList().get(0).getPositionNo(), positionName3.getPositionNo());
            assertEquals(result.getContent().get(1).getProjectSimplePositionDtoList().get(0).getPositionName(), positionName3.getPositionName());
            assertEquals(result.getContent().get(1).getProjectSimplePositionDtoList().get(1).getProjectNo(), positionName4.getProjectNo());
            assertEquals(result.getContent().get(1).getProjectSimplePositionDtoList().get(1).getPositionNo(), positionName4.getPositionNo());
            assertEquals(result.getContent().get(1).getProjectSimplePositionDtoList().get(1).getPositionName(), positionName4.getPositionName());

            assertEquals(result.getContent().get(1).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), technicalStackName3.getProjectNo());
            assertEquals(result.getContent().get(1).getProjectSimpleTechnicalStackDtoList().get(0).getImage(), technicalStackName3.getImage());
            assertEquals(result.getContent().get(1).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), technicalStackName3.getTechnicalStackName());
            assertEquals(result.getContent().get(1).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), technicalStackName4.getProjectNo());
            assertEquals(result.getContent().get(1).getProjectSimpleTechnicalStackDtoList().get(1).getImage(), technicalStackName4.getImage());
            assertEquals(result.getContent().get(1).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), technicalStackName4.getTechnicalStackName());

            assertEquals(result.isLast(), true);
        }
    }
}