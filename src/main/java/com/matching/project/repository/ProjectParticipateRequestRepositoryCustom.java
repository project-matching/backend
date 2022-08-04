package com.matching.project.repository;

import com.matching.project.dto.projectparticipate.ProjectParticipateFormResponseDto;
import com.matching.project.entity.ProjectParticipateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectParticipateRequestRepositoryCustom {
    public Page<ProjectParticipateFormResponseDto> findProjectParticipateRequestByProjectNo(Long projectNo, Pageable pageable) throws Exception;
    public ProjectParticipateRequest findProjectPositionAndUserAndProjectFetchJoinByNo(Long no) throws Exception;
    public long deleteByNo(Long no) throws Exception;
    public void deleteByProjectNo(Long projectNo) throws Exception;
}
