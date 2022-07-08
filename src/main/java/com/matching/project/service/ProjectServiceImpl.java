package com.matching.project.service;

import com.matching.project.dto.comment.CommentDto;
import com.matching.project.dto.project.*;
import com.matching.project.dto.user.UserSimpleInfoDto;
import com.matching.project.entity.*;
import com.matching.project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
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
    private final CommentRepository commentRepository;

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

    @Override
    public ProjectDto getProjectDetail(Long projectNo) {
        ProjectQueryDto detailProject = projectRepository.findDetailProject(projectNo);
        ProjectDto projectDto = ProjectDto.builder()
                .name(detailProject.getName())
                .profile(null)
                .createDate(detailProject.getCreateDate())
                .startDate(detailProject.getStartDate())
                .endDate(detailProject.getEndDate())
                .state(detailProject.isState())
                .introduction(detailProject.getIntroduction())
                .maxPeople(detailProject.getMaxPeople())
                // 추가 필요
                .bookmark(false)
                .register(detailProject.getCreateUserName())
                .build();

        // ProjectPositionDto List 변경
        List<ProjectPositionDto> projectPositionDtoList = new ArrayList<>();
        for (ProjectPositionQueryDto projectPositionQueryDto : detailProject.getProjectPositionList()) {
            ProjectPositionDto projectPositionDto = ProjectPositionQueryDto.toProjectPositionDto(projectPositionQueryDto);
            projectPositionDtoList.add(projectPositionDto);
        }
        projectDto.setProjectPosition(projectPositionDtoList);

        List<ProjectUser> projectUserList = projectUserRepository.findByProjectNo(detailProject.getNo());
        
        // UserSimpleInfoDto List 변경
        // todo 이미지 기능 추가시 변경 필요
        List<UserSimpleInfoDto> userSimpleInfoDtoList = projectUserList.stream()
                .map(projectUser ->
                        new UserSimpleInfoDto(
                                projectUser.getUser().getNo(),
                                projectUser.getUser().getName(),
                                projectUser.getProjectPosition(),
                                projectUser.isCreator()
                        )
                ).collect(Collectors.toList());

        projectDto.setUserSimpleInfoDtoList(userSimpleInfoDtoList);

        // CommentDto List 변경
        List<Comment> commentList = commentRepository.findByProjectNo(detailProject.getNo());
        List<CommentDto> commentDtoList = commentList.stream()
                .map(comment -> new CommentDto(comment.getNo(), comment.getUser().getName(), comment.getContent()))
                .collect(Collectors.toList());

        projectDto.setCommentDtoList(commentDtoList);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 비인증 상태면 북마크 상태인지 확인 안하고 리턴 인증 상태면 북마크 상태인지 확인 후 리턴
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return projectDto;
        } else  {
            User user = (User)authentication.getPrincipal();
            // 북마크가 존재하는지 확인후 세팅
            projectDto.setBookmark(bookMarkRepository.existBookMark(user.getNo(), detailProject.getNo()));
        }

        return projectDto;
    }
}
