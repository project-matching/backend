package com.matching.project.service;

import com.matching.project.dto.SliceDto;
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
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
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

    //임시
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        // 수정 필요
        // 참고 : https://velog.io/@jakeseo_me/JPA-%EC%82%AC%EC%9A%A9%ED%95%A0-%EB%95%8C-%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98-%EC%A3%BC%EC%9D%98%EC%82%AC%ED%95%AD-%EC%A0%95%EB%A6%AC

        // 42번 라인 처럼 받아오면 영속상태가 아니여서 반영이 안될 뿐더러 연관관계에 있는 객체도 가져오지 못함
        // 따라서 한번 더 조회가 필요하다. ( SecurityContextHolder에 엔티티 객체를 말고 새로 객체를 만들어서 담는것을 고려 )
        // 리펙토링시 위 상황을 고려해서 작성할 것.

        User user = null;
        if (principal instanceof User)
            user = (User)principal;
        else
            throw new CustomException(ErrorCode.GET_USER_AUTHENTICATION_EXCEPTION);
        return user;
    }

    //임시
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
        User user = userRepository.findByNoWithPositionUsingLeftFetchJoin(getAuthenticatedUser().getNo())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FIND_USER_NO_EXCEPTION));

        // Image
        String imageUrl = imageService.getImageUrl(user.getImageNo());

        // Position
        String posision = null;
        if (user.getPosition() != null)
            posision = user.getPosition().getName();

        // TechnicalStack
        List<UserTechnicalStack> userTechnicalStackList = userTechnicalStackRepository.findUserTechnicalStacksByUser(user.getNo());
        List<TechnicalStackDto> technicalStackDtoList = new ArrayList<>();
        for (int i = 0; i < userTechnicalStackList.size() && i < 3 ; i++) {
            TechnicalStack technicalStack = userTechnicalStackList.get(i).getTechnicalStack();
            technicalStackDtoList.add(TechnicalStackDto.builder()
                    .name(technicalStack.getName())
                    .image(imageService.getImageUrl(technicalStack.getImageNo()))
                    .build());
        }
        UserInfoResponseDto dto = UserInfoResponseDto.builder()
                .no(user.getNo())
                .role(user.getPermission())
                .name(user.getName())
                .email(user.getEmail())
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
    public SliceDto<UserSimpleInfoDto> userInfoList(Long UserNo, UserFilterDto userFilterDto, Pageable pageable) {
        Slice<User> users = userRepositoryCustom.findByNoOrderByNoDescUsingQueryDsl(UserNo, userFilterDto, pageable);
        SliceDto<UserSimpleInfoDto> dto = SliceDto.<UserSimpleInfoDto>builder()
                .content(users.get().map(user -> UserSimpleInfoDto.builder()
                                .userNo(user.getNo())
                                .name(user.getName())
                                .email(user.getEmail())
                                .image(imageService.getImageUrl(user.getImageNo()))
                                .build()
                        )
                        .collect(Collectors.toList()))
                .last(users.isLast())
                .build();
        return dto;
    }

    @Transactional
    @Override
    public User userPasswordUpdate(PasswordUpdateRequestDto dto) {
        User user = userRepository.findById(getAuthenticatedUser().getNo())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FIND_USER_NO_EXCEPTION));

        if (user.getOauthCategory() == OAuth.NORMAL) {
            if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword()))
                throw new CustomException(ErrorCode.INCORRECT_PASSWORD_EXCEPTION);
            user.updatePassword(passwordEncoder, dto.getNewPassword());
        }
        else
            throw new CustomException(ErrorCode.SOCIAL_USER_NOT_ALLOWED_FEATURE_EXCEPTION);
        return user;
    }

    @Override
    public UserProfileInfoResponseDto userProfileInfo() {
        User user = userRepository.findByNoWithPositionUsingLeftFetchJoin(getAuthenticatedUser().getNo())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FIND_USER_NO_EXCEPTION));

        // Image
        String imageUrl = imageService.getImageUrl(user.getImageNo());

        // Position
        String posision = null;
        if (user.getPosition() != null)
            posision = user.getPosition().getName();

        // TechnicalStack
        List<UserTechnicalStack> userTechnicalStackList = userTechnicalStackRepository.findUserTechnicalStacksByUser(user.getNo());
        UserProfileInfoResponseDto dto = UserProfileInfoResponseDto.builder()
                .image(imageUrl)
                .name(user.getName())
                .email(user.getEmail())
                .sex(user.getSex())
                .position(posision)
                .technicalStackList(userTechnicalStackList.stream()
                        .map(UserTechnicalStack::getTechnicalStack)
                        .map(TechnicalStack::getName)
                        .collect(Collectors.toList()))
                .github(user.getGithub())
                .selfIntroduction(user.getSelfIntroduction())
                .loginCategory(user.getOauthCategory().toString())
                .build();
        return dto;
    }

    @Transactional
    @Override
    public User userUpdate(UserUpdateRequestDto dto, MultipartFile file) {
        User user = userRepository.findById(getAuthenticatedUser().getNo())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FIND_USER_NO_EXCEPTION));

        // Get Entity
        Position position = getPositionForSave(dto.getPosition());
        List<TechnicalStack> saveTechnicalStacksList = getTechnicalStacksListForSave(dto.getTechnicalStackList());

        // Delete existing images & New Image Upload
        Long imageNo = null;
        if (!file.isEmpty()) {
            // Delete existing images
            if (user.getImageNo() != null)
                imageService.imageDelete(user.getImageNo());
            // New Image Upload
            imageNo = imageService.imageUpload(file, 56, 56);
        }
        user.setProfileImageNo(imageNo);

        // User & Position Update
        user.updateUser(dto, position);

        // UserTechnicalStacks Delete & Save
        if (userTechnicalStackRepository.findUserTechnicalStacksByUser(user.getNo()) != null)
            userTechnicalStackRepository.deleteAllByUser(user);
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

    @Transactional
    @Override
    public User userSignOut() {
        User user = userRepository.findById(getAuthenticatedUser().getNo())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FIND_USER_NO_EXCEPTION));

        if (user.isWithdrawal())
            throw new CustomException(ErrorCode.USER_ALREADY_WITHDRAWAL_EXCEPTION);

        // User SignOut
        user.userWithdrawal();

        return user;
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
