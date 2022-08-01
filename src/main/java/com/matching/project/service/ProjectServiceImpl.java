package com.matching.project.service;

import com.matching.project.dto.comment.CommentDto;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.project.*;
import com.matching.project.dto.projectposition.ProjectPositionRegisterDto;
import com.matching.project.dto.projectposition.ProjectPositionUpdateFormDto;
import com.matching.project.dto.user.ProjectUpdateFormUserDto;
import com.matching.project.entity.*;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import com.matching.project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
            new CustomException(ErrorCode.POSITION_NO_SUCH_ELEMENT_EXCEPTION);
        }
    }

    private void technicalStackValidation(List<TechnicalStack> technicalStackList, List<String> technicalStackNameList) {
        List<TechnicalStack> filterPositionList = technicalStackList.stream().filter(technicalStack -> technicalStackNameList.contains(technicalStack.getName())).collect(Collectors.toList());
        if (filterPositionList.size() != technicalStackNameList.size()) {
            new IllegalArgumentException("잘못된 기술스택입니다.");
        }
    }

    @Override
    public ProjectRegisterFormResponseDto findProjectRegisterForm() throws Exception {
        List<Position> positionList = positionRepository.findAll();
        List<PositionRegisterFormDto> positionRegisterFormDtoList = positionList.stream()
                .map(position -> new PositionRegisterFormDto(position.getNo(), position.getName()))
                .collect(Collectors.toList());

        List<TechnicalStack> technicalStackList = technicalStackRepository.findAll();
        List<TechnicalStackRegisterFormDto> technicalStackRegisterFormDtoList = technicalStackList.stream()
                .map(technicalStack -> new TechnicalStackRegisterFormDto(technicalStack.getNo(), technicalStack.getName()))
                .collect(Collectors.toList());

        return new ProjectRegisterFormResponseDto(positionRegisterFormDtoList, technicalStackRegisterFormDtoList);
    }

    @Override
    public Long projectRegister(ProjectRegisterRequestDto projectRegisterRequestDto) throws Exception{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        
        // 프로젝트 저장
        Project project = Project.of(projectRegisterRequestDto, user);
        project = projectRepository.save(project);

        // 프로젝트 포지션 저장
        List<ProjectPositionRegisterDto> projectPositionRegisterDtoList = projectRegisterRequestDto.getProjectPositionRegisterDtoList();

        List<Long> positionNoList = projectPositionRegisterDtoList.stream()
                .map(projectPositionRegisterDto -> projectPositionRegisterDto.getPositionNo()).collect(Collectors.toList());
        List<Position> positionList = positionRepository.findByNoIn(positionNoList);

        for (ProjectPositionRegisterDto projectPositionRegisterDto : projectPositionRegisterDtoList) {
            ProjectPosition projectPosition = null;
            if (projectPositionRegisterDto.getProjectRegisterUserDto() != null) {
                projectPosition = ProjectPosition.builder()
                        .state(true)
                        .position(positionList.stream().filter(position -> position.getNo() == projectPositionRegisterDto.getPositionNo()).findAny().orElseThrow())
                        .user(user)
                        .creator(true)
                        .build();
                projectPosition.setProject(project);
            } else {
                projectPosition = ProjectPosition.builder()
                        .state(false)
                        .position(positionList.stream().filter(position -> position.getNo() == projectPositionRegisterDto.getPositionNo()).findAny().orElseThrow())
                        .user(null)
                        .build();
                projectPosition.setProject(project);
            }
            projectPositionRepository.save(projectPosition);
        }

        // 프로젝트 기술스택 저장
        List<Long> projectTechnicalStackNoList = projectRegisterRequestDto.getProjectTechnicalStackList();
        List<TechnicalStack> technicalStackList = technicalStackRepository.findByNoIn(projectTechnicalStackNoList);
        for (Long no : projectTechnicalStackNoList) {
            ProjectTechnicalStack projectTechnicalStack = ProjectTechnicalStack.builder()
                    .technicalStack(technicalStackList.stream().filter(technicalStack -> technicalStack.getNo() == no).findAny().orElseThrow())
                    .build();
            projectTechnicalStack.setProject(project);
            projectTechnicalStackRepository.save(projectTechnicalStack);
        }

        return project.getNo();
    }
    
    // 프로젝트 조회
    @Override
    public Page<ProjectSimpleDto> findProjectList(boolean state, boolean delete, ProjectSearchRequestDto projectSearchRequestDto, Pageable pageable) throws Exception {
        Page<ProjectSimpleDto> projectSimpleDtoPage = projectRepository.findProjectByStatusAndDelete(pageable, state, delete, projectSearchRequestDto);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_USER.toString()))) {
            Object principal = authentication.getPrincipal();
            User user = (User) principal;

            // 유저 즐겨찾기 조회
            findBookMark(projectSimpleDtoPage.getContent(), user);
        }
        return projectSimpleDtoPage;
    }
    
    // 유저가 만든 프로젝트 조회
    @Override
    public Page<ProjectSimpleDto> findUserProjectList(boolean delete, Pageable pageable) throws Exception {
        // 현재 로그인한 유저 정보 가져오기
        User user = getUser();
        
        // 유저가 등록한 프로젝트 조회
        Page<ProjectSimpleDto> projectSimpleDtoPage = projectRepository.findUserProjectByDelete(pageable, user, delete);

        // 유저 즐겨찾기 조회
        findBookMark(projectSimpleDtoPage.getContent(), user);

        return projectSimpleDtoPage;
    }
    
    // 프로젝트 수정 폼 조회
    @Override
    public ProjectUpdateFormResponseDto getProjectUpdateForm(Long projectNo) throws Exception {
        // 프로젝트 조회
        Project project = projectRepository.findById(projectNo).orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NO_SUCH_ELEMENT_EXCEPTION));

        // 포지션 조회
        List<PositionUpdateFormDto> positionUpdateFormDtoList = positionRepository.findAll().stream()
                .map(position -> new PositionUpdateFormDto(position.getNo(), position.getName()))
                .collect(Collectors.toList());

        // 기술스택 조회
        List<TechnicalStackUpdateFormDto> technicalStackUpdateFormDtoList = technicalStackRepository.findAll().stream()
                .map(technicalStack -> new TechnicalStackUpdateFormDto(technicalStack.getNo(), technicalStack.getName()))
                .collect(Collectors.toList());

        // 프로젝트 포지션 조회
        List<ProjectPositionUpdateFormDto> projectPositionUpdateFormDtoList = projectPositionRepository.findProjectAndPositionAndUserUsingFetchJoinByProjectNo(project).stream()
                .map(projectPosition -> new ProjectPositionUpdateFormDto(
                        projectPosition.getNo(),
                        projectPosition.getPosition().getNo(),
                        projectPosition.getPosition().getName(),
                        projectPosition.getUser() == null ? null : new ProjectUpdateFormUserDto(projectPosition.getUser().getNo())
                )).collect(Collectors.toList());

        // 프로젝트 기술스택 조회
        List<String> projectTechnicalStackList = projectTechnicalStackRepository.findTechnicalStackAndProjectUsingFetchJoin(project).stream()
                .map(projectTechnicalStack -> projectTechnicalStack.getTechnicalStack().getName())
                .collect(Collectors.toList());

        ProjectUpdateFormResponseDto projectUpdateFormResponseDto = ProjectUpdateFormResponseDto.builder()
                .projectNo(project.getNo())
                .name(project.getName())
                .state(project.isState())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .introduction(project.getIntroduction())
                .build();
        projectUpdateFormResponseDto.setPositionUpdateFormDtoList(positionUpdateFormDtoList);
        projectUpdateFormResponseDto.setProjectPositionUpdateFormDtoList(projectPositionUpdateFormDtoList);
        projectUpdateFormResponseDto.setTechnicalStackUpdateFormDtoList(technicalStackUpdateFormDtoList);
        projectUpdateFormResponseDto.setProjectTechnicalStackList(projectTechnicalStackList);

        return projectUpdateFormResponseDto;
    }

    // 참여중인 프로젝트 조회
    @Override
    public Page<ProjectSimpleDto> findParticipateProjectList(boolean delete, Pageable pageable) throws Exception {
        // 현재 로그인한 유저 정보 가져오기
        User user = getUser();

        // 유저가 등록한 프로젝트 조회
        Page<ProjectSimpleDto> projectSimpleDtoPage = projectRepository.findParticipateProjectByDelete(pageable, user, delete);
        
        // 유저 즐겨찾기 조회
        findBookMark(projectSimpleDtoPage.getContent(), user);

        return projectSimpleDtoPage;
    }
    
    // 신청중인 프로젝트 조회
    @Override
    public Page<ProjectSimpleDto> findParticipateRequestProjectList(boolean delete, Pageable pageable) throws Exception {
        // 현재 로그인한 유저 정보 가져오기
        User user = getUser();

        // 유저가 등록한 프로젝트 조회
        Page<ProjectSimpleDto> projectSimpleDtoPage = projectRepository.findParticipateRequestProjectByDelete(pageable, user, delete);

        // 유저 즐겨찾기 조회
        findBookMark(projectSimpleDtoPage.getContent(), user);

        return projectSimpleDtoPage;
    }
    
    // 유저 즐겨찾기 조회
    private void findBookMark(List<ProjectSimpleDto> projectSimpleDtoList, User user) {
        List<Long> bookMarkList = bookMarkRepository.findByUserNo(user.getNo()).stream().map(bookMark -> bookMark.getProject().getNo()).collect(Collectors.toList());
        for (ProjectSimpleDto projectSimpleDto : projectSimpleDtoList) {
            projectSimpleDto.setBookMark(bookMarkList.contains(projectSimpleDto.getProjectNo()));
        }
    }
    
    // 현재 로그인한 유저 정보 가져오기
    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        return (User) principal;
    }
    
    @Override
    public ProjectDto getProjectDetail(Long projectNo) {
//        // 프로젝트 조회
//        Project project = projectRepository.findById(projectNo).orElseThrow(() -> new NoSuchElementException("프로젝트를 찾지 못했습니다."));
//        // 포지션 조회
//        List<ProjectPosition> projectPositionList = projectPositionRepository.findByProjectWithPositionAndProjectAndUserUsingLeftFetchJoin(project);
//        // 댓글 조회
//        List<Comment> commentList = commentRepository.findByProjectNo(project);
//        // 기술 스택 조회
//        List<ProjectTechnicalStack> projectTechnicalStackList = projectTechnicalStackRepository.findByProjectWithTechnicalStackAndProjectUsingFetchJoin(project);
//
//        ProjectDto projectDto = ProjectDto.builder()
//                .name(project.getName())
//                // 이미지 추가 필요
//                .profile(null)
//                .createDate(project.getCreateDate())
//                .startDate(project.getStartDate())
//                .endDate(project.getEndDate())
//                .state(project.isState())
//                .introduction(project.getIntroduction())
//                .maxPeople(project.getMaxPeople())
//                .bookmark(false)
//                .register(project.getCreateUserName())
//                .build();
//
//        // projectPositionDto로 변환
//        List<ProjectPositionDetailDto> projectPositionDetailDtoList = projectPositionList.stream()
//                .map(projectPosition ->
//                        new ProjectPositionDetailDto(
//                                projectPosition.getPosition().getName(),
//                                projectPosition.getUser() == null ? null : projectPosition.getUser().getNo(),
//                                projectPosition.getUser() == null ? null : projectPosition.getUser().getName(),
//                                projectPosition.isState())
//                ).collect(Collectors.toList());
//        projectDto.setProjectPositionDetailDtoList(projectPositionDetailDtoList);
//
//        // commentDto로 변환
//        List<CommentDto> commentDtoList = commentList.stream()
//                .map(comment -> new CommentDto(
//                                comment.getNo(),
//                                comment.getUser().getName(),
//                                comment.getContent()
//                        )
//                ).collect(Collectors.toList());
//        projectDto.setCommentDtoList(commentDtoList);
//
//        // 기술스택 String으로 변환
//        List<String> technicalStackList = projectTechnicalStackList.stream()
//                .map(projectTechnicalStack -> projectTechnicalStack.getTechnicalStack().getName())
//                .collect(Collectors.toList());
//        projectDto.setTechnicalStack(technicalStackList);
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_USER.toString()))) {
//            Object principal = authentication.getPrincipal();
//            User user = (User) principal;
//            boolean bookMark = bookMarkRepository.existBookMark(user, project);
//            projectDto.setBookmark(bookMark);
//        }
//
//        return projectDto;
        return null;
    }
}
