package com.matching.project.service;

import com.matching.project.dto.SliceDto;
import com.matching.project.dto.project.ProjectParticipateRequestDto;
import com.matching.project.dto.projectparticipate.ProjectParticipateFormResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjectParticipateRequestService {
    public boolean projectParticipateRequestRegister(ProjectParticipateRequestDto projectParticipateRequestDto) throws Exception;
    public SliceDto<ProjectParticipateFormResponseDto> findProjectParticipateManagementForm(Long projectNo, Long lastProjectNo, Pageable pageable) throws Exception;
    public boolean permitProjectParticipate(Long projectParticipateNo) throws Exception;
    public boolean refusalProjectParticipate(Long projectParticipateNo, String reason) throws Exception;
}
