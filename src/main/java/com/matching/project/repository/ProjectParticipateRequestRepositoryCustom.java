package com.matching.project.repository;

import com.matching.project.dto.projectparticipate.ProjectParticipateFormResponseDto;
import com.matching.project.entity.ProjectParticipateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface ProjectParticipateRequestRepositoryCustom {
    public Optional<Slice<ProjectParticipateFormResponseDto>> findProjectParticipateRequestByProjectNo(Long projectNo, Long projectParticipateRequestNo, Pageable pageable) throws Exception;
    public Optional<ProjectParticipateRequest> findProjectPositionAndUserAndProjectFetchJoinByNo(Long no) throws Exception;
    public Optional<List<ProjectParticipateRequest>> findProjectPositionAndUserAndProjectFetchJoinByProjectNo(Long projectNo) throws Exception;
    public long deleteByNo(Long no) throws Exception;
    public void deleteByProjectNo(Long projectNo) throws Exception;
}
