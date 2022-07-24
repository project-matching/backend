package com.matching.project.service;

import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.enumerate.UserFilter;
import com.matching.project.dto.user.*;
import com.matching.project.entity.*;
import com.matching.project.error.CustomException;
import com.matching.project.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    /*
        ref : https://github.com/HomoEfficio/dev-tips/blob/master/Spring-Boot-%EB%A0%88%EC%9D%B4%EC%96%B4%EB%B3%84-%ED%85%8C%EC%8A%A4%ED%8A%B8.md
     */

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRepositoryCustom userRepositoryCustom;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private TechnicalStackRepository technicalStackRepository;

    @Mock
    private UserTechnicalStackRepository userTechnicalStackRepository;

    @Mock
    private ImageService imageService;

    @Spy
    private BCryptPasswordEncoder passwordEncoder; // 타입이 'PasswordEncoder'이면 @spy로 하여도 동작하지 않음.

    @InjectMocks
    private UserServiceImpl userService;

    @DisplayName("비밀번호 변경 실패 : 소셜 유저가 비밀번호 변경 시도")
    @Test
    public void userPasswordUpdateFail1() {
        //given
        Long no = 3L;
        String name = "테스터";
        String email = "leeworld9@gmail.com";

        Optional<User> user = Optional.of(User.builder()
                .no(no)
                .name(name)
                .email(email)
                .oauthCategory(OAuth.GOOGLE)
                .permission(Role.ROLE_ADMIN)
                .build()
        );

        String newPassword = "testPassword";

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user.get(), user.get().getEmail(), user.get().getAuthorities()));

        given(userRepository.findByNoWithPositionUsingLeftFetchJoin(no)).willReturn(user);

        PasswordUpdateRequestDto dto = PasswordUpdateRequestDto.builder()
                .oldPassword("test")
                .newPassword(newPassword)
                .build();
        //when
        CustomException e = Assertions.assertThrows(CustomException.class, () -> {
            User resUser = userService.userPasswordUpdate(dto);
        });

        //then
        assertThat(e.getErrorCode().getDetail()).isEqualTo("Social users are not allowed feature");
    }

    @DisplayName("비밀번호 변경 실패 : 기존 비밀번호 틀림")
    @Test
    public void userPasswordUpdateFail2() {
        //given
        Long no = 3L;
        String name = "테스터";
        String email = "leeworld9@gmail.com";
        String password = "stlskejrlkjsdf";

        Optional<User> user = Optional.of(User.builder()
                .no(no)
                .name(name)
                .email(email)
                .oauthCategory(OAuth.NORMAL)
                .permission(Role.ROLE_ADMIN)
                .password(passwordEncoder.encode(password))
                .build()
        );

        String newPassword = "testPassword";

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user.get(), user.get().getEmail(), user.get().getAuthorities()));

        given(userRepository.findByNoWithPositionUsingLeftFetchJoin(no)).willReturn(user);

        PasswordUpdateRequestDto dto = PasswordUpdateRequestDto.builder()
                .oldPassword("test1")
                .newPassword(newPassword)
                .build();
        //when
        CustomException e = Assertions.assertThrows(CustomException.class, () -> {
            User resUser = userService.userPasswordUpdate(dto);
        });

        //then
        assertThat(e.getErrorCode().getDetail()).isEqualTo("This is an incorrect password");
    }

    @DisplayName("비밀번호 변경 성공")
    @Test
    public void userPasswordUpdateSuccess() {
        //given
        Long no = 3L;
        String name = "테스터";
        String email = "leeworld9@gmail.com";
        String password = "stlskejrlkjsdf";

        Optional<User> user = Optional.of(User.builder()
                .no(no)
                .name(name)
                .email(email)
                .oauthCategory(OAuth.NORMAL)
                .permission(Role.ROLE_ADMIN)
                .password(passwordEncoder.encode(password))
                .build()
        );

        String newPassword = "testPassword";

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user.get(), user.get().getEmail(), user.get().getAuthorities()));

        given(userRepository.findByNoWithPositionUsingLeftFetchJoin(no)).willReturn(user);

        PasswordUpdateRequestDto dto = PasswordUpdateRequestDto.builder()
                .oldPassword(password)
                .newPassword(newPassword)
                .build();
        //when
        User resUser = userService.userPasswordUpdate(dto);

        //then
        assertThat(passwordEncoder.matches(newPassword, resUser.getPassword())).isTrue();
    }

    @DisplayName("회원 차단 해제 실패 : 존재하지 않는 유저")
    @Test
    public void userUnBlockFail() {
        //given
        Long no = 3L;
        String name = "테스터";
        String email = "leeworld9@gmail.com";

        Optional<User> user = Optional.of(User.builder()
                .no(no)
                .name(name)
                .email(email)
                .permission(Role.ROLE_ADMIN)
                .block(true)
                .build()
        );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user.get(), user.get().getEmail(), user.get().getAuthorities()));

        given(userRepository.findById(no)).willReturn(Optional.empty());

        //when
        CustomException e = Assertions.assertThrows(CustomException.class, () -> {
            User resUser = userService.userUnBlock(no);
        });

        //then
        assertThat(e.getErrorCode().getDetail()).isEqualTo("Not Find User No");
    }

    @DisplayName("회원 차단 해제 성공")
    @Test
    public void userUnBlockSuccess() {
        //given
        Long no = 3L;
        String name = "테스터";
        String email = "leeworld9@gmail.com";
        String blockReason = "test";

        Optional<User> user = Optional.of(User.builder()
                .no(no)
                .name(name)
                .email(email)
                .permission(Role.ROLE_ADMIN)
                .block(true)
                .build()
        );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user.get(), user.get().getEmail(), user.get().getAuthorities()));

        given(userRepository.findById(no)).willReturn(user);

        //when
        User resUser = userService.userUnBlock(no);

        //then
        assertThat(resUser.isBlock()).isFalse();
        assertThat(resUser.getBlockReason()).isNull();
    }

    @DisplayName("회원 차단 실패 : 존재하지 않는 유저")
    @Test
    public void userBlockFail() {
        //given
        Long no = 3L;
        String name = "테스터";
        String email = "leeworld9@gmail.com";
        String blockReason = "test";

        Optional<User> user = Optional.of(User.builder()
                .no(no)
                .name(name)
                .email(email)
                .permission(Role.ROLE_ADMIN)
                .block(false)
                .build()
        );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user.get(), user.get().getEmail(), user.get().getAuthorities()));

        given(userRepository.findById(no)).willReturn(Optional.empty());

        //when
        CustomException e = Assertions.assertThrows(CustomException.class, () -> {
            User resUser = userService.userBlock(no, blockReason);
        });

        //then
        assertThat(e.getErrorCode().getDetail()).isEqualTo("Not Find User No");
    }

    @DisplayName("회원 차단 성공")
    @Test
    public void userBlockSuccess() {
        //given
        Long no = 3L;
        String name = "테스터";
        String email = "leeworld9@gmail.com";
        String blockReason = "test";

        Optional<User> user = Optional.of(User.builder()
                .no(no)
                .name(name)
                .email(email)
                .permission(Role.ROLE_ADMIN)
                .block(false)
                .build()
        );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user.get(), user.get().getEmail(), user.get().getAuthorities()));

        given(userRepository.findById(no)).willReturn(user);

        //when
        User resUser = userService.userBlock(no, blockReason);

        //then
        assertThat(resUser.isBlock()).isTrue();
    }

    @DisplayName("회원 탈퇴 성공")
    @Test
    public void signOutSuccess() {
        //given
        Long no = 3L;
        String name = "테스터";
        String email = "leeworld9@gmail.com";

        Optional<User> user = Optional.of(User.builder()
                .no(no)
                .name(name)
                .email(email)
                .permission(Role.ROLE_USER)
                .withdrawal(false)
                .build()
        );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user.get(), user.get().getEmail(), user.get().getAuthorities()));

        given(userRepository.findById(no)).willReturn(user);

        //when
        User resUser = userService.userSignOut();

        //then
        assertThat(resUser.isWithdrawal()).isTrue();
        assertThat(resUser.getWithdrawalTime()).isNotNull();
    }

    @DisplayName("회원 정보 수정 실패 : 존재하지 않는 포지션 입력")
    @Test
    public void userUpdateFail1() {
        //given
        Long no = 2L;
        String name = "테스터";
        String email = "leeworld9@gmail.com";

        Optional<User> user = Optional.of(User.builder()
                .no(no)
                .name(name)
                .email(email)
                .permission(Role.ROLE_USER)
                .build()
        );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user.get(), user.get().getEmail(), user.get().getAuthorities()));

        Long imageNo = 8L;
        String newName = "테스터2";
        String newSex = "W";
        String newGithub ="https://github.com/ggggg";
        String newSelfIntroduction = "자기소개2222";
        String newPosition = "FRONTEND";
        List<String> newTechnicalStackList = new ArrayList<>();
        newTechnicalStackList.add("Spring Boot");
        newTechnicalStackList.add("React");

        MockMultipartFile file = new MockMultipartFile("file", "file".getBytes());

        UserUpdateRequestDto dto = UserUpdateRequestDto.builder()
                .name(newName)
                .sex(newSex)
                .github(newGithub)
                .selfIntroduction(newSelfIntroduction)
                .position(newPosition)
                .technicalStackList(newTechnicalStackList)
                .build();

        given(userRepository.findById(no)).willReturn(user);
        given(positionRepository.findAllByName(newPosition)).willReturn(Optional.empty());

        //when
        CustomException e = Assertions.assertThrows(CustomException.class, () -> {
            userService.userUpdate(dto, file);
        });

        //then
        assertThat(e.getErrorCode().getDetail()).isEqualTo("Unregistered Position");

    }

    @DisplayName("회원 정보 수정 실패 : 존재하지 않는 기술스택 입력")
    @Test
    public void userUpdateFail2() {
        //given
        Long no = 2L;
        String name = "테스터";
        String email = "leeworld9@gmail.com";

        Optional<User> user = Optional.of(User.builder()
                .no(no)
                .name(name)
                .email(email)
                .permission(Role.ROLE_USER)
                .build()
        );

        List<TechnicalStack> technicalStacks = new ArrayList<>();
        TechnicalStack technicalStack1 = TechnicalStack.builder().no(1L).imageNo(1L).name("Spring Boot").build();
        TechnicalStack technicalStack2 = TechnicalStack.builder().no(2L).imageNo(2L).name("JPA").build();
        TechnicalStack technicalStack3 = TechnicalStack.builder().no(3L).imageNo(3L).name("React").build();
        technicalStacks.add(technicalStack1);
        technicalStacks.add(technicalStack2);
        technicalStacks.add(technicalStack3);

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user.get(), user.get().getEmail(), user.get().getAuthorities()));

        Long imageNo = 8L;
        String newName = "테스터2";
        String newSex = "W";
        String newGithub ="https://github.com/ggggg";
        String newSelfIntroduction = "자기소개2222";
        String newPosition = "FRONTEND";
        List<String> newTechnicalStackList = new ArrayList<>();
        newTechnicalStackList.add("Spring Boot");
        newTechnicalStackList.add("Node.js");

        Position p = Position.builder().no(2L).name(newPosition).build();

        MockMultipartFile file = new MockMultipartFile("file", "file".getBytes());

        UserUpdateRequestDto dto = UserUpdateRequestDto.builder()
                .name(newName)
                .sex(newSex)
                .github(newGithub)
                .selfIntroduction(newSelfIntroduction)
                .position(newPosition)
                .technicalStackList(newTechnicalStackList)
                .build();

        given(userRepository.findById(no)).willReturn(user);
        given(positionRepository.findAllByName(newPosition)).willReturn(Optional.ofNullable(p));
        given(technicalStackRepository.findAll()).willReturn(technicalStacks);

        //when
        CustomException e = Assertions.assertThrows(CustomException.class, () -> {
            userService.userUpdate(dto, file);
        });

        //then
        assertThat(e.getErrorCode().getDetail()).isEqualTo("Unregistered TechnicalStack");

    }


    @DisplayName("회원 정보 수정 성공")
    @Test
    public void userUpdateSuccess() {
        //given
        Long no = 2L;
        String name = "테스터";
        String email = "leeworld9@gmail.com";

        Optional<User> user = Optional.of(User.builder()
                .no(no)
                .name(name)
                .email(email)
                .permission(Role.ROLE_USER)
                .build()
        );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user.get(), user.get().getEmail(), user.get().getAuthorities()));

        Long imageNo = 8L;
        String newName = "테스터2";
        String newSex = "W";
        String newGithub ="https://github.com/ggggg";
        String newSelfIntroduction = "자기소개2222";
        String newPosition = "FRONTEND";
        List<String> newTechnicalStackList = new ArrayList<>();
        newTechnicalStackList.add("Spring Boot");
        newTechnicalStackList.add("React");

        Position p = Position.builder().no(2L).name(newPosition).build();

        List<TechnicalStack> technicalStacks = new ArrayList<>();
        TechnicalStack technicalStack1 = TechnicalStack.builder().no(1L).imageNo(1L).name("Spring Boot").build();
        TechnicalStack technicalStack2 = TechnicalStack.builder().no(2L).imageNo(2L).name("JPA").build();
        TechnicalStack technicalStack3 = TechnicalStack.builder().no(3L).imageNo(3L).name("React").build();
        technicalStacks.add(technicalStack1);
        technicalStacks.add(technicalStack2);
        technicalStacks.add(technicalStack3);

        MockMultipartFile file = new MockMultipartFile("file", "file".getBytes());

        UserUpdateRequestDto dto = UserUpdateRequestDto.builder()
                .name(newName)
                .sex(newSex)
                .github(newGithub)
                .selfIntroduction(newSelfIntroduction)
                .position(newPosition)
                .technicalStackList(newTechnicalStackList)
                .build();

        List <UserTechnicalStack> userTechnicalStackList = new ArrayList<>();
        userTechnicalStackList.add(UserTechnicalStack.builder().no(1L).user(user.get()).technicalStack(technicalStack1).build());
        userTechnicalStackList.add(UserTechnicalStack.builder().no(2L).user(user.get()).technicalStack(technicalStack2).build());

        given(userRepository.findById(no)).willReturn(user);
        given(positionRepository.findAllByName(newPosition)).willReturn(Optional.ofNullable(p));
        given(technicalStackRepository.findAll()).willReturn(technicalStacks);
        given(imageService.imageUpload(file, 56, 56)).willReturn(imageNo);
        given(userTechnicalStackRepository.findUserTechnicalStacksByUser(no)).willReturn(userTechnicalStackList);

        //when
        User resultUser = userService.userUpdate(dto, file);

        //then
        assertThat(resultUser.getName()).isEqualTo(dto.getName());
        assertThat(resultUser.getSex()).isEqualTo(dto.getSex());
        assertThat(resultUser.getGithub()).isEqualTo(dto.getGithub());
        assertThat(resultUser.getSelfIntroduction()).isEqualTo(dto.getSelfIntroduction());
        assertThat(resultUser.getPosition().getName()).isEqualTo(dto.getPosition());
        assertThat(resultUser.getImageNo()).isEqualTo(imageNo);

        //verify
        verify(userTechnicalStackRepository, times(2)).save(any());
    }

    @DisplayName("회원 리스트 조회 성공")
    @Test
    public void searchUserList() {
        //given
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            userList.add(User.builder()
                    .no(Integer.toUnsignedLong(i))
                    .name("테스터 " + Integer.toString(i))
                    .email("test" + Integer.toString(i) + "@naver.com")
                    .imageNo(Integer.toUnsignedLong(i))
                    .build()
            );
        }

        int page = 1;
        int size = 2;
        Pageable pageable = PageRequest.of(page, size, Sort.by("no").descending());
        int start = (int)pageable.getOffset();
        int end = (start + pageable.getPageSize()) > userList.size() ? userList.size() : (start + pageable.getPageSize());
        Page<User> users = new PageImpl<>(userList.subList(start, end), pageable, userList.size());

        UserFilterDto userFilterDto = UserFilterDto.builder().userFilter(UserFilter.NAME).content("테스터").build();

        given(userRepositoryCustom.findByNoUsingQueryDsl(pageable, userFilterDto)).willReturn(users);
        for (int i = start; i < start + size; i++)
            given(imageService.getImageUrl(Integer.toUnsignedLong(i))).willReturn("url_"+i);

        //when
        List<UserSimpleInfoDto> dtoList = userService.userInfoList(pageable, userFilterDto);

        //then
        assertThat(dtoList.get(0).getUserNo()).isEqualTo(2);
        assertThat(dtoList.get(0).getName()).isEqualTo("테스터 2");
        assertThat(dtoList.get(0).getEmail()).isEqualTo("test2@naver.com");
        assertThat(dtoList.get(0).getImage()).isEqualTo("url_2");
        assertThat(dtoList.get(1).getUserNo()).isEqualTo(3);
        assertThat(dtoList.get(1).getName()).isEqualTo("테스터 3");
        assertThat(dtoList.get(1).getEmail()).isEqualTo("test3@naver.com");
        assertThat(dtoList.get(1).getImage()).isEqualTo("url_3");
        assertThat(dtoList.size()).isEqualTo(size);
    }

    @DisplayName("회원 정보 조회 성공")
    @Test
    public void infoSuccess() {
        //given
        Long no = 1L;
        String name = "테스터";
        String sex = "M";
        String email = "leeworld9@gmail.com";
        String password = "asldkjfwlejkf";
        String github ="https://github.com/leeworld9";
        String selfIntroduction = "자기소개~~~";
        String position = "BACKEND";
        Long imageNo = 8L;
        List<String> technicalStackList = new ArrayList<>();
        technicalStackList.add("JPA");

        Image profile = Image.builder().no(imageNo).url("profile url").build();

        Position p = Position.builder().no(1L).name("BACKEND").build();

        List<TechnicalStack> technicalStacks = new ArrayList<>();
        TechnicalStack technicalStack1 = TechnicalStack.builder().no(1L).imageNo(1L).name("Spring Boot").build();
        TechnicalStack technicalStack2 = TechnicalStack.builder().no(2L).imageNo(2L).name("JPA").build();
        TechnicalStack technicalStack3 = TechnicalStack.builder().no(3L).imageNo(3L).name("React").build();
        technicalStacks.add(technicalStack1);
        technicalStacks.add(technicalStack2);
        technicalStacks.add(technicalStack3);

        Image tech1 = Image.builder().no(1L).url("spring boot url").build();
        Image tech2 = Image.builder().no(2L).url("jpa url").build();
        Image tech3 = Image.builder().no(3L).url("react url").build();

        Optional<User> user = Optional.ofNullable(User.builder()
                .no(no)
                .name(name)
                .sex(sex)
                .email(email)
                .password(passwordEncoder.encode(password))
                .github(github)
                .selfIntroduction(selfIntroduction)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .imageNo(imageNo)
                .block(false)
                .withdrawal(false)
                .email_auth(true)
                .position(p)
                .build()
        );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user.get(), user.get().getPassword(), user.get().getAuthorities()));

        List <UserTechnicalStack> userTechnicalStackList = new ArrayList<>();
        userTechnicalStackList.add(UserTechnicalStack.builder().no(1L).user(user.get()).technicalStack(technicalStack2).build());

        given(userRepository.findByNoWithPositionUsingLeftFetchJoin(no)).willReturn(user);
        given(imageService.getImageUrl(user.get().getImageNo())).willReturn(profile.getUrl());
        given(userTechnicalStackRepository.findUserTechnicalStacksByUser(no)).willReturn(userTechnicalStackList);
        given(imageService.getImageUrl(2L)).willReturn(tech2.getUrl());

        //when
        UserInfoResponseDto userInfo = userService.getUserInfo();

        //then
        assertThat(userInfo.getName()).isEqualTo(name);
        assertThat(userInfo.getRole()).isEqualTo(user.get().getPermission());
        assertThat(userInfo.getEmail()).isEqualTo(email);
        assertThat(userInfo.getImage()).isEqualTo("profile url");
        assertThat(userInfo.getPosition()).isEqualTo(position);
        assertThat(userInfo.getTechnicalStackDtoList().get(0).getName()).isEqualTo("JPA");
        assertThat(userInfo.getTechnicalStackDtoList().get(0).getImage()).isEqualTo("jpa url");
    }

    @DisplayName("회원 가입 실패 : 이메일 중복 가입")
    @Test
    public void signupFail1() {
        //given
        String name = "테스터";
        String email = "leeworld9@gmail.com";
        String password = "asldkjfwlejkf";

        SignUpRequestDto dto = SignUpRequestDto.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();

        User user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();

        given(userRepository.findByEmail(dto.getEmail())).willReturn(Optional.ofNullable(user));

        //when
        CustomException e = Assertions.assertThrows(CustomException.class, () -> {
            userService.userSignUp(dto);
        });

        //then
        assertThat(e.getErrorCode().getDetail()).isEqualTo("Duplicated Email");

    }

    @DisplayName("회원 가입 성공")
    @Test
    public void signupSuccess() {
        //given
        String name = "테스터";
        String email = "leeworld9@gmail.com";
        String password = "asldkjfwlejkf";

        SignUpRequestDto dto = SignUpRequestDto.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();

        User user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();

        given(userRepository.findByEmail(dto.getEmail())).willReturn(Optional.empty());

        //when
        User wUser = userService.userSignUp(dto);

        //then
        assertThat(wUser.getName()).isEqualTo(dto.getName());
        assertThat(wUser.getEmail()).isEqualTo(dto.getEmail());

        //verify
        verify(userRepository, times(1)).save(any());

    }
}