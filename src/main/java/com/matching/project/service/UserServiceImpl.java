package com.matching.project.service;

import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.user.*;
import com.matching.project.entity.*;
import com.matching.project.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
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
    private final ImageService imageService;
    private final ImageRepository imageRepository;

    public void signUpValidCheck(SignUpRequestDto dto) {
        // 참고 : https://lovefor-you.tistory.com/113
        if ("".equals(dto.getName()) || dto.getName() == null)
            throw new RuntimeException("Name value is blanked");
        if (!"".equals(dto.getSex()) && dto.getSex() != null)  {
            if (dto.getSex().length() > 1 || !dto.getSex().matches("[mMwWoO]"))
                throw new RuntimeException("Sex value is Invalid");
        }
        if ("".equals(dto.getEmail()) || dto.getEmail() == null)
            throw new RuntimeException("Email value is blanked");
        if ("".equals(dto.getPassword()) || dto.getPassword() == null)
            throw new RuntimeException("Password value is blanked");
        if (userRepository.findByEmail(dto.getEmail()).isPresent())
            throw new RuntimeException("Email is duplicated.");
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
    public User userSignUp(SignUpRequestDto dto, MultipartFile file){

        // Valid Check
        signUpValidCheck(dto);

        // Password Encode
        dto.setEncodePassword(passwordEncoder.encode(dto.getPassword()));

        // Get Entity
        Position position = getPositionForSave(dto.getPosition());
        List<TechnicalStack> saveTechnicalStacksList = getTechnicalStacksListForSave(dto.getTechnicalStackList());

        // Image Upload
        Long imageNo = null;
        try {
            if (!file.isEmpty())
                imageNo = imageService.imageUpload(file, 56, 56);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Position Save
        if (position != null)
            positionRepository.save(position);

        // User Save
        User user = dto.toUserEntity(dto, position);
        user.setProfileImageNo(imageNo);
        userRepository.save(user);

        // TechnicalStacks Save
        if (!saveTechnicalStacksList.isEmpty()) {
            for (TechnicalStack t : saveTechnicalStacksList) {
                userTechnicalStackRepository.save(UserTechnicalStack.builder()
                        .technicalStack(t)
                        .user(user)
                        .build()
                );
            }
        }
        return user;
    }

    @Override
    public String getUserProfileImage(Long no) {
        String imgUrl = null;
        if (no != null) {
            Optional<Image> img = imageRepository.findById(no);
            img.orElseThrow(() -> new RuntimeException("Not Find Profile Image"));
            if (img.isPresent())
                imgUrl = img.get().getUrl();
        }
        return imgUrl;
    }

    @Override
    public UserInfoResponseDto userInfo(Long no) {
        Optional<User> user = userRepository.findById(no);
        user.orElseThrow(() -> new RuntimeException("Not Find User No"));

        String position = null;
        if (user.get().getPosition() != null)
            position = user.get().getPosition().getName();

        List<String> technicalStackList = userTechnicalStackRepository.findUserTechnicalStacksByUser(no)
                .stream()
                .map(UserTechnicalStack::getTechnicalStack)
                .map(TechnicalStack::getName)
                .collect(Collectors.toList());

        return UserInfoResponseDto.builder()
                .name(user.get().getName())
                .sex(user.get().getSex())
                .email(user.get().getEmail())
                .position(position)
                .technicalStackList(technicalStackList)
                .github(user.get().getGithub())
                .selfIntroduction(user.get().getSelfIntroduction())
                .profile(getUserProfileImage(user.get().getImageNo()))
                .build();
    }

    @Override
    public List<UserSimpleInfoDto> userInfoList(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        List<UserSimpleInfoDto> userSimpleInfoDtos = new ArrayList<>();
        for (User user: users) {
            String imgUrl = getUserProfileImage(user.getImageNo());
            userSimpleInfoDtos.add(UserSimpleInfoDto.builder()
                    .no(user.getNo())
                    .name(user.getName())
                    .email(user.getEmail())
                    .profile(imgUrl)
                    .build()
            );
        }
        return userSimpleInfoDtos;
    }

    public void updateValidCheck(UserUpdateRequestDto dto, User user) {
        if ("".equals(dto.getName()) || dto.getName() == null)
            throw new RuntimeException("Name value is blanked");
        if (!"".equals(dto.getSex()) && dto.getSex() != null)  {
            if (dto.getSex().length() > 1 || !dto.getSex().matches("[mMwWoO]"))
                throw new RuntimeException("Sex value is Invalid");
        }
        if (user.getOauthCategory() == OAuth.NORMAL) {
            if ("".equals(dto.getOriginPassword()) || dto.getOriginPassword() == null)
                throw new RuntimeException("Original Password value is blanked");
            else if (!passwordEncoder.matches(dto.getOriginPassword(), user.getPassword()))
                throw new RuntimeException("Original Password is Wrong");
        }
    }

    @Transactional
    @Override
    public User userUpdate(Long no, UserUpdateRequestDto dto, MultipartFile file) {
        // Identification Check
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User identificationUser = (User)auth.getPrincipal();
        if (!Objects.equals(identificationUser.getNo(), no))
            throw new RuntimeException("Identification Check Fail");

        Optional<User> optionalUser = userRepository.findById(no);
        optionalUser.orElseThrow(() -> new RuntimeException("Not Find User No"));

        // Valid Check
        updateValidCheck(dto, optionalUser.get());

        // New Password Encode And Save
        if (optionalUser.get().getOauthCategory() == OAuth.NORMAL)
            optionalUser.get().updatePassword(passwordEncoder, dto.getNewPassword());

        // Get Entity
        Position position = getPositionForSave(dto.getPosition());
        List<TechnicalStack> saveTechnicalStacksList = getTechnicalStacksListForSave(dto.getTechnicalStackList());

        // Delete existing images & New Image Upload
        Long imageNo = null;
        if (!file.isEmpty()) {
            try {
                // Delete existing images
                if (optionalUser.get().getImageNo() != null)
                    imageService.imageDelete(optionalUser.get().getImageNo());
                // New Image Upload
                imageNo = imageService.imageUpload(file, 56, 56);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        optionalUser.get().setProfileImageNo(imageNo);

        // User & Position Update
        optionalUser.get().updateUser(dto, position);

        // UserTechnicalStacks Delete & Save
        if (userTechnicalStackRepository.findUserTechnicalStacksByUser(no) != null)
            userTechnicalStackRepository.deleteAllByUser(optionalUser.get());
        if (!saveTechnicalStacksList.isEmpty()) {
            for (TechnicalStack t : saveTechnicalStacksList) {
                userTechnicalStackRepository.save(UserTechnicalStack.builder()
                        .technicalStack(t)
                        .user(optionalUser.get())
                        .build()
                );
            }
        }
        return optionalUser.get();
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
