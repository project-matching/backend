package com.matching.project.service;

import org.springframework.web.bind.annotation.PathVariable;

public interface ProjectPositionService {
    // 프로젝트 탈퇴
    public Long projectPositionWithdraw(Long projectPositionNo) throws Exception;
    // 프로젝트 추방
    public boolean projectPositionExpulsion(@PathVariable Long projectPositionNo, String reason) throws Exception;
}
