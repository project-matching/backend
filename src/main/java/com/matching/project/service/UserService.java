package com.matching.project.service;

import com.matching.project.dto.common.NormalLoginRequestDto;
import com.matching.project.dto.user.SignUpRequestDto;
import com.matching.project.entity.User;
import org.springframework.stereotype.Service;

public interface UserService {
    User userSignUp(SignUpRequestDto signUpRequestDto);
}
