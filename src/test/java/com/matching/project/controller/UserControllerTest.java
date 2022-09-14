package com.matching.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matching.project.config.EmbeddedRedisConfig;
import com.matching.project.dto.token.TokenClaimsDto;
import com.matching.project.dto.enumerate.EmailAuthPurpose;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.user.*;
import com.matching.project.entity.*;
import com.matching.project.repository.*;
import com.matching.project.service.EmailService;
import com.matching.project.service.ImageService;
import com.matching.project.service.JwtTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = EmbeddedRedisConfig.class)
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenService jwtTokenService;

    @Autowired
    TechnicalStackRepository technicalStackRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    ImageService imageService;;

    @Autowired
    EmailService emailService;

    @Autowired
    UserTechnicalStackRepository userTechnicalStackRepository;

    User saveUser() {
        User user = User.builder()
                .name("testUser")
                .sex("M")
                .email("leeworld9@naver.com")
                .password(passwordEncoder.encode("test"))
                .github(null)
                .selfIntroduction(null)
                .block(false)
                .blockReason(null)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(false)
                .imageNo(null)
                .position(null)
                .build();

        return userRepository.save(user);
    }

    User saveUser(String email) {
        User user = User.builder()
                .name("testUser")
                .sex("M")
                .email(email)
                .password(passwordEncoder.encode("test"))
                .github(null)
                .selfIntroduction(null)
                .block(false)
                .blockReason(null)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(false)
                .imageNo(null)
                .position(null)
                .build();

        return userRepository.save(user);
    }

    User saveAdmin() {
        User user = User.builder()
                .name("testUser")
                .sex("M")
                .email("leeworld9@gmail.com")
                .password(passwordEncoder.encode("test"))
                .github(null)
                .selfIntroduction(null)
                .block(false)
                .blockReason(null)
                .permission(Role.ROLE_ADMIN)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(false)
                .imageNo(null)
                .position(null)
                .build();

        return userRepository.save(user);
    }

    String getToken(User user) {
        return jwtTokenService.createToken(TokenClaimsDto.builder().email(user.getEmail()).build()).getAccess();
    }

    void savePosition() {
        positionRepository.save(Position.builder().name("FRONTEND").build());
        positionRepository.save(Position.builder().name("BACKEND").build());
        positionRepository.save(Position.builder().name("FULLSTACK").build());
    }

    void saveTechnicalStack() {
        Image image1 = imageRepository.save(Image.builder().logicalName("SpringTest1l").physicalName("SpringTest1p").url("SpringTest1url").build());
        Image image2 = imageRepository.save(Image.builder().logicalName("ReactTest2l").physicalName("ReactTest2p").url("ReactTest2url").build());
        Image image3 = imageRepository.save(Image.builder().logicalName("JPATest3l").physicalName("JPATest3p").url("JPATest3url").build());

        technicalStackRepository.save(TechnicalStack.builder().name("Spring").imageNo(image1.getNo()).build());
        technicalStackRepository.save(TechnicalStack.builder().name("React").imageNo(image2.getNo()).build());
        technicalStackRepository.save(TechnicalStack.builder().name("JPA").imageNo(image3.getNo()).build());
    }

    @Nested
    @DisplayName("회원 가입")
    class signUp {
        @DisplayName("성공")
        @Test
        void success() throws Exception {
            //given
            String email = "leeworld9@naver.com";
            String password = "asdfwg2h2efzsd";
            String name = "testUser";

            SignUpRequestDto requestDto = SignUpRequestDto.builder()
                    .email(email)
                    .password(password)
                    .name(name)
                    .build();

            //when
            mvc.perform(post("/v1/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(requestDto))
                    )
                    .andDo(print())
                    .andExpect(status().isOk());

            //then
            List<User> all = userRepository.findAll();
            assertThat(all.get(0).getEmail()).isEqualTo(email);
            assertThat(passwordEncoder.matches(password, all.get(0).getPassword())).isTrue();
            assertThat(all.get(0).getName()).isEqualTo(name);
        }

        /*
        @DisplayName("실패 : 이메일 중복")
        @Test
        void fail() throws Exception {
            //given
            saveUser();

            String email = "leeworld9@naver.com";
            String password = "asdfwg2h2efzsd";
            String name = "testuser";

            SignUpRequestDto requestDto = SignUpRequestDto.builder()
                    .email(email)
                    .password(password)
                    .name(name)
                    .build();

            //when
            ResultActions resultActions = mvc.perform(post("/v1/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(requestDto))
                    );

            //then
            resultActions.andDo(print())
                    .andExpect(status().is5xxServerError())
                    .andExpect(jsonPath("$.error.code").value("DUPLICATE_EMAIL_EXCEPTION"));
        }
         */
    }

    @Nested
    @DisplayName("유저 정보")
    class userInfo {

        @DisplayName("성공")
        @Test
        void success() throws Exception {
            //given
            User user = saveUser();
            String token = getToken(user);

            //when
            ResultActions resultActions = mvc.perform(get("/v1/user/info")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token));


            //then
            resultActions.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.role").value(Role.ROLE_USER.toString()))
                    .andExpect(jsonPath("$.data.name").value("testUser"))
                    .andExpect(jsonPath("$.data.email").value("leeworld9@naver.com"));
                    // 나머지 생략

        }

        @DisplayName("실패 : 로그인 하지 않은 경우")
        @Test
        void fail() throws Exception {
            //given

            //when
            ResultActions resultActions = mvc.perform(get("/v1/user/info"));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("이메일 인증")
    class confirmEmail {

        @DisplayName("성공")
        @Test
        void success() throws Exception {
            //given
            User user = saveUser();
            EmailAuth emailAuth = emailService.emailAuthTokenSave(user.getEmail(), EmailAuthPurpose.EMAIL_AUTHENTICATION);

            EmailAuthRequestDto requestDto = EmailAuthRequestDto.builder()
                    .authToken(emailAuth.getAuthToken())
                    .email(user.getEmail())
                    .build();

            //when
            ResultActions resultActions = mvc.perform(post("/v1/user/confirm")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(requestDto))
                    );

            //then
            resultActions.andDo(print())
                    .andExpect(status().isOk());
            assertThat(userRepository.findByEmail(user.getEmail()).get().isEmail_auth()).isTrue();
        }
    }

    @Nested
    @DisplayName("내 프로필 조회")
    class userProfile {
        @DisplayName("성공")
        @Test
        void success() throws Exception {
            //given
            User user = saveUser();
            String token = getToken(user);

            //when
            ResultActions resultActions = mvc.perform(get("/v1/user").contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token));


            //then
            resultActions.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.name").value("testUser"))
                    .andExpect(jsonPath("$.data.sex").value("M"))
                    .andExpect(jsonPath("$.data.email").value("leeworld9@naver.com"));
                    // 나머지 생략
        }

        @DisplayName("실패 : 로그인 하지 않은 경우")
        @Test
        void fail() throws Exception {
            //given

            //when
            ResultActions resultActions = mvc.perform(get("/v1/user"));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("비밀번호 변경")
    class userPasswordUpdate {

        @DisplayName("성공")
        @Test
        void success() throws Exception {
            //given
            User user = saveUser();
            String token = getToken(user);

            PasswordUpdateRequestDto requestDto = PasswordUpdateRequestDto.builder()
                    .oldPassword("test")
                    .newPassword("test2")
                    .build();

            //when
            ResultActions resultActions = mvc.perform(patch("/v1/user/password")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(requestDto))
            );

            //then
            resultActions.andDo(print())
                    .andExpect(status().isOk());
            assertThat(passwordEncoder.matches("test2",
                    userRepository.findByEmail(user.getEmail()).get().getPassword())).isTrue();
        }

        @DisplayName("실패 : 로그인 하지 않은 경우")
        @Test
        void fail() throws Exception {
            //given

            //when
            ResultActions resultActions = mvc.perform(patch("/v1/user/password"));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("개인정보 변경")
    class userProfileUpdate {

        @DisplayName("성공")
        @Test
        void success() throws Exception {
            User user = saveUser();
            savePosition();
            saveTechnicalStack();
            String token = getToken(user);

            String nName = "updateTest";
            String nSex = "W";
            String nPosition = "BACKEND";
            String nTechnicalStackList = "Spring, React, JPA";
            String nGithub = "updateGithub";
            String nSelfIntroduction = "updateSelfIntroduction";

            MockMultipartFile image = new MockMultipartFile("image", "image.jpeg", MediaType.IMAGE_JPEG_VALUE, "".getBytes(StandardCharsets.UTF_8));

            //when
            ResultActions resultActions = mvc.perform(multipart("/v1/user")
                            .file(image)
                            .param("name", nName)
                            .param("sex", nSex)
                            .param("position", nPosition)
                            .param("technicalStackList", nTechnicalStackList.toString())
                            .param("github", nGithub)
                            .param("selfIntroduction", nSelfIntroduction)
                    .with(requestProcessor -> {
                        requestProcessor.setMethod("PATCH");
                        return requestProcessor;
                    })
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
            );

            //then
            resultActions.andDo(print())
                    .andExpect(status().isOk());
            User resUser = userRepository.findByEmail(user.getEmail()).get();
            assertThat(resUser.getName()).isEqualTo(nName);
            assertThat(resUser.getSex()).isEqualTo(nSex);
            assertThat(resUser.getPosition().getName()).isEqualTo(nPosition);
            assertThat(resUser.getGithub()).isEqualTo(nGithub);
            assertThat(resUser.getSelfIntroduction()).isEqualTo(nSelfIntroduction);
            Optional<List<UserTechnicalStack>> ust = userTechnicalStackRepository.findUserTechnicalStacksByUser(resUser.getNo());
            assertThat(ust.get().size()).isEqualTo(3);
        }

        @DisplayName("실패")
        @Test
        void fail() throws Exception {
            //given

            //when
            ResultActions resultActions = mvc.perform(patch("/v1/user"));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("회원 탈퇴")
    class userSingOut {

        @DisplayName("성공")
        @Test
        void success() throws Exception {
            //given
            User user = saveUser();
            String token = getToken(user);

            //when
            ResultActions resultActions = mvc.perform(delete("/v1/user")
                    .header("Authorization", "Bearer " + token));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isOk());
            User resUser = userRepository.findByEmail(user.getEmail()).get();
            assertThat(resUser.isWithdrawal()).isTrue();
        }

        @DisplayName("실패 : 로그인 하지 않는 경우")
        @Test
        void fail() throws Exception {
            //given

            //when
            ResultActions resultActions = mvc.perform(delete("/v1/user"));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("회원 목록 조회 (관리자)")
    class userInfoList {

        @DisplayName("성공")
        @Test
        void success() throws Exception {
            //given
            User user1 = saveUser("test1@user.com");
            User user2 = saveUser("test2@user.com");
            User user3 = saveUser("test3@user.com");
            User user4 = saveUser("test4@user.com");
            User admin = saveAdmin();
            String token = getToken(admin);

            //when
            ResultActions resultActions = mvc.perform(get("/v1/user/list?userFilter=EMAIL&content=test&size=3")
                            .header("Authorization", "Bearer " + token));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content.[0].userNo").value(user4.getNo()))
                    .andExpect(jsonPath("$.data.content.[0].image").value(imageService.getImageUrl(user4.getImageNo())))
                    .andExpect(jsonPath("$.data.content.[0].name").value(user4.getName()))
                    .andExpect(jsonPath("$.data.content.[0].email").value(user4.getEmail()))
                    .andExpect(jsonPath("$.data.content.[0].block").value(user4.isBlock()))
                    .andExpect(jsonPath("$.data.content.[1].userNo").value(user3.getNo()))
                    .andExpect(jsonPath("$.data.content.[1].image").value(imageService.getImageUrl(user3.getImageNo())))
                    .andExpect(jsonPath("$.data.content.[1].name").value(user3.getName()))
                    .andExpect(jsonPath("$.data.content.[1].email").value(user3.getEmail()))
                    .andExpect(jsonPath("$.data.content.[1].block").value(user3.isBlock()))
                    .andExpect(jsonPath("$.data.content.[2].userNo").value(user2.getNo()))
                    .andExpect(jsonPath("$.data.content.[2].image").value(imageService.getImageUrl(user2.getImageNo())))
                    .andExpect(jsonPath("$.data.content.[2].name").value(user2.getName()))
                    .andExpect(jsonPath("$.data.content.[2].email").value(user2.getEmail()))
                    .andExpect(jsonPath("$.data.content.[2].block").value(user2.isBlock()))
                    .andExpect(jsonPath("$.data.last").value(false));
        }

        @DisplayName("실패 : 로그인 하지 않는 경우")
        @Test
        void fail1() throws Exception {
            //given

            //when
            ResultActions resultActions = mvc.perform(get("/v1/user/list"));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @DisplayName("실패 : 접근 권한이 없는 경우")
        @Test
        void fail2() throws Exception {
            //given
            User user = saveUser();
            String token = getToken(user);

            //when
            ResultActions resultActions = mvc.perform(get("/v1/user/list")
                    .header("Authorization", "Bearer " + token));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("회원 차단 (관리자)")
    class userBlock {

        @DisplayName("성공")
        @Test
        void success() throws Exception {
            //given
            User user = saveAdmin();
            String token = getToken(user);

            UserBlockRequestDto requestDto = UserBlockRequestDto.builder()
                    .blockReason("test")
                    .build();

            //when
            ResultActions resultActions = mvc.perform(patch("/v1/user/block/" + user.getNo())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(requestDto)));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isOk());
            User resUser = userRepository.findByEmail(user.getEmail()).get();
            assertThat(resUser.isBlock()).isTrue();
            assertThat(resUser.getBlockReason()).isEqualTo(requestDto.getBlockReason());

        }

        @DisplayName("실패 : 로그인 하지 않는 경우")
        @Test
        void fail1() throws Exception {
            //given
            User user = saveUser();

            //when
            ResultActions resultActions = mvc.perform(patch("/v1/user/block/" + user.getNo()));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isUnauthorized());

        }

        @DisplayName("실패 : 접근 권한이 없는 경우")
        @Test
        void fail2() throws Exception {
            //given
            User user = saveUser();
            String token = getToken(user);

            //when
            ResultActions resultActions = mvc.perform(patch("/v1/user/block/" + user.getNo())
                    .header("Authorization", "Bearer " + token));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isForbidden());

        }
    }

    @Nested
    @DisplayName("회원 차단 해제 (관리자)")
    class userUnBlock {

        @DisplayName("성공")
        @Test
        void success() throws Exception {
            //given
            User user = saveUser();
            User admin = saveAdmin();
            String token = getToken(admin);

            // User Block
            user.userBlock("test");

            //when
            ResultActions resultActions = mvc.perform(patch("/v1/user/unblock/" + user.getNo())
                    .header("Authorization", "Bearer " + token));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isOk());
            User resUser = userRepository.findByEmail(user.getEmail()).get();
            assertThat(resUser.isBlock()).isFalse();
            assertThat(resUser.getBlockReason()).isNull();
        }

        @DisplayName("실패 : 로그인 하지 않는 경우")
        @Test
        void fail1() throws Exception {
            //given
            User user = saveUser();

            //when
            ResultActions resultActions = mvc.perform(patch("/v1/user/unblock/" + user.getNo()));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isUnauthorized());

        }

        @DisplayName("실패 : 접근 권한이 없는 경우")
        @Test
        void fail2() throws Exception {
            //given
            User user = saveUser();
            String token = getToken(user);

            //when
            ResultActions resultActions = mvc.perform(patch("/v1/user/unblock/" + user.getNo())
                      .header("Authorization", "Bearer " + token));

            //then
            resultActions.andDo(print())
                    .andExpect(status().isForbidden());
        }
    }
}