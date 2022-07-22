package com.matching.project.service;

import com.matching.project.dto.common.NormalLoginRequestDto;
import com.matching.project.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommonServiceImpl implements CommonService {
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User normalLogin(NormalLoginRequestDto normalLoginRequestDto) {
        User user = (User) customUserDetailsService.loadUserByUsername(normalLoginRequestDto.getEmail());
        if (!passwordEncoder.matches(normalLoginRequestDto.getPassword(), user.getPassword()))
            throw new RuntimeException("This is an incorrect password");
        else if (user.isBlock())
            throw new RuntimeException("This is blocked User ID");
        else if (!user.isEmail_auth())
            throw new RuntimeException("This is an unsigned email");
        return user;
    }
}
