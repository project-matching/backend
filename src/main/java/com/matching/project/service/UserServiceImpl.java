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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final UserPositionRepository userPositionRepository;
    private final UserTechnicalStackRepository userTechnicalStackRepository;
    private final PasswordEncoder passwordEncoder;

    public void signUpValidCheck(SignUpRequestDto dto) {

        if (dto.getName().equals("") || dto.getName() == null)
            throw new RuntimeException("Name value is blanked");
        else if (dto.getSex().equals("") || dto.getSex() == null || dto.getSex().length() > 1 || !dto.getSex().matches("[mMwWoO]"))
            throw new RuntimeException("Sex value is blanked OR Invalid");
        else if (dto.getEmail().equals("") || dto.getEmail() == null)
            throw new RuntimeException("Email value is blanked");
        else if (dto.getPassword().equals("") || dto.getPassword() == null)
            throw new RuntimeException("Password value is blanked");
        else if (userRepository.findByEmail(dto.getEmail()).isPresent())
            throw new RuntimeException("Email is duplicated.");
    }

    @Override
    public User userSignUp(SignUpRequestDto dto){

        // Valid Check
        signUpValidCheck(dto);

        // Password Encode
        dto.setEncodePassword(passwordEncoder.encode(dto.getPassword()));

        // Save UserPosition
        UserPosition userPosition = null;
        if (dto.getPosition() != null) {
            userPosition = dto.toPositionEntity(dto.getPosition());
            userPositionRepository.save(userPosition);
        }

        // Save UserTechnicalStack
        if (dto.getTechnicalStackList() != null) {
            for (UserTechnicalStack stack : dto.toTechStackListEntity(dto.getTechnicalStackList(), userPosition))
                userTechnicalStackRepository.save(stack);
        }

        // Save User
        User user = dto.toUserEntity(dto, userPosition);
        return userRepository.save(user);
    }
}
