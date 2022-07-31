package com.matching.project.repository;

import com.matching.project.dto.project.ProjectSearchDto;
import com.matching.project.dto.project.ProjectSearchRequestDto;
import com.matching.project.dto.project.ProjectSimpleDto;
import com.matching.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectRepositoryCustom {
    public Page<ProjectSimpleDto> findProjectByStatus(Pageable pageable, boolean status, boolean delete, ProjectSearchRequestDto projectSearchRequestDto);
    public Project findProjectWithUserUsingFetchJoinByProjectNo(Long projectNo);
}
