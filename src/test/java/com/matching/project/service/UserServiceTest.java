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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;


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
    private PositionRepository positionRepository;

    @Mock
    private TechnicalStackRepository technicalStackRepository;

    @Mock
    private UserTechnicalStackRepository userTechnicalStackRepository;

    @Spy
    private BCryptPasswordEncoder passwordEncoder; // 타입이 'PasswordEncoder'이면 @spy로 하여도 동작하지 않음.

    @InjectMocks
    private UserServiceImpl userService;

    @DisplayName("회원 차단 해제 성공")
    @Test
    public void userUnBlockSuccess() {
        //given
        Long no = 3L;
        String name = "테스터";
        String sex = "M";
        String email = "leeworld9@gmail.com";
        String password ="asdfqwef2351235";
        String github ="https://github.com/leeworld9";
        String selfIntroduction = "자기소개~~~";
        String position = "BACKEND";
        List<String> technicalStackList = new ArrayList<>();
        technicalStackList.add("Spring Boot");
        technicalStackList.add("JPA");
        technicalStackList.add("React");

        Position p1 = Position.builder().no(1L).name(position).build();

        Optional<User> user = Optional.of(User.builder()
                .no(no)
                .name(name)
                .sex(sex.charAt(0))
                .email(email)
                .password(passwordEncoder.encode(password))
                .github(github)
                .selfIntroduction(selfIntroduction)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .block(true)
                .position(p1)
                .build()
        );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user.get(), user.get().getEmail(), user.get().getAuthorities()));

        given(userRepository.findById(no)).willReturn(user);

        //when
        User resUser = userService.userUnBlock(no);

        //then
        assertThat(resUser.isBlock()).isFalse();
    }

    @DisplayName("회원 차단 성공")
    @Test
    public void userBlockSuccess() {
        //given
        Long no = 3L;
        String name = "테스터";
        String sex = "M";
        String email = "leeworld9@gmail.com";
        String password ="asdfqwef2351235";
        String github ="https://github.com/leeworld9";
        String selfIntroduction = "자기소개~~~";
        String position = "BACKEND";
        List<String> technicalStackList = new ArrayList<>();
        technicalStackList.add("Spring Boot");
        technicalStackList.add("JPA");
        technicalStackList.add("React");

        Position p1 = Position.builder().no(1L).name(position).build();

        Optional<User> user = Optional.of(User.builder()
                .no(no)
                .name(name)
                .sex(sex.charAt(0))
                .email(email)
                .password(passwordEncoder.encode(password))
                .github(github)
                .selfIntroduction(selfIntroduction)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .block(false)
                .position(p1)
                .build()
        );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user.get(), user.get().getEmail(), user.get().getAuthorities()));

        given(userRepository.findById(no)).willReturn(user);

        UserBlockRequestDto dto = UserBlockRequestDto.builder().blockReason("test").build();

        //when
        User resUser = userService.userBlock(no, dto);

        //then
        assertThat(resUser.isBlock()).isTrue();
    }



    @DisplayName("회원 탈퇴 실패 : 잘못된 사용자 접근 시도")
    @Test
    public void signOutFail1() {
        //given
        Long no = 3L;
        String name = "테스터";
        String sex = "M";
        String email = "leeworld9@gmail.com";
        String password ="asdfqwef2351235";
        String github ="https://github.com/leeworld9";
        String selfIntroduction = "자기소개~~~";
        String position = "BACKEND";
        List<String> technicalStackList = new ArrayList<>();
        technicalStackList.add("Spring Boot");
        technicalStackList.add("JPA");
        technicalStackList.add("React");

        Position p1 = Position.builder().no(1L).name(position).build();

        Optional<User> user = Optional.of(User.builder()
                .no(no)
                .name(name)
                .sex(sex.charAt(0))
                .email(email)
                .password(passwordEncoder.encode(password))
                .github(github)
                .selfIntroduction(selfIntroduction)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .block(false)
                .position(p1)
                .build()
        );

        Optional<User> defUser = Optional.of(User.builder()
                .no(4L)
                .name(name)
                .sex(sex.charAt(0))
                .email(email)
                .password(passwordEncoder.encode(password))
                .github(github)
                .selfIntroduction(selfIntroduction)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .block(false)
                .position(p1)
                .build()
        );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(defUser.get(), defUser.get().getEmail(), defUser.get().getAuthorities()));

        SignOutRequestDto dto = SignOutRequestDto.builder().password("asdfqwef2351235").build();

        //when
        Exception e = Assertions.assertThrows(RuntimeException.class, () -> {
            userService.userSignOut(no, dto);
        });

        //then
        assertThat(e.getMessage()).isEqualTo("Identification Check Fail");
    }

    @DisplayName("회원 탈퇴 실패 : 패스워드 틀림")
    @Test
    public void signOutFail2() {
        //given
        Long no = 3L;
        String name = "테스터";
        String sex = "M";
        String email = "leeworld9@gmail.com";
        String password ="asdfqwef2351235";
        String github ="https://github.com/leeworld9";
        String selfIntroduction = "자기소개~~~";
        String position = "BACKEND";
        List<String> technicalStackList = new ArrayList<>();
        technicalStackList.add("Spring Boot");
        technicalStackList.add("JPA");
        technicalStackList.add("React");

        Position p1 = Position.builder().no(1L).name(position).build();

        Optional<User> user = Optional.of(User.builder()
                .no(no)
                .name(name)
                .sex(sex.charAt(0))
                .email(email)
                .password(passwordEncoder.encode(password))
                .github(github)
                .selfIntroduction(selfIntroduction)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .block(false)
                .position(p1)
                .build()
        );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user.get(), user.get().getEmail(), user.get().getAuthorities()));

        given(userRepository.findById(no)).willReturn(user);

        SignOutRequestDto dto = SignOutRequestDto.builder().password("test").build();

        //when
        Exception e = Assertions.assertThrows(RuntimeException.class, () -> {
            userService.userSignOut(no, dto);
        });

        //then
        assertThat(e.getMessage()).isEqualTo("Password is Wrong");
    }

    @DisplayName("회원 탈퇴 성공")
    @Test
    public void signOutSuccess() {
        //given
        Long no = 3L;
        String name = "테스터";
        String sex = "M";
        String email = "leeworld9@gmail.com";
        String password ="asdfqwef2351235";
        String github ="https://github.com/leeworld9";
        String selfIntroduction = "자기소개~~~";
        String position = "BACKEND";
        List<String> technicalStackList = new ArrayList<>();
        technicalStackList.add("Spring Boot");
        technicalStackList.add("JPA");
        technicalStackList.add("React");

        Position p1 = Position.builder().no(1L).name(position).build();

        Optional<User> user = Optional.of(User.builder()
                .no(no)
                .name(name)
                .sex(sex.charAt(0))
                .email(email)
                .password(passwordEncoder.encode(password))
                .github(github)
                .selfIntroduction(selfIntroduction)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .block(false)
                .position(p1)
                .build()
        );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user.get(), user.get().getEmail(), user.get().getAuthorities()));

        given(userRepository.findById(no)).willReturn(user);

        SignOutRequestDto dto = SignOutRequestDto.builder().password("asdfqwef2351235").build();

        //when
        Long resultNo = userService.userSignOut(no, dto);

        //then
        assertThat(resultNo).isEqualTo(no);

        //verify
        verify(userRepository, times(1)).deleteById(no);
    }


    @DisplayName("회원 정보 수정 실패 : 잘못된 사용자 접근 시도")
    @Test
    public void userUpdateFail1() {
        //given
        Long no = 3L;
        String name = "테스터";
        String sex = "M";
        String email = "leeworld9@gmail.com";
        String password ="asdfqwef2351235";
        String github ="https://github.com/leeworld9";
        String selfIntroduction = "자기소개~~~";
        String position = "BACKEND";
        List<String> technicalStackList = new ArrayList<>();
        technicalStackList.add("Spring Boot");
        technicalStackList.add("JPA");
        technicalStackList.add("React");

        Position p1 = Position.builder().no(1L).name(position).build();

        Optional<User> user = Optional.of(User.builder()
                .no(no)
                .name(name)
                .sex(sex.charAt(0))
                .email(email)
                .password(passwordEncoder.encode(password))
                .github(github)
                .selfIntroduction(selfIntroduction)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .block(false)
                .position(p1)
                .build()
        );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user.get(), user.get().getEmail(), user.get().getAuthorities()));

        String newName = "테스터2";
        String newSex = "W";
        String originPassword = "asdfqwef2351235";
        String newPassword = "231241d2";
        String newGithub ="https://github.com/ggggg";
        String newSelfIntroduction = "자기소개2222";
        String newPosition = "FRONTEND";
        List<String> newTechnicalStackList = new ArrayList<>();
        newTechnicalStackList.add("Spring Boot");
        newTechnicalStackList.add("React");

        UserUpdateRequestDto dto = UserUpdateRequestDto.builder()
                .name(newName)
                .sex(newSex)
                .originPassword(originPassword)
                .newPassword(newPassword)
                .github(newGithub)
                .selfIntroduction(newSelfIntroduction)
                .position(newPosition)
                .technicalStackList(newTechnicalStackList)
                .build();

        //when
        Exception e = Assertions.assertThrows(RuntimeException.class, () -> {
            userService.userUpdate(7L, dto);
        });

        //then
        assertThat(e.getMessage()).isEqualTo("Identification Check Fail");

    }

    @DisplayName("회원 정보 수정 실패 : 비밀번호 인증 실패")
    @Test
    public void userUpdateFai2() {
        //given
        Long no = 2L;
        String name = "테스터";
        String sex = "M";
        String email = "leeworld9@gmail.com";
        String password = "asdfqwef2351235";
        String github ="https://github.com/leeworld9";
        String selfIntroduction = "자기소개~~~";
        String position = "BACKEND";
        List<String> technicalStackList = new ArrayList<>();
        technicalStackList.add("Spring Boot");
        technicalStackList.add("JPA");
        technicalStackList.add("React");

        Position p1 = Position.builder().no(1L).name(position).build();

        Optional<User> user = Optional.of(User.builder()
                .no(2L)
                .name(name)
                .sex(sex.charAt(0))
                .email(email)
                .password(passwordEncoder.encode(password))
                .github(github)
                .selfIntroduction(selfIntroduction)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .block(false)
                .position(p1)
                .build()
        );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user.get(), user.get().getEmail(), user.get().getAuthorities()));

        String newName = "테스터2";
        String newSex = "W";
        String originPassword = "1251521231";
        String newPassword = "231241d2";
        String newGithub ="https://github.com/ggggg";
        String newSelfIntroduction = "자기소개2222";
        String newPosition = "FRONTEND";
        List<String> newTechnicalStackList = new ArrayList<>();
        newTechnicalStackList.add("Spring Boot");
        newTechnicalStackList.add("React");

        UserUpdateRequestDto dto = UserUpdateRequestDto.builder()
                .name(newName)
                .sex(newSex)
                .originPassword(originPassword)
                .newPassword(newPassword)
                .github(newGithub)
                .selfIntroduction(newSelfIntroduction)
                .position(newPosition)
                .technicalStackList(newTechnicalStackList)
                .build();

        given(userRepository.findById(no)).willReturn(user);

        //when
        Exception e = Assertions.assertThrows(RuntimeException.class, () -> {
            userService.userUpdate(no, dto);
        });

        //then
        assertThat(e.getMessage()).isEqualTo("Original Password is Wrong");

    }

    @DisplayName("회원 정보 수정 성공")
    @Test
    public void userUpdateSuccess() {
        //given
        Long no = 2L;
        String name = "테스터";
        String sex = "M";
        String email = "leeworld9@gmail.com";
        String password = "asdfqwef2351235";
        String github ="https://github.com/leeworld9";
        String selfIntroduction = "자기소개~~~";
        String position = "BACKEND";
        List<String> technicalStackList = new ArrayList<>();
        technicalStackList.add("Spring Boot");
        technicalStackList.add("JPA");
        technicalStackList.add("React");

        Position p1 = Position.builder().no(1L).name(position).build();

        List<TechnicalStack> technicalStacks = new ArrayList<>();
        TechnicalStack technicalStack1 = TechnicalStack.builder().no(1L).name("Spring Boot").build();
        TechnicalStack technicalStack2 = TechnicalStack.builder().no(2L).name("JPA").build();
        TechnicalStack technicalStack3 = TechnicalStack.builder().no(3L).name("React").build();
        technicalStacks.add(technicalStack1);
        technicalStacks.add(technicalStack2);
        technicalStacks.add(technicalStack3);

        Optional<User> user = Optional.of(User.builder()
                .no(2L)
                .name(name)
                .sex(sex.charAt(0))
                .email(email)
                .password(passwordEncoder.encode(password))
                .github(github)
                .selfIntroduction(selfIntroduction)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .block(false)
                .position(p1)
                .build()
        );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user.get(), user.get().getEmail(), user.get().getAuthorities()));

        String newName = "테스터2";
        String newSex = "W";
        String originPassword = "asdfqwef2351235";
        String newPassword = "231241d2";
        String newGithub ="https://github.com/ggggg";
        String newSelfIntroduction = "자기소개2222";
        String newPosition = "FRONTEND";
        List<String> newTechnicalStackList = new ArrayList<>();
        newTechnicalStackList.add("Spring Boot");
        newTechnicalStackList.add("React");

        Position p2 = Position.builder().no(2L).name(newPosition).build();

        UserUpdateRequestDto dto = UserUpdateRequestDto.builder()
                .name(newName)
                .sex(newSex)
                .originPassword(originPassword)
                .newPassword(newPassword)
                .github(newGithub)
                .selfIntroduction(newSelfIntroduction)
                .position(newPosition)
                .technicalStackList(newTechnicalStackList)
                .build();

        List <UserTechnicalStack> userTechnicalStackList = new ArrayList<>();
        userTechnicalStackList.add(UserTechnicalStack.builder().technicalStack(technicalStack1).user(user.get()).no(1L).build());
        userTechnicalStackList.add(UserTechnicalStack.builder().technicalStack(technicalStack3).user(user.get()).no(2L).build());

        given(userRepository.findById(no)).willReturn(user);
        given(positionRepository.findAllByName(newPosition)).willReturn(Optional.ofNullable(p2));
        given(technicalStackRepository.findAll()).willReturn(technicalStacks);
        given(userTechnicalStackRepository.findUserTechnicalStacksByUser(no)).willReturn(userTechnicalStackList);

        //when
        User resultUser = userService.userUpdate(no, dto);

        //then
        assertThat(resultUser.getName()).isEqualTo(dto.getName());
        assertThat(resultUser.getSex()).isEqualTo(dto.getSex().charAt(0));
        assertThat(resultUser.getPassword()).isNotEqualTo(dto.getOriginPassword());
        assertThat(resultUser.getGithub()).isEqualTo(dto.getGithub());
        assertThat(resultUser.getSelfIntroduction()).isEqualTo(dto.getSelfIntroduction());
        assertThat(resultUser.getPosition().getName()).isEqualTo(dto.getPosition());

        //verify
        verify(userTechnicalStackRepository, times(1)).deleteAllByUser(user.get());
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
                    .sex('M')
                    .password(passwordEncoder.encode("1111"))
                    .build()
            );
        }

        int page = 1;
        int size = 2;
        Pageable pageable = PageRequest.of(page, size, Sort.by("no").descending());
        int start = (int)pageable.getOffset();
        int end = (start + pageable.getPageSize()) > userList.size() ? userList.size() : (start + pageable.getPageSize());
        Page<User> users = new PageImpl<>(userList.subList(start, end), pageable, userList.size());

        given(userRepository.findAll(pageable)).willReturn(users);

        //when
        List<UserSimpleInfoDto> dtoList = userService.userInfoList(pageable);

        //then
        assertThat(dtoList.get(0).getName()).isEqualTo("테스터 2");
        assertThat(dtoList.get(1).getName()).isEqualTo("테스터 3");
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
        List<String> technicalStackList = new ArrayList<>();
        technicalStackList.add("JPA");


        Position p = Position.builder().no(1L).name("BACKEND").build();

        List<TechnicalStack> technicalStacks = new ArrayList<>();
        TechnicalStack technicalStack1 = TechnicalStack.builder().no(1L).name("Spring Boot").build();
        TechnicalStack technicalStack2 = TechnicalStack.builder().no(2L).name("JPA").build();
        TechnicalStack technicalStack3 = TechnicalStack.builder().no(3L).name("React").build();
        technicalStacks.add(technicalStack1);
        technicalStacks.add(technicalStack2);
        technicalStacks.add(technicalStack3);

        Optional<User> user = Optional.ofNullable(User.builder()
                .no(no)
                .name(name)
                .sex(sex.charAt(0))
                .email(email)
                .password(passwordEncoder.encode(password))
                .github(github)
                .selfIntroduction(selfIntroduction)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .block(false)
                .position(p)
                .build()
        );

        List <UserTechnicalStack> userTechnicalStackList = new ArrayList<>();
        userTechnicalStackList.add(UserTechnicalStack.builder().technicalStack(technicalStack2).user(user.get()).no(1L).build());

        given(userRepository.findById(no)).willReturn(user);
        given(userTechnicalStackRepository.findUserTechnicalStacksByUser(no)).willReturn(userTechnicalStackList);

        //when
        UserInfoResponseDto userInfo = userService.userInfo(no);

        //then
        assertThat(userInfo.getName()).isEqualTo(name);
        assertThat(userInfo.getSex()).isEqualTo(sex.charAt(0));
        assertThat(userInfo.getEmail()).isEqualTo(email);
        assertThat(userInfo.getGithub()).isEqualTo(github);
        assertThat(userInfo.getSelfIntroduction()).isEqualTo(selfIntroduction);
        assertThat(userInfo.getPosition()).isEqualTo(position);
        assertThat(userInfo.getTechnicalStackList().get(0)).isEqualTo("JPA");



    }

    @DisplayName("회원 가입 실패 : 이메일 중복 가입")
    @Test
    public void signupFail1() {
        //given
        String name = "테스터";
        String sex = "M";
        String email = "leeworld9@gmail.com";
        String password = "asldkjfwlejkf";
        String github ="https://github.com/leeworld9";
        String selfIntroduction = "자기소개~~~";
        String position = "BACKEND";

        SignUpRequestDto dto = SignUpRequestDto.builder()
                .name(name)
                .sex(sex)
                .email(email)
                .password(password)
                .github(github)
                .selfIntroduction(selfIntroduction)
                .position(position)
                .build();

        Position p = Position.builder().no(1L).name("BACKEND").build();

        Optional<User> user = Optional.of(User.builder()
                .name(name)
                .sex(sex.charAt(0))
                .email(email)
                .password(passwordEncoder.encode(password))
                .github(github)
                .selfIntroduction(selfIntroduction)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .block(false)
                .position(null)
                .build()
                );

        given(userRepository.findByEmail(dto.getEmail())).willReturn(user);

        //when
        Exception e = Assertions.assertThrows(RuntimeException.class, () -> {
            userService.userSignUp(dto);
        });

        //then
        assertThat(e.getMessage()).isEqualTo("Email is duplicated.");
    }

    @DisplayName("회원 가입 실패 : 패스워드(필수 입력 값) 공백 에러")
    @Test
    public void signupFail2() {
        //given
        String name = "테스터";
        String sex = "M";
        String email = "leeworld9@gmail.com";
        String password = "";
        String github ="https://github.com/leeworld9";
        String selfIntroduction = "자기소개~~~";
        String position = "BACKEND";

        SignUpRequestDto dto = SignUpRequestDto.builder()
                .name(name)
                .sex(sex)
                .email(email)
                .password(password)
                .github(github)
                .selfIntroduction(selfIntroduction)
                .position(position)
                .build();

        //when
        Exception e = Assertions.assertThrows(RuntimeException.class, () -> {
            userService.userSignUp(dto);
        });

        //then
        assertThat(e.getMessage()).isEqualTo("Password value is blanked");
    }

    @DisplayName("회원 가입 실패 : 성별 값이 비정상적으로 들어왔을 경우 에러")
    @Test
    public void signupFail3() {
        //given
        String name = "테스터";
        String sex = "MMM";
        String email = "leeworld9@gmail.com";
        String password = "asldkjfwlejkf";
        String github ="https://github.com/leeworld9";
        String selfIntroduction = "자기소개~~~";
        String position = "BACKEND";

        SignUpRequestDto dto = SignUpRequestDto.builder()
                .name(name)
                .sex(sex)
                .email(email)
                .password(password)
                .github(github)
                .selfIntroduction(selfIntroduction)
                .position(position)
                .build();

        //when
        Exception e = Assertions.assertThrows(RuntimeException.class, () -> {
            userService.userSignUp(dto);
        });

        //then
        assertThat(e.getMessage()).isEqualTo("Sex value is Invalid");
    }

    @DisplayName("회원 가입 성공")
    @Test
    public void signupSuccess() {
        //given
        String name = "테스터";
        String sex = "M";
        String email = "leeworld9@gmail.com";
        String password = "asldkjfwlejkf";
        String github ="https://github.com/leeworld9";
        String selfIntroduction = "자기소개~~~";
        String position = "BACKEND";
        List<String> technicalStackList = new ArrayList<>();
        technicalStackList.add("Spring Boot");
        technicalStackList.add("React");

        SignUpRequestDto dto = SignUpRequestDto.builder()
                .name(name)
                .sex(sex)
                .email(email)
                .password(password)
                .github(github)
                .selfIntroduction(selfIntroduction)
                .position(position)
                .technicalStackList(technicalStackList)
                .build();

        Position p = Position.builder().no(1L).name("BACKEND").build();

        List<TechnicalStack> technicalStacks = new ArrayList<>();
        TechnicalStack technicalStack1 = TechnicalStack.builder().no(1L).name("Spring Boot").build();
        TechnicalStack technicalStack2 = TechnicalStack.builder().no(2L).name("JPA").build();
        TechnicalStack technicalStack3 = TechnicalStack.builder().no(3L).name("React").build();
        technicalStacks.add(technicalStack1);
        technicalStacks.add(technicalStack2);
        technicalStacks.add(technicalStack3);

        User user = User.builder()
                .name(name)
                .sex(sex.charAt(0))
                .email(email)
                .password(passwordEncoder.encode(password))
                .github(github)
                .selfIntroduction(selfIntroduction)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .block(false)
                .position(p)
                .build();

        given(userRepository.findByEmail(dto.getEmail())).willReturn(Optional.empty());
        given(positionRepository.findAllByName(position)).willReturn(Optional.ofNullable(p));
        given(technicalStackRepository.findAll()).willReturn(technicalStacks);
        given(positionRepository.save(p)).willReturn(p);
        given(userRepository.save(any(User.class))).willReturn(user);

        //when
        User wUser = userService.userSignUp(dto);

        //then
        assertThat(wUser.getName()).isEqualTo(dto.getName());
        assertThat(wUser.getSex()).isEqualTo(dto.getSex().charAt(0));
        assertThat(wUser.getEmail()).isEqualTo(dto.getEmail());
        assertThat(wUser.getGithub()).isEqualTo(dto.getGithub());
        assertThat(wUser.getSelfIntroduction()).isEqualTo(dto.getSelfIntroduction());
        assertThat(wUser.getPosition().getName()).isEqualTo(dto.getPosition());

        //verify
        verify(userTechnicalStackRepository, times(2)).save(any());

    }

}