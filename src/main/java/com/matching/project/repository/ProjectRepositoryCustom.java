package com.matching.project.repository;

import com.matching.project.dto.project.*;
import com.matching.project.entity.Project;
import com.matching.project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ProjectRepositoryCustom {
    public Project findProjectWithUserUsingFetchJoinByProjectNo(Long projectNo);
    public Slice<ProjectSimpleDto> findProjectByStatus(Pageable pageable, Long no, boolean status);
    public Page<ProjectSimpleDto> findUserProject(Pageable pageable, User user);
    public Page<ProjectSimpleDto> findParticipateProject(Pageable pageable, User user);
    public Page<ProjectSimpleDto> findParticipateRequestProject(Pageable pageable, User user);
    public Page<ProjectSimpleDto> findBookMarkProject(Pageable pageable, User user);
    public boolean existUserProjectByUser(Long userNo, Long projectNo);
}
