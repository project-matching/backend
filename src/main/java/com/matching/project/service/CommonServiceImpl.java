package com.matching.project.service;

import com.matching.project.dto.common.NormalLoginRequestDto;
import com.matching.project.entity.User;
import com.matching.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommonServiceImpl implements CommonService {
    private final UserRepository userRepository;

    public boolean UserNormalLogin(NormalLoginRequestDto normalLoginRequestDto)
    {
        Optional<User> userOptional = userRepository.findUserByEmailAndPassword(normalLoginRequestDto.getEmail(), normalLoginRequestDto.getPassword());

        if (!userOptional.isPresent())
            return false;
        else
            return true;
    }
}
