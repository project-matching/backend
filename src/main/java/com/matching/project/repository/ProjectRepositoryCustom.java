package com.matching.project.repository;

import com.matching.project.dto.project.*;
import com.matching.project.entity.Project;
import com.matching.project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectRepositoryCustom {
    public Project findProjectWithUserUsingFetchJoinByProjectNo(Long projectNo);
    public Page<ProjectSimpleDto> findProjectByStatusAndDelete(Pageable pageable, boolean status, boolean delete, ProjectSearchRequestDto projectSearchRequestDto);
    public Page<ProjectSimpleDto> findUserProjectByDelete(Pageable pageable, User user, boolean delete);
    public Page<ProjectSimpleDto> findParticipateProjectByDelete(Pageable pageable, User user, boolean delete);
    public Page<ProjectSimpleDto> findParticipateRequestProjectByDelete(Pageable pageable, User user, boolean delete);
    public Page<ProjectSimpleDto> findBookMarkProjectByDelete(Pageable pageable, User user, boolean delete);
}
