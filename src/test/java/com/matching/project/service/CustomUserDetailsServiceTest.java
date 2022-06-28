package com.matching.project.service;

import com.matching.project.dto.common.NormalLoginRequestDto;
import com.matching.project.dto.enumerate.Position;
import com.matching.project.dto.user.SignUpRequestDto;
import com.matching.project.repository.UserPositionRepository;
import com.matching.project.repository.UserRepository;
import com.matching.project.repository.UserTechnicalStackRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CustomUserDetailsServiceTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        String name = "테스터";
        String sex = "M";
        String email = "leeworld9@gmail.com";
        String password = "testuser";
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

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @DisplayName("로그인 성공")
    @WithUserDetails(value = "leeworld9@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void loginSuccess() {
        //given
        NormalLoginRequestDto dto = NormalLoginRequestDto.builder()
                .email("leeworld9@gmail.com")
                .password("testuser")
                .build();

        String url = "http://localhost:" + port + "/v1/common/login";
        HttpEntity<?> requestEntity = new HttpEntity<>(dto);

        //when
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);

        //then
        assertThat(responseEntity.getBody()).isEqualTo("Authentication Success");
    }

    @DisplayName("로그인 실패")
    @WithUserDetails(value = "leeworld9@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void loginFail() {
        //given
        NormalLoginRequestDto dto = NormalLoginRequestDto.builder()
                .email("leeworld9@gmail.com")
                .password("fsddlkfj")
                .build();

        String url = "http://localhost:" + port + "/v1/common/login";
        HttpEntity<?> requestEntity = new HttpEntity<>(dto);

        //when
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);

        //then
        assertThat(responseEntity.getBody()).isEqualTo("Authentication Failure");
    }


    //@WithAnonymousUser
    //익명 사용자 관련 테스트도 추가 필요

}