package com.matching.project.service;

import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.user.*;
import com.matching.project.entity.Position;
import com.matching.project.entity.TechnicalStack;
import com.matching.project.entity.User;
import com.matching.project.entity.UserTechnicalStack;
import com.matching.project.repository.PositionRepository;
import com.matching.project.repository.TechnicalStackRepository;
import com.matching.project.repository.UserRepository;
import com.matching.project.repository.UserTechnicalStackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final PositionRepository positionRepository;
    private final TechnicalStackRepository technicalStackRepository;
    private final UserTechnicalStackRepository userTechnicalStackRepository;
    private final PasswordEncoder passwordEncoder;

    public void signUpValidCheck(SignUpRequestDto dto) {
//        // 참고 : https://lovefor-you.tistory.com/113
//        if ("".equals(dto.getName()) || dto.getName() == null)
//            throw new RuntimeException("Name value is blanked");
//        if (!"".equals(dto.getSex()) && dto.getSex() != null)  {
//            if (dto.getSex().length() > 1 || !dto.getSex().matches("[mMwWoO]"))
//                throw new RuntimeException("Sex value is Invalid");
//        }
//        if ("".equals(dto.getEmail()) || dto.getEmail() == null)
//            throw new RuntimeException("Email value is blanked");
//        if ("".equals(dto.getPassword()) || dto.getPassword() == null)
//            throw new RuntimeException("Password value is blanked");
//        if (userRepository.findByEmail(dto.getEmail()).isPresent())
//            throw new RuntimeException("Email is duplicated.");
    }

    public Position getPositionForSave(String s) {
        Position position = null;
        if (!"".equals(s) && s != null)
        {
            Optional<Position> optionalPosition = positionRepository.findAllByName(s);
            optionalPosition.orElseThrow(() -> new RuntimeException("Unregistered Position"));
            if (optionalPosition.isPresent())
                position = optionalPosition.get();
        }
        return position;
    }

    public List<TechnicalStack> getTechnicalStacksListForSave(List<String> s) {
        List<TechnicalStack> saveTechnicalStacksList = new ArrayList<>();
        if (s != null && !s.isEmpty()) {
            List<TechnicalStack> technicalStacks = technicalStackRepository.findAll();
            for (String stack : s) {
                int i;
                for (i = 0 ; i < technicalStacks.size() ; i++) {
                    if (technicalStacks.get(i).getName().equals(stack)) {
                        // 현재는 리스트라서 Position 테이블에 존재하면 다수 입력 가능
                        // 중복 허용하지 않으려면 set 으로 바꿔주도록 하자.
                        saveTechnicalStacksList.add(technicalStacks.get(i));
                        break ;
                    }
                }
                if ( i == technicalStacks.size() )
                    throw new RuntimeException("Unregistered TechnicalStack");
            }
        }
        return saveTechnicalStacksList;
    }

    @Override
    public User userSignUp(SignUpRequestDto dto){
//
//        // Valid Check
//        signUpValidCheck(dto);
//
//        // Password Encode
//        dto.setEncodePassword(passwordEncoder.encode(dto.getPassword()));
//
//        // Get Entity
//        Position position = getPositionForSave(dto.getPosition());
//        List<TechnicalStack> saveTechnicalStacksList = getTechnicalStacksListForSave(dto.getTechnicalStackList());
//
//        // Position Save
//        if (position != null)
//            positionRepository.save(position);
//
//        // User Save
//        User user = dto.toUserEntity(dto, position);
//        userRepository.save(user);
//
//        // TechnicalStacks Save
//        if (!saveTechnicalStacksList.isEmpty()) {
//            for (TechnicalStack t : saveTechnicalStacksList) {
//                userTechnicalStackRepository.save(UserTechnicalStack.builder()
//                        .technicalStack(t)
//                        .user(user)
//                        .build()
//                );
//            }
//        }
        return null;
    }

    @Override
    public UserInfoResponseDto userInfo(Long no) {
//        Optional<User> user = userRepository.findById(no);
//        user.orElseThrow(() -> new RuntimeException("Not Find User No"));
//
//        String position = null;
//        if (user.get().getPosition() != null)
//            position = user.get().getPosition().getName();
//        List<String> technicalStackList = userTechnicalStackRepository.findUserTechnicalStacksByUser(no)
//                .stream()
//                .map(UserTechnicalStack::getTechnicalStack)
//                .map(TechnicalStack::getName)
//                .collect(Collectors.toList());
//
//        return UserInfoResponseDto.builder()
//                .name(user.get().getName())
//                .sex(user.get().getSex())
//                .email(user.get().getEmail())
//                .position(position)
//                .technicalStackList(technicalStackList)
//                .github(user.get().getGithub())
//                .selfIntroduction(user.get().getSelfIntroduction())
//                .build();
        return null;
    }

    @Override
    public List<UserSimpleInfoDto> userInfoList(Pageable pageable) {
//        Page<User> users = userRepository.findAll(pageable);
//        return users.get().map(UserSimpleInfoDto::toUserSimpleInfoDto).collect(Collectors.toList());
        return null;
    }

    public void updateValidCheck(UserUpdateRequestDto dto, User user) {
//        if ("".equals(dto.getName()) || dto.getName() == null)
//            throw new RuntimeException("Name value is blanked");
//        if (!"".equals(dto.getSex()) && dto.getSex() != null)  {
//            if (dto.getSex().length() > 1 || !dto.getSex().matches("[mMwWoO]"))
//                throw new RuntimeException("Sex value is Invalid");
//        }
//        if (user.getOauthCategory() == OAuth.NORMAL) {
//            if ("".equals(dto.getOriginPassword()) || dto.getOriginPassword() == null)
//                throw new RuntimeException("Original Password value is blanked");
//            else if (!passwordEncoder.matches(dto.getOriginPassword(), user.getPassword()))
//                throw new RuntimeException("Original Password is Wrong");
//        }
    }

    @Transactional
    @Override
    public User userUpdate(Long no, UserUpdateRequestDto dto) {
//        // Identification Check
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        User identificationUser = (User)auth.getPrincipal();
//        if (!Objects.equals(identificationUser.getNo(), no))
//            throw new RuntimeException("Identification Check Fail");
//
//        Optional<User> optionalUser = userRepository.findById(no);
//        optionalUser.orElseThrow(() -> new RuntimeException("Not Find User No"));
//
//        // Valid Check
//        updateValidCheck(dto, optionalUser.get());
//
//        // New Password Encode And Save
//        if (optionalUser.get().getOauthCategory() == OAuth.NORMAL)
//            optionalUser.get().updatePassword(passwordEncoder, dto.getNewPassword());
//
//        // Get Entity
//        Position position = getPositionForSave(dto.getPosition());
//        List<TechnicalStack> saveTechnicalStacksList = getTechnicalStacksListForSave(dto.getTechnicalStackList());
//
//        // User & Position Update
//        optionalUser.get().updateUser(dto, position);
//
//        // UserTechnicalStacks Delete & Save
//        if (userTechnicalStackRepository.findUserTechnicalStacksByUser(no) != null)
//            userTechnicalStackRepository.deleteAllByUser(optionalUser.get());
//        if (!saveTechnicalStacksList.isEmpty()) {
//            for (TechnicalStack t : saveTechnicalStacksList) {
//                userTechnicalStackRepository.save(UserTechnicalStack.builder()
//                        .technicalStack(t)
//                        .user(optionalUser.get())
//                        .build()
//                );
//            }
//        }
//        return optionalUser.get();
        return null;
    }

    @Transactional
    @Override
    public Long userSignOut(Long no, SignOutRequestDto dto) {
        // Identification Check
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User identificationUser = (User)auth.getPrincipal();
        if (!Objects.equals(identificationUser.getNo(), no))
            throw new RuntimeException("Identification Check Fail");

        Optional<User> optionalUser = userRepository.findById(no);
        optionalUser.orElseThrow(() -> new RuntimeException("Not Find User No"));

        // Password Check
        if (optionalUser.get().getOauthCategory() == OAuth.NORMAL) {
            if (!passwordEncoder.matches(dto.getPassword(), optionalUser.get().getPassword()))
                throw new RuntimeException("Password is Wrong");
        }

        // UserTechnicalStacks Delete
        if (userTechnicalStackRepository.findUserTechnicalStacksByUser(no) != null)
            userTechnicalStackRepository.deleteAllByUser(optionalUser.get());

        // User Delete
        userRepository.deleteById(no);

        return no;
    }

    @Transactional
    @Override
    public User userBlock(Long no, UserBlockRequestDto dto) {
        // Permission Check
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User identificationUser = (User)auth.getPrincipal();

        // 시큐리티에서도 셋팅 할 수 있으나 임시로 작성
        //if (identificationUser.getPermission() != Role.ROLE_ADMIN)
        //    throw new RuntimeException("Permission Denied");

        Optional<User> optionalUser = userRepository.findById(no);
        optionalUser.orElseThrow(() -> new RuntimeException("Not Find User No"));

        if (optionalUser.get().isBlock())
            throw new RuntimeException("Users already blocked");

        optionalUser.get().userBlock(dto.getBlockReason());

        return optionalUser.get();
    }

    @Transactional
    @Override
    public User userUnBlock(Long no) {
        // Permission Check
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User identificationUser = (User)auth.getPrincipal();

        // 시큐리티에서도 셋팅 할 수 있으나 임시로 작성
        //if (identificationUser.getPermission() != Role.ROLE_ADMIN)
        //    throw new RuntimeException("Permission Denied");

        Optional<User> optionalUser = userRepository.findById(no);
        optionalUser.orElseThrow(() -> new RuntimeException("Not Find User No"));

        if (!optionalUser.get().isBlock())
            throw new RuntimeException("Users who have already been unblocked");

        optionalUser.get().userUnBlock();

        return optionalUser.get();
    }
}
