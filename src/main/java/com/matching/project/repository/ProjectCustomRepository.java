package com.matching.project.repository;

import com.matching.project.dto.project.ProjectQueryDto;

public interface ProjectCustomRepository {
    public ProjectQueryDto findDetailProject(Long projectNo);
}
