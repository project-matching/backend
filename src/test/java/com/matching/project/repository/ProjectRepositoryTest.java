package com.matching.project.repository;

import com.matching.project.config.JpaConfig;
import com.matching.project.config.QuerydslConfiguration;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.ProjectFilter;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.project.*;
import com.matching.project.entity.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Import({QuerydslConfiguration.class, JpaConfig.class})
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectPositionRepository projectPositionRepository;

    @Autowired
    private ProjectTechnicalStackRepository projectTechnicalStackRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private TechnicalStackRepository technicalStackRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectParticipateRequestRepository projectParticipateRequestRepository;

    @Autowired
    private BookMarkRepository bookMarkRepository;

    @Autowired
    private EntityManager em;

    @Test
    public void 프로젝트_탐색() {
        // given
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        // 모집 중인 프로젝트 객체
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

        // 모집 중인 프로젝트 객체
        Project project2 = Project.builder()
                .name("testName2")
                .createUserName("user1")
                .startDate(startDate)
                .endDate(endDate)
                .state(true)
                .introduction("testIntroduction2")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .commentCount(10)
                .build();

        // 모집 완료된 프로젝트 객체
        Project project3 = Project.builder()
                .name("testName3")
                .createUserName("user1")
                .startDate(startDate)
                .endDate(endDate)
                .state(false)
                .introduction("testIntroduction3")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .commentCount(10)
                .build();

        // 모집 중인 프로젝트
        Project saveProject1 = projectRepository.saveAndFlush(project1);
        // 모집 중인 프로젝트
        Project saveProject2 = projectRepository.saveAndFlush(project2);
        // 모집 완료된 프로젝트
        Project saveProject3 = projectRepository.saveAndFlush(project3);

        // 포지션 세팅
        Position position1 = Position.builder()
                .name("testPosition1")
                .build();
        Position position2 = Position.builder()
                .name("testPosition2")
                .build();
        positionRepository.save(position1);
        positionRepository.save(position2);

        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .state(true)
                .project(project1)
                .position(position1)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition projectPosition2 = ProjectPosition.builder()
                .state(false)
                .project(project1)
                .position(position2)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition projectPosition3 = ProjectPosition.builder()
                .state(true)
                .project(project2)
                .position(position1)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition projectPosition4 = ProjectPosition.builder()
                .state(false)
                .project(project2)
                .position(position2)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);
        ProjectPosition saveProjectPosition2 = projectPositionRepository.save(projectPosition2);
        ProjectPosition saveProjectPosition3 = projectPositionRepository.save(projectPosition3);
        ProjectPosition saveProjectPosition4 = projectPositionRepository.save(projectPosition4);
        
        // 이미지 세팅
        Image image1 = Image.builder()
                .logicalName("testLogicalName1")
                .physicalName("testPhysicalName1")
                .url("testUrl1")
                .build();
        Image saveImage1 = imageRepository.save(image1);
        
        // 기술 스택 세팅
        TechnicalStack technicalStack1 = TechnicalStack.builder()
                .name("testTechnicalStack1")
                .imageNo(saveImage1.getNo())
                .build();
        TechnicalStack technicalStack2 = TechnicalStack.builder()
                .name("testTechnicalStack2")
                .build();
        technicalStackRepository.save(technicalStack1);
        technicalStackRepository.save(technicalStack2);



        ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
                .project(project1)
                .technicalStack(technicalStack1)
                .build();
        ProjectTechnicalStack projectTechnicalStack2 = ProjectTechnicalStack.builder()
                .project(project1)
                .technicalStack(technicalStack2)
                .build();
        ProjectTechnicalStack projectTechnicalStack3 = ProjectTechnicalStack.builder()
                .project(project2)
                .technicalStack(technicalStack1)
                .build();
        ProjectTechnicalStack projectTechnicalStack4 = ProjectTechnicalStack.builder()
                .project(project2)
                .technicalStack(technicalStack2)
                .build();
        ProjectTechnicalStack saveProjectTechnicalStack1 = projectTechnicalStackRepository.save(projectTechnicalStack1);
        ProjectTechnicalStack saveProjectTechnicalStack2 = projectTechnicalStackRepository.save(projectTechnicalStack2);
        ProjectTechnicalStack saveProjectTechnicalStack3 = projectTechnicalStackRepository.save(projectTechnicalStack3);
        ProjectTechnicalStack saveProjectTechnicalStack4 = projectTechnicalStackRepository.save(projectTechnicalStack4);

        // when
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdDate").descending());

        // 모집 중이고, 삭제되지 않은 프로젝트 탐색
        Slice<ProjectSimpleDto> projectSimpleDtoSlice = null;
        try {
            projectSimpleDtoSlice = projectRepository.findProjectByStatus(pageable, Long.MAX_VALUE,true, new ProjectSearchRequestDto(ProjectFilter.PROJECT_NAME_AND_CONTENT, null)).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<ProjectSimpleDto> projectList =  projectSimpleDtoSlice.getContent();

        //then
        assertEquals(projectSimpleDtoSlice.getNumber(), 0);
        assertEquals(projectSimpleDtoSlice.getNumberOfElements(), 2);
        assertEquals(projectSimpleDtoSlice.hasNext(), false);
        assertEquals(projectSimpleDtoSlice.isFirst(), true);
        assertEquals(projectSimpleDtoSlice.isLast(), true);
        assertEquals(projectSimpleDtoSlice.hasContent(), true);

        assertEquals(projectList.size(), 2);
        assertEquals(projectList.get(0).getProjectNo(), saveProject2.getNo());
        assertEquals(projectList.get(0).getName(), saveProject2.getName());
        assertEquals(projectList.get(0).getMaxPeople(), saveProject2.getMaxPeople());
        assertEquals(projectList.get(0).getCurrentPeople(), saveProject2.getCurrentPeople());
        assertEquals(projectList.get(0).getViewCount(), saveProject2.getViewCount());
        assertEquals(projectList.get(0).getRegister(), saveProject2.getCreateUserName());
        assertEquals(projectList.get(0).isBookMark(), false);

        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getProjectNo(), saveProjectPosition3.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getPositionNo(), saveProjectPosition3.getPosition().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getPositionName(), saveProjectPosition3.getPosition().getName());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getProjectNo(), saveProjectPosition4.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getPositionNo(), saveProjectPosition4.getPosition().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getPositionName(), saveProjectPosition4.getPosition().getName());

        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), saveProjectTechnicalStack3.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), saveProjectTechnicalStack3.getTechnicalStack().getName());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getImage(), saveImage1.getLogicalName());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), saveProjectTechnicalStack4.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), saveProjectTechnicalStack4.getTechnicalStack().getName());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getImage(), null);

        assertEquals(projectList.get(1).getProjectNo(), saveProject1.getNo());
        assertEquals(projectList.get(1).getName(), saveProject1.getName());
        assertEquals(projectList.get(1).getMaxPeople(), saveProject1.getMaxPeople());
        assertEquals(projectList.get(1).getCurrentPeople(), saveProject1.getCurrentPeople());
        assertEquals(projectList.get(1).getViewCount(), saveProject1.getViewCount());
        assertEquals(projectList.get(1).getRegister(), saveProject1.getCreateUserName());
        assertEquals(projectList.get(1).isBookMark(), false);

        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(0).getProjectNo(), saveProjectPosition1.getProject().getNo());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(0).getPositionNo(), saveProjectPosition1.getPosition().getNo());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(0).getPositionName(), saveProjectPosition1.getPosition().getName());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(1).getProjectNo(), saveProjectPosition2.getProject().getNo());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(1).getPositionNo(), saveProjectPosition2.getPosition().getNo());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(1).getPositionName(), saveProjectPosition2.getPosition().getName());

        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), saveProjectTechnicalStack1.getProject().getNo());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), saveProjectTechnicalStack1.getTechnicalStack().getName());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getImage(), saveImage1.getLogicalName());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), saveProjectTechnicalStack2.getProject().getNo());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), saveProjectTechnicalStack2.getTechnicalStack().getName());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getImage(), null);
    }

    @Test
    public void 프로젝트_검색() {
        // given
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        // 모집 중인 프로젝트 객체
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

        // 모집 중인 프로젝트 객체
        Project project2 = Project.builder()
                .name("testName2")
                .createUserName("user1")
                .startDate(startDate)
                .endDate(endDate)
                .state(true)
                .introduction("testIntroduction2")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .commentCount(10)
                .build();

        // 모집 완료된 프로젝트 객체
        Project project3 = Project.builder()
                .name("testName3")
                .createUserName("user1")
                .startDate(startDate)
                .endDate(endDate)
                .state(false)
                .introduction("testIntroduction3")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .commentCount(10)
                .build();

        // 모집 중인 프로젝트
        Project saveProject1 = projectRepository.saveAndFlush(project1);
        // 모집 중인 프로젝트
        Project saveProject2 = projectRepository.saveAndFlush(project2);
        // 모집 완료된 프로젝트
        projectRepository.saveAndFlush(project3);

        // 포지션 세팅
        Position position1 = Position.builder()
                .name("testPosition1")
                .build();
        Position position2 = Position.builder()
                .name("testPosition2")
                .build();
        positionRepository.save(position1);
        positionRepository.save(position2);

        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .state(true)
                .project(project1)
                .position(position1)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition projectPosition2 = ProjectPosition.builder()
                .state(false)
                .project(project1)
                .position(position2)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition projectPosition3 = ProjectPosition.builder()
                .state(true)
                .project(project2)
                .position(position1)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition projectPosition4 = ProjectPosition.builder()
                .state(false)
                .project(project2)
                .position(position2)
                .user(null)
                .creator(false)
                .build();
        projectPositionRepository.save(projectPosition1);
        projectPositionRepository.save(projectPosition2);
        ProjectPosition saveProjectPosition3 = projectPositionRepository.save(projectPosition3);
        ProjectPosition saveProjectPosition4 = projectPositionRepository.save(projectPosition4);

        // 이미지 세팅
        Image image1 = Image.builder()
                .logicalName("testLogicalName1")
                .physicalName("testPhysicalName1")
                .url("testUrl1")
                .build();
        Image saveImage1 = imageRepository.save(image1);

        // 기술 스택 세팅
        TechnicalStack technicalStack1 = TechnicalStack.builder()
                .name("testTechnicalStack1")
                .imageNo(saveImage1.getNo())
                .build();
        TechnicalStack technicalStack2 = TechnicalStack.builder()
                .name("testTechnicalStack2")
                .build();
        technicalStackRepository.save(technicalStack1);
        technicalStackRepository.save(technicalStack2);

        ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
                .project(project1)
                .technicalStack(technicalStack1)
                .build();
        ProjectTechnicalStack projectTechnicalStack2 = ProjectTechnicalStack.builder()
                .project(project1)
                .technicalStack(technicalStack2)
                .build();
        ProjectTechnicalStack projectTechnicalStack3 = ProjectTechnicalStack.builder()
                .project(project2)
                .technicalStack(technicalStack1)
                .build();
        ProjectTechnicalStack projectTechnicalStack4 = ProjectTechnicalStack.builder()
                .project(project2)
                .technicalStack(technicalStack2)
                .build();
        projectTechnicalStackRepository.save(projectTechnicalStack1);
        projectTechnicalStackRepository.save(projectTechnicalStack2);
        ProjectTechnicalStack saveProjectTechnicalStack3 = projectTechnicalStackRepository.save(projectTechnicalStack3);
        ProjectTechnicalStack saveProjectTechnicalStack4 = projectTechnicalStackRepository.save(projectTechnicalStack4);

        // when
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdDate").descending());

        // 모집 중이고, 삭제되지 않은 프로젝트 중 Name2을 포함하고있는 리스트 탐색
        Slice<ProjectSimpleDto> projectSimpleDtoSlice = null;
        try {
            projectSimpleDtoSlice = projectRepository.findProjectByStatus(pageable, Long.MAX_VALUE, true, new ProjectSearchRequestDto(ProjectFilter.PROJECT_NAME_AND_CONTENT, "Name2")).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<ProjectSimpleDto> projectList =  projectSimpleDtoSlice.getContent();

        //then
        assertEquals(projectSimpleDtoSlice.getNumber(), 0);
        assertEquals(projectSimpleDtoSlice.getNumberOfElements(), 1);
        assertEquals(projectSimpleDtoSlice.hasNext(), false);
        assertEquals(projectSimpleDtoSlice.isFirst(), true);
        assertEquals(projectSimpleDtoSlice.isLast(), true);
        assertEquals(projectSimpleDtoSlice.hasContent(), true);

        assertEquals(projectList.size(), 1);
        assertEquals(projectList.get(0).getProjectNo(), saveProject2.getNo());
        assertEquals(projectList.get(0).getName(), saveProject2.getName());
        assertEquals(projectList.get(0).getMaxPeople(), saveProject2.getMaxPeople());
        assertEquals(projectList.get(0).getCurrentPeople(), saveProject2.getCurrentPeople());
        assertEquals(projectList.get(0).getViewCount(), saveProject2.getViewCount());
        assertEquals(projectList.get(0).getRegister(), saveProject2.getCreateUserName());
        assertEquals(projectList.get(0).isBookMark(), false);

        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getProjectNo(), saveProjectPosition3.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getPositionNo(), saveProjectPosition3.getPosition().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getPositionName(), saveProjectPosition3.getPosition().getName());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getProjectNo(), saveProjectPosition4.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getPositionNo(), saveProjectPosition4.getPosition().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getPositionName(), saveProjectPosition4.getPosition().getName());

        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), saveProjectTechnicalStack3.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), saveProjectTechnicalStack3.getTechnicalStack().getName());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getImage(), saveImage1.getLogicalName());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), saveProjectTechnicalStack4.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), saveProjectTechnicalStack4.getTechnicalStack().getName());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getImage(), null);
    }

    @Test
    public void 내가_등록한_프로젝트_탐색() {
        // given
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

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

        // 모집 중인 프로젝트 객체
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
                .user(saveUser1)
                .build();

        // 모집 완료된 프로젝트 객체
        Project project2 = Project.builder()
                .name("testName2")
                .createUserName("user1")
                .startDate(startDate)
                .endDate(endDate)
                .state(false)
                .introduction("testIntroduction2")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .commentCount(10)
                .user(saveUser1)
                .build();

        // 모집 완료된 프로젝트 객체
        Project project3 = Project.builder()
                .name("testName3")
                .createUserName("user1")
                .startDate(startDate)
                .endDate(endDate)
                .state(false)
                .introduction("testIntroduction3")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .commentCount(10)
                .build();

        // 모집 중인 프로젝트 (유저가 만든 프로젝트)
        Project saveProject1 = projectRepository.saveAndFlush(project1);
        // 모집 완료된 프로젝트 (유저가 만든 프로젝트)
        Project saveProject2 = projectRepository.saveAndFlush(project2);
        // 모집 완료된 프로젝트
        projectRepository.saveAndFlush(project3);

        // 포지션 세팅
        Position position1 = Position.builder()
                .name("testPosition1")
                .build();
        Position position2 = Position.builder()
                .name("testPosition2")
                .build();
        positionRepository.save(position1);
        positionRepository.save(position2);

        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .state(true)
                .project(project1)
                .position(position1)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition projectPosition2 = ProjectPosition.builder()
                .state(false)
                .project(project1)
                .position(position2)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition projectPosition3 = ProjectPosition.builder()
                .state(true)
                .project(project2)
                .position(position1)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition projectPosition4 = ProjectPosition.builder()
                .state(false)
                .project(project2)
                .position(position2)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);
        ProjectPosition saveProjectPosition2 = projectPositionRepository.save(projectPosition2);
        ProjectPosition saveProjectPosition3 = projectPositionRepository.save(projectPosition3);
        ProjectPosition saveProjectPosition4 = projectPositionRepository.save(projectPosition4);

        // 이미지 세팅
        Image image1 = Image.builder()
                .logicalName("testLogicalName1")
                .physicalName("testPhysicalName1")
                .url("testUrl1")
                .build();
        Image saveImage1 = imageRepository.save(image1);

        // 기술 스택 세팅
        TechnicalStack technicalStack1 = TechnicalStack.builder()
                .name("testTechnicalStack1")
                .imageNo(saveImage1.getNo())
                .build();
        TechnicalStack technicalStack2 = TechnicalStack.builder()
                .name("testTechnicalStack2")
                .build();
        technicalStackRepository.save(technicalStack1);
        technicalStackRepository.save(technicalStack2);

        ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
                .project(project1)
                .technicalStack(technicalStack1)
                .build();
        ProjectTechnicalStack projectTechnicalStack2 = ProjectTechnicalStack.builder()
                .project(project1)
                .technicalStack(technicalStack2)
                .build();
        ProjectTechnicalStack projectTechnicalStack3 = ProjectTechnicalStack.builder()
                .project(project2)
                .technicalStack(technicalStack1)
                .build();
        ProjectTechnicalStack projectTechnicalStack4 = ProjectTechnicalStack.builder()
                .project(project2)
                .technicalStack(technicalStack2)
                .build();
        ProjectTechnicalStack saveProjectTechnicalStack1 = projectTechnicalStackRepository.save(projectTechnicalStack1);
        ProjectTechnicalStack saveProjectTechnicalStack2 = projectTechnicalStackRepository.save(projectTechnicalStack2);
        ProjectTechnicalStack saveProjectTechnicalStack3 = projectTechnicalStackRepository.save(projectTechnicalStack3);
        ProjectTechnicalStack saveProjectTechnicalStack4 = projectTechnicalStackRepository.save(projectTechnicalStack4);

        // when
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdDate").descending());

        // 유저가 만든, 삭제되지 않은 프로젝트 탐색
        Slice<ProjectSimpleDto> projectSimpleDtoSlice = null;
        try {
            projectSimpleDtoSlice = projectRepository.findUserProject(pageable, Long.MAX_VALUE, saveUser1).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<ProjectSimpleDto> projectList =  projectSimpleDtoSlice.getContent();

        //then
        assertEquals(projectSimpleDtoSlice.getNumber(), 0);
        assertEquals(projectSimpleDtoSlice.getNumberOfElements(), 2);
        assertEquals(projectSimpleDtoSlice.hasNext(), false);
        assertEquals(projectSimpleDtoSlice.isFirst(), true);
        assertEquals(projectSimpleDtoSlice.isLast(), true);
        assertEquals(projectSimpleDtoSlice.hasContent(), true);

        assertEquals(projectList.size(), 2);
        assertEquals(projectList.get(0).getProjectNo(), saveProject2.getNo());
        assertEquals(projectList.get(0).getName(), saveProject2.getName());
        assertEquals(projectList.get(0).getMaxPeople(), saveProject2.getMaxPeople());
        assertEquals(projectList.get(0).getCurrentPeople(), saveProject2.getCurrentPeople());
        assertEquals(projectList.get(0).getViewCount(), saveProject2.getViewCount());
        assertEquals(projectList.get(0).getRegister(), saveProject2.getCreateUserName());
        assertEquals(projectList.get(0).isBookMark(), false);

        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getProjectNo(), saveProjectPosition3.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getPositionNo(), saveProjectPosition3.getPosition().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getPositionName(), saveProjectPosition3.getPosition().getName());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getProjectNo(), saveProjectPosition4.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getPositionNo(), saveProjectPosition4.getPosition().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getPositionName(), saveProjectPosition4.getPosition().getName());

        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), saveProjectTechnicalStack3.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), saveProjectTechnicalStack3.getTechnicalStack().getName());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getImage(), saveImage1.getLogicalName());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), saveProjectTechnicalStack4.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), saveProjectTechnicalStack4.getTechnicalStack().getName());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getImage(), null);

        assertEquals(projectList.get(1).getProjectNo(), saveProject1.getNo());
        assertEquals(projectList.get(1).getName(), saveProject1.getName());
        assertEquals(projectList.get(1).getMaxPeople(), saveProject1.getMaxPeople());
        assertEquals(projectList.get(1).getCurrentPeople(), saveProject1.getCurrentPeople());
        assertEquals(projectList.get(1).getViewCount(), saveProject1.getViewCount());
        assertEquals(projectList.get(1).getRegister(), saveProject1.getCreateUserName());
        assertEquals(projectList.get(1).isBookMark(), false);

        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(0).getProjectNo(), saveProjectPosition1.getProject().getNo());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(0).getPositionNo(), saveProjectPosition1.getPosition().getNo());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(0).getPositionName(), saveProjectPosition1.getPosition().getName());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(1).getProjectNo(), saveProjectPosition2.getProject().getNo());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(1).getPositionNo(), saveProjectPosition2.getPosition().getNo());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(1).getPositionName(), saveProjectPosition2.getPosition().getName());

        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), saveProjectTechnicalStack1.getProject().getNo());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), saveProjectTechnicalStack1.getTechnicalStack().getName());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getImage(), saveImage1.getLogicalName());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), saveProjectTechnicalStack2.getProject().getNo());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), saveProjectTechnicalStack2.getTechnicalStack().getName());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getImage(), null);
    }

    @Test
    public void 참여중인_프로젝트_탐색() {
        // given
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

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

        // 모집 중인 프로젝트 객체
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

        // 모집 완료된 프로젝트 객체
        Project project2 = Project.builder()
                .name("testName2")
                .createUserName("user1")
                .startDate(startDate)
                .endDate(endDate)
                .state(false)
                .introduction("testIntroduction2")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .commentCount(10)
                .build();

        // 모집 완료된 프로젝트 객체
        Project project3 = Project.builder()
                .name("testName3")
                .createUserName("user1")
                .startDate(startDate)
                .endDate(endDate)
                .state(false)
                .introduction("testIntroduction3")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .commentCount(10)
                .build();

        // 모집 중인 프로젝트 (참여중인 프로젝트)
        Project saveProject1 = projectRepository.saveAndFlush(project1);
        // 모집 완료된 프로젝트 (참여중인 프로젝트)
        Project saveProject2 = projectRepository.saveAndFlush(project2);
        // 모집 완료된 프로젝트
        projectRepository.saveAndFlush(project3);

        // 포지션 세팅
        Position position1 = Position.builder()
                .name("testPosition1")
                .build();
        Position position2 = Position.builder()
                .name("testPosition2")
                .build();
        positionRepository.save(position1);
        positionRepository.save(position2);

        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .state(true)
                .project(project1)
                .position(position1)
                .user(saveUser1)
                .creator(false)
                .build();
        ProjectPosition projectPosition2 = ProjectPosition.builder()
                .state(false)
                .project(project1)
                .position(position2)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition projectPosition3 = ProjectPosition.builder()
                .state(true)
                .project(project2)
                .position(position1)
                .user(saveUser1)
                .creator(false)
                .build();
        ProjectPosition projectPosition4 = ProjectPosition.builder()
                .state(false)
                .project(project2)
                .position(position2)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);
        ProjectPosition saveProjectPosition2 = projectPositionRepository.save(projectPosition2);
        ProjectPosition saveProjectPosition3 = projectPositionRepository.save(projectPosition3);
        ProjectPosition saveProjectPosition4 = projectPositionRepository.save(projectPosition4);

        // 이미지 세팅
        Image image1 = Image.builder()
                .logicalName("testLogicalName1")
                .physicalName("testPhysicalName1")
                .url("testUrl1")
                .build();
        Image saveImage1 = imageRepository.save(image1);

        // 기술 스택 세팅
        TechnicalStack technicalStack1 = TechnicalStack.builder()
                .name("testTechnicalStack1")
                .imageNo(saveImage1.getNo())
                .build();
        TechnicalStack technicalStack2 = TechnicalStack.builder()
                .name("testTechnicalStack2")
                .build();
        technicalStackRepository.save(technicalStack1);
        technicalStackRepository.save(technicalStack2);

        ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
                .project(project1)
                .technicalStack(technicalStack1)
                .build();
        ProjectTechnicalStack projectTechnicalStack2 = ProjectTechnicalStack.builder()
                .project(project1)
                .technicalStack(technicalStack2)
                .build();
        ProjectTechnicalStack projectTechnicalStack3 = ProjectTechnicalStack.builder()
                .project(project2)
                .technicalStack(technicalStack1)
                .build();
        ProjectTechnicalStack projectTechnicalStack4 = ProjectTechnicalStack.builder()
                .project(project2)
                .technicalStack(technicalStack2)
                .build();
        ProjectTechnicalStack saveProjectTechnicalStack1 = projectTechnicalStackRepository.save(projectTechnicalStack1);
        ProjectTechnicalStack saveProjectTechnicalStack2 = projectTechnicalStackRepository.save(projectTechnicalStack2);
        ProjectTechnicalStack saveProjectTechnicalStack3 = projectTechnicalStackRepository.save(projectTechnicalStack3);
        ProjectTechnicalStack saveProjectTechnicalStack4 = projectTechnicalStackRepository.save(projectTechnicalStack4);

        // when
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdDate").descending());

        // 참여중인 프로젝트 조회
        Slice<ProjectSimpleDto> projectSimpleDtoSlice = null;
        try {
            projectSimpleDtoSlice = projectRepository.findParticipateProject(pageable, Long.MAX_VALUE, saveUser1).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<ProjectSimpleDto> projectList =  projectSimpleDtoSlice.getContent();

        //then
        assertEquals(projectSimpleDtoSlice.getNumber(), 0);
        assertEquals(projectSimpleDtoSlice.getNumberOfElements(), 2);
        assertEquals(projectSimpleDtoSlice.hasNext(), false);
        assertEquals(projectSimpleDtoSlice.isFirst(), true);
        assertEquals(projectSimpleDtoSlice.isLast(), true);
        assertEquals(projectSimpleDtoSlice.hasContent(), true);

        assertEquals(projectList.size(), 2);
        assertEquals(projectList.get(0).getProjectNo(), saveProject2.getNo());
        assertEquals(projectList.get(0).getName(), saveProject2.getName());
        assertEquals(projectList.get(0).getMaxPeople(), saveProject2.getMaxPeople());
        assertEquals(projectList.get(0).getCurrentPeople(), saveProject2.getCurrentPeople());
        assertEquals(projectList.get(0).getViewCount(), saveProject2.getViewCount());
        assertEquals(projectList.get(0).getRegister(), saveProject2.getCreateUserName());
        assertEquals(projectList.get(0).isBookMark(), false);

        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getProjectNo(), saveProjectPosition3.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getPositionNo(), saveProjectPosition3.getPosition().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getPositionName(), saveProjectPosition3.getPosition().getName());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getProjectNo(), saveProjectPosition4.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getPositionNo(), saveProjectPosition4.getPosition().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getPositionName(), saveProjectPosition4.getPosition().getName());

        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), saveProjectTechnicalStack3.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), saveProjectTechnicalStack3.getTechnicalStack().getName());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getImage(), saveImage1.getLogicalName());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), saveProjectTechnicalStack4.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), saveProjectTechnicalStack4.getTechnicalStack().getName());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getImage(), null);

        assertEquals(projectList.get(1).getProjectNo(), saveProject1.getNo());
        assertEquals(projectList.get(1).getName(), saveProject1.getName());
        assertEquals(projectList.get(1).getMaxPeople(), saveProject1.getMaxPeople());
        assertEquals(projectList.get(1).getCurrentPeople(), saveProject1.getCurrentPeople());
        assertEquals(projectList.get(1).getViewCount(), saveProject1.getViewCount());
        assertEquals(projectList.get(1).getRegister(), saveProject1.getCreateUserName());
        assertEquals(projectList.get(1).isBookMark(), false);

        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(0).getProjectNo(), saveProjectPosition1.getProject().getNo());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(0).getPositionNo(), saveProjectPosition1.getPosition().getNo());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(0).getPositionName(), saveProjectPosition1.getPosition().getName());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(1).getProjectNo(), saveProjectPosition2.getProject().getNo());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(1).getPositionNo(), saveProjectPosition2.getPosition().getNo());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(1).getPositionName(), saveProjectPosition2.getPosition().getName());

        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), saveProjectTechnicalStack1.getProject().getNo());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), saveProjectTechnicalStack1.getTechnicalStack().getName());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getImage(), saveImage1.getLogicalName());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), saveProjectTechnicalStack2.getProject().getNo());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), saveProjectTechnicalStack2.getTechnicalStack().getName());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getImage(), null);
    }

    /**
     * 만약 한 유저가 한 프로젝트에 여러 포지션에 들어있는 경우에도 중복제거된 프로젝트 리스트가 나와야한다.
     */
    @Test
    public void 참여중인_프로젝트_탐색_복수_포지션() {
        // given
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

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

        // 모집 중인 프로젝트 객체
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

        // 모집 완료된 프로젝트 객체
        Project project2 = Project.builder()
                .name("testName2")
                .createUserName("user1")
                .startDate(startDate)
                .endDate(endDate)
                .state(false)
                .introduction("testIntroduction2")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .commentCount(10)
                .build();

        // 모집 완료된 프로젝트 객체
        Project project3 = Project.builder()
                .name("testName3")
                .createUserName("user1")
                .startDate(startDate)
                .endDate(endDate)
                .state(false)
                .introduction("testIntroduction3")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .commentCount(10)
                .build();

        // 모집 중인 프로젝트 (참여중인 프로젝트)
        Project saveProject1 = projectRepository.saveAndFlush(project1);
        // 모집 완료된 프로젝트 (참여중인 프로젝트)
        Project saveProject2 = projectRepository.saveAndFlush(project2);
        // 모집 완료된 프로젝트
        projectRepository.saveAndFlush(project3);

        // 포지션 세팅
        Position position1 = Position.builder()
                .name("testPosition1")
                .build();
        Position position2 = Position.builder()
                .name("testPosition2")
                .build();
        positionRepository.save(position1);
        positionRepository.save(position2);

        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .state(true)
                .project(project1)
                .position(position1)
                .user(saveUser1)
                .creator(false)
                .build();
        ProjectPosition projectPosition2 = ProjectPosition.builder()
                .state(false)
                .project(project1)
                .position(position2)
                .user(saveUser1)
                .creator(false)
                .build();
        ProjectPosition projectPosition3 = ProjectPosition.builder()
                .state(true)
                .project(project2)
                .position(position1)
                .user(saveUser1)
                .creator(false)
                .build();
        ProjectPosition projectPosition4 = ProjectPosition.builder()
                .state(false)
                .project(project2)
                .position(position2)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);
        ProjectPosition saveProjectPosition2 = projectPositionRepository.save(projectPosition2);
        ProjectPosition saveProjectPosition3 = projectPositionRepository.save(projectPosition3);
        ProjectPosition saveProjectPosition4 = projectPositionRepository.save(projectPosition4);

        // 이미지 세팅
        Image image1 = Image.builder()
                .logicalName("testLogicalName1")
                .physicalName("testPhysicalName1")
                .url("testUrl1")
                .build();
        Image saveImage1 = imageRepository.save(image1);

        // 기술 스택 세팅
        TechnicalStack technicalStack1 = TechnicalStack.builder()
                .name("testTechnicalStack1")
                .imageNo(saveImage1.getNo())
                .build();
        TechnicalStack technicalStack2 = TechnicalStack.builder()
                .name("testTechnicalStack2")
                .build();
        technicalStackRepository.save(technicalStack1);
        technicalStackRepository.save(technicalStack2);

        ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
                .project(project1)
                .technicalStack(technicalStack1)
                .build();
        ProjectTechnicalStack projectTechnicalStack2 = ProjectTechnicalStack.builder()
                .project(project1)
                .technicalStack(technicalStack2)
                .build();
        ProjectTechnicalStack projectTechnicalStack3 = ProjectTechnicalStack.builder()
                .project(project2)
                .technicalStack(technicalStack1)
                .build();
        ProjectTechnicalStack projectTechnicalStack4 = ProjectTechnicalStack.builder()
                .project(project2)
                .technicalStack(technicalStack2)
                .build();
        ProjectTechnicalStack saveProjectTechnicalStack1 = projectTechnicalStackRepository.save(projectTechnicalStack1);
        ProjectTechnicalStack saveProjectTechnicalStack2 = projectTechnicalStackRepository.save(projectTechnicalStack2);
        ProjectTechnicalStack saveProjectTechnicalStack3 = projectTechnicalStackRepository.save(projectTechnicalStack3);
        ProjectTechnicalStack saveProjectTechnicalStack4 = projectTechnicalStackRepository.save(projectTechnicalStack4);

        // when
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdDate").descending());

        // 참여중인 프로젝트 조회
        Slice<ProjectSimpleDto> projectSimpleDtoSlice = null;
        try {
            projectSimpleDtoSlice = projectRepository.findParticipateProject(pageable, Long.MAX_VALUE, saveUser1).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<ProjectSimpleDto> projectList =  projectSimpleDtoSlice.getContent();

        //then
        assertEquals(projectSimpleDtoSlice.getNumber(), 0);
        assertEquals(projectSimpleDtoSlice.getNumberOfElements(), 2);
        assertEquals(projectSimpleDtoSlice.hasNext(), false);
        assertEquals(projectSimpleDtoSlice.isFirst(), true);
        assertEquals(projectSimpleDtoSlice.isLast(), true);
        assertEquals(projectSimpleDtoSlice.hasContent(), true);

        assertEquals(projectList.size(), 2);
        assertEquals(projectList.get(0).getProjectNo(), saveProject2.getNo());
        assertEquals(projectList.get(0).getName(), saveProject2.getName());
        assertEquals(projectList.get(0).getMaxPeople(), saveProject2.getMaxPeople());
        assertEquals(projectList.get(0).getCurrentPeople(), saveProject2.getCurrentPeople());
        assertEquals(projectList.get(0).getViewCount(), saveProject2.getViewCount());
        assertEquals(projectList.get(0).getRegister(), saveProject2.getCreateUserName());
        assertEquals(projectList.get(0).isBookMark(), false);

        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getProjectNo(), saveProjectPosition3.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getPositionNo(), saveProjectPosition3.getPosition().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getPositionName(), saveProjectPosition3.getPosition().getName());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getProjectNo(), saveProjectPosition4.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getPositionNo(), saveProjectPosition4.getPosition().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getPositionName(), saveProjectPosition4.getPosition().getName());

        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), saveProjectTechnicalStack3.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), saveProjectTechnicalStack3.getTechnicalStack().getName());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getImage(), saveImage1.getLogicalName());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), saveProjectTechnicalStack4.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), saveProjectTechnicalStack4.getTechnicalStack().getName());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getImage(), null);

        assertEquals(projectList.get(1).getProjectNo(), saveProject1.getNo());
        assertEquals(projectList.get(1).getName(), saveProject1.getName());
        assertEquals(projectList.get(1).getMaxPeople(), saveProject1.getMaxPeople());
        assertEquals(projectList.get(1).getCurrentPeople(), saveProject1.getCurrentPeople());
        assertEquals(projectList.get(1).getViewCount(), saveProject1.getViewCount());
        assertEquals(projectList.get(1).getRegister(), saveProject1.getCreateUserName());
        assertEquals(projectList.get(1).isBookMark(), false);

        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(0).getProjectNo(), saveProjectPosition1.getProject().getNo());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(0).getPositionNo(), saveProjectPosition1.getPosition().getNo());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(0).getPositionName(), saveProjectPosition1.getPosition().getName());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(1).getProjectNo(), saveProjectPosition2.getProject().getNo());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(1).getPositionNo(), saveProjectPosition2.getPosition().getNo());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(1).getPositionName(), saveProjectPosition2.getPosition().getName());

        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), saveProjectTechnicalStack1.getProject().getNo());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), saveProjectTechnicalStack1.getTechnicalStack().getName());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getImage(), saveImage1.getLogicalName());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), saveProjectTechnicalStack2.getProject().getNo());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), saveProjectTechnicalStack2.getTechnicalStack().getName());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getImage(), null);
    }

    @Test
    public void 신청중인_프로젝트_탐색() {
        // given
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

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

        // 모집 중인 프로젝트 객체
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

        // 모집 완료된 프로젝트 객체
        Project project2 = Project.builder()
                .name("testName2")
                .createUserName("user1")
                .startDate(startDate)
                .endDate(endDate)
                .state(false)
                .introduction("testIntroduction2")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .commentCount(10)
                .build();

        // 모집 완료된 프로젝트 객체
        Project project3 = Project.builder()
                .name("testName3")
                .createUserName("user1")
                .startDate(startDate)
                .endDate(endDate)
                .state(false)
                .introduction("testIntroduction3")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .commentCount(10)
                .build();

        // 모집 중인 프로젝트 (신청중인 프로젝트)
        Project saveProject1 = projectRepository.saveAndFlush(project1);
        // 모집 완료된 프로젝트 (신청중인 프로젝트)
        Project saveProject2 = projectRepository.saveAndFlush(project2);
        // 모집 완료된 프로젝트
        projectRepository.saveAndFlush(project3);

        // 포지션 세팅
        Position position1 = Position.builder()
                .name("testPosition1")
                .build();
        Position position2 = Position.builder()
                .name("testPosition2")
                .build();
        positionRepository.save(position1);
        positionRepository.save(position2);

        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .state(true)
                .project(project1)
                .position(position1)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition projectPosition2 = ProjectPosition.builder()
                .state(false)
                .project(project1)
                .position(position2)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition projectPosition3 = ProjectPosition.builder()
                .state(true)
                .project(project2)
                .position(position1)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition projectPosition4 = ProjectPosition.builder()
                .state(false)
                .project(project2)
                .position(position2)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);
        ProjectPosition saveProjectPosition2 = projectPositionRepository.save(projectPosition2);
        ProjectPosition saveProjectPosition3 = projectPositionRepository.save(projectPosition3);
        ProjectPosition saveProjectPosition4 = projectPositionRepository.save(projectPosition4);

        // 이미지 세팅
        Image image1 = Image.builder()
                .logicalName("testLogicalName1")
                .physicalName("testPhysicalName1")
                .url("testUrl1")
                .build();
        Image saveImage1 = imageRepository.save(image1);

        // 기술 스택 세팅
        TechnicalStack technicalStack1 = TechnicalStack.builder()
                .name("testTechnicalStack1")
                .imageNo(saveImage1.getNo())
                .build();
        TechnicalStack technicalStack2 = TechnicalStack.builder()
                .name("testTechnicalStack2")
                .build();
        technicalStackRepository.save(technicalStack1);
        technicalStackRepository.save(technicalStack2);

        ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
                .project(project1)
                .technicalStack(technicalStack1)
                .build();
        ProjectTechnicalStack projectTechnicalStack2 = ProjectTechnicalStack.builder()
                .project(project1)
                .technicalStack(technicalStack2)
                .build();
        ProjectTechnicalStack projectTechnicalStack3 = ProjectTechnicalStack.builder()
                .project(project2)
                .technicalStack(technicalStack1)
                .build();
        ProjectTechnicalStack projectTechnicalStack4 = ProjectTechnicalStack.builder()
                .project(project2)
                .technicalStack(technicalStack2)
                .build();
        ProjectTechnicalStack saveProjectTechnicalStack1 = projectTechnicalStackRepository.save(projectTechnicalStack1);
        ProjectTechnicalStack saveProjectTechnicalStack2 = projectTechnicalStackRepository.save(projectTechnicalStack2);
        ProjectTechnicalStack saveProjectTechnicalStack3 = projectTechnicalStackRepository.save(projectTechnicalStack3);
        ProjectTechnicalStack saveProjectTechnicalStack4 = projectTechnicalStackRepository.save(projectTechnicalStack4);

        // 참여 신청 요청 세팅
        ProjectParticipateRequest projectParticipateRequest1 = ProjectParticipateRequest.builder()
                .user(saveUser1)
                .projectPosition(saveProjectPosition1)
                .motive("testMotive1")
                .github("testGitHub1")
                .build();

        ProjectParticipateRequest projectParticipateRequest2 = ProjectParticipateRequest.builder()
                .user(saveUser1)
                .projectPosition(saveProjectPosition3)
                .motive("testMotive2")
                .github("testGitHub2")
                .build();

        projectParticipateRequestRepository.save(projectParticipateRequest1);
        projectParticipateRequestRepository.save(projectParticipateRequest2);

        // when
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdDate").descending());

        // 참여중인 프로젝트 조회
        Slice<ProjectSimpleDto> projectSimpleDtoSlice = null;
        try {
            projectSimpleDtoSlice = projectRepository.findParticipateRequestProject(pageable, Long.MAX_VALUE, saveUser1).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<ProjectSimpleDto> projectList =  projectSimpleDtoSlice.getContent();

        //then
        assertEquals(projectSimpleDtoSlice.getNumber(), 0);
        assertEquals(projectSimpleDtoSlice.getNumberOfElements(), 2);
        assertEquals(projectSimpleDtoSlice.hasNext(), false);
        assertEquals(projectSimpleDtoSlice.isFirst(), true);
        assertEquals(projectSimpleDtoSlice.isLast(), true);
        assertEquals(projectSimpleDtoSlice.hasContent(), true);

        assertEquals(projectList.size(), 2);
        assertEquals(projectList.get(0).getProjectNo(), saveProject2.getNo());
        assertEquals(projectList.get(0).getName(), saveProject2.getName());
        assertEquals(projectList.get(0).getMaxPeople(), saveProject2.getMaxPeople());
        assertEquals(projectList.get(0).getCurrentPeople(), saveProject2.getCurrentPeople());
        assertEquals(projectList.get(0).getViewCount(), saveProject2.getViewCount());
        assertEquals(projectList.get(0).getRegister(), saveProject2.getCreateUserName());
        assertEquals(projectList.get(0).isBookMark(), false);

        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getProjectNo(), saveProjectPosition3.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getPositionNo(), saveProjectPosition3.getPosition().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getPositionName(), saveProjectPosition3.getPosition().getName());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getProjectNo(), saveProjectPosition4.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getPositionNo(), saveProjectPosition4.getPosition().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getPositionName(), saveProjectPosition4.getPosition().getName());

        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), saveProjectTechnicalStack3.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), saveProjectTechnicalStack3.getTechnicalStack().getName());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getImage(), saveImage1.getLogicalName());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), saveProjectTechnicalStack4.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), saveProjectTechnicalStack4.getTechnicalStack().getName());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getImage(), null);

        assertEquals(projectList.get(1).getProjectNo(), saveProject1.getNo());
        assertEquals(projectList.get(1).getName(), saveProject1.getName());
        assertEquals(projectList.get(1).getMaxPeople(), saveProject1.getMaxPeople());
        assertEquals(projectList.get(1).getCurrentPeople(), saveProject1.getCurrentPeople());
        assertEquals(projectList.get(1).getViewCount(), saveProject1.getViewCount());
        assertEquals(projectList.get(1).getRegister(), saveProject1.getCreateUserName());
        assertEquals(projectList.get(1).isBookMark(), false);

        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(0).getProjectNo(), saveProjectPosition1.getProject().getNo());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(0).getPositionNo(), saveProjectPosition1.getPosition().getNo());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(0).getPositionName(), saveProjectPosition1.getPosition().getName());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(1).getProjectNo(), saveProjectPosition2.getProject().getNo());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(1).getPositionNo(), saveProjectPosition2.getPosition().getNo());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(1).getPositionName(), saveProjectPosition2.getPosition().getName());

        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), saveProjectTechnicalStack1.getProject().getNo());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), saveProjectTechnicalStack1.getTechnicalStack().getName());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getImage(), saveImage1.getLogicalName());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), saveProjectTechnicalStack2.getProject().getNo());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), saveProjectTechnicalStack2.getTechnicalStack().getName());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getImage(), null);
    }

    @Test
    public void 신청중인_프로젝트_탐색_복수_참여_신청() {
        // given
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

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

        // 모집 중인 프로젝트 객체
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

        // 모집 완료된 프로젝트 객체
        Project project2 = Project.builder()
                .name("testName2")
                .createUserName("user1")
                .startDate(startDate)
                .endDate(endDate)
                .state(false)
                .introduction("testIntroduction2")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .commentCount(10)
                .build();

        // 모집 완료된 프로젝트 객체
        Project project3 = Project.builder()
                .name("testName3")
                .createUserName("user1")
                .startDate(startDate)
                .endDate(endDate)
                .state(false)
                .introduction("testIntroduction3")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .commentCount(10)
                .build();

        // 모집 중인 프로젝트 (신청중인 프로젝트)
        Project saveProject1 = projectRepository.saveAndFlush(project1);
        // 모집 완료된 프로젝트 (신청중인 프로젝트)
        Project saveProject2 = projectRepository.saveAndFlush(project2);
        // 모집 완료된 프로젝트
        projectRepository.saveAndFlush(project3);

        // 포지션 세팅
        Position position1 = Position.builder()
                .name("testPosition1")
                .build();
        Position position2 = Position.builder()
                .name("testPosition2")
                .build();
        positionRepository.save(position1);
        positionRepository.save(position2);

        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .state(true)
                .project(project1)
                .position(position1)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition projectPosition2 = ProjectPosition.builder()
                .state(false)
                .project(project1)
                .position(position2)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition projectPosition3 = ProjectPosition.builder()
                .state(true)
                .project(project2)
                .position(position1)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition projectPosition4 = ProjectPosition.builder()
                .state(false)
                .project(project2)
                .position(position2)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);
        ProjectPosition saveProjectPosition2 = projectPositionRepository.save(projectPosition2);
        ProjectPosition saveProjectPosition3 = projectPositionRepository.save(projectPosition3);
        ProjectPosition saveProjectPosition4 = projectPositionRepository.save(projectPosition4);

        // 이미지 세팅
        Image image1 = Image.builder()
                .logicalName("testLogicalName1")
                .physicalName("testPhysicalName1")
                .url("testUrl1")
                .build();
        Image saveImage1 = imageRepository.save(image1);

        // 기술 스택 세팅
        TechnicalStack technicalStack1 = TechnicalStack.builder()
                .name("testTechnicalStack1")
                .imageNo(saveImage1.getNo())
                .build();
        TechnicalStack technicalStack2 = TechnicalStack.builder()
                .name("testTechnicalStack2")
                .build();
        technicalStackRepository.save(technicalStack1);
        technicalStackRepository.save(technicalStack2);

        ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
                .project(project1)
                .technicalStack(technicalStack1)
                .build();
        ProjectTechnicalStack projectTechnicalStack2 = ProjectTechnicalStack.builder()
                .project(project1)
                .technicalStack(technicalStack2)
                .build();
        ProjectTechnicalStack projectTechnicalStack3 = ProjectTechnicalStack.builder()
                .project(project2)
                .technicalStack(technicalStack1)
                .build();
        ProjectTechnicalStack projectTechnicalStack4 = ProjectTechnicalStack.builder()
                .project(project2)
                .technicalStack(technicalStack2)
                .build();
        ProjectTechnicalStack saveProjectTechnicalStack1 = projectTechnicalStackRepository.save(projectTechnicalStack1);
        ProjectTechnicalStack saveProjectTechnicalStack2 = projectTechnicalStackRepository.save(projectTechnicalStack2);
        ProjectTechnicalStack saveProjectTechnicalStack3 = projectTechnicalStackRepository.save(projectTechnicalStack3);
        ProjectTechnicalStack saveProjectTechnicalStack4 = projectTechnicalStackRepository.save(projectTechnicalStack4);

        // 참여 신청 요청 세팅
        ProjectParticipateRequest projectParticipateRequest1 = ProjectParticipateRequest.builder()
                .user(saveUser1)
                .projectPosition(saveProjectPosition1)
                .motive("testMotive1")
                .github("testGitHub1")
                .build();

        ProjectParticipateRequest projectParticipateRequest2 = ProjectParticipateRequest.builder()
                .user(saveUser1)
                .projectPosition(saveProjectPosition2)
                .motive("testMotive1")
                .github("testGitHub1")
                .build();

        ProjectParticipateRequest projectParticipateRequest3 = ProjectParticipateRequest.builder()
                .user(saveUser1)
                .projectPosition(saveProjectPosition3)
                .motive("testMotive2")
                .github("testGitHub2")
                .build();

        ProjectParticipateRequest projectParticipateRequest4 = ProjectParticipateRequest.builder()
                .user(saveUser1)
                .projectPosition(saveProjectPosition4)
                .motive("testMotive2")
                .github("testGitHub2")
                .build();

        projectParticipateRequestRepository.save(projectParticipateRequest1);
        projectParticipateRequestRepository.save(projectParticipateRequest2);
        projectParticipateRequestRepository.save(projectParticipateRequest3);
        projectParticipateRequestRepository.save(projectParticipateRequest4);

        // when
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdDate").descending());

        // 참여중인 프로젝트 조회
        Slice<ProjectSimpleDto> projectSimpleDtoSlice = null;
        try {
            projectSimpleDtoSlice = projectRepository.findParticipateRequestProject(pageable, Long.MAX_VALUE, saveUser1).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<ProjectSimpleDto> projectList =  projectSimpleDtoSlice.getContent();

        //then
        assertEquals(projectSimpleDtoSlice.getNumber(), 0);
        assertEquals(projectSimpleDtoSlice.getNumberOfElements(), 2);
        assertEquals(projectSimpleDtoSlice.hasNext(), false);
        assertEquals(projectSimpleDtoSlice.isFirst(), true);
        assertEquals(projectSimpleDtoSlice.isLast(), true);
        assertEquals(projectSimpleDtoSlice.hasContent(), true);

        assertEquals(projectList.size(), 2);
        assertEquals(projectList.get(0).getProjectNo(), saveProject2.getNo());
        assertEquals(projectList.get(0).getName(), saveProject2.getName());
        assertEquals(projectList.get(0).getMaxPeople(), saveProject2.getMaxPeople());
        assertEquals(projectList.get(0).getCurrentPeople(), saveProject2.getCurrentPeople());
        assertEquals(projectList.get(0).getViewCount(), saveProject2.getViewCount());
        assertEquals(projectList.get(0).getRegister(), saveProject2.getCreateUserName());
        assertEquals(projectList.get(0).isBookMark(), false);

        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getProjectNo(), saveProjectPosition3.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getPositionNo(), saveProjectPosition3.getPosition().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getPositionName(), saveProjectPosition3.getPosition().getName());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getProjectNo(), saveProjectPosition4.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getPositionNo(), saveProjectPosition4.getPosition().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getPositionName(), saveProjectPosition4.getPosition().getName());

        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), saveProjectTechnicalStack3.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), saveProjectTechnicalStack3.getTechnicalStack().getName());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getImage(), saveImage1.getLogicalName());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), saveProjectTechnicalStack4.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), saveProjectTechnicalStack4.getTechnicalStack().getName());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getImage(), null);

        assertEquals(projectList.get(1).getProjectNo(), saveProject1.getNo());
        assertEquals(projectList.get(1).getName(), saveProject1.getName());
        assertEquals(projectList.get(1).getMaxPeople(), saveProject1.getMaxPeople());
        assertEquals(projectList.get(1).getCurrentPeople(), saveProject1.getCurrentPeople());
        assertEquals(projectList.get(1).getViewCount(), saveProject1.getViewCount());
        assertEquals(projectList.get(1).getRegister(), saveProject1.getCreateUserName());
        assertEquals(projectList.get(1).isBookMark(), false);

        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(0).getProjectNo(), saveProjectPosition1.getProject().getNo());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(0).getPositionNo(), saveProjectPosition1.getPosition().getNo());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(0).getPositionName(), saveProjectPosition1.getPosition().getName());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(1).getProjectNo(), saveProjectPosition2.getProject().getNo());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(1).getPositionNo(), saveProjectPosition2.getPosition().getNo());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(1).getPositionName(), saveProjectPosition2.getPosition().getName());

        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), saveProjectTechnicalStack1.getProject().getNo());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), saveProjectTechnicalStack1.getTechnicalStack().getName());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getImage(), saveImage1.getLogicalName());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), saveProjectTechnicalStack2.getProject().getNo());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), saveProjectTechnicalStack2.getTechnicalStack().getName());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getImage(), null);
    }

    @Test
    public void 즐겨찾기중인_프로젝트_탐색() {
        // given
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

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

        // 모집 중인 프로젝트 객체
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

        // 모집 완료된 프로젝트 객체
        Project project2 = Project.builder()
                .name("testName2")
                .createUserName("user1")
                .startDate(startDate)
                .endDate(endDate)
                .state(false)
                .introduction("testIntroduction2")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .commentCount(10)
                .build();

        // 모집 완료된 프로젝트 객체
        Project project3 = Project.builder()
                .name("testName3")
                .createUserName("user1")
                .startDate(startDate)
                .endDate(endDate)
                .state(false)
                .introduction("testIntroduction3")
                .maxPeople(10)
                .currentPeople(4)
                .viewCount(10)
                .commentCount(10)
                .build();

        // 모집 중인 프로젝트 (즐겨찾기중인 프로젝트)
        Project saveProject1 = projectRepository.saveAndFlush(project1);
        // 모집 완료된 프로젝트 (즐겨찾기중인 프로젝트)
        Project saveProject2 = projectRepository.saveAndFlush(project2);
        // 모집 완료된 프로젝트
        projectRepository.saveAndFlush(project3);

        // 포지션 세팅
        Position position1 = Position.builder()
                .name("testPosition1")
                .build();
        Position position2 = Position.builder()
                .name("testPosition2")
                .build();
        positionRepository.save(position1);
        positionRepository.save(position2);

        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .state(true)
                .project(project1)
                .position(position1)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition projectPosition2 = ProjectPosition.builder()
                .state(false)
                .project(project1)
                .position(position2)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition projectPosition3 = ProjectPosition.builder()
                .state(true)
                .project(project2)
                .position(position1)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition projectPosition4 = ProjectPosition.builder()
                .state(false)
                .project(project2)
                .position(position2)
                .user(null)
                .creator(false)
                .build();
        ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);
        ProjectPosition saveProjectPosition2 = projectPositionRepository.save(projectPosition2);
        ProjectPosition saveProjectPosition3 = projectPositionRepository.save(projectPosition3);
        ProjectPosition saveProjectPosition4 = projectPositionRepository.save(projectPosition4);

        // 이미지 세팅
        Image image1 = Image.builder()
                .logicalName("testLogicalName1")
                .physicalName("testPhysicalName1")
                .url("testUrl1")
                .build();
        Image saveImage1 = imageRepository.save(image1);

        // 기술 스택 세팅
        TechnicalStack technicalStack1 = TechnicalStack.builder()
                .name("testTechnicalStack1")
                .imageNo(saveImage1.getNo())
                .build();
        TechnicalStack technicalStack2 = TechnicalStack.builder()
                .name("testTechnicalStack2")
                .build();
        technicalStackRepository.save(technicalStack1);
        technicalStackRepository.save(technicalStack2);

        ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
                .project(project1)
                .technicalStack(technicalStack1)
                .build();
        ProjectTechnicalStack projectTechnicalStack2 = ProjectTechnicalStack.builder()
                .project(project1)
                .technicalStack(technicalStack2)
                .build();
        ProjectTechnicalStack projectTechnicalStack3 = ProjectTechnicalStack.builder()
                .project(project2)
                .technicalStack(technicalStack1)
                .build();
        ProjectTechnicalStack projectTechnicalStack4 = ProjectTechnicalStack.builder()
                .project(project2)
                .technicalStack(technicalStack2)
                .build();
        ProjectTechnicalStack saveProjectTechnicalStack1 = projectTechnicalStackRepository.save(projectTechnicalStack1);
        ProjectTechnicalStack saveProjectTechnicalStack2 = projectTechnicalStackRepository.save(projectTechnicalStack2);
        ProjectTechnicalStack saveProjectTechnicalStack3 = projectTechnicalStackRepository.save(projectTechnicalStack3);
        ProjectTechnicalStack saveProjectTechnicalStack4 = projectTechnicalStackRepository.save(projectTechnicalStack4);

        // 참여 신청 요청 세팅
        BookMark bookMark1 = BookMark.builder()
                .project(saveProject1)
                .user(saveUser1)
                .build();

        BookMark bookMark2 = BookMark.builder()
                .project(saveProject2)
                .user(saveUser1)
                .build();

        BookMark saveBookMark1 = bookMarkRepository.save(bookMark1);
        BookMark saveBookMark2 = bookMarkRepository.save(bookMark2);

        // when
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdDate").descending());

        // 참여중인 프로젝트 조회
        Slice<ProjectSimpleDto> projectSimpleDtoSlice = null;
        try {
            projectSimpleDtoSlice = projectRepository.findBookMarkProject(pageable, Long.MAX_VALUE, saveUser1).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<ProjectSimpleDto> projectList =  projectSimpleDtoSlice.getContent();

        //then
        assertEquals(projectSimpleDtoSlice.getNumber(), 0);
        assertEquals(projectSimpleDtoSlice.getNumberOfElements(), 2);
        assertEquals(projectSimpleDtoSlice.hasNext(), false);
        assertEquals(projectSimpleDtoSlice.isFirst(), true);
        assertEquals(projectSimpleDtoSlice.isLast(), true);
        assertEquals(projectSimpleDtoSlice.hasContent(), true);

        assertEquals(projectList.size(), 2);
        assertEquals(projectList.get(0).getProjectNo(), saveProject2.getNo());
        assertEquals(projectList.get(0).getName(), saveProject2.getName());
        assertEquals(projectList.get(0).getMaxPeople(), saveProject2.getMaxPeople());
        assertEquals(projectList.get(0).getCurrentPeople(), saveProject2.getCurrentPeople());
        assertEquals(projectList.get(0).getViewCount(), saveProject2.getViewCount());
        assertEquals(projectList.get(0).getRegister(), saveProject2.getCreateUserName());
        assertEquals(projectList.get(0).isBookMark(), true);

        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getProjectNo(), saveProjectPosition3.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getPositionNo(), saveProjectPosition3.getPosition().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getPositionName(), saveProjectPosition3.getPosition().getName());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getProjectNo(), saveProjectPosition4.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getPositionNo(), saveProjectPosition4.getPosition().getNo());
        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getPositionName(), saveProjectPosition4.getPosition().getName());

        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), saveProjectTechnicalStack3.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), saveProjectTechnicalStack3.getTechnicalStack().getName());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getImage(), saveImage1.getLogicalName());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), saveProjectTechnicalStack4.getProject().getNo());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), saveProjectTechnicalStack4.getTechnicalStack().getName());
        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getImage(), null);

        assertEquals(projectList.get(1).getProjectNo(), saveProject1.getNo());
        assertEquals(projectList.get(1).getName(), saveProject1.getName());
        assertEquals(projectList.get(1).getMaxPeople(), saveProject1.getMaxPeople());
        assertEquals(projectList.get(1).getCurrentPeople(), saveProject1.getCurrentPeople());
        assertEquals(projectList.get(1).getViewCount(), saveProject1.getViewCount());
        assertEquals(projectList.get(1).getRegister(), saveProject1.getCreateUserName());
        assertEquals(projectList.get(1).isBookMark(), true);

        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(0).getProjectNo(), saveProjectPosition1.getProject().getNo());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(0).getPositionNo(), saveProjectPosition1.getPosition().getNo());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(0).getPositionName(), saveProjectPosition1.getPosition().getName());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(1).getProjectNo(), saveProjectPosition2.getProject().getNo());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(1).getPositionNo(), saveProjectPosition2.getPosition().getNo());
        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(1).getPositionName(), saveProjectPosition2.getPosition().getName());

        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), saveProjectTechnicalStack1.getProject().getNo());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), saveProjectTechnicalStack1.getTechnicalStack().getName());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getImage(), saveImage1.getLogicalName());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), saveProjectTechnicalStack2.getProject().getNo());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), saveProjectTechnicalStack2.getTechnicalStack().getName());
        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getImage(), null);
    }

    @Nested
    @DisplayName("프로젝트 유저 조인 유저 존재 확인")
    class existUserProjectByUser {
        @Test
        @DisplayName("성공 테스트")
        public void testSuccess() {
            // given
            LocalDate startDate = LocalDate.of(2022, 06, 24);
            LocalDate endDate = LocalDate.of(2022, 06, 28);

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

            // 모집중 프로젝트 객체
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
                    .user(saveUser1)
                    .commentCount(10)
                    .build();
            Project saveProject1 = projectRepository.saveAndFlush(project1);

            // when
            boolean result = projectRepository.existUserProjectByUser(saveUser1.getNo(), saveProject1.getNo());

            // then
            assertEquals(result, true);
        }
    }
}