package com.matching.project.service;

import com.matching.project.dto.project.*;
import com.matching.project.entity.*;
import com.matching.project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectPositionRepository projectPositionRepository;
    private final ProjectTechnicalStackRepository projectTechnicalStackRepository;
    private final BookMarkRepository bookMarkRepository;
    private final UserRepository userRepository;
    private final ProjectUserRepository projectUserRepository;

    @Override
    public ProjectRegisterResponseDto projectRegister(ProjectRegisterRequestDto projectRegisterRequestDto) throws Exception{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Project project = Project.of(projectRegisterRequestDto, user);
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
                .user(user)
                .project(project)
                .creator(true)
                .build();

        projectUserRepository.save(projectUser);
        
        return ProjectRegisterResponseDto.of(returnProject);
    }

    @Override
    public List<NoneLoginProjectSimpleDto> NoneLoginProjectRecruitingList(Pageable pageable) throws Exception {
        Page<Project> projectPage = projectRepository.findByStateProjectPage(true, false, pageable);

        List<NoneLoginProjectSimpleDto> projectSimpleDtoList = projectPage.map(project -> NoneLoginProjectSimpleDto.builder()
                .no(project.getNo())
                .name(project.getName())
                .profile(null)
                .maxPeople(project.getMaxPeople())
                .currentPeople(project.getCurrentPeople())
                .viewCount(project.getViewCount())
                .commentCount(project.getCommentCount())
                .register(project.getCreateUserName())
                .build()).toList();

        return projectSimpleDtoList;
    }

    @Override
    public List<LoginProjectSimpleDto> LoginProjectRecruitingList(Pageable pageable) throws Exception {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Long> projectBookMarkList = bookMarkRepository.findByUserNo(user.getNo()).stream().map(bookMark -> bookMark.getProject().getNo()).collect(Collectors.toList());
        Page<Project> projectPage = projectRepository.findByStateProjectPage(true, false, pageable);

        List<LoginProjectSimpleDto> projectSimpleDtoList = projectPage.map(project -> LoginProjectSimpleDto.builder()
                .no(project.getNo())
                .name(project.getName())
                .profile(null)
                .maxPeople(project.getMaxPeople())
                .currentPeople(project.getCurrentPeople())
                .bookMark(projectBookMarkList.contains(project.getNo()) ? true : false)
                .viewCount(project.getViewCount())
                .commentCount(project.getCommentCount())
                .register(project.getCreateUserName())
                .build()).toList();

        return projectSimpleDtoList;
    }
}
