package com.matching.project.service;

import com.matching.project.dto.common.NormalLoginRequestDto;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Position;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.user.SignUpRequestDto;
import com.matching.project.dto.user.UserInfoResponseDto;
import com.matching.project.dto.user.UserSimpleInfoDto;
import com.matching.project.dto.user.UserUpdateRequestDto;
import com.matching.project.entity.Image;
import com.matching.project.entity.User;
import com.matching.project.entity.UserPosition;
import com.matching.project.entity.UserTechnicalStack;
import com.matching.project.repository.UserPositionRepository;
import com.matching.project.repository.UserRepository;
import com.matching.project.repository.UserTechnicalStackRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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
    private UserPositionRepository userPositionRepository;

    @Mock
    private UserTechnicalStackRepository userTechnicalStackRepository;

    @Spy
    private BCryptPasswordEncoder passwordEncoder; // 타입이 'PasswordEncoder'이면 @spy로 하여도 동작하지 않음.

    @InjectMocks
    private UserServiceImpl userService;

    @DisplayName("잘못된 사용자 접근 시도(회원 정보 수정)")
    @Test
    public void userUpdateFail1() {
        //given
        Long no = 2L;
        String name = "테스터";
        String sex = "M";
        String email = "leeworld9@gmail.com";
        String password = passwordEncoder.encode("asdfqwef2351235");
        String github ="https://github.com/leeworld9";
        String selfIntroduction = "자기소개~~~";
        Position position = Position.BACKEND;
        List<String> technicalStackList = new ArrayList<>();
        technicalStackList.add("Spring Boot");
        technicalStackList.add("JPA");
        technicalStackList.add("React");

        Optional<UserPosition> userPosition = Optional.ofNullable(
                UserPosition.builder()
                        .no(3L)
                        .name(position.toString())
                        .build()
        );

        Optional<User> user = Optional.ofNullable(User.builder()
                .no(no)
                .name(name)
                .sex(sex.charAt(0))
                .email(email)
                .password(password)
                .github(github)
                .selfIntroduction(selfIntroduction)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .block(false)
                .userPosition(userPosition.get())
                .build()
        );

        List<UserTechnicalStack> userTechnicalStackList = new ArrayList<>();
        UserTechnicalStack userTechnicalStack1 = UserTechnicalStack.builder().name(technicalStackList.get(0)).userPosition(userPosition.get()).build();
        UserTechnicalStack userTechnicalStack2 = UserTechnicalStack.builder().name(technicalStackList.get(1)).userPosition(userPosition.get()).build();
        UserTechnicalStack userTechnicalStack3 = UserTechnicalStack.builder().name(technicalStackList.get(2)).userPosition(userPosition.get()).build();
        userTechnicalStackList.add(userTechnicalStack1);
        userTechnicalStackList.add(userTechnicalStack2);
        userTechnicalStackList.add(userTechnicalStack3);

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user.get(), user.get().getEmail(), user.get().getAuthorities()));

        String newName = "테스터2";
        String newSex = "W";
        String originPassword = "412151asdf";
        String newPassword = "231241d2";
        String newGithub ="https://github.com/ggggg";
        String newSelfIntroduction = "자기소개2222";
        Position newPosition = Position.BACKEND;
        List<String> newTechnicalStackList = new ArrayList<>();
        newTechnicalStackList.add("Querydsl");
        newTechnicalStackList.add("Spring Boot");

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

        List<UserTechnicalStack> newUserTechnicalStackList = new ArrayList<>();
        UserTechnicalStack newUserTechnicalStack1 = UserTechnicalStack.builder().name(newTechnicalStackList.get(0)).userPosition(userPosition.get()).build();
        UserTechnicalStack newUserTechnicalStack2 = UserTechnicalStack.builder().name(newTechnicalStackList.get(1)).userPosition(userPosition.get()).build();
        newUserTechnicalStackList.add(newUserTechnicalStack1);
        newUserTechnicalStackList.add(newUserTechnicalStack2);

        //when
        Exception e = Assertions.assertThrows(RuntimeException.class, () -> {
            userService.userUpdate(7L, dto);
        });

        //then
        assertThat(e.getMessage()).isEqualTo("Identification Check Fail");

    }

    @DisplayName("비밀번호 인증 실패")
    @Test
    public void userUpdateFai2() {
        //given
        Long no = 2L;
        String name = "테스터";
        String sex = "M";
        String email = "leeworld9@gmail.com";
        String password = passwordEncoder.encode("asdfqwef2351235");
        String github ="https://github.com/leeworld9";
        String selfIntroduction = "자기소개~~~";
        Position position = Position.BACKEND;
        List<String> technicalStackList = new ArrayList<>();
        technicalStackList.add("Spring Boot");
        technicalStackList.add("JPA");
        technicalStackList.add("React");

        Optional<UserPosition> userPosition = Optional.ofNullable(
                UserPosition.builder()
                        .no(3L)
                        .name(position.toString())
                        .build()
        );

        Optional<User> user = Optional.ofNullable(User.builder()
                .no(no)
                .name(name)
                .sex(sex.charAt(0))
                .email(email)
                .password(password)
                .github(github)
                .selfIntroduction(selfIntroduction)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .block(false)
                .userPosition(userPosition.get())
                .build()
        );

        List<UserTechnicalStack> userTechnicalStackList = new ArrayList<>();
        UserTechnicalStack userTechnicalStack1 = UserTechnicalStack.builder().name(technicalStackList.get(0)).userPosition(userPosition.get()).build();
        UserTechnicalStack userTechnicalStack2 = UserTechnicalStack.builder().name(technicalStackList.get(1)).userPosition(userPosition.get()).build();
        UserTechnicalStack userTechnicalStack3 = UserTechnicalStack.builder().name(technicalStackList.get(2)).userPosition(userPosition.get()).build();
        userTechnicalStackList.add(userTechnicalStack1);
        userTechnicalStackList.add(userTechnicalStack2);
        userTechnicalStackList.add(userTechnicalStack3);

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user.get(), user.get().getEmail(), user.get().getAuthorities()));

        String newName = "테스터2";
        String newSex = "W";
        String originPassword = "412151asdf";
        String newPassword = "231241d2";
        String newGithub ="https://github.com/ggggg";
        String newSelfIntroduction = "자기소개2222";
        Position newPosition = Position.BACKEND;
        List<String> newTechnicalStackList = new ArrayList<>();
        newTechnicalStackList.add("Querydsl");
        newTechnicalStackList.add("Spring Boot");

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

        List<UserTechnicalStack> newUserTechnicalStackList = new ArrayList<>();
        UserTechnicalStack newUserTechnicalStack1 = UserTechnicalStack.builder().name(newTechnicalStackList.get(0)).userPosition(userPosition.get()).build();
        UserTechnicalStack newUserTechnicalStack2 = UserTechnicalStack.builder().name(newTechnicalStackList.get(1)).userPosition(userPosition.get()).build();
        newUserTechnicalStackList.add(newUserTechnicalStack1);
        newUserTechnicalStackList.add(newUserTechnicalStack2);

        given(userRepository.findById(no)).willReturn(user);


        //when
        Exception e = Assertions.assertThrows(RuntimeException.class, () -> {
            userService.userUpdate(no, dto);
        });

        //then
        assertThat(e.getMessage()).isEqualTo("Original Password is Wrong");

    }

    @DisplayName("회원 정보 업데이트 성공")
    @Test
    public void userUpdateSuccess() {
        //given
        Long no = 2L;
        String name = "테스터";
        String sex = "M";
        String email = "leeworld9@gmail.com";
        String password = passwordEncoder.encode("asdfqwef2351235");
        String github ="https://github.com/leeworld9";
        String selfIntroduction = "자기소개~~~";
        Position position = Position.BACKEND;
        List<String> technicalStackList = new ArrayList<>();
        technicalStackList.add("Spring Boot");
        technicalStackList.add("JPA");
        technicalStackList.add("React");

        Optional<UserPosition> userPosition = Optional.ofNullable(
                UserPosition.builder()
                        .no(3L)
                        .name(position.toString())
                        .build()
        );

        Optional<User> user = Optional.ofNullable(User.builder()
                .no(no)
                .name(name)
                .sex(sex.charAt(0))
                .email(email)
                .password(password)
                .github(github)
                .selfIntroduction(selfIntroduction)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .block(false)
                .userPosition(userPosition.get())
                .build()
        );

        List<UserTechnicalStack> userTechnicalStackList = new ArrayList<>();
        UserTechnicalStack userTechnicalStack1 = UserTechnicalStack.builder().name(technicalStackList.get(0)).userPosition(userPosition.get()).build();
        UserTechnicalStack userTechnicalStack2 = UserTechnicalStack.builder().name(technicalStackList.get(1)).userPosition(userPosition.get()).build();
        UserTechnicalStack userTechnicalStack3 = UserTechnicalStack.builder().name(technicalStackList.get(2)).userPosition(userPosition.get()).build();
        userTechnicalStackList.add(userTechnicalStack1);
        userTechnicalStackList.add(userTechnicalStack2);
        userTechnicalStackList.add(userTechnicalStack3);

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user.get(), user.get().getEmail(), user.get().getAuthorities()));

        String newName = "테스터2";
        String newSex = "W";
        String originPassword = "asdfqwef2351235";
        String newPassword = "231241d2";
        String newGithub ="https://github.com/ggggg";
        String newSelfIntroduction = "자기소개2222";
        Position newPosition = Position.BACKEND;
        List<String> newTechnicalStackList = new ArrayList<>();
        newTechnicalStackList.add("Querydsl");
        newTechnicalStackList.add("Spring Boot");

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

        List<UserTechnicalStack> newUserTechnicalStackList = new ArrayList<>();
        UserTechnicalStack newUserTechnicalStack1 = UserTechnicalStack.builder().name(newTechnicalStackList.get(0)).userPosition(userPosition.get()).build();
        UserTechnicalStack newUserTechnicalStack2 = UserTechnicalStack.builder().name(newTechnicalStackList.get(1)).userPosition(userPosition.get()).build();
        newUserTechnicalStackList.add(newUserTechnicalStack1);
        newUserTechnicalStackList.add(newUserTechnicalStack2);

        given(userRepository.findById(no)).willReturn(user);
        given(userPositionRepository.findById(userPosition.get().getNo())).willReturn(userPosition);
        given(userTechnicalStackRepository.findAllByUserPosition(userPosition.get())).willReturn(userTechnicalStackList);

        //when
        User resultUser = userService.userUpdate(no, dto);

        //then
        assertThat(resultUser.getName()).isEqualTo(dto.getName());
        assertThat(resultUser.getSex()).isEqualTo(dto.getSex().charAt(0));
        assertThat(resultUser.getPassword()).isNotEqualTo(dto.getOriginPassword());
        assertThat(resultUser.getGithub()).isEqualTo(dto.getGithub());
        assertThat(resultUser.getSelfIntroduction()).isEqualTo(dto.getSelfIntroduction());
        assertThat(resultUser.getUserPosition().getName()).isEqualTo(dto.getPosition().toString());

        //verify
        verify(userTechnicalStackRepository, times(1)).deleteByUserPosition(userPosition.get());
        verify(userTechnicalStackRepository, times(2)).save(any());
    }

    @DisplayName("회원 리스트 조회")
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

    @DisplayName("회원 정보 조회 실패")
    @Test
    public void infoFail() {
        //given
        Long no = 1L;

        given(userRepository.findById(no)).willThrow(new RuntimeException("Not Find User No"));

        //when
        Exception e = Assertions.assertThrows(RuntimeException.class, () -> {
            userService.userInfo(no);
        });

        //then
        assertThat(e.getMessage()).isEqualTo("Not Find User No");
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
        Position position = Position.BACKEND;
        List<String> technicalStackList = new ArrayList<>();
        technicalStackList.add("Spring Boot");
        technicalStackList.add("JPA");
        technicalStackList.add("React");


        UserPosition userPosition = UserPosition.builder().no(2L).name(position.toString()).build();
        List<UserTechnicalStack> userTechnicalStackList = new ArrayList<>();
        UserTechnicalStack userTechnicalStack1 = UserTechnicalStack.builder().name(technicalStackList.get(0)).userPosition(userPosition).build();
        UserTechnicalStack userTechnicalStack2 = UserTechnicalStack.builder().name(technicalStackList.get(1)).userPosition(userPosition).build();
        UserTechnicalStack userTechnicalStack3 = UserTechnicalStack.builder().name(technicalStackList.get(2)).userPosition(userPosition).build();
        userTechnicalStackList.add(userTechnicalStack1);
        userTechnicalStackList.add(userTechnicalStack2);
        userTechnicalStackList.add(userTechnicalStack3);

        Optional<User> user = Optional.ofNullable(User.builder()
                .no(no)
                .name(name)
                .sex(sex.charAt(0))
                .email(email)
                .password(password)
                .github(github)
                .selfIntroduction(selfIntroduction)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .block(false)
                .userPosition(userPosition)
                .build()
        );
        given(userRepository.findById(no)).willReturn(user);
        given(userTechnicalStackRepository.findAllByUserPosition(userPosition)).willReturn(
                userTechnicalStackList);

        //when
        UserInfoResponseDto userInfo = userService.userInfo(no);

        //then
        assertThat(userInfo.getName()).isEqualTo(name);
        assertThat(userInfo.getSex()).isEqualTo(sex.charAt(0));
        assertThat(userInfo.getEmail()).isEqualTo(email);
        assertThat(userInfo.getGithub()).isEqualTo(github);
        assertThat(userInfo.getSelfIntroduction()).isEqualTo(selfIntroduction);
        assertThat(userInfo.getPosition()).isEqualTo(position.toString());
        assertThat(userInfo.getTechnicalStackList().get(0)).isEqualTo(userTechnicalStack1.getName());
        assertThat(userInfo.getTechnicalStackList().get(1)).isEqualTo(userTechnicalStack2.getName());
        assertThat(userInfo.getTechnicalStackList().get(2)).isEqualTo(userTechnicalStack3.getName());


    }

    @DisplayName("이메일 중복 가입 테스트")
    @Test
    public void signupFail1() {
        //given
        String name = "테스터";
        String sex = "M";
        String email = "leeworld9@gmail.com";
        String password = "asldkjfwlejkf";
        String github ="https://github.com/leeworld9";
        String selfIntroduction = "자기소개~~~";
        Position position = Position.BACKEND;

        SignUpRequestDto dto = SignUpRequestDto.builder()
                .name(name)
                .sex(sex)
                .email(email)
                .password(password)
                .github(github)
                .selfIntroduction(selfIntroduction)
                .position(position)
                .build();

        UserPosition userPosition = UserPosition.builder().name(dto.getPosition().toString()).build();
        Optional<User> user = Optional.of(User.builder()
                .name(name)
                .sex(sex.charAt(0))
                .email(email)
                .password(password)
                .github(github)
                .selfIntroduction(selfIntroduction)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .block(false)
                .userPosition(userPosition)
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

    @DisplayName("패스워드(필수 입력 값) 공백 에러 테스트")
    @Test
    public void signupFail2() {
        //given
        String name = "테스터";
        String sex = "M";
        String email = "leeworld9@gmail.com";
        String password = "";
        String github ="https://github.com/leeworld9";
        String selfIntroduction = "자기소개~~~";
        Position position = Position.BACKEND;

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

    @DisplayName("성별 값이 비정상적으로 들어왔을 경우 에러 테스트")
    @Test
    public void signupFail3() {
        //given
        String name = "테스터";
        String sex = "MMM";
        String email = "leeworld9@gmail.com";
        String password = "asldkjfwlejkf";
        String github ="https://github.com/leeworld9";
        String selfIntroduction = "자기소개~~~";
        Position position = Position.BACKEND;

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
        assertThat(e.getMessage()).isEqualTo("Sex value is blanked OR Invalid");
    }

    @DisplayName("정상 회원가입 성공")
    @Test
    public void signupSuccess() {
        //given
        String name = "테스터";
        String sex = "M";
        String email = "leeworld9@gmail.com";
        String password = "asldkjfwlejkf";
        String github ="https://github.com/leeworld9";
        String selfIntroduction = "자기소개~~~";
        Position position = Position.BACKEND;
        List<String> technicalStackList = new ArrayList<>();
        technicalStackList.add("Spring Boot");
        technicalStackList.add("JPA");
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

        UserPosition userPosition = UserPosition.builder().name(dto.getPosition().toString()).build();
        UserTechnicalStack userTechnicalStack1 = UserTechnicalStack.builder().name(dto.getTechnicalStackList().get(0)).userPosition(userPosition).build();
        UserTechnicalStack userTechnicalStack2 = UserTechnicalStack.builder().name(dto.getTechnicalStackList().get(1)).userPosition(userPosition).build();
        UserTechnicalStack userTechnicalStack3 = UserTechnicalStack.builder().name(dto.getTechnicalStackList().get(2)).userPosition(userPosition).build();
        User user = User.builder()
                .name(name)
                .sex(sex.charAt(0))
                .email(email)
                .password(password)
                .github(github)
                .selfIntroduction(selfIntroduction)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .block(false)
                .userPosition(userPosition)
                .build();

        given(userRepository.save(any(User.class))).willReturn(user);

        //when
        User wUser = userService.userSignUp(dto);

        //then
        assertThat(wUser.getName()).isEqualTo(dto.getName());
        assertThat(wUser.getSex()).isEqualTo(dto.getSex().charAt(0));
        assertThat(wUser.getEmail()).isEqualTo(dto.getEmail());
        assertThat(wUser.getGithub()).isEqualTo(dto.getGithub());
        assertThat(wUser.getSelfIntroduction()).isEqualTo(dto.getSelfIntroduction());
        assertThat(wUser.getUserPosition().getName()).isEqualTo(dto.getPosition().toString());

        //verify
        verify(userPositionRepository, times(1)).save(any(UserPosition.class));
        verify(userTechnicalStackRepository, times(technicalStackList.size())).save(any(UserTechnicalStack.class));
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(any(String.class));

    }
}