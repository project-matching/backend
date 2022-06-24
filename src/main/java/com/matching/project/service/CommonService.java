package com.matching.project.service;

import com.matching.project.dto.common.NormalLoginRequestDto;

public interface CommonService {
    boolean UserNormalLogin(NormalLoginRequestDto normalLoginRequestDto);
}
