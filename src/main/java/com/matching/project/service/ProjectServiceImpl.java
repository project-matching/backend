package com.matching.project.service;

import com.matching.project.dto.project.ProjectPositionDto;
import com.matching.project.dto.project.ProjectRegisterRequestDto;
import com.matching.project.dto.project.ProjectRegisterResponseDto;
import com.matching.project.entity.Project;
import com.matching.project.entity.ProjectPosition;
import com.matching.project.entity.ProjectTechnicalStack;
import com.matching.project.repository.ProjectPositionRepository;
import com.matching.project.repository.ProjectRepository;
import com.matching.project.repository.ProjectTechnicalStackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectPositionRepository projectPositionRepository;
    private final ProjectTechnicalStackRepository projectTechnicalStackRepository;

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
}
