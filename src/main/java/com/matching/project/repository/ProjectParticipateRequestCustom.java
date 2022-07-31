package com.matching.project.repository;

import com.matching.project.dto.projectparticipate.ProjectParticipateFormResponseDto;
import com.matching.project.entity.ProjectParticipateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjectParticipateRequestCustom {
    public Page<ProjectParticipateFormResponseDto> findProjectParticipateRequestByProjectNo(Long projectNo, Pageable pageable) throws Exception;
}
