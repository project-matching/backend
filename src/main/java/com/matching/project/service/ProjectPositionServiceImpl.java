package com.matching.project.service;

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

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectPositionServiceImpl implements ProjectPositionService{
    private final ProjectPositionRepository projectPositionRepository;
    private final EntityManager entityManager;
    private final ProjectRepository projectRepository;

    @Override
    public Long projectPositionWithdraw(Long projectPositionNo) throws Exception {
        ProjectPosition projectPosition = projectPositionRepository.findUserFetchJoinByProjectPositionNo(projectPositionNo).orElseThrow(() -> new CustomException(ErrorCode.PROJECT_POSITION_NO_SUCH_ELEMENT_EXCEPTION));
        
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

        return projectPosition.getNo();
    }

    @Override
    public boolean projectPositionExpulsion(Long projectPositionNo, String reason) throws Exception {

        ProjectPosition projectPosition = projectPositionRepository.findUserFetchJoinByProjectPositionNo(projectPositionNo).orElseThrow(() -> new CustomException(ErrorCode.PROJECT_POSITION_NO_SUCH_ELEMENT_EXCEPTION));

        // 자신이 만든 프로젝트인지 판단
        if (!isRegisterProjectUser(projectPosition.getProject().getNo())) {
            throw new CustomException(ErrorCode.PROJECT_NOT_REGISTER_USER);
        }

        // 프로젝트 포지션에 유저가 없는 경우
        if (projectPosition.getUser() == null) {
            throw new CustomException(ErrorCode.PROJECT_POSITION_NOT_EXISTENCE_USER);
        }

        // 유저 정보 삭제
        projectPosition.changeUser(null);
        entityManager.flush();
        entityManager.clear();

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
