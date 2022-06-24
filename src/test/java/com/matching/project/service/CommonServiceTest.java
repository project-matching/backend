package com.matching.project.service;

import com.matching.project.dto.common.NormalLoginRequestDto;
import com.matching.project.dto.enumerate.Position;
import com.matching.project.dto.user.SignUpRequestDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CommonServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    CommonService commonService;

    SignUpRequestDto signUpRequestDto;

    @BeforeAll
    public void singUp()
    {
        String name = "테스터";
        char sex = 'M';
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
    }


    @Test
    public void NormalLoginTestFail()
    {
        boolean res = commonService.UserNormalLogin(NormalLoginRequestDto.builder()
                .email(signUpRequestDto.getEmail())
                .password("gggg")
                .build());
        assertThat(res).isEqualTo(false);
    }

    @Test
    public void NormalLoginTestSuccess()
    {
        boolean res = commonService.UserNormalLogin(NormalLoginRequestDto.builder()
                .email(signUpRequestDto.getEmail())
                .password(signUpRequestDto.getPassword())
                .build());
        assertThat(res).isEqualTo(true);
    }
}