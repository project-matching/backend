package com.matching.project.service;

import com.matching.project.dto.project.ProjectPositionDto;
import com.matching.project.dto.project.ProjectRegisterRequestDto;
import com.matching.project.dto.project.ProjectRegisterResponseDto;
import com.matching.project.dto.project.ProjectSimpleDto;
import com.matching.project.entity.*;
import com.matching.project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.SpringSessionContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectPositionRepository projectPositionRepository;
    private final ProjectTechnicalStackRepository projectTechnicalStackRepository;
    private final UserRepository userRepository;
    private final ProjectUserRepository projectUserRepository;

    @Override
    public ProjectRegisterResponseDto projectRegister(ProjectRegisterRequestDto projectRegisterRequestDto) throws Exception{
        //TODO JWT 미구현으로 인한 임시 하드코딩
        Long userNo = 1L;
        User user = userRepository.findById(userNo).orElseThrow(() -> new NoSuchElementException());

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

        ProjectUser projectUser = ProjectUser.builder()
                .userNo(user)
                .projectNo(project)
                .creator(true)
                .build();

        projectUserRepository.save(projectUser);
        
        return ProjectRegisterResponseDto.of(returnProject);
    }

    @Override
    public List<ProjectSimpleDto> projectRecruitingList() throws Exception {
        return null;
    }
}
