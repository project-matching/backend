package com.matching.project.service;

import com.matching.project.dto.common.NormalLoginRequestDto;
import com.matching.project.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface CommonService{
    User normalLogin(NormalLoginRequestDto normalLoginRequestDto);
    void userLogout(String accessToken);
}
