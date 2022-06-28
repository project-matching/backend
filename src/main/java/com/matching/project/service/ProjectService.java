package com.matching.project.service;

import com.matching.project.dto.project.ProjectRegisterRequestDto;
import com.matching.project.dto.project.ProjectRegisterResponseDto;
import com.matching.project.dto.project.ProjectSimpleDto;

import java.util.List;

public interface ProjectService {
    public ProjectRegisterResponseDto projectRegister(ProjectRegisterRequestDto projectRegisterRequestDto) throws Exception;

    public List<ProjectSimpleDto> projectRecruitingList() throws Exception;
}
