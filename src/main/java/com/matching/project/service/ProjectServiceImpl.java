package com.matching.project.service;

import com.matching.project.dto.comment.CommentDto;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.project.*;
import com.matching.project.entity.*;
import com.matching.project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectPositionRepository projectPositionRepository;
    private final ProjectTechnicalStackRepository projectTechnicalStackRepository;
    private final BookMarkRepository bookMarkRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PositionRepository positionRepository;
    private final TechnicalStackRepository technicalStackRepository;

    private void positionValidation(List<Position> positionList, List<String> positionNameList) throws Exception{
        List<Position> filterPositionList = positionList.stream().filter(position -> positionNameList.contains(position.getName())).collect(Collectors.toList());
        if (filterPositionList.size() != positionList.size()) {
            new IllegalArgumentException("잘못된 포지션입니다.");
        }
    }

    private void technicalStackValidation(List<TechnicalStack> technicalStackList, List<String> technicalStackNameList) {
        List<TechnicalStack> filterPositionList = technicalStackList.stream().filter(technicalStack -> technicalStackNameList.contains(technicalStack.getName())).collect(Collectors.toList());
        if (filterPositionList.size() != technicalStackNameList.size()) {
            new IllegalArgumentException("잘못된 기술스택입니다.");
        }
    }
    
    // 프로젝트 포지션 저장 메소드
    private void saveProjectPosition(Project project, List<ProjectPositionDto> projectPositionDtoList, List<Position> positionList) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        for (ProjectPositionDto projectPositionDto : projectPositionDtoList) {
            ProjectPosition projectPosition = null;
            if (projectPositionDto.isState()) {
                projectPosition = ProjectPosition.builder()
                        .state(projectPositionDto.isState())
                        .project(project)
                        .position(positionList.stream()
                                .filter(position -> (position.getName()).equals(projectPositionDto.getName()))
                                .findAny()
                                .orElseThrow(() -> new NoSuchElementException("잘못된 position")))
                        .user(user)
                        .creator(true)
                        .build();
            } else {
                projectPosition = ProjectPosition.builder()
                        .state(projectPositionDto.isState())
                        .project(project)
                        .position(positionList.stream()
                                .filter(position -> (position.getName()).equals(projectPositionDto.getName()))
                                .findAny()
                                .orElseThrow(() -> new NoSuchElementException("잘못된 position")))
                        .creator(false)
                        .build();
            }
            projectPositionRepository.save(projectPosition);
        }
    }
    
    // 기술 스택 저장
    public void saveTechnicalStack(Project project, List<String> technicalStackNameList, List<TechnicalStack> technicalStackList) {
        for (String name : technicalStackNameList) {
            ProjectTechnicalStack projectTechnicalStack = ProjectTechnicalStack.builder()
                    .technicalStack(technicalStackList.stream()
                            .filter(technicalStack -> (technicalStack.getName()).equals(name))
                            .findAny()
                            .orElseThrow(() -> new NoSuchElementException("잘못된 technicalStack")))
                    .project(project)
                    .build();
            projectTechnicalStackRepository.save(projectTechnicalStack);
        }
    }
    @Override
    public ProjectRegisterResponseDto projectRegister(ProjectRegisterRequestDto projectRegisterRequestDto) throws Exception{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Project project = Project.of(projectRegisterRequestDto, user);
        project = projectRepository.save(project);

        List<ProjectPositionDto> projectPositionDtoList = projectRegisterRequestDto.getProjectPositionDtoList();
        List<String> positionNameList = projectPositionDtoList.stream()
                .map(projectPositionDto -> projectPositionDto.getName()).collect(Collectors.toList());

        List<Position> positionList = positionRepository.findByNameIn(positionNameList);

        // 포지션 존재여부 판단
        positionValidation(positionList, positionNameList);
        
        // 포지션 저장
        saveProjectPosition(project, projectPositionDtoList, positionList);

        List<String> technicalStackNameList = projectRegisterRequestDto.getProjectTechnicalStack();
        List<TechnicalStack> technicalStackList = technicalStackRepository.findByNameIn(technicalStackNameList);

        // 기술스택 존재 여부 판단
        technicalStackValidation(technicalStackList, technicalStackNameList);

        // 기술스택 저장
        saveTechnicalStack(project, technicalStackNameList, technicalStackList);

        ProjectRegisterResponseDto projectRegisterResponseDto = ProjectRegisterResponseDto.builder()
                .no(project.getNo())
                .name(project.getName())
                .createUser(project.getCreateUserName())
                .profile(null)
                .createDate(project.getCreateDate())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .state(project.isState())
                .introduction(project.getIntroduction())
                .maxPeople(project.getMaxPeople())
                .currentPeople(project.getCurrentPeople())
                .viewCount(project.getViewCount())
                .commentCount(project.getCommentCount())
                .build();
        projectRegisterResponseDto.setProjectPositionDtoList(projectPositionDtoList);
        projectRegisterResponseDto.setProjectTechnicalStack(technicalStackNameList);

        return projectRegisterResponseDto;
    }

    @Override
    public List<ProjectSimpleDto> findProjectList(boolean state, boolean delete, Pageable pageable) throws Exception {
        Page<Project> projectPage = projectRepository.findByStateProjectPage(state, delete, pageable);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<ProjectSimpleDto> projectSimpleDtoList = null;
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_ANONYMOUS.toString()))) {
            projectSimpleDtoList = projectPage.map(project -> ProjectSimpleDto.builder()
                    .no(project.getNo())
                    .name(project.getName())
                    .profile(null)
                    .maxPeople(project.getMaxPeople())
                    .currentPeople(project.getCurrentPeople())
                    .viewCount(project.getViewCount())
                    .commentCount(project.getCommentCount())
                    .register(project.getCreateUserName())
                    .bookMark(false)
                    .build()).toList();

        } else {
            Object principal = authentication.getPrincipal();
            User user = (User) principal;
            List<Long> bookMarkList = bookMarkRepository.findByUserNo(user.getNo()).stream().map(bookMark -> bookMark.getProject().getNo()).collect(Collectors.toList());
            projectSimpleDtoList = projectPage.map(project -> ProjectSimpleDto.builder()
                    .no(project.getNo())
                    .name(project.getName())
                    .profile(null)
                    .maxPeople(project.getMaxPeople())
                    .currentPeople(project.getCurrentPeople())
                    .viewCount(project.getViewCount())
                    .commentCount(project.getCommentCount())
                    .register(project.getCreateUserName())
                    .bookMark(bookMarkList.contains(project.getNo()))
                    .build()).toList();
        }

        return projectSimpleDtoList;
    }

    @Override
    public ProjectDto getProjectDetail(Long projectNo) {
        // 프로젝트 조회
        Project project = projectRepository.findById(projectNo).orElseThrow(() -> new NoSuchElementException("프로젝트를 찾지 못했습니다."));
        // 포지션 조회
        List<ProjectPosition> projectPositionList = projectPositionRepository.findByProjectWithPositionAndProjectAndUserUsingLeftFetchJoin(project);
        // 댓글 조회
        List<Comment> commentList = commentRepository.findByProjectNo(project);
        // 기술 스택 조회
        List<ProjectTechnicalStack> projectTechnicalStackList = projectTechnicalStackRepository.findByProjectWithTechnicalStackAndProjectUsingFetchJoin(project);

        ProjectDto projectDto = ProjectDto.builder()
                .name(project.getName())
                // 이미지 추가 필요
                .profile(null)
                .createDate(project.getCreateDate())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .state(project.isState())
                .introduction(project.getIntroduction())
                .maxPeople(project.getMaxPeople())
                .bookmark(false)
                .register(project.getCreateUserName())
                .build();
        
        // projectPositionDto로 변환
        List<ProjectPositionDetailDto> projectPositionDetailDtoList = projectPositionList.stream()
                .map(projectPosition ->
                        new ProjectPositionDetailDto(
                                projectPosition.getNo(),
                                projectPosition.getPosition().getName(),
                                projectPosition.isState(),
                                projectPosition.getUser() == null ? null : new UserDetailDto(projectPosition.getUser().getNo(), projectPosition.getUser().getName()))
                ).collect(Collectors.toList());
        projectDto.setProjectPositionDetailDtoList(projectPositionDetailDtoList);
        
        // commentDto로 변환
        List<CommentDto> commentDtoList = commentList.stream()
                .map(comment -> new CommentDto(
                                comment.getNo(),
                                comment.getUser().getName(),
                                comment.getContent()
                        )
                ).collect(Collectors.toList());
        projectDto.setCommentDtoList(commentDtoList);
        
        // 기술스택 String으로 변환
        List<String> technicalStackList = projectTechnicalStackList.stream()
                .map(projectTechnicalStack -> projectTechnicalStack.getTechnicalStack().getName())
                .collect(Collectors.toList());
        projectDto.setTechnicalStack(technicalStackList);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_USER.toString()))) {
            Object principal = authentication.getPrincipal();
            User user = (User) principal;
            boolean bookMark = bookMarkRepository.existBookMark(user, project);
            projectDto.setBookmark(bookMark);
        }

        return projectDto;
    }
    
    // 프로젝트를 생성한 유저인지 판단
    private boolean isCreate(User user, Project project) {
        return projectRepository.existProject(user, project);
    }
    
    @Override
    public ProjectUpdateResponseDto updateProject(Long projectNo, ProjectUpdateRequestDto projectUpdateRequestDto) throws Exception {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;

        // 프로젝트 조회
        Project project = projectRepository.findById(projectNo).orElseThrow(() -> new NoSuchElementException("프로젝트를 찾지 못했습니다."));

        // 프로젝트를 생성한 유저인지 판단
        if (!isCreate(user, project)) {
            new IllegalArgumentException("잘못된 접근입니다.");
        }
        // 프로젝트 수정
        project.changeUpdateInfo(projectUpdateRequestDto.getName(),
                projectUpdateRequestDto.getStartDate(),
                projectUpdateRequestDto.getEndDate(),
                projectUpdateRequestDto.getIntroduction(),
                projectUpdateRequestDto.getMaxPeople());

        // 포지션 존재여부 판단
        List<ProjectPositionUpdateDto> projectPositionUpdateDto = projectUpdateRequestDto.getProjectPositionDtoList();
        List<String> positionNameList = projectPositionUpdateDto.stream()
                .map(projectPositionDto -> projectPositionDto.getName()).collect(Collectors.toList());
        List<Position> positionList = positionRepository.findByNameIn(positionNameList);

        positionValidation(positionList, positionNameList);

        // 유저가 없는 포지션 삭제
        projectPositionRepository.deleteByProjectAndUserIsNull(project);

        // 유저가 있는지 판단 후 유저가 없는 프로젝트만 save
        List<ProjectPositionUpdateDto> noneUserProjectPositionDto = projectPositionUpdateDto.stream()
                .filter(projectPositionDto -> projectPositionDto.getUserUpdateDto() == null)
                .collect(Collectors.toList());

        for (ProjectPositionUpdateDto positionUpdateDto : noneUserProjectPositionDto) {
            ProjectPosition projectPosition = ProjectPosition.builder()
                    .state(false)
                    .project(project)
                    .position(positionList.stream()
                            .filter(position -> (position.getName()).equals(positionUpdateDto.getName()))
                            .findAny()
                            .orElseThrow(() -> new NoSuchElementException("잘못된 position")))
                    .user(null)
                    .creator(true)
                    .build();

            projectPositionRepository.save(projectPosition);
        }

        List<String> technicalStackNameList = projectUpdateRequestDto.getProjectTechnicalStack();
        List<TechnicalStack> technicalStackList = technicalStackRepository.findByNameIn(technicalStackNameList);

        // 기술스택 존재 여부 판단
        technicalStackValidation(technicalStackList, technicalStackNameList);

        // 기술 스택 삭제
        projectTechnicalStackRepository.deleteByProject(project);
        // 기술스택 저장
        saveTechnicalStack(project, technicalStackNameList, technicalStackList);

        ProjectUpdateResponseDto projectUpdateResponseDto = ProjectUpdateResponseDto.builder()
                .no(project.getNo())
                .name(project.getName())
                .createUser(project.getCreateUserName())
                .profile(null)
                .createDate(project.getCreateDate())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .state(project.isState())
                .introduction(project.getIntroduction())
                .maxPeople(project.getMaxPeople())
                .currentPeople(project.getCurrentPeople())
                .viewCount(project.getViewCount())
                .commentCount(project.getCommentCount())
                .build();
        projectUpdateResponseDto.setProjectPositionDtoList(projectPositionUpdateDto);
        projectUpdateResponseDto.setProjectTechnicalStack(technicalStackNameList);

        return projectUpdateResponseDto;
    }
}
