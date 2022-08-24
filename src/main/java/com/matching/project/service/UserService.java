package com.matching.project.service;

import com.matching.project.dto.SliceDto;
import com.matching.project.dto.user.*;
import com.matching.project.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    User userSignUp(SignUpRequestDto signUpRequestDto);
    UserInfoResponseDto getUserInfo();
    UserProfileInfoResponseDto userProfileInfo();
    SliceDto<UserSimpleInfoDto> userInfoList(Long UserNo, UserFilterDto userFilterDto, Pageable pageable);
    User userUpdate(UserUpdateRequestDto userUpdateRequestDto, MultipartFile file);
    User userPasswordUpdate(PasswordUpdateRequestDto passwordUpdateRequestDto);
    User userSignOut() throws Exception;
    User userBlock(Long userNo, String reason);
    User userUnBlock(Long userNo);
}
