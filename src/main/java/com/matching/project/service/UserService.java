package com.matching.project.service;

import com.matching.project.dto.user.*;
import com.matching.project.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    User userSignUp(SignUpRequestDto signUpRequestDto);
    UserInfoResponseDto getUserInfo();
    UserProfileInfoResponseDto userProfileInfo();
    List<UserSimpleInfoDto> userInfoList(Pageable pageable, UserFilterDto userFilterDto);
    User userUpdate(UserUpdateRequestDto userUpdateRequestDto, MultipartFile file);
    User userPasswordUpdate(PasswordUpdateRequestDto passwordUpdateRequestDto);
    User userSignOut();
    User userBlock(Long userNo, String reason);
    User userUnBlock(Long userNo);
}
