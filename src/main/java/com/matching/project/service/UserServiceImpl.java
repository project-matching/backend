package com.matching.project.service;

import com.matching.project.dto.common.NormalLoginRequestDto;
import com.matching.project.dto.user.SignUpRequestDto;
import com.matching.project.dto.user.UserInfoResponseDto;
import com.matching.project.dto.user.UserSimpleInfoDto;
import com.matching.project.dto.user.UserUpdateRequestDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final UserPositionRepository userPositionRepository;
    private final UserTechnicalStackRepository userTechnicalStackRepository;
    private final PasswordEncoder passwordEncoder;

    public void signUpValidCheck(SignUpRequestDto dto) {
        // 참고 : https://lovefor-you.tistory.com/113
        if ("".equals(dto.getName()) || dto.getName() == null)
            throw new RuntimeException("Name value is blanked");
        else if ("".equals(dto.getSex()) || dto.getSex() == null || dto.getSex().length() > 1 || !dto.getSex().matches("[mMwWoO]"))
            throw new RuntimeException("Sex value is blanked OR Invalid");
        else if ("".equals(dto.getEmail()) || dto.getEmail() == null)
            throw new RuntimeException("Email value is blanked");
        else if ("".equals(dto.getPassword()) || dto.getPassword() == null)
            throw new RuntimeException("Password value is blanked");
        else if (userRepository.findByEmail(dto.getEmail()).isPresent())
            throw new RuntimeException("Email is duplicated.");
    }

    public void updateValidCheck(UserUpdateRequestDto dto, String originPassword) {
        if ("".equals(dto.getName()) || dto.getName() == null)
            throw new RuntimeException("Name value is blanked");
        else if ("".equals(dto.getSex()) || dto.getSex() == null || dto.getSex().length() > 1 || !dto.getSex().matches("[mMwWoO]"))
            throw new RuntimeException("Sex value is blanked OR Invalid");
        else if ("".equals(dto.getOriginPassword()) || dto.getOriginPassword() == null)
            throw new RuntimeException("Original Password value is blanked");
        else if (!passwordEncoder.matches(dto.getOriginPassword(), originPassword))
            throw new RuntimeException("Original Password is Wrong");
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

    @Override
    public  List<String> userInfoTechStackList(UserPosition userPosition) {
        List<UserTechnicalStack> userTechnicalStackList = null;
        if (userPosition != null)
           userTechnicalStackList = userTechnicalStackRepository.findAllByUserPosition(userPosition);
        if (userTechnicalStackList != null)
            return userTechnicalStackList.stream().map(UserTechnicalStack::getName).collect(Collectors.toList());
        else
            return null;
    }

    @Override
    public UserInfoResponseDto userInfo(Long no) {
        Optional<User> user = userRepository.findById(no);
        user.orElseThrow(() -> new RuntimeException("Not Find User No"));

        UserPosition userPosition = user.get().getUserPosition();
        String position = (userPosition != null) ? userPosition.getName() : null;

        List<String> technicalStackList = userInfoTechStackList(user.get().getUserPosition());

        return UserInfoResponseDto.builder()
                .name(user.get().getName())
                .sex(user.get().getSex())
                .email(user.get().getEmail())
                .position(position)
                .technicalStackList(technicalStackList)
                .github(user.get().getGithub())
                .selfIntroduction(user.get().getSelfIntroduction())
                .build();
    }

    @Override
    public List<UserSimpleInfoDto> userInfoList(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.get().map(UserSimpleInfoDto::toUserSimpleInfoDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public User userUpdate(Long no, UserUpdateRequestDto dto) {
        // Identification Check
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User identificationUser = (User)auth.getPrincipal();
        if (!Objects.equals(identificationUser.getNo(), no))
            throw new RuntimeException("Identification Check Fail");

        Optional<User> optionalUser = userRepository.findById(no);
        optionalUser.orElseThrow(() -> new RuntimeException("Not Find User No"));

        // Valid Check
        updateValidCheck(dto, optionalUser.get().getPassword());

        // New Password Encode And Save
        optionalUser.get().updatePassword(passwordEncoder, dto.getNewPassword());

        // Position Update
        UserPosition userPosition = null;
        Optional<UserPosition> optionalUserPosition = Optional.empty();
        if (optionalUser.get().getUserPosition() != null)
            optionalUserPosition = userPositionRepository.findById(optionalUser.get().getUserPosition().getNo());
        // 기존 데이터 조회 시 있는지 여부에 따라 분기
        if (optionalUserPosition.isPresent()) { // 존재
            /*
                유저 포지션이 삭제/변경/유지 되는 케이스 모두 기술 스택을 지우도록 작성하였다.
                유저 포지션을 유지 하는 경우에도 기술 스택의 변경이 많은 경우에는 로직의 시간 복잡도가 n^2 이상 되어서
                그냥 지우고 새로 하는것이 코스트가 적다고 판단.
             */
            if (userTechnicalStackRepository.findAllByUserPosition(optionalUserPosition.get()) != null)
                userTechnicalStackRepository.deleteByUserPosition(optionalUserPosition.get());
            if (dto.getPosition() == null) // position은 enum 타입이라서 "" 가 들어올 수 없음.
                userPositionRepository.deleteByNo(optionalUserPosition.get().getNo());
            else if (!dto.getPosition().toString().equals(optionalUserPosition.get().getName()))
                userPosition = optionalUserPosition.get().updatePosition(dto.getPosition());
            else
                userPosition = optionalUserPosition.get();
        }
        else {
            if (dto.getPosition() != null) // 존재 x
                userPosition = userPositionRepository.save(UserUpdateRequestDto.toPositionEntity(dto.getPosition()));
        }

        // TechnicalStack Update
        if (userPosition != null) {
            if (dto.getTechnicalStackList() != null) {
                for (UserTechnicalStack stack : dto.toTechStackListEntity(dto.getTechnicalStackList(), userPosition))
                    userTechnicalStackRepository.save(stack);
            }}
        return optionalUser.get().updateUser(dto, userPosition);
    }
}
