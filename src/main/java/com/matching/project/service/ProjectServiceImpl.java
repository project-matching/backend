package com.matching.project.service;

import com.matching.project.dto.SliceDto;
import com.matching.project.dto.comment.CommentDto;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.project.*;
import com.matching.project.dto.projectposition.ProjectPositionAddDto;
import com.matching.project.dto.projectposition.ProjectPositionDeleteDto;
import com.matching.project.dto.projectposition.ProjectPositionRegisterDto;
import com.matching.project.dto.projectposition.ProjectPositionUpdateFormDto;
import com.matching.project.dto.user.ProjectUpdateFormUserDto;
import com.matching.project.dto.user.UserDto;
import com.matching.project.entity.*;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import com.matching.project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
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
    private final ParticipateRequestTechnicalStackRepository participateRequestTechnicalStackRepository;
    private final ProjectParticipateRequestRepository projectParticipateRequestRepository;
    private final EntityManager entityManager;

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
                        .position(positionList.stream().filter(position -> position.getNo().equals(projectPositionRegisterDto.getPositionNo())).findAny().orElseThrow())
                        .user(user)
                        .creator(true)
                        .build();
                projectPosition.setProject(project);
            } else {
                projectPosition = ProjectPosition.builder()
                        .state(false)
                        .position(positionList.stream().filter(position -> position.getNo().equals(projectPositionRegisterDto.getPositionNo())).findAny().orElseThrow())
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
                    .technicalStack(technicalStackList.stream().filter(technicalStack -> technicalStack.getNo().equals(no)).findAny().orElseThrow())
                    .build();
            projectTechnicalStack.setProject(project);
            projectTechnicalStackRepository.save(projectTechnicalStack);
        }

        return project.getNo();
    }
    
    // 프로젝트 조회
    @Override
    public SliceDto<ProjectSimpleDto> findProjectList(Long no, boolean state, Pageable pageable) throws Exception {
        Slice<ProjectSimpleDto> projectSimpleDtoSlice = projectRepository.findProjectByStatus(pageable, no != null ? no : Long.MAX_VALUE, state);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_USER.toString()))) {
            Object principal = authentication.getPrincipal();
            User user = (User) principal;

            // 유저 즐겨찾기 조회
            findBookMark(projectSimpleDtoSlice.getContent(), user);
        }

        return new SliceDto<ProjectSimpleDto>(projectSimpleDtoSlice.getContent(), projectSimpleDtoSlice.isLast());
    }
    
    // 유저가 만든 프로젝트 조회
    @Override
    public List<ProjectSimpleDto> findUserProjectList(Pageable pageable) throws Exception {
        // 현재 로그인한 유저 정보 가져오기
        User user = getUser();
        
        // 유저가 등록한 프로젝트 조회
        Page<ProjectSimpleDto> projectSimpleDtoPage = projectRepository.findUserProject(pageable, user);

        // 유저 즐겨찾기 조회
        findBookMark(projectSimpleDtoPage.getContent(), user);

        return projectSimpleDtoPage.getContent();
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
    public List<ProjectSimpleDto> findParticipateProjectList(Pageable pageable) throws Exception {
        // 현재 로그인한 유저 정보 가져오기
        User user = getUser();

        // 유저가 등록한 프로젝트 조회
        Page<ProjectSimpleDto> projectSimpleDtoPage = projectRepository.findParticipateProject(pageable, user);
        
        // 유저 즐겨찾기 조회
        findBookMark(projectSimpleDtoPage.getContent(), user);

        return projectSimpleDtoPage.getContent();
    }
    
    // 신청중인 프로젝트 조회
    @Override
    public List<ProjectSimpleDto> findParticipateRequestProjectList(Pageable pageable) throws Exception {
        // 현재 로그인한 유저 정보 가져오기
        User user = getUser();

        // 유저가 등록한 프로젝트 조회
        Page<ProjectSimpleDto> projectSimpleDtoPage = projectRepository.findParticipateRequestProject(pageable, user);

        // 유저 즐겨찾기 조회
        findBookMark(projectSimpleDtoPage.getContent(), user);

        return projectSimpleDtoPage.getContent();
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
        // 프로젝트 조회
        Project project = projectRepository.findById(projectNo).orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NO_SUCH_ELEMENT_EXCEPTION));

        // 포지션 조회
        List<ProjectPositionDetailDto> projectPositionDetailDtoList = projectPositionRepository.findProjectAndPositionAndUserUsingFetchJoinByProjectNo(project).stream()
                .map(projectPosition -> new ProjectPositionDetailDto(
                        projectPosition.getNo(),
                        projectPosition.getPosition().getName(),
                        projectPosition.getUser() == null ? null : new UserDto(projectPosition.getUser().getNo(),
                                projectPosition.getUser().getName(),
                                projectPosition.isCreator())
                )).collect(Collectors.toList());

        // 기술 스택 조회
        List<String> technicalStackList = projectTechnicalStackRepository.findTechnicalStackAndProjectUsingFetchJoin(project).stream()
                .map(projectTechnicalStack -> projectTechnicalStack.getTechnicalStack().getName())
                .collect(Collectors.toList());

        // Dto 세팅
        ProjectDto projectDto = ProjectDto.builder()
                .projectNo(project.getNo())
                .name(project.getName())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .state(project.isState())
                .introduction(project.getIntroduction())
                .currentPeople(project.getCurrentPeople())
                .maxPeople(project.getMaxPeople())
                .bookmark(false)
                .applicationStatus(false)
                .build();
        projectDto.setProjectPositionDetailDtoList(projectPositionDetailDtoList);
        projectDto.setTechnicalStackList(technicalStackList);
        
        // 권한 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 로그인한 유저인 경우(유저, 어드민)
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_USER.toString()))
                || authentication.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_ADMIN.toString())) ) {
            Object principal = authentication.getPrincipal();
            User user = (User) principal;
            
            // 북마크 세팅
            boolean bookMark = bookMarkRepository.existBookMark(user, project);
            projectDto.setBookmark(bookMark);

            // 프로젝트 참여 여부 판단
            boolean applicationStatus = false;
            for (ProjectPositionDetailDto projectPositionDetailDto : projectPositionDetailDtoList) {
                if (projectPositionDetailDto.getUserDto() != null && projectPositionDetailDto.getUserDto().getNo().equals(user.getNo())) {
                    applicationStatus = true;
                    break;
                }
            }
            projectDto.setApplicationStatus(applicationStatus);
        }

        return projectDto;
    }

    @Override
    public Long projectUpdate(Long projectNo, ProjectUpdateRequestDto projectUpdateRequestDto) throws Exception {
        // 자신이 만든 프로젝트인지 판단
        if (!isRegisterProjectUser(projectNo)) {
            throw new CustomException(ErrorCode.PROJECT_NOT_REGISTER_USER);
        }

        Project project = projectRepository.findById(projectNo).orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NO_SUCH_ELEMENT_EXCEPTION));
        LocalDate startDate = LocalDate.parse(projectUpdateRequestDto.getStartDate(), DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate.parse(projectUpdateRequestDto.getEndDate(), DateTimeFormatter.ISO_DATE);

        // 프로젝트 업데이트
        project.updateProject(projectUpdateRequestDto.getName(), startDate, endDate, projectUpdateRequestDto.getIntroduction());
        entityManager.flush();
        entityManager.clear();

        // 프로젝트 포지션 삭제
        if (projectUpdateRequestDto.getProjectPositionDeleteDtoList() != null) {
            List<Long> projectPositionDeleteNos = projectUpdateRequestDto.getProjectPositionDeleteDtoList().stream()
                    .map(projectPositionDeleteDto -> projectPositionDeleteDto.getProjectPositionNo()).collect(Collectors.toList());
            projectPositionRepository.deleteByNoIn(projectPositionDeleteNos);
        }

        // 프로젝트 포지션 추가
        if (projectUpdateRequestDto.getProjectPositionAddDtoList() != null) {
            List<Long> positionNos = projectUpdateRequestDto.getProjectPositionAddDtoList().stream()
                    .map(projectPositionAddDto -> projectPositionAddDto.getPositionNo()).distinct().collect(Collectors.toList());

            Map<Long, Position> positionMap = positionRepository.findByNoIn(positionNos).stream()
                    .collect(Collectors.toMap(Position::getNo, position -> position));

            for (ProjectPositionAddDto projectPositionAddDto : projectUpdateRequestDto.getProjectPositionAddDtoList()) {
                // count만큼 save
                for (int i = 0 ; i < projectPositionAddDto.getCount() ; i++) {
                    ProjectPosition projectPosition = ProjectPosition.builder()
                            .project(project)
                            .position(positionMap.get(projectPositionAddDto.getPositionNo()))
                            .user(null)
                            .creator(false)
                            .state(false)
                            .build();
                    projectPositionRepository.saveAndFlush(projectPosition);
                    entityManager.clear();
                }
            }
        }

        // 기존 기술스택 삭제
        projectTechnicalStackRepository.deleteByProjectNo(project.getNo());

        // 기술스택 추가
        if (projectUpdateRequestDto.getProjectTechnicalStackNoList() != null) {
            List<TechnicalStack> technicalStackList = technicalStackRepository.findByNoIn(projectUpdateRequestDto.getProjectTechnicalStackNoList()).stream()
                    .collect(Collectors.toList());
            for (TechnicalStack technicalStack : technicalStackList) {
                ProjectTechnicalStack projectTechnicalStack = ProjectTechnicalStack.builder()
                        .project(project)
                        .technicalStack(technicalStack)
                        .build();
                projectTechnicalStack.setProject(project);
                projectTechnicalStackRepository.saveAndFlush(projectTechnicalStack);
                entityManager.clear();
            }
        }

        return project.getNo();
    }

    @Override
    public boolean projectDelete(Long projectNo) throws Exception {
        // 자신이 만든 프로젝트인지 판단
        if (!isRegisterProjectUser(projectNo)) {
            throw new CustomException(ErrorCode.PROJECT_NOT_REGISTER_USER);
        }
        // 프로젝트 관련 프로젝트 참여 신청 기술 삭제
        participateRequestTechnicalStackRepository.deleteByProjectNo(projectNo);
        
        // 프로젝트 관련 프로젝트 신청 삭제
        projectParticipateRequestRepository.deleteByProjectNo(projectNo);

        // 프로젝트 관련 프로젝트 포지션 삭제
        projectPositionRepository.deleteByProjectNo(projectNo);

        // 프로젝트 관련 프로젝트 기술 삭제
        projectTechnicalStackRepository.deleteByProjectNo(projectNo);
        
        // 프로젝트 관련 북마크 삭제
        bookMarkRepository.deleteByProjectNo(projectNo);

        // 프로젝트 관련 댓글 삭제
        commentRepository.deleteByProjectNo(projectNo);

        // 프로젝트 삭제
        projectRepository.deleteById(projectNo);

        entityManager.flush();
        entityManager.clear();
        return true;
    }

    // 유저가 만든 프로젝트인지 판단하는 메소드
    private boolean isRegisterProjectUser(Long projectNo) throws Exception {
        return projectRepository.existUserProjectByUser(getUser().getNo(), projectNo);
    }
}
