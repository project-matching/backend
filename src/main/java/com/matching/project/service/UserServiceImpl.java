package com.matching.project.service;

import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.technicalstack.TechnicalStackDto;
import com.matching.project.dto.user.*;
import com.matching.project.entity.*;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import com.matching.project.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final UserRepositoryCustom userRepositoryCustom;
    private final PositionRepository positionRepository;
    private final TechnicalStackRepository technicalStackRepository;
    private final UserTechnicalStackRepository userTechnicalStackRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageRepository imageRepository;
    private final ImageService imageService;

    public Position getPositionForSave(String s) {
        Position position = null;
        if (!"".equals(s) && s != null)
        {
            Optional<Position> optionalPosition = positionRepository.findAllByName(s);
            optionalPosition.orElseThrow(() -> new CustomException(ErrorCode.UNREGISTERED_POSITION_EXCEPTION));
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
                        // 중복 허용하지 않으려면 set 으로 바꿔주도록 하자.
                        saveTechnicalStacksList.add(technicalStacks.get(i));
                        break ;
                    }
                }
                if ( i == technicalStacks.size() )
                    throw new CustomException(ErrorCode.UNREGISTERED_TECHNICAL_STACK_EXCEPTION);
            }
        }
        return saveTechnicalStacksList;
    }

    @Override
    public UserInfoResponseDto getUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User identificationUser = (User)auth.getPrincipal();

        Optional<User> optionalUser = userRepository.findByNoWithPositionUsingLeftFetchJoin(identificationUser.getNo());
        optionalUser.orElseThrow(() -> new CustomException(ErrorCode.NOT_REGISTERED_EMAIL_EXCEPTION));

        // Image
        String imageUrl = imageService.getImageUrl(optionalUser.get().getImageNo());

        // Position
        String posision = null;
        if (optionalUser.get().getPosition() != null)
            posision = optionalUser.get().getPosition().getName();

        // TechnicalStack
        List<UserTechnicalStack> userTechnicalStackList = userTechnicalStackRepository.findUserTechnicalStacksByUser(optionalUser.get().getNo());
        List<TechnicalStackDto> technicalStackDtoList = new ArrayList<>();
        for (int i = 0; i < userTechnicalStackList.size() && i < 3 ; i++) {
            TechnicalStack technicalStack = userTechnicalStackList.get(i).getTechnicalStack();
            technicalStackDtoList.add(TechnicalStackDto.builder()
                    .name(technicalStack.getName())
                    .image(imageService.getImageUrl(technicalStack.getImageNo()))
                    .build());
        }
        UserInfoResponseDto dto = UserInfoResponseDto.builder()
                .no(optionalUser.get().getNo())
                .role(optionalUser.get().getPermission())
                .name(optionalUser.get().getName())
                .email(optionalUser.get().getEmail())
                .image(imageUrl)
                .position(posision)
                .technicalStackDtoList(technicalStackDtoList)
                .build();
        return dto;
    }

    @Override
    public User userSignUp(SignUpRequestDto dto){

        // Valid Check
        if (userRepository.findByEmail(dto.getEmail()).isPresent())
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL_EXCEPTION);

        // Password Encode
        dto.setEncodePassword(passwordEncoder.encode(dto.getPassword()));

        // User Save
        User user = dto.toUserEntity(dto);
        userRepository.save(user);

        return user;
    }

    @Override
    public List<UserSimpleInfoDto> userInfoList(Pageable pageable, UserFilterDto userFilterDto) {
        Page<User> users = userRepositoryCustom.findByNoUsingQueryDsl(pageable, userFilterDto);
        return users.get().map(user -> UserSimpleInfoDto.builder()
                .userNo(user.getNo())
                .name(user.getName())
                .email(user.getEmail())
                .image(imageService.getImageUrl(user.getImageNo()))
                .build()
        ).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public User userPasswordUpdate(PasswordUpdateRequestDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User identificationUser = (User)auth.getPrincipal();

        Optional<User> optionalUser = userRepository.findByNoWithPositionUsingLeftFetchJoin(identificationUser.getNo());
        //optionalUser.orElseThrow(() -> new CustomException(ErrorCode.NOT_REGISTERED_EMAIL_EXCEPTION));

        if (optionalUser.get().getOauthCategory() == OAuth.NORMAL) {
            if (!passwordEncoder.matches(dto.getOldPassword(), optionalUser.get().getPassword()))
                throw new CustomException(ErrorCode.INCORRECT_PASSWORD_EXCEPTION);
            optionalUser.get().updatePassword(passwordEncoder, dto.getNewPassword());
        }
        else
            throw new CustomException(ErrorCode.SOCIAL_USER_NOT_ALLOWED_FEATURE_EXCEPTION);
        return optionalUser.get();
    }

    @Override
    public UserProfileInfoResponseDto userProfileInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User identificationUser = (User)auth.getPrincipal();

        Optional<User> optionalUser = userRepository.findByNoWithPositionUsingLeftFetchJoin(identificationUser.getNo());
        //optionalUser.orElseThrow(() -> new CustomException(ErrorCode.NOT_REGISTERED_EMAIL_EXCEPTION));

        // Image
        String imageUrl = imageService.getImageUrl(optionalUser.get().getImageNo());

        // Position
        String posision = null;
        if (optionalUser.get().getPosition() != null)
            posision = optionalUser.get().getPosition().getName();

        // TechnicalStack
        List<UserTechnicalStack> userTechnicalStackList = userTechnicalStackRepository.findUserTechnicalStacksByUser(optionalUser.get().getNo());
        UserProfileInfoResponseDto dto = UserProfileInfoResponseDto.builder()
                .image(imageUrl)
                .name(optionalUser.get().getName())
                .email(optionalUser.get().getEmail())
                .sex(optionalUser.get().getSex())
                .position(posision)
                .technicalStackList(userTechnicalStackList.stream()
                        .map(UserTechnicalStack::getTechnicalStack)
                        .map(TechnicalStack::getName)
                        .collect(Collectors.toList()))
                .github(optionalUser.get().getGithub())
                .selfIntroduction(optionalUser.get().getSelfIntroduction())
                .build();
        return dto;
    }

    @Transactional
    @Override
    public User userUpdate(UserUpdateRequestDto dto, MultipartFile file) {
        // Identification Check
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User identificationUser = (User)auth.getPrincipal();

        Optional<User> optionalUser = userRepository.findById(identificationUser.getNo());
        //optionalUser.orElseThrow(() -> new CustomException(ErrorCode.NOT_REGISTERED_EMAIL_EXCEPTION));

        // Get Entity
        Position position = getPositionForSave(dto.getPosition());
        List<TechnicalStack> saveTechnicalStacksList = getTechnicalStacksListForSave(dto.getTechnicalStackList());

        // Delete existing images & New Image Upload
        Long imageNo = null;
        if (!file.isEmpty()) {
            // Delete existing images
            if (optionalUser.get().getImageNo() != null)
                imageService.imageDelete(optionalUser.get().getImageNo());
            // New Image Upload
            imageNo = imageService.imageUpload(file, 56, 56);
        }
        optionalUser.get().setProfileImageNo(imageNo);

        // User & Position Update
        optionalUser.get().updateUser(dto, position);

        // UserTechnicalStacks Delete & Save
        if (userTechnicalStackRepository.findUserTechnicalStacksByUser(identificationUser.getNo()) != null)
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
    public User userSignOut() {
        // Identification Check
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User identificationUser = (User)auth.getPrincipal();

        Optional<User> optionalUser = userRepository.findById(identificationUser.getNo());
        //optionalUser.orElseThrow(() -> new CustomException(ErrorCode.NOT_REGISTERED_EMAIL_EXCEPTION));

        if (optionalUser.get().isWithdrawal())
            throw new CustomException(ErrorCode.USER_ALREADY_WITHDRAWAL_EXCEPTION);

        // UserTechnicalStacks Delete
        if (userTechnicalStackRepository.findUserTechnicalStacksByUser(optionalUser.get().getNo()) != null)
            userTechnicalStackRepository.deleteAllByUser(optionalUser.get());

        // User SignOut
        optionalUser.get().userWithdrawal();

        return optionalUser.get();
    }

    @Transactional
    @Override
    public User userBlock(Long no, String reason) {
        Optional<User> optionalUser = userRepository.findById(no);
        optionalUser.orElseThrow(() -> new CustomException(ErrorCode.NOT_FIND_USER_NO_EXCEPTION));
        if (optionalUser.get().isBlock())
            throw new CustomException(ErrorCode.USER_ALREADY_BLOCKED_EXCEPTION);
        optionalUser.get().userBlock(reason);
        return optionalUser.get();
    }

    @Transactional
    @Override
    public User userUnBlock(Long no) {
        Optional<User> optionalUser = userRepository.findById(no);
        optionalUser.orElseThrow(() -> new CustomException(ErrorCode.NOT_FIND_USER_NO_EXCEPTION));
        if (!optionalUser.get().isBlock())
            throw new CustomException(ErrorCode.USER_ALREADY_UNBLOCKED_EXCEPTION);
        optionalUser.get().userUnBlock();
        return optionalUser.get();
    }
}
