package com.matching.project.service;

import com.matching.project.dto.common.NormalLoginRequestDto;
import com.matching.project.entity.User;

import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommonServiceImpl implements CommonService {
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        User user = null;
        if (principal instanceof User)
            user = (User)principal;
        else
            throw new CustomException(ErrorCode.GET_USER_AUTHENTICATION_EXCEPTION);
        return user;
    }

    @Override
    public User normalLogin(NormalLoginRequestDto normalLoginRequestDto) {
        User user = (User) customUserDetailsService.loadUserByUsername(normalLoginRequestDto.getEmail());
        if (!passwordEncoder.matches(normalLoginRequestDto.getPassword(), user.getPassword()))
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD_EXCEPTION);
        else if (user.isWithdrawal())
            throw new CustomException(ErrorCode.WITHDRAWAL_EXCEPTION);
        else if (user.isBlock())
            throw new CustomException(ErrorCode.BLOCKED_EXCEPTION); // 에러에서 사유도 출력되도록 할 필요가 있음
        else if (!user.isEmail_auth())
            throw new CustomException(ErrorCode.UNSIGNED_EMAIL_EXCEPTION);
        return user;
    }

    @Override
    public void userLogout(String accessToken) {
        User user = getAuthenticatedUser();

        // Refresh Token 제거
        jwtTokenService.deleteRefreshToken(user.getEmail());

        // Access Token Black List 추가
        jwtTokenService.setBlackList(accessToken);

        // SecurityContextHolder 에서 Context 제거
        SecurityContextHolder.clearContext();
    }
}
