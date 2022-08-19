package com.matching.project.service;

import com.matching.project.dto.enumerate.Type;
import com.matching.project.entity.ProjectPosition;
import com.matching.project.entity.User;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import com.matching.project.repository.ProjectPositionRepository;
import com.matching.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectPositionServiceImpl implements ProjectPositionService{
    private final ProjectPositionRepository projectPositionRepository;
    private final EntityManager entityManager;
    private final ProjectRepository projectRepository;
    private final NotificationService notificationService;

    @Override
    public Long projectPositionWithdraw(Long projectPositionNo) throws Exception {
        ProjectPosition projectPosition = projectPositionRepository.findUserAndProjectFetchJoinByProjectPositionNo(projectPositionNo).orElseThrow(() -> new CustomException(ErrorCode.PROJECT_POSITION_NO_SUCH_ELEMENT_EXCEPTION));

        // 프로젝트 포지션에 유저가 없는 경우
        if (projectPosition.getUser() == null) {
            throw new CustomException(ErrorCode.PROJECT_POSITION_NOT_EXISTENCE_USER);
        }
        // 현재 로그인한 유저 정보
        User user = getUser();
        
        // 프로젝트 포지션에 있는 유저와 현재 로그인한 유저 정보와 다를때
        if (projectPosition.getUser().getNo() != user.getNo()) {
            throw new CustomException(ErrorCode.PROJECT_POSITION_NOT_EQUAL_USER);
        }
        
        // 유저 정보 삭제
        projectPosition.changeUser(null);
        entityManager.flush();
        entityManager.clear();

        // 알림
        List<ProjectPosition> projectPositionList = projectPositionRepository.findProjectAndPositionAndUserUsingFetchJoinByProject(projectPosition.getProject());

        // 알림 내용 생성
        String remainingParticipants  = "현재 남은 참여자 : ";

        for (ProjectPosition position : projectPositionList) {
            User positionUser = position.getUser();
            if (positionUser != null) {
                remainingParticipants =  remainingParticipants + positionUser.getName() + ", ";
            }
        }
        remainingParticipants = remainingParticipants.substring(0, remainingParticipants.length() - 2);

        // 알림 전송
        for (ProjectPosition position : projectPositionList) {
            if (position.getUser() != null) {
                notificationService.sendNotification(Type.PROJECT_POSITION_WITHDRAW, position.getUser().getEmail(),
                        "[프로젝트 탈퇴] " + position.getProject().getName(),
                        user.getName() + "이 " + position.getProject().getName() + " 프로젝트에서 탈퇴했습니다.\n"
                                + remainingParticipants
                );
            }
        }

        return projectPosition.getNo();
    }

    @Override
    public boolean projectPositionExpulsion(Long projectPositionNo, String reason) throws Exception {
        ProjectPosition projectPosition = projectPositionRepository.findUserAndProjectFetchJoinByProjectPositionNo(projectPositionNo).orElseThrow(() -> new CustomException(ErrorCode.PROJECT_POSITION_NO_SUCH_ELEMENT_EXCEPTION));

        // 자신이 만든 프로젝트인지 판단
        if (!isRegisterProjectUser(projectPosition.getProject().getNo())) {
            throw new CustomException(ErrorCode.PROJECT_NOT_REGISTER_USER);
        }

        // 프로젝트 포지션에 유저가 없는 경우
        if (projectPosition.getUser() == null) {
            throw new CustomException(ErrorCode.PROJECT_POSITION_NOT_EXISTENCE_USER);
        }

        // 유저 이메일
        String expulsionUserEmail = projectPosition.getUser().getEmail();

        // 유저 정보 삭제
        projectPosition.changeUser(null);
        entityManager.flush();
        entityManager.clear();
        
        notificationService.sendNotification(Type.PROJECT_POSITION_EXPULSION, expulsionUserEmail, "[프로젝트 추방] " + projectPosition.getProject().getName(), projectPosition.getProject().getName() +" 프로젝트에서 추방당하셨습니다.");
        return true;
    }

    // 현재 로그인한 유저 정보 가져오기
    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        return (User) principal;
    }

    // 유저가 만든 프로젝트인지 판단하는 메소드
    private boolean isRegisterProjectUser(Long projectNo) throws Exception {
        return projectRepository.existUserProjectByUser(getUser().getNo(), projectNo);
    }
}
