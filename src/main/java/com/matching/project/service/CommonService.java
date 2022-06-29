package com.matching.project.service;

import com.matching.project.dto.common.NormalLoginRequestDto;
import com.matching.project.entity.User;

public interface CommonService {
    User normalLogin(NormalLoginRequestDto normalLoginRequestDto);
}
