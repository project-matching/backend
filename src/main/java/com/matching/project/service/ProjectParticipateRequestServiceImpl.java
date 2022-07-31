package com.matching.project.service;

import com.matching.project.dto.ResponseDto;
import com.matching.project.dto.project.ProjectParticipateRequestDto;
import com.matching.project.dto.projectparticipate.ProjectParticipateFormResponseDto;
import com.matching.project.entity.*;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import com.matching.project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.EntityManager;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectParticipateRequestServiceImpl implements ProjectParticipateRequestService {
    private final ProjectParticipateRequestRepository projectParticipateRequestRepository;
    private final ParticipateRequestTechnicalStackRepository participateRequestTechnicalStackRepository;
    private final ProjectPositionRepository projectPositionRepository;
    private final TechnicalStackRepository technicalStackRepository;
    private final ProjectRepository projectRepository;

    // 프로젝트 참가 신청 등록
    @Override
    public boolean projectParticipateRequestRegister(ProjectParticipateRequestDto projectParticipateRequestDto) throws Exception {
        // 현재 유저 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        ProjectPosition projectPosition = projectPositionRepository.findById(projectParticipateRequestDto.getProjectPositionNo()).orElseThrow(() -> new CustomException(ErrorCode.PROJECT_POSITION_NO_SUCH_ELEMENT_EXCEPTION));
        
        // 현재 프로젝트 포지션에 유저가 존재하는 경우 false 리턴
        if (projectPosition.getUser() != null) {
            throw new CustomException(ErrorCode.PROJECT_POSITION_EXISTENCE_USER);
        }

        // 프로젝트 참여 신청 저장
        ProjectParticipateRequest projectParticipateRequest = ProjectParticipateRequest.builder()
                .user(user)
                .projectPosition(projectPosition)
                .motive(projectParticipateRequestDto.getMotive())
                .github(projectParticipateRequestDto.getGitHub())
                .build();

        projectParticipateRequest = projectParticipateRequestRepository.save(projectParticipateRequest);
        
        // 신청 기술 스택 저장
        List<TechnicalStack> technicalStackList = technicalStackRepository.findByNameIn(projectParticipateRequestDto.getTechnicalStackList());

        for (String technicalStackName : projectParticipateRequestDto.getTechnicalStackList()) {
            ParticipateRequestTechnicalStack participateRequestTechnicalStack = ParticipateRequestTechnicalStack.builder()
                    .projectParticipateRequest(projectParticipateRequest)
                    .technicalStack(technicalStackList.stream().filter(technicalStack -> technicalStack.getName().equals(technicalStackName))
                            .findAny()
                            .orElseThrow(() -> new CustomException(ErrorCode.TECHNICAL_STACK_NOT_FOUND)))
                    .build();
            participateRequestTechnicalStackRepository.save(participateRequestTechnicalStack);
        }

        return true;
    }
    
    // 프로젝트 참가 신청 폼 조회
    @Override
    public Page<ProjectParticipateFormResponseDto> findProjectParticipateManagementForm(Long projectNo, Pageable pageable) throws Exception {
        // 현재 유저 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        // 유저가 만든 프로젝트인지 판단
        Project project = projectRepository.findProjectWithUserUsingFetchJoinByProjectNo(projectNo);
        isCreatedProject(user, project);

        return projectParticipateRequestRepository.findProjectParticipateRequestByProjectNo(projectNo, pageable);
    }
    
    // 프로젝트 참가 신청 수락
    @Override
    public boolean permitProjectParticipate(Long projectParticipateNo) throws Exception {
        // 현재 유저 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        // 프로젝트 신청 조회
        ProjectParticipateRequest projectParticipateRequest = projectParticipateRequestRepository.findProjectPositionAndUserAndProjectFetchJoinByNo(projectParticipateNo);

        // 유저가 만든 프로젝트인지 판단
        isCreatedProject(user, projectParticipateRequest.getProjectPosition().getProject());
        
        // 프로젝트 신청 기술스택 삭제
        participateRequestTechnicalStackRepository.deleteByProjectParticipateNo(projectParticipateRequest.getNo());
        
        // 프로젝트 신청 삭제
        projectParticipateRequestRepository.deleteByNo(projectParticipateRequest.getNo());

        // 포지션 유저 업데이트
        ProjectPosition projectPosition = projectPositionRepository.findById(projectParticipateRequest.getProjectPosition().getNo())
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_POSITION_NO_SUCH_ELEMENT_EXCEPTION));
        projectPosition.changeUser(projectParticipateRequest.getUser());
        
        //todo 알림 추가 필요
        return true;
    }

    // 프로젝트 참가 신청 거절
    @Override
    public boolean refusalProjectParticipate(Long projectParticipateNo, String reason) throws Exception {
        // 현재 유저 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        // 프로젝트 신청 조회
        ProjectParticipateRequest projectParticipateRequest = projectParticipateRequestRepository.findProjectPositionAndUserAndProjectFetchJoinByNo(projectParticipateNo);

        // 유저가 만든 프로젝트인지 판단
        isCreatedProject(user, projectParticipateRequest.getProjectPosition().getProject());

        // 프로젝트 신청 기술스택 삭제
        participateRequestTechnicalStackRepository.deleteByProjectParticipateNo(projectParticipateRequest.getNo());

        // 프로젝트 신청 삭제
        projectParticipateRequestRepository.deleteByNo(projectParticipateRequest.getNo());

        //todo 알림 추가 필요
        return true;
    }

    // 유저가 만든 프로젝트인지 판단
    private void isCreatedProject(User user, Project project) {
        if (project.getUser() == null || project.getUser().getNo() != user.getNo()) {
            throw new CustomException(ErrorCode.PROJECT_NOT_REGISTER_USER);
        }
    }

}
