package com.matching.project.repository;

import com.matching.project.config.QuerydslConfiguration;
import com.matching.project.entity.Project;
import com.matching.project.entity.ProjectTechnicalStack;
import com.matching.project.entity.TechnicalStack;
import org.junit.jupiter.api.DisplayName;
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
class ProjectTechnicalStackRepositoryTest {
    @Autowired
    private ProjectTechnicalStackRepository projectTechnicalStackRepository;

    @Autowired
    private TechnicalStackRepository technicalStackRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    public void 프로젝트_기술스택_프로젝트_조회() {
        // given
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

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

        TechnicalStack technicalStack1 = TechnicalStack.builder()
                .name("testTechnicalStack1")
                .build();
        TechnicalStack technicalStack2 = TechnicalStack.builder()
                .name("testTechnicalStack2")
                .build();
        TechnicalStack saveTechnicalStack1 = technicalStackRepository.save(technicalStack1);
        TechnicalStack saveTechnicalStack2 = technicalStackRepository.save(technicalStack2);

        ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
                .project(saveProject1)
                .technicalStack(technicalStack1)
                .build();
        ProjectTechnicalStack projectTechnicalStack2 = ProjectTechnicalStack.builder()
                .project(saveProject1)
                .technicalStack(technicalStack2)
                .build();
        ProjectTechnicalStack saveProjectTechnicalStack1 = projectTechnicalStackRepository.save(projectTechnicalStack1);
        ProjectTechnicalStack saveProjectTechnicalStack2 = projectTechnicalStackRepository.save(projectTechnicalStack2);

        // when
        List<ProjectTechnicalStack> projectTechnicalStackList = projectTechnicalStackRepository.findTechnicalStackAndProjectUsingFetchJoin(project1);

        // then
        assertEquals(projectTechnicalStackList.size(), 2);
        assertEquals(projectTechnicalStackList.get(0), saveProjectTechnicalStack1);
        assertEquals(projectTechnicalStackList.get(0).getProject(), saveProject1);
        assertEquals(projectTechnicalStackList.get(0).getTechnicalStack(), saveTechnicalStack1);
        assertEquals(projectTechnicalStackList.get(1), saveProjectTechnicalStack2);
        assertEquals(projectTechnicalStackList.get(1).getProject(), saveProject1);
        assertEquals(projectTechnicalStackList.get(1).getTechnicalStack(), saveTechnicalStack2);
    }

    @Test
    @DisplayName("프로젝트에 들어있는 프로젝트 기술스택 삭제")
    public void testDeleteByProjectNo() {
        // given
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);

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

        TechnicalStack technicalStack1 = TechnicalStack.builder()
                .name("testTechnicalStack1")
                .build();
        TechnicalStack technicalStack2 = TechnicalStack.builder()
                .name("testTechnicalStack2")
                .build();
        technicalStackRepository.save(technicalStack1);
        technicalStackRepository.save(technicalStack2);

        ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
                .project(saveProject1)
                .technicalStack(technicalStack1)
                .build();
        ProjectTechnicalStack projectTechnicalStack2 = ProjectTechnicalStack.builder()
                .project(saveProject1)
                .technicalStack(technicalStack2)
                .build();
        projectTechnicalStackRepository.save(projectTechnicalStack1);
        projectTechnicalStackRepository.save(projectTechnicalStack2);

        // when
        List<ProjectTechnicalStack> beforeProjectTechnicalStackList = projectTechnicalStackRepository.findAll();
        assertEquals(beforeProjectTechnicalStackList.size(), 2);

        projectTechnicalStackRepository.deleteByProjectNo(saveProject1.getNo());

        // then
        List<ProjectTechnicalStack> afterProjectTechnicalStackList = projectTechnicalStackRepository.findAll();
        assertEquals(afterProjectTechnicalStackList.size(), 0);
    }

}