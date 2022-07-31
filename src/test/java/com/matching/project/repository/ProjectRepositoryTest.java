package com.matching.project.repository;

import com.matching.project.config.QuerydslConfiguration;
import com.matching.project.dto.enumerate.Filter;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.project.*;
import com.matching.project.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Import(QuerydslConfiguration.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class ProjectRepositoryTest {

//    @Autowired
//    public ProjectRepository projectRepository;
//
//    @Autowired
//    public ProjectPositionRepository projectPositionRepository;
//
//    @Autowired
//    public ProjectTechnicalStackRepository projectTechnicalStackRepository;
//
//    @Autowired
//    public PositionRepository positionRepository;
//
//    @Autowired
//    public TechnicalStackRepository technicalStackRepository;
//
//    @Test
//    public void 프로젝트_탐색() {
//        // given
//        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
//        LocalDate startDate = LocalDate.of(2022, 06, 24);
//        LocalDate endDate = LocalDate.of(2022, 06, 28);
//
//        // 삭제된 프로젝트 객체
//        Project project1 = Project.builder()
//                .name("testName1")
//                .createUserName("user1")
//                .createDate(createDate.plusDays(1))
//                .startDate(startDate)
//                .endDate(endDate)
//                .state(true)
//                .introduction("testIntroduction1")
//                .maxPeople(10)
//                .currentPeople(4)
//                .delete(true)
//                .deleteReason(null)
//                .viewCount(10)
//                .commentCount(10)
//                .build();
//
//        // 모집 중인 프로젝트 객체
//        Project project2 = Project.builder()
//                .name("testName2")
//                .createUserName("user1")
//                .createDate(createDate.plusDays(2))
//                .startDate(startDate)
//                .endDate(endDate)
//                .state(true)
//                .introduction("testIntroduction2")
//                .maxPeople(10)
//                .currentPeople(4)
//                .delete(false)
//                .deleteReason(null)
//                .viewCount(10)
//                .commentCount(10)
//                .build();
//
//        // 모집 중인 프로젝트 객체
//        Project project3 = Project.builder()
//                .name("testName3")
//                .createUserName("user1")
//                .createDate(createDate.plusDays(3))
//                .startDate(startDate)
//                .endDate(endDate)
//                .state(true)
//                .introduction("testIntroduction3")
//                .maxPeople(10)
//                .currentPeople(4)
//                .delete(false)
//                .deleteReason(null)
//                .viewCount(10)
//                .commentCount(10)
//                .build();
//
//        // 모집 중인 프로젝트
//        projectRepository.save(project1);
//        // 모집 중인 프로젝트 객체
//        Project saveProject2 = projectRepository.save(project2);
//        // 모집 중인 프로젝트
//        Project saveProject3 = projectRepository.save(project3);
//
//        // 포지션 세팅
//        Position position1 = Position.builder()
//                .name("testPosition1")
//                .build();
//        Position position2 = Position.builder()
//                .name("testPosition2")
//                .build();
//        positionRepository.save(position1);
//        positionRepository.save(position2);
//
//        ProjectPosition projectPosition1 = ProjectPosition.builder()
//                .state(true)
//                .project(project2)
//                .position(position1)
//                .user(null)
//                .creator(false)
//                .build();
//        ProjectPosition projectPosition2 = ProjectPosition.builder()
//                .state(false)
//                .project(project2)
//                .position(position2)
//                .user(null)
//                .creator(false)
//                .build();
//        ProjectPosition projectPosition3 = ProjectPosition.builder()
//                .state(true)
//                .project(project3)
//                .position(position1)
//                .user(null)
//                .creator(false)
//                .build();
//        ProjectPosition projectPosition4 = ProjectPosition.builder()
//                .state(false)
//                .project(project3)
//                .position(position2)
//                .user(null)
//                .creator(false)
//                .build();
//        ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);
//        ProjectPosition saveProjectPosition2 = projectPositionRepository.save(projectPosition2);
//        ProjectPosition saveProjectPosition3 = projectPositionRepository.save(projectPosition3);
//        ProjectPosition saveProjectPosition4 = projectPositionRepository.save(projectPosition4);
//
//        // 기술 스택 세팅
//        TechnicalStack technicalStack1 = TechnicalStack.builder()
//                .name("testTechnicalStack1")
//                .build();
//        TechnicalStack technicalStack2 = TechnicalStack.builder()
//                .name("testTechnicalStack2")
//                .build();
//        technicalStackRepository.save(technicalStack1);
//        technicalStackRepository.save(technicalStack2);
//
//        ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
//                .project(project2)
//                .technicalStack(technicalStack1)
//                .build();
//        ProjectTechnicalStack projectTechnicalStack2 = ProjectTechnicalStack.builder()
//                .project(project2)
//                .technicalStack(technicalStack2)
//                .build();
//        ProjectTechnicalStack projectTechnicalStack3 = ProjectTechnicalStack.builder()
//                .project(project3)
//                .technicalStack(technicalStack1)
//                .build();
//        ProjectTechnicalStack projectTechnicalStack4 = ProjectTechnicalStack.builder()
//                .project(project3)
//                .technicalStack(technicalStack2)
//                .build();
//        ProjectTechnicalStack saveProjectTechnicalStack1 = projectTechnicalStackRepository.save(projectTechnicalStack1);
//        ProjectTechnicalStack saveProjectTechnicalStack2 = projectTechnicalStackRepository.save(projectTechnicalStack2);
//        ProjectTechnicalStack saveProjectTechnicalStack3 = projectTechnicalStackRepository.save(projectTechnicalStack3);
//        ProjectTechnicalStack saveProjectTechnicalStack4 = projectTechnicalStackRepository.save(projectTechnicalStack4);
//
//        // when
//        Pageable pageable = PageRequest.of(0, 4, Sort.by("createDate").descending());
//
//        // 모집 중이고, 삭제되지 않은 프로젝트 탐색
//        Page<ProjectSimpleDto> projectSimpleDtoPage = null;
//        try {
//            projectSimpleDtoPage = projectRepository.findProjectByStatus(pageable, true, false, null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        List<ProjectSimpleDto> projectList =  projectSimpleDtoPage.getContent();
//
//        //then
//        assertEquals(projectSimpleDtoPage.getTotalPages(), 1);
//        assertEquals(projectSimpleDtoPage.getNumber(), 0);
//        assertEquals(projectSimpleDtoPage.getNumberOfElements(), 2);
//        assertEquals(projectSimpleDtoPage.hasNext(), false);
//        assertEquals(projectSimpleDtoPage.isFirst(), true);
//        assertEquals(projectSimpleDtoPage.isLast(), true);
//        assertEquals(projectSimpleDtoPage.hasContent(), true);
//
//        assertEquals(projectList.size(), 2);
//        assertEquals(projectList.get(0).getNo(), saveProject3.getNo());
//        assertEquals(projectList.get(0).getName(), saveProject3.getName());
//        assertEquals(projectList.get(0).getProfile(), null);
//        assertEquals(projectList.get(0).getMaxPeople(), saveProject3.getMaxPeople());
//        assertEquals(projectList.get(0).getCurrentPeople(), saveProject3.getCurrentPeople());
//        assertEquals(projectList.get(0).getViewCount(), saveProject3.getViewCount());
//        assertEquals(projectList.get(0).getCommentCount(), saveProject3.getCommentCount());
//        assertEquals(projectList.get(0).getRegister(), saveProject3.getCreateUserName());
//        assertEquals(projectList.get(0).isBookMark(), false);
//
//        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getProjectNo(), saveProjectPosition3.getProject().getNo());
//        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getPositionName(), saveProjectPosition3.getPosition().getName());
//        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).isState(), saveProjectPosition3.isState());
//        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getImage(), null);
//        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getProjectNo(), saveProjectPosition4.getProject().getNo());
//        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getPositionName(), saveProjectPosition4.getPosition().getName());
//        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).isState(), saveProjectPosition4.isState());
//        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getImage(), null);
//
//        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), saveProjectTechnicalStack3.getProject().getNo());
//        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), saveProjectTechnicalStack3.getTechnicalStack().getName());
//        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), saveProjectTechnicalStack4.getProject().getNo());
//        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), saveProjectTechnicalStack4.getTechnicalStack().getName());
//
//        assertEquals(projectList.get(1).getNo(), saveProject2.getNo());
//        assertEquals(projectList.get(1).getName(), saveProject2.getName());
//        assertEquals(projectList.get(1).getProfile(), null);
//        assertEquals(projectList.get(1).getMaxPeople(), saveProject2.getMaxPeople());
//        assertEquals(projectList.get(1).getCurrentPeople(), saveProject2.getCurrentPeople());
//        assertEquals(projectList.get(1).getViewCount(), saveProject2.getViewCount());
//        assertEquals(projectList.get(1).getCommentCount(), saveProject2.getCommentCount());
//        assertEquals(projectList.get(1).getRegister(), saveProject2.getCreateUserName());
//        assertEquals(projectList.get(1).isBookMark(), false);
//
//        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(0).getProjectNo(), saveProjectPosition1.getProject().getNo());
//        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(0).getPositionName(), saveProjectPosition1.getPosition().getName());
//        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(0).isState(), saveProjectPosition1.isState());
//        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(0).getImage(), null);
//        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(1).getProjectNo(), saveProjectPosition2.getProject().getNo());
//        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(1).getPositionName(), saveProjectPosition2.getPosition().getName());
//        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(1).isState(), saveProjectPosition2.isState());
//        assertEquals(projectList.get(1).getProjectSimplePositionDtoList().get(1).getImage(), null);
//
//        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), saveProjectTechnicalStack1.getProject().getNo());
//        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), saveProjectTechnicalStack1.getTechnicalStack().getName());
//        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), saveProjectTechnicalStack2.getProject().getNo());
//        assertEquals(projectList.get(1).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), saveProjectTechnicalStack2.getTechnicalStack().getName());
//    }
//
//    @Test
//    public void 프로젝트_검색_타이틀() {
//        // given
//        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
//        LocalDate startDate = LocalDate.of(2022, 06, 24);
//        LocalDate endDate = LocalDate.of(2022, 06, 28);
//
//        // 삭제된 프로젝트 객체
//        Project project1 = Project.builder()
//                .name("testName1")
//                .createUserName("user1")
//                .createDate(createDate.plusDays(1))
//                .startDate(startDate)
//                .endDate(endDate)
//                .state(true)
//                .introduction("testIntroduction1")
//                .maxPeople(10)
//                .currentPeople(4)
//                .delete(true)
//                .deleteReason(null)
//                .viewCount(10)
//                .commentCount(10)
//                .build();
//
//        // 모집 중인 프로젝트 객체
//        Project project2 = Project.builder()
//                .name("testName2")
//                .createUserName("user1")
//                .createDate(createDate.plusDays(2))
//                .startDate(startDate)
//                .endDate(endDate)
//                .state(true)
//                .introduction("testIntroduction2")
//                .maxPeople(10)
//                .currentPeople(4)
//                .delete(false)
//                .deleteReason(null)
//                .viewCount(10)
//                .commentCount(10)
//                .build();
//
//        // 모집 중인 프로젝트 객체
//        Project project3 = Project.builder()
//                .name("testName3")
//                .createUserName("user1")
//                .createDate(createDate.plusDays(3))
//                .startDate(startDate)
//                .endDate(endDate)
//                .state(true)
//                .introduction("testIntroduction3")
//                .maxPeople(10)
//                .currentPeople(4)
//                .delete(false)
//                .deleteReason(null)
//                .viewCount(10)
//                .commentCount(10)
//                .build();
//
//        // 모집 중인 프로젝트
//        projectRepository.save(project1);
//        // 모집 중인 프로젝트 객체
//        Project saveProject2 = projectRepository.save(project2);
//        // 모집 중인 프로젝트
//        Project saveProject3 = projectRepository.save(project3);
//
//        // 포지션 세팅
//        Position position1 = Position.builder()
//                .name("testPosition1")
//                .build();
//        Position position2 = Position.builder()
//                .name("testPosition2")
//                .build();
//        positionRepository.save(position1);
//        positionRepository.save(position2);
//
//        ProjectPosition projectPosition1 = ProjectPosition.builder()
//                .state(true)
//                .project(project2)
//                .position(position1)
//                .user(null)
//                .creator(false)
//                .build();
//        ProjectPosition projectPosition2 = ProjectPosition.builder()
//                .state(false)
//                .project(project2)
//                .position(position2)
//                .user(null)
//                .creator(false)
//                .build();
//        ProjectPosition projectPosition3 = ProjectPosition.builder()
//                .state(true)
//                .project(project3)
//                .position(position1)
//                .user(null)
//                .creator(false)
//                .build();
//        ProjectPosition projectPosition4 = ProjectPosition.builder()
//                .state(false)
//                .project(project3)
//                .position(position2)
//                .user(null)
//                .creator(false)
//                .build();
//        ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);
//        ProjectPosition saveProjectPosition2 = projectPositionRepository.save(projectPosition2);
//        ProjectPosition saveProjectPosition3 = projectPositionRepository.save(projectPosition3);
//        ProjectPosition saveProjectPosition4 = projectPositionRepository.save(projectPosition4);
//
//        // 기술 스택 세팅
//        TechnicalStack technicalStack1 = TechnicalStack.builder()
//                .name("testTechnicalStack1")
//                .build();
//        TechnicalStack technicalStack2 = TechnicalStack.builder()
//                .name("testTechnicalStack2")
//                .build();
//        technicalStackRepository.save(technicalStack1);
//        technicalStackRepository.save(technicalStack2);
//
//        ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
//                .project(project2)
//                .technicalStack(technicalStack1)
//                .build();
//        ProjectTechnicalStack projectTechnicalStack2 = ProjectTechnicalStack.builder()
//                .project(project2)
//                .technicalStack(technicalStack2)
//                .build();
//        ProjectTechnicalStack projectTechnicalStack3 = ProjectTechnicalStack.builder()
//                .project(project3)
//                .technicalStack(technicalStack1)
//                .build();
//        ProjectTechnicalStack projectTechnicalStack4 = ProjectTechnicalStack.builder()
//                .project(project3)
//                .technicalStack(technicalStack2)
//                .build();
//        ProjectTechnicalStack saveProjectTechnicalStack1 = projectTechnicalStackRepository.save(projectTechnicalStack1);
//        ProjectTechnicalStack saveProjectTechnicalStack2 = projectTechnicalStackRepository.save(projectTechnicalStack2);
//        ProjectTechnicalStack saveProjectTechnicalStack3 = projectTechnicalStackRepository.save(projectTechnicalStack3);
//        ProjectTechnicalStack saveProjectTechnicalStack4 = projectTechnicalStackRepository.save(projectTechnicalStack4);
//
//        // when
//        Pageable pageable = PageRequest.of(0, 4, Sort.by("createDate").descending());
//
//        // 타이틀이나 내용에 Name2가 들어간 프로젝트 찾기
//        ProjectSearchRequestDto projectSearchDto = ProjectSearchRequestDto.builder()
//                .filter(Filter.PROJECT_NAME_AND_CONTENT)
//                .content("Name2")
//                .build();
//
//        // 모집 중이고, 삭제되지 않은 프로젝트 탐색
//        Page<ProjectSimpleDto> projectSimpleDtoPage = null;
//        try {
//            projectSimpleDtoPage = projectRepository.findProjectByStatus(pageable, true, false, projectSearchDto);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        List<ProjectSimpleDto> projectList =  projectSimpleDtoPage.getContent();
//
//        //then
//        assertEquals(projectSimpleDtoPage.getTotalPages(), 1);
//        assertEquals(projectSimpleDtoPage.getNumber(), 0);
//        assertEquals(projectSimpleDtoPage.getNumberOfElements(), 1);
//        assertEquals(projectSimpleDtoPage.hasNext(), false);
//        assertEquals(projectSimpleDtoPage.isFirst(), true);
//        assertEquals(projectSimpleDtoPage.isLast(), true);
//        assertEquals(projectSimpleDtoPage.hasContent(), true);
//
//        assertEquals(projectList.size(), 1);
//
//        assertEquals(projectList.get(0).getNo(), saveProject2.getNo());
//        assertEquals(projectList.get(0).getName(), saveProject2.getName());
//        assertEquals(projectList.get(0).getProfile(), null);
//        assertEquals(projectList.get(0).getMaxPeople(), saveProject2.getMaxPeople());
//        assertEquals(projectList.get(0).getCurrentPeople(), saveProject2.getCurrentPeople());
//        assertEquals(projectList.get(0).getViewCount(), saveProject2.getViewCount());
//        assertEquals(projectList.get(0).getCommentCount(), saveProject2.getCommentCount());
//        assertEquals(projectList.get(0).getRegister(), saveProject2.getCreateUserName());
//        assertEquals(projectList.get(0).isBookMark(), false);
//
//        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getProjectNo(), saveProjectPosition1.getProject().getNo());
//        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getPositionName(), saveProjectPosition1.getPosition().getName());
//        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).isState(), saveProjectPosition1.isState());
//        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getImage(), null);
//        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getProjectNo(), saveProjectPosition2.getProject().getNo());
//        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getPositionName(), saveProjectPosition2.getPosition().getName());
//        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).isState(), saveProjectPosition2.isState());
//        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getImage(), null);
//
//        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), saveProjectTechnicalStack1.getProject().getNo());
//        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), saveProjectTechnicalStack1.getTechnicalStack().getName());
//        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), saveProjectTechnicalStack2.getProject().getNo());
//        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), saveProjectTechnicalStack2.getTechnicalStack().getName());
//    }
//
//    @Test
//    public void 프로젝트_검색_내용() {
//        // given
//        LocalDateTime createDate = LocalDateTime.of(2022, 06, 24, 10, 10, 10);
//        LocalDate startDate = LocalDate.of(2022, 06, 24);
//        LocalDate endDate = LocalDate.of(2022, 06, 28);
//
//        // 삭제된 프로젝트 객체
//        Project project1 = Project.builder()
//                .name("testName1")
//                .createUserName("user1")
//                .createDate(createDate.plusDays(1))
//                .startDate(startDate)
//                .endDate(endDate)
//                .state(true)
//                .introduction("testIntroduction1")
//                .maxPeople(10)
//                .currentPeople(4)
//                .delete(true)
//                .deleteReason(null)
//                .viewCount(10)
//                .commentCount(10)
//                .build();
//
//        // 모집 중인 프로젝트 객체
//        Project project2 = Project.builder()
//                .name("testName2")
//                .createUserName("user1")
//                .createDate(createDate.plusDays(2))
//                .startDate(startDate)
//                .endDate(endDate)
//                .state(true)
//                .introduction("testIntroduction2")
//                .maxPeople(10)
//                .currentPeople(4)
//                .delete(false)
//                .deleteReason(null)
//                .viewCount(10)
//                .commentCount(10)
//                .build();
//
//        // 모집 중인 프로젝트 객체
//        Project project3 = Project.builder()
//                .name("testName3")
//                .createUserName("user1")
//                .createDate(createDate.plusDays(3))
//                .startDate(startDate)
//                .endDate(endDate)
//                .state(true)
//                .introduction("testIntroduction3")
//                .maxPeople(10)
//                .currentPeople(4)
//                .delete(false)
//                .deleteReason(null)
//                .viewCount(10)
//                .commentCount(10)
//                .build();
//
//        // 모집 중인 프로젝트
//        projectRepository.save(project1);
//        // 모집 중인 프로젝트 객체
//        Project saveProject2 = projectRepository.save(project2);
//        // 모집 중인 프로젝트
//        Project saveProject3 = projectRepository.save(project3);
//
//        // 포지션 세팅
//        Position position1 = Position.builder()
//                .name("testPosition1")
//                .build();
//        Position position2 = Position.builder()
//                .name("testPosition2")
//                .build();
//        positionRepository.save(position1);
//        positionRepository.save(position2);
//
//        ProjectPosition projectPosition1 = ProjectPosition.builder()
//                .state(true)
//                .project(project2)
//                .position(position1)
//                .user(null)
//                .creator(false)
//                .build();
//        ProjectPosition projectPosition2 = ProjectPosition.builder()
//                .state(false)
//                .project(project2)
//                .position(position2)
//                .user(null)
//                .creator(false)
//                .build();
//        ProjectPosition projectPosition3 = ProjectPosition.builder()
//                .state(true)
//                .project(project3)
//                .position(position1)
//                .user(null)
//                .creator(false)
//                .build();
//        ProjectPosition projectPosition4 = ProjectPosition.builder()
//                .state(false)
//                .project(project3)
//                .position(position2)
//                .user(null)
//                .creator(false)
//                .build();
//        ProjectPosition saveProjectPosition1 = projectPositionRepository.save(projectPosition1);
//        ProjectPosition saveProjectPosition2 = projectPositionRepository.save(projectPosition2);
//        ProjectPosition saveProjectPosition3 = projectPositionRepository.save(projectPosition3);
//        ProjectPosition saveProjectPosition4 = projectPositionRepository.save(projectPosition4);
//
//        // 기술 스택 세팅
//        TechnicalStack technicalStack1 = TechnicalStack.builder()
//                .name("testTechnicalStack1")
//                .build();
//        TechnicalStack technicalStack2 = TechnicalStack.builder()
//                .name("testTechnicalStack2")
//                .build();
//        technicalStackRepository.save(technicalStack1);
//        technicalStackRepository.save(technicalStack2);
//
//        ProjectTechnicalStack projectTechnicalStack1 = ProjectTechnicalStack.builder()
//                .project(project2)
//                .technicalStack(technicalStack1)
//                .build();
//        ProjectTechnicalStack projectTechnicalStack2 = ProjectTechnicalStack.builder()
//                .project(project2)
//                .technicalStack(technicalStack2)
//                .build();
//        ProjectTechnicalStack projectTechnicalStack3 = ProjectTechnicalStack.builder()
//                .project(project3)
//                .technicalStack(technicalStack1)
//                .build();
//        ProjectTechnicalStack projectTechnicalStack4 = ProjectTechnicalStack.builder()
//                .project(project3)
//                .technicalStack(technicalStack2)
//                .build();
//        ProjectTechnicalStack saveProjectTechnicalStack1 = projectTechnicalStackRepository.save(projectTechnicalStack1);
//        ProjectTechnicalStack saveProjectTechnicalStack2 = projectTechnicalStackRepository.save(projectTechnicalStack2);
//        ProjectTechnicalStack saveProjectTechnicalStack3 = projectTechnicalStackRepository.save(projectTechnicalStack3);
//        ProjectTechnicalStack saveProjectTechnicalStack4 = projectTechnicalStackRepository.save(projectTechnicalStack4);
//
//        // when
//        Pageable pageable = PageRequest.of(0, 4, Sort.by("createDate").descending());
//
//        // 타이틀이나 내용에 Name2가 들어간 프로젝트 찾기
//        ProjectSearchRequestDto projectSearchDto = ProjectSearchRequestDto.builder()
//                .filter(Filter.PROJECT_NAME_AND_CONTENT)
//                .content("Introduction2")
//                .build();
//
//        // 모집 중이고, 삭제되지 않은 프로젝트 탐색
//        Page<ProjectSimpleDto> projectSimpleDtoPage = null;
//        try {
//            projectSimpleDtoPage = projectRepository.findProjectByStatus(pageable, true, false, projectSearchDto);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        List<ProjectSimpleDto> projectList =  projectSimpleDtoPage.getContent();
//
//        //then
//        assertEquals(projectSimpleDtoPage.getTotalPages(), 1);
//        assertEquals(projectSimpleDtoPage.getNumber(), 0);
//        assertEquals(projectSimpleDtoPage.getNumberOfElements(), 1);
//        assertEquals(projectSimpleDtoPage.hasNext(), false);
//        assertEquals(projectSimpleDtoPage.isFirst(), true);
//        assertEquals(projectSimpleDtoPage.isLast(), true);
//        assertEquals(projectSimpleDtoPage.hasContent(), true);
//
//        assertEquals(projectList.size(), 1);
//
//        assertEquals(projectList.get(0).getNo(), saveProject2.getNo());
//        assertEquals(projectList.get(0).getName(), saveProject2.getName());
//        assertEquals(projectList.get(0).getProfile(), null);
//        assertEquals(projectList.get(0).getMaxPeople(), saveProject2.getMaxPeople());
//        assertEquals(projectList.get(0).getCurrentPeople(), saveProject2.getCurrentPeople());
//        assertEquals(projectList.get(0).getViewCount(), saveProject2.getViewCount());
//        assertEquals(projectList.get(0).getCommentCount(), saveProject2.getCommentCount());
//        assertEquals(projectList.get(0).getRegister(), saveProject2.getCreateUserName());
//        assertEquals(projectList.get(0).isBookMark(), false);
//
//        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getProjectNo(), saveProjectPosition1.getProject().getNo());
//        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getPositionName(), saveProjectPosition1.getPosition().getName());
//        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).isState(), saveProjectPosition1.isState());
//        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(0).getImage(), null);
//        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getProjectNo(), saveProjectPosition2.getProject().getNo());
//        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getPositionName(), saveProjectPosition2.getPosition().getName());
//        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).isState(), saveProjectPosition2.isState());
//        assertEquals(projectList.get(0).getProjectSimplePositionDtoList().get(1).getImage(), null);
//
//        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getProjectNo(), saveProjectTechnicalStack1.getProject().getNo());
//        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(0).getTechnicalStackName(), saveProjectTechnicalStack1.getTechnicalStack().getName());
//        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getProjectNo(), saveProjectTechnicalStack2.getProject().getNo());
//        assertEquals(projectList.get(0).getProjectSimpleTechnicalStackDtoList().get(1).getTechnicalStackName(), saveProjectTechnicalStack2.getTechnicalStack().getName());
//    }
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

    @Test
    public void 프로젝트_탐색() {
        // given
        LocalDateTime createDate = LocalDateTime.now();
        LocalDate startDate = LocalDate.of(2022, 06, 24);
        LocalDate endDate = LocalDate.of(2022, 06, 28);
        
        // 유저 객체
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
        
        // 프로젝트 객체
        Project project1 = Project.builder()
                .name("testName1")
                .createUserName("user1")
                .createDate(createDate.plusDays(1))
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
                .user(saveUser1)
                .build();
        Project saveProject1 = projectRepository.save(project1);

        // when
        Project resultProject = projectRepository.findProjectWithUserUsingFetchJoinByProjectNo(saveProject1.getNo());

        //then
        assertEquals(resultProject.getNo(), saveProject1.getNo());
        assertEquals(resultProject.getName(), saveProject1.getName());
        assertEquals(resultProject.getCreateDate(), saveProject1.getCreateDate());
        assertEquals(resultProject.getStartDate(), saveProject1.getStartDate());
        assertEquals(resultProject.getEndDate(), saveProject1.getEndDate());
        assertEquals(resultProject.isState(), saveProject1.isState());
        assertEquals(resultProject.getIntroduction(), saveProject1.getIntroduction());
        assertEquals(resultProject.getMaxPeople(), saveProject1.getMaxPeople());
        assertEquals(resultProject.getCurrentPeople(), saveProject1.getCurrentPeople());
        assertEquals(resultProject.isDelete(), saveProject1.isDelete());
        assertEquals(resultProject.getDeleteReason(), null);
        assertEquals(resultProject.getViewCount(), saveProject1.getViewCount());
        assertEquals(resultProject.getCommentCount(), saveProject1.getCommentCount());
        assertEquals(resultProject.getUser(), saveProject1.getUser());
    }
}