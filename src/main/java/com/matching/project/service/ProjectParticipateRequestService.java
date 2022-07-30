package com.matching.project.service;

import com.matching.project.dto.project.ProjectParticipateRequestDto;

public interface ProjectParticipateRequestService {
    public boolean projectParticipateRequestRegister(ProjectParticipateRequestDto projectParticipateRequestDto) throws Exception;
}
