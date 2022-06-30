package com.matching.project.service;

import com.matching.project.dto.common.NormalLoginRequestDto;
import com.matching.project.entity.User;
import com.matching.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommonServiceImpl implements CommonService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User normalLogin(NormalLoginRequestDto normalLoginRequestDto) {
        Optional<User> user = userRepository.findByEmail(normalLoginRequestDto.getEmail());

        if (user.isEmpty())
            throw new IllegalArgumentException("가입되지 않은 E-MAIL 입니다.");
        if (!passwordEncoder.matches(normalLoginRequestDto.getPassword(), user.get().getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        return user.get();
    }
}
