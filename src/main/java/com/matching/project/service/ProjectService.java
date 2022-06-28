package com.matching.project.service;

import com.matching.project.dto.project.ProjectRegisterRequestDto;
import com.matching.project.dto.project.ProjectRegisterResponseDto;

public interface ProjectService {
    public ProjectRegisterResponseDto projectRegister(ProjectRegisterRequestDto projectRegisterRequestDto) throws Exception;
}
