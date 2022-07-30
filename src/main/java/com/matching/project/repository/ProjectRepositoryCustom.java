package com.matching.project.repository;

import com.matching.project.dto.project.*;
import com.matching.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectRepositoryCustom {
    public Page<ProjectSimpleDto> findProjectByStatus(Pageable pageable, boolean status, boolean delete, ProjectSearchRequestDto projectSearchRequestDto);
}
