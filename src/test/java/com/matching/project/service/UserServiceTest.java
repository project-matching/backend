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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPositionRepository userPositionRepository;

    @Autowired
    private UserTechnicalStackRepository userTechnicalStackRepository;

    @AfterEach
    public void tearDown(){
        userRepository.deleteAll();
    }

    @DisplayName("패스워드(필수 입력 값) 공백 에러 테스트")
    @Test
    public void SignUpTestFail1()
    {
        String name = "테스터";
        String sex = "M";
        String email = "leeworld9@gmail.com";
        String password = "";
        String github ="https://github.com/leeworld9";
        String selfIntroduction = "자기소개~~~";
        Position position = Position.BACKEND;
        List<String> technicalStackList = new ArrayList<>();
        technicalStackList.add("Spring Boot");
        technicalStackList.add("JPA");
        technicalStackList.add("React");

        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .name(name)
                .sex(sex)
                .email(email)
                .password(password)
                .github(github)
                .selfIntroduction(selfIntroduction)
                .position(position)
                .technicalStackList(technicalStackList)
                .build();

        Assertions.assertThrows(RuntimeException.class, () -> {
            userService.userSignUp(signUpRequestDto);
        });
    }

    @DisplayName("이메일 중복 가입 에러 테스트")
    @Test
    public void SignUpTestFail2()
    {
        String name = "테스터";
        String sex = "M";
        String email = "leeworld9@gmail.com";
        String password = "rkdjslfwkj";
        String github ="https://github.com/leeworld9";
        String selfIntroduction = "자기소개~~~";
        Position position = Position.BACKEND;
        List<String> technicalStackList = new ArrayList<>();
        technicalStackList.add("Spring Boot");
        technicalStackList.add("JPA");
        technicalStackList.add("React");

        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .name(name)
                .sex(sex)
                .email(email)
                .password(password)
                .github(github)
                .selfIntroduction(selfIntroduction)
                .position(position)
                .technicalStackList(technicalStackList)
                .build();

        userService.userSignUp(signUpRequestDto);

        Assertions.assertThrows(RuntimeException.class, () -> {
            userService.userSignUp(signUpRequestDto);
        });
    }

    @DisplayName("성별 값이 비정상적으로 들어왔을 경우 에러 테스트")
    @Test
    public void SignUpTestFail3()
    {
        String name = "테스터";
        String sex = "MMM";
        String email = "leeworld9@gmail.com";
        String password = "rkdjslfwkj";
        String github ="https://github.com/leeworld9";
        String selfIntroduction = "자기소개~~~";
        Position position = Position.BACKEND;
        List<String> technicalStackList = new ArrayList<>();
        technicalStackList.add("Spring Boot");
        technicalStackList.add("JPA");
        technicalStackList.add("React");

        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .name(name)
                .sex(sex)
                .email(email)
                .password(password)
                .github(github)
                .selfIntroduction(selfIntroduction)
                .position(position)
                .technicalStackList(technicalStackList)
                .build();

        Assertions.assertThrows(RuntimeException.class, () -> {
            userService.userSignUp(signUpRequestDto);
        });
    }

    @DisplayName("정상적인 가입 테스트")
    @Test
    public void SignUpTestSuccess()
    {
        String name = "테스터";
        String sex = "M";
        String email = "leeworld9@gmail.com";
        String password = "rkdjslfwkj";
        String github ="https://github.com/leeworld9";
        String selfIntroduction = "자기소개~~~";
        Position position = Position.BACKEND;
        List<String> technicalStackList = new ArrayList<>();
        technicalStackList.add("Spring Boot");
        technicalStackList.add("JPA");
        technicalStackList.add("React");

        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .name(name)
                .sex(sex)
                .email(email)
                .password(password)
                .github(github)
                .selfIntroduction(selfIntroduction)
                .position(position)
                .technicalStackList(technicalStackList)
                .build();
        userService.userSignUp(signUpRequestDto);

        List<User> all = userRepository.findAll();
        assertThat(all.get(0).getName()).isEqualTo(name);
        assertThat(all.get(0).getEmail()).isEqualTo(email);
        assertThat(all.get(0).getGithub()).isEqualTo(github);
        assertThat(all.get(0).getSelfIntroduction()).isEqualTo(selfIntroduction);

        List<UserPosition> pos = userPositionRepository.findAll();
        assertThat(pos.get(0).getName()).isEqualTo(position.name());

        List<UserTechnicalStack> st = userTechnicalStackRepository.findAll();
        for (int i = 0 ; i < technicalStackList.size() ; i++)
            assertThat(st.get(i).getName()).isEqualTo(technicalStackList.get(i));
    }
}