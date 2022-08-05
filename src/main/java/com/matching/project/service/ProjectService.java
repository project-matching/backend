package com.matching.project.service;

import com.matching.project.dto.project.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ProjectService {
    public ProjectRegisterFormResponseDto findProjectRegisterForm() throws Exception;
    public Long projectRegister(ProjectRegisterRequestDto projectRegisterRequestDto) throws Exception;
    public List<ProjectSimpleDto> findProjectList(boolean state, boolean delete, ProjectSearchRequestDto projectSearchRequestDto, Pageable pageable) throws Exception;
    public List<ProjectSimpleDto> findUserProjectList(boolean delete, Pageable pageable) throws Exception;
    public List<ProjectSimpleDto> findParticipateProjectList(boolean delete, Pageable pageable) throws Exception;
    public List<ProjectSimpleDto> findParticipateRequestProjectList(boolean delete, Pageable pageable) throws Exception;
    public ProjectDto getProjectDetail(Long projectNo) throws Exception;
    public ProjectUpdateFormResponseDto getProjectUpdateForm(Long projectNo) throws Exception;
    public Long projectUpdate(Long projectNo, ProjectUpdateRequestDto projectUpdateRequestDto) throws Exception;
    public boolean projectDelete(Long projectNo) throws Exception;
}
