package com.matching.project.repository;

import com.matching.project.dto.project.ProjectQueryDto;
import com.matching.project.entity.Project;
import com.matching.project.entity.ProjectPosition;
import com.matching.project.entity.ProjectTechnicalStack;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class ProjectRepositoryTest {

    @Autowired
    public ProjectRepository projectRepository;

    @Autowired
    public ProjectPositionRepository projectPositionRepository;

    @Autowired
    public ProjectTechnicalStackRepository projectTechnicalStackRepository;

    @Test
    public void 프로젝트_탐색() {
        // given
        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);
        
        // 삭제된 프로젝트 객체
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
        
        // 모집 완료된 프로젝트 객체
        Project project2 = Project.builder()
                .name("testName2")
                .createUserName("user1")
                .createDate(createDate)
                .startDate(startDate)
                .endDate(endDate)
                .state(false)
                .introduction("testIntroduction2")
                .maxPeople(10)
                .currentPeople(4)
                .delete(false)
                .deleteReason(null)
                .viewCount(10)
                .commentCount(10)
                .build();
        
        // 모집중인 프로젝트 객체
        Project project3 = Project.builder()
                .name("testName3")
                .createUserName("user1")
                .createDate(createDate)
                .startDate(startDate)
                .endDate(endDate)
                .state(true)
                .introduction("testIntroduction2")
                .maxPeople(10)
                .currentPeople(4)
                .delete(false)
                .deleteReason(null)
                .viewCount(10)
                .commentCount(10)
                .build();
        
        // 모집 중인 프로젝트
        Project saveProject1 = projectRepository.save(project1);
        // 모집 완료된 프로젝트
        Project saveProject2 = projectRepository.save(project2);
        // 모집 중인 프로젝트
        Project saveProject3 = projectRepository.save(project3);

        // when
        Pageable pageable = PageRequest.of(0, 4, Sort.by("no").descending());
        
        // 모집 중이고, 삭제되지 않은 프로젝트 탐색
        Page<Project> projectPage = projectRepository.findByStateProjectPage(true, false, pageable);
        List<Project> projectList = projectPage.getContent();

        //then
        assertEquals(projectPage.getTotalPages(), 1);
        assertEquals(projectPage.getNumber(), 0);
        assertEquals(projectPage.getNumberOfElements(), 1);
        assertEquals(projectPage.hasNext(), false);
        assertEquals(projectPage.isFirst(), true);
        assertEquals(projectPage.isLast(), true);
        assertEquals(projectPage.hasContent(), true);

        assertEquals(projectList.size(), 1);

        assertEquals(projectList.get(0).getNo(), saveProject3.getNo());
        assertEquals(projectList.get(0).getName(), saveProject3.getName());
        assertEquals(projectList.get(0).getCreateUserName(), saveProject3.getCreateUserName());
        assertEquals(projectList.get(0).getStartDate(), saveProject3.getStartDate());
        assertEquals(projectList.get(0).getEndDate(), saveProject3.getEndDate());
        assertEquals(projectList.get(0).isState(), saveProject3.isState());
        assertEquals(projectList.get(0).getIntroduction(), saveProject3.getIntroduction());
        assertEquals(projectList.get(0).getMaxPeople(), saveProject3.getMaxPeople());
        assertEquals(projectList.get(0).getCurrentPeople(), saveProject3.getCurrentPeople());
        assertEquals(projectList.get(0).isDelete(), saveProject3.isDelete());
        assertEquals(projectList.get(0).getDeleteReason(), saveProject3.getDeleteReason());
        assertEquals(projectList.get(0).getViewCount(), saveProject3.getViewCount());
        assertEquals(projectList.get(0).getCommentCount(), saveProject3.getCommentCount());
    }

    @Test
    public void 상세_프로젝트_조회() {
        // given
        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

        // 프로젝트 객체
        Project project = Project.builder()
                .name("projectTestName")
                .createUserName("user")
                .createDate(createDate)
                .startDate(startDate)
                .endDate(endDate)
                .state(true)
                .introduction("testIntroduction")
                .maxPeople(10)
                .currentPeople(4)
                .delete(false)
                .deleteReason(null)
                .viewCount(10)
                .commentCount(10)
                .build();
        // 프로젝트 저장
        project = projectRepository.save(project);

        // 프로젝트 포지션 저장
        ProjectPosition projectPosition1 = ProjectPosition.builder()
                .state(true)
                .build();
        projectPosition1 = projectPositionRepository.save(projectPosition1);

        ProjectPosition projectPosition2 = ProjectPosition.builder()
                .state(true)
                .build();
        projectPosition2 = projectPositionRepository.save(projectPosition2);
        
        // 프론트 기술스택 저장
        ProjectTechnicalStack frontTechnicalStack1 = ProjectTechnicalStack.builder()
                .build();
        projectTechnicalStackRepository.save(frontTechnicalStack1);

        ProjectTechnicalStack frontTechnicalStack2 = ProjectTechnicalStack.builder()
                .build();

        projectTechnicalStackRepository.save(frontTechnicalStack2);

        // 백엔드 기술스택 저장
        ProjectTechnicalStack backTechnicalStack1 = ProjectTechnicalStack.builder()

                .build();

        projectTechnicalStackRepository.save(backTechnicalStack1);

        ProjectTechnicalStack backTechnicalStack2 = ProjectTechnicalStack.builder()

                .build();

        projectTechnicalStackRepository.save(backTechnicalStack2);


        // when
        ProjectQueryDto detailProject = projectRepository.findDetailProject(project.getNo());


        // then
        // 프로젝트 값 검증
        assertEquals(detailProject.getNo(), project.getNo());
        assertEquals(detailProject.getName(), project.getName());
        assertEquals(detailProject.getCreateDate(), project.getCreateDate());
        assertEquals(detailProject.getStartDate(), project.getStartDate());
        assertEquals(detailProject.getEndDate(), project.getEndDate());
        assertEquals(detailProject.isState(), project.isState());
        assertEquals(detailProject.getIntroduction(), project.getIntroduction());
        assertEquals(detailProject.getMaxPeople(), project.getMaxPeople());
        assertEquals(detailProject.getCurrentPeople(), project.getCurrentPeople());
        assertEquals(detailProject.isDelete(), project.isDelete());
        assertEquals(detailProject.getDeleteReason(), project.getDeleteReason());
        assertEquals(detailProject.getViewCount(), project.getViewCount());
        assertEquals(detailProject.getCommentCount(), project.getCommentCount());
        
        // 프로젝트 포지션 값 검증
        assertEquals(detailProject.getProjectPositionList().get(0).getNo(), projectPosition1.getNo());

        assertEquals(detailProject.getProjectPositionList().get(0).isState(), projectPosition1.isState());

        assertEquals(detailProject.getProjectPositionList().get(1).getNo(), projectPosition2.getNo());

        assertEquals(detailProject.getProjectPositionList().get(1).isState(), projectPosition2.isState());

        // 프로젝트 기술 스택 검증
        assertEquals(detailProject.getProjectPositionList().get(0).getProjectTechnicalStackList().get(0).getNo(), frontTechnicalStack1.getNo());

        assertEquals(detailProject.getProjectPositionList().get(0).getProjectTechnicalStackList().get(1).getNo(), frontTechnicalStack2.getNo());


        assertEquals(detailProject.getProjectPositionList().get(1).getProjectTechnicalStackList().get(0).getNo(), backTechnicalStack1.getNo());

        assertEquals(detailProject.getProjectPositionList().get(1).getProjectTechnicalStackList().get(1).getNo(), backTechnicalStack2.getNo());

    }
}