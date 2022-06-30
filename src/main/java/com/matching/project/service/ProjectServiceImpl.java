package com.matching.project.service;

import com.matching.project.dto.project.ProjectPositionDto;
import com.matching.project.dto.project.ProjectRegisterRequestDto;
import com.matching.project.dto.project.ProjectRegisterResponseDto;
import com.matching.project.dto.project.ProjectSimpleDto;
import com.matching.project.entity.*;
import com.matching.project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectPositionRepository projectPositionRepository;
    private final ProjectTechnicalStackRepository projectTechnicalStackRepository;
    private final BookMarkRepository bookMarkRepository;

    @Override
    public ProjectRegisterResponseDto projectRegister(ProjectRegisterRequestDto projectRegisterRequestDto) throws Exception{
        Project project = Project.of(projectRegisterRequestDto);
        Project returnProject = projectRepository.save(project);

        for (ProjectPositionDto projectPositionDto : projectRegisterRequestDto.getProjectPosition()) {
            ProjectPosition projectPosition = ProjectPosition.of(projectPositionDto);
            projectPosition.setProject(project);
            projectPositionRepository.save(projectPosition);

            for (String technicalStack : projectPositionDto.getTechnicalStack()) {
                ProjectTechnicalStack projectTechnicalStack = ProjectTechnicalStack.of(technicalStack);
                projectTechnicalStack.setProjectPosition(projectPosition);
                projectTechnicalStackRepository.save(projectTechnicalStack);
            }
        }


        return ProjectRegisterResponseDto.of(returnProject);
    }

    @Override
    public List<ProjectSimpleDto> projectRecruitingList(Pageable pageable) throws Exception {
        //TODO JWT 미구현으로 인한 하드 코딩
        Long userNo = 1L;
        List<Long> projectBookMarkList = bookMarkRepository.findByUserNo(userNo).stream().map(bookMark -> bookMark.getProject().getNo()).collect(Collectors.toList());
        Page<Project> projectPage = projectRepository.findByStateProjectPage(true, false, pageable);
        List<ProjectSimpleDto> projectSimpleDtoList = projectPage.map(project -> ProjectSimpleDto.builder()
                .no(project.getNo())
                .name(project.getName())
                .profile(null)
                .bookmark(projectBookMarkList.contains(project.getNo()) ? true : false)
                .maxPeople(project.getMaxPeople())
                .currentPeople(project.getCurrentPeople())
                .viewCount(project.getViewCount())
                .commentCount(project.getCommentCount())
                .register(project.getCreateUserName())
                .build()).toList();

        return projectSimpleDtoList;
    }
}
