package com.matching.project.repository;

import com.matching.project.dto.project.*;
import com.matching.project.entity.Project;
import com.matching.project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectRepositoryCustom {
    public Project findProjectWithUserUsingFetchJoinByProjectNo(Long projectNo);
    public Page<ProjectSimpleDto> findProjectByStatus(Pageable pageable, boolean status, ProjectSearchRequestDto projectSearchRequestDto);
    public Page<ProjectSimpleDto> findUserProject(Pageable pageable, User user);
    public Page<ProjectSimpleDto> findParticipateProject(Pageable pageable, User user);
    public Page<ProjectSimpleDto> findParticipateRequestProject(Pageable pageable, User user);
    public Page<ProjectSimpleDto> findBookMarkProject(Pageable pageable, User user);
    public boolean existUserProjectByUser(Long userNo, Long projectNo);
}
