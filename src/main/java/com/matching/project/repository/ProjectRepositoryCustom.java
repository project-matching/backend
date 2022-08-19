package com.matching.project.repository;

import com.matching.project.dto.project.*;
import com.matching.project.entity.Project;
import com.matching.project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface ProjectRepositoryCustom {
    public Optional<Project> findProjectWithUserUsingFetchJoinByProjectNo(Long projectNo);
    public Optional<Slice<ProjectSimpleDto>> findProjectByStatus(Pageable pageable, Long projectNo, boolean status, ProjectSearchRequestDto projectSearchRequestDto);
    public Optional<Slice<ProjectSimpleDto>> findUserProject(Pageable pageable, Long projectNo, User user);
    public Optional<Slice<ProjectSimpleDto>> findParticipateProject(Pageable pageable, Long projectNo, User user);
    public Optional<Slice<ProjectSimpleDto>> findParticipateRequestProject(Pageable pageable, Long projectNo, User user);
    public Optional<Slice<ProjectSimpleDto>> findBookMarkProject(Pageable pageable, Long projectNo, User user);
    public boolean existUserProjectByUser(Long userNo, Long projectNo);
}
