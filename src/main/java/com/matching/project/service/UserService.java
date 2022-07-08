package com.matching.project.service;

import com.matching.project.dto.common.NormalLoginRequestDto;
import com.matching.project.dto.user.SignUpRequestDto;
import com.matching.project.dto.user.UserInfoResponseDto;
import com.matching.project.dto.user.UserSimpleInfoDto;
import com.matching.project.dto.user.UserUpdateRequestDto;
import com.matching.project.entity.User;
import com.matching.project.entity.UserPosition;
import com.matching.project.entity.UserTechnicalStack;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {
    User userSignUp(SignUpRequestDto signUpRequestDto);
    List<String> userInfoTechStackList(UserPosition userPosition);
    UserInfoResponseDto userInfo(Long no);
    List<UserSimpleInfoDto> userInfoList(Pageable pageable);
    User userUpdate(Long no, UserUpdateRequestDto userUpdateRequestDto);
}
