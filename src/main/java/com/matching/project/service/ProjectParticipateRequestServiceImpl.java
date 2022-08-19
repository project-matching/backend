package com.matching.project.service;

import com.matching.project.dto.SliceDto;
import com.matching.project.dto.enumerate.Type;
import com.matching.project.dto.project.ProjectParticipateRequestDto;
import com.matching.project.dto.projectparticipate.ProjectParticipateFormResponseDto;
import com.matching.project.entity.*;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import com.matching.project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import org.springframework.transaction.annotation.Transactional;

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
    private final NotificationService notificationService;

    // 프로젝트 참가 신청 등록
    @Override
    public boolean projectParticipateRequestRegister(ProjectParticipateRequestDto projectParticipateRequestDto) throws Exception {
        // 현재 유저 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        ProjectPosition projectPosition = projectPositionRepository.findById(projectParticipateRequestDto.getProjectPositionNo()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FIND_PROJECT_POSITION_EXCEPTION));

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
        List<TechnicalStack> technicalStackList = technicalStackRepository.findByNameIn(projectParticipateRequestDto.getTechnicalStackList())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FIND_TECHNICAL_STACK_EXCEPTION));;

        for (String technicalStackName : projectParticipateRequestDto.getTechnicalStackList()) {
            ParticipateRequestTechnicalStack participateRequestTechnicalStack = ParticipateRequestTechnicalStack.builder()
                    .projectParticipateRequest(projectParticipateRequest)
                    .technicalStack(technicalStackList.stream().filter(technicalStack -> technicalStack.getName().equals(technicalStackName))
                            .findAny()
                            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FIND_TECHNICAL_STACK_EXCEPTION)))
                    .build();
            participateRequestTechnicalStackRepository.save(participateRequestTechnicalStack);
        }

        // 프로젝트 생성 유저 조회
        Project project = projectRepository.findProjectWithUserUsingFetchJoinByProjectNo(projectPosition.getProject().getNo())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FIND_PROJECT_EXCEPTION));
        String receiver = project.getUser().getEmail();

        // 알림 전송
        notificationService.sendNotification(Type.PROJECT_PARTICIPATION_REQUEST, receiver, "[프로젝트 참가 신청] " + project.getName(), user.getName() + "님이 " + project.getName() + "에 참가 신청했습니다.");

        return true;
    }
    
    // 프로젝트 참가 신청 폼 조회
    @Override
    public SliceDto<ProjectParticipateFormResponseDto> findProjectParticipateManagementForm(Long projectNo, Long projectParticipateRequestNo, Pageable pageable) throws Exception {
        // 현재 유저 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        // 유저가 만든 프로젝트인지 판단
        Project project = projectRepository.findProjectWithUserUsingFetchJoinByProjectNo(projectNo)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FIND_PROJECT_EXCEPTION));

        isCreatedProject(user, project);

        Slice<ProjectParticipateFormResponseDto> projectParticipateFormResponseDtoSlice =
                projectParticipateRequestRepository.findProjectParticipateRequestByProjectNo(projectNo, projectParticipateRequestNo != null ? projectParticipateRequestNo : Long.MAX_VALUE, pageable)
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FIND_PROJECT_PARTICIPATE_REQUEST_EXCEPTION));

        return new SliceDto<>(projectParticipateFormResponseDtoSlice.getContent(), projectParticipateFormResponseDtoSlice.isLast());
    }
    
    // 프로젝트 참가 신청 수락
    @Override
    public boolean permitProjectParticipate(Long projectParticipateNo) throws Exception {
        // 현재 유저 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        // 프로젝트 신청 조회
        ProjectParticipateRequest projectParticipateRequest = projectParticipateRequestRepository.findProjectPositionAndUserAndProjectFetchJoinByNo(projectParticipateNo)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FIND_PROJECT_PARTICIPATE_REQUEST_EXCEPTION));

        // 유저가 만든 프로젝트인지 판단
        isCreatedProject(user, projectParticipateRequest.getProjectPosition().getProject());
        
        // 프로젝트 신청 기술스택 삭제
        participateRequestTechnicalStackRepository.deleteByProjectParticipateNo(projectParticipateRequest.getNo());
        
        // 프로젝트 신청 삭제
        projectParticipateRequestRepository.deleteByNo(projectParticipateRequest.getNo());

        // 포지션 유저 업데이트
        ProjectPosition projectPosition = projectPositionRepository.findById(projectParticipateRequest.getProjectPosition().getNo())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FIND_PROJECT_POSITION_EXCEPTION));
        projectPosition.changeUser(projectParticipateRequest.getUser());

        // 알림
        // 프로젝트 신청한 유저 이메일
        String projectParticipateRequestUserEmail  = projectParticipateRequest.getUser().getEmail();

        // 신청한 프로젝트 이름
        String projectName = projectParticipateRequest.getProjectPosition().getProject().getName();

        notificationService.sendNotification(Type.PROJECT_PARTICIPATION_SUCCESS, projectParticipateRequestUserEmail, "[프로젝트 신청 수락] " + projectName, projectName + " 프로젝트에 참가 완료되었습니다.");
        return true;
    }

    // 프로젝트 참가 신청 거절
    @Override
    public boolean refusalProjectParticipate(Long projectParticipateNo, String reason) throws Exception {
        // 현재 유저 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        // 프로젝트 신청 조회
        ProjectParticipateRequest projectParticipateRequest = projectParticipateRequestRepository.findProjectPositionAndUserAndProjectFetchJoinByNo(projectParticipateNo)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FIND_PROJECT_PARTICIPATE_REQUEST_EXCEPTION));

        // 유저가 만든 프로젝트인지 판단
        isCreatedProject(user, projectParticipateRequest.getProjectPosition().getProject());

        // 프로젝트 신청 기술스택 삭제
        participateRequestTechnicalStackRepository.deleteByProjectParticipateNo(projectParticipateRequest.getNo());

        // 프로젝트 신청 삭제
        projectParticipateRequestRepository.deleteByNo(projectParticipateRequest.getNo());

        // 알림
        // 프로젝트 신청한 유저 이메일
        String projectParticipateRequestUserEmail  = projectParticipateRequest.getUser().getEmail();

        // 신청한 프로젝트 이름
        String projectName = projectParticipateRequest.getProjectPosition().getProject().getName();

        notificationService.sendNotification(Type.PROJECT_PARTICIPATION_REFUSE, projectParticipateRequestUserEmail,
                "[프로젝트 신청 거절] " + projectName,
                projectName + " 프로젝트에 참가 신청이 거절되었습니다.\n"
                 + "거부사유 : " + reason);
        return true;
    }

    // 유저가 만든 프로젝트인지 판단
    private void isCreatedProject(User user, Project project) {
        if (project.getUser() == null || !project.getUser().getNo().equals(user.getNo())) {
            throw new CustomException(ErrorCode.PROJECT_NOT_REGISTER_USER);
        }
    }

}
