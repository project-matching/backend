package com.matching.project.repository;

import com.matching.project.dto.project.*;
import com.matching.project.entity.Project;
import com.matching.project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ProjectRepositoryCustom {
    public Project findProjectWithUserUsingFetchJoinByProjectNo(Long projectNo);
    public Slice<ProjectSimpleDto> findProjectByStatus(Pageable pageable, Long projectNo, boolean status, ProjectSearchRequestDto projectSearchRequestDto);
    public Slice<ProjectSimpleDto> findUserProject(Pageable pageable, Long projectNo, User user);
    public Slice<ProjectSimpleDto> findParticipateProject(Pageable pageable, Long projectNo, User user);
    public Slice<ProjectSimpleDto> findParticipateRequestProject(Pageable pageable, Long projectNo, User user);
    public Slice<ProjectSimpleDto> findBookMarkProject(Pageable pageable, Long projectNo, User user);
    public boolean existUserProjectByUser(Long userNo, Long projectNo);
}
