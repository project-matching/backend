package com.matching.project.service;

import com.matching.project.dto.enumerate.Filter;
import com.matching.project.dto.project.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ProjectService {
    public ProjectRegisterResponseDto projectRegister(ProjectRegisterRequestDto projectRegisterRequestDto) throws Exception;
    public Page<ProjectSimpleDto> findProjectList(boolean state, boolean delete, ProjectSearchRequestDto projectSearchRequestDto, Pageable pageable) throws Exception;
    public ProjectDto getProjectDetail(Long projectNo) throws Exception;
}
