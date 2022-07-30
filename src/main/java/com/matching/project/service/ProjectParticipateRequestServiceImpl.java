package com.matching.project.service;

import com.matching.project.dto.project.ProjectParticipateRequestDto;
import com.matching.project.entity.*;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import com.matching.project.repository.ParticipateRequestTechnicalStackRepository;
import com.matching.project.repository.ProjectParticipateRequestRepository;
import com.matching.project.repository.ProjectPositionRepository;
import com.matching.project.repository.TechnicalStackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectParticipateRequestServiceImpl implements ProjectParticipateRequestService {
    private final ProjectParticipateRequestRepository projectParticipateRequestRepository;
    private final ParticipateRequestTechnicalStackRepository participateRequestTechnicalStackRepository;
    private final ProjectPositionRepository projectPositionRepository;
    private final TechnicalStackRepository technicalStackRepository;
    
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
}
