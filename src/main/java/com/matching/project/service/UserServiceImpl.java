package com.matching.project.service;

import com.matching.project.dto.common.NormalLoginRequestDto;
import com.matching.project.dto.user.SignUpRequestDto;
import com.matching.project.entity.User;
import com.matching.project.entity.UserPosition;
import com.matching.project.entity.UserTechnicalStack;
import com.matching.project.repository.UserPositionRepository;
import com.matching.project.repository.UserRepository;
import com.matching.project.repository.UserTechnicalStackRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserPositionRepository userPositionRepository;
    private final UserTechnicalStackRepository userTechnicalStackRepository;

    public boolean signUpValidCheck(SignUpRequestDto dto) {
        boolean result = false;
        if (dto.getName().equals(""))
            log.warn("Name value is blanked");
        else if (dto.getSex() == ' ')
            log.warn("Sex value is blanked");
        else if (dto.getEmail().equals(""))
            log.warn("Email value is blanked");
        else if (dto.getPassword().equals(""))
            log.warn("Password value is blanked");
        else
            result = true;
        return result;
    }

    public User userSignUp(SignUpRequestDto dto){
        // Valid Check
        if (!signUpValidCheck(dto))
            throw new RuntimeException("signUpValidCheck fail");

        // Save UserPosition
        UserPosition userPosition = dto.toPositionEntity(dto.getPosition());
        userPositionRepository.save(userPosition);

        // Save UserTechnicalStack
        for (UserTechnicalStack stack : dto.toTechStackListEntity(dto.getTechnicalStackList(), userPosition))
            userTechnicalStackRepository.save(stack);

        // Save User
        User user = dto.toUserEntity(dto, userPosition);
        return userRepository.save(user);
    }
}
