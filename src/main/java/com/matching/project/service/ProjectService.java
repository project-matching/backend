package com.matching.project.service;

import com.matching.project.dto.project.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ProjectService {
    public ProjectRegisterFormResponseDto findProjectRegisterForm() throws Exception;
    public Long projectRegister(ProjectRegisterRequestDto projectRegisterRequestDto) throws Exception;
    public Page<ProjectSimpleDto> findProjectList(boolean state, boolean delete, ProjectSearchRequestDto projectSearchRequestDto, Pageable pageable) throws Exception;
    public Page<ProjectSimpleDto> findUserProjectList(boolean delete, Pageable pageable) throws Exception;
    public Page<ProjectSimpleDto> findParticipateProjectList(boolean delete, Pageable pageable) throws Exception;
    public Page<ProjectSimpleDto> findParticipateRequestProjectList(boolean delete, Pageable pageable) throws Exception;
    public ProjectDto getProjectDetail(Long projectNo) throws Exception;
    public ProjectUpdateFormResponseDto getProjectUpdateForm(Long projectNo) throws Exception;
}
