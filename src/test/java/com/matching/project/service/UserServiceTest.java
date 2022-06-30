package com.matching.project.service;

import com.matching.project.dto.common.NormalLoginRequestDto;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Position;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.user.SignUpRequestDto;
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

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

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