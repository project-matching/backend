package com.matching.project.service;

import com.matching.project.dto.SliceDto;
import com.matching.project.dto.project.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface ProjectService {
    public ProjectRegisterFormResponseDto findProjectRegisterForm() throws Exception;
    public Long projectRegister(ProjectRegisterRequestDto projectRegisterRequestDto) throws Exception;
    public SliceDto<ProjectSimpleDto> findProjectList(Long projectNo, boolean state, ProjectSearchRequestDto projectSearchRequestDto, Pageable pageable) throws Exception;
    public SliceDto<ProjectSimpleDto> findUserProjectList(Long projectNo, Pageable pageable) throws Exception;
    public SliceDto<ProjectSimpleDto> findParticipateProjectList(Long projectNo, Pageable pageable) throws Exception;
    public SliceDto<ProjectSimpleDto> findParticipateRequestProjectList(Long projectNo, Pageable pageable) throws Exception;
    public ProjectDto getProjectDetail(Long projectNo) throws Exception;
    public ProjectUpdateFormResponseDto getProjectUpdateForm(Long projectNo) throws Exception;
    public Long projectUpdate(Long projectNo, ProjectUpdateRequestDto projectUpdateRequestDto) throws Exception;
    public boolean projectDelete(Long projectNo) throws Exception;
}
