package com.matching.project.service;

import com.matching.project.dto.project.*;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ProjectService {
    public ProjectRegisterResponseDto projectRegister(ProjectRegisterRequestDto projectRegisterRequestDto) throws Exception;
    public List<ProjectSimpleDto> findProjectList(boolean state, boolean delete, Pageable pageable) throws Exception;
    public ProjectDto getProjectDetail(Long projectNo);
}
