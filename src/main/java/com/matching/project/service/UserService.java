package com.matching.project.service;

import com.matching.project.dto.user.*;
import com.matching.project.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    User userSignUp(SignUpRequestDto signUpRequestDto);
    UserInfoResponseDto userInfo(Long no);
    List<UserSimpleInfoDto> userInfoList(Pageable pageable);
    User userUpdate(Long no, UserUpdateRequestDto userUpdateRequestDto);
    Long userSignOut(Long no, SignOutRequestDto signOutRequestDto);
    User userBlock(Long no, UserBlockRequestDto userBlockRequestDto);
    User userUnBlock(Long no);
}
