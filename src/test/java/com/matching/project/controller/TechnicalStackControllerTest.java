package com.matching.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matching.project.dto.common.TokenDto;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.technicalstack.TechnicalStackRegisterRequestDto;
import com.matching.project.dto.technicalstack.TechnicalStackUpdateRequestDto;
import com.matching.project.entity.Image;
import com.matching.project.entity.TechnicalStack;
import com.matching.project.entity.User;
import com.matching.project.repository.ImageRepository;
import com.matching.project.repository.TechnicalStackRepository;
import com.matching.project.repository.UserRepository;
import com.matching.project.service.JwtTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TechnicalStackControllerTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    TechnicalStackRepository technicalStackRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtTokenService jwtTokenService;

    @Nested
    @DisplayName("기술스택 추가 폼")
    class technicalStackRegisterForm {
        @Test
        @DisplayName("성공")
        public void success() throws Exception {
            // given
            // 어드민 세팅
            User adminUser1 = User.builder()
                    .name("userName1")
                    .sex("M")
                    .email("wkemrm12@naver.com")
                    .password("testPassword")
                    .github("testGithub")
                    .selfIntroduction("testSelfIntroduction")
                    .block(false)
                    .blockReason(null)
                    .permission(Role.ROLE_ADMIN)
                    .oauthCategory(OAuth.NORMAL)
                    .email_auth(false)
                    .imageNo(0L)
                    .position(null)
                    .build();
            User saveAdminUser1 = userRepository.save(adminUser1);

            // 이미지 세팅
            Image image1 = Image.builder()
                    .logicalName("testLogicalName1")
                    .physicalName("testPhysicalName1")
                    .url("testUrl1")
                    .build();
            Image image2 = Image.builder()
                    .logicalName("testLogicalName2")
                    .physicalName("testPhysicalName2")
                    .url("testUrl2")
                    .build();
            Image image3 = Image.builder()
                    .logicalName("testLogicalName3")
                    .physicalName("testPhysicalName3")
                    .url("testUrl3")
                    .build();
            Image saveImage1 = imageRepository.save(image1);
            Image saveImage2 = imageRepository.save(image2);
            Image saveImage3 = imageRepository.save(image3);

            // 기술스택 세팅
            TechnicalStack technicalStack1 = TechnicalStack.builder()
                    .name("testTechnicalStack1")
                    .imageNo(saveImage1.getNo())
                    .build();
            TechnicalStack technicalStack2 = TechnicalStack.builder()
                    .name("testTechnicalStack2")
                    .imageNo(saveImage2.getNo())
                    .build();
            TechnicalStack technicalStack3 = TechnicalStack.builder()
                    .name("testTechnicalStack3")
                    .imageNo(saveImage3.getNo())
                    .build();
            TechnicalStack saveTechnicalStack1 = technicalStackRepository.save(technicalStack1);
            TechnicalStack saveTechnicalStack2 = technicalStackRepository.save(technicalStack2);
            TechnicalStack saveTechnicalStack3 = technicalStackRepository.save(technicalStack3);

            // when
            String token = jwtTokenService.createToken(new TokenDto(saveAdminUser1.getEmail()));

            ResultActions resultActions = mvc.perform(get("/v1/technicalStack").contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token));

            // then
            resultActions
                    .andDo(print())
                    .andExpect(header().string("Content-type", "application/json"))
                    .andExpect(jsonPath("$.data[0].technicalStackNo").value(saveTechnicalStack1.getNo()))
                    .andExpect(jsonPath("$.data[0].technicalStackName").value(saveTechnicalStack1.getName()))
                    .andExpect(jsonPath("$.data[0].image").value(saveImage1.getUrl()))
                    .andExpect(jsonPath("$.data[1].technicalStackNo").value(saveTechnicalStack2.getNo()))
                    .andExpect(jsonPath("$.data[1].technicalStackName").value(saveTechnicalStack2.getName()))
                    .andExpect(jsonPath("$.data[1].image").value(saveImage2.getUrl()))
                    .andExpect(jsonPath("$.data[2].technicalStackNo").value(saveTechnicalStack3.getNo()))
                    .andExpect(jsonPath("$.data[2].technicalStackName").value(saveTechnicalStack3.getName()))
                    .andExpect(jsonPath("$.data[2].image").value(saveImage3.getUrl()))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("실패 : 권한이 유저인 경우")
        public void fail1() throws Exception {
            // given
            User user1 = User.builder()
                    .name("userName1")
                    .sex("M")
                    .email("wkemrm12@naver.com")
                    .password("testPassword")
                    .github("testGithub")
                    .selfIntroduction("testSelfIntroduction")
                    .block(false)
                    .blockReason(null)
                    .permission(Role.ROLE_USER)
                    .oauthCategory(OAuth.NORMAL)
                    .email_auth(false)
                    .imageNo(0L)
                    .position(null)
                    .build();
            User saveUser1 = userRepository.save(user1);

            // when
            String token = jwtTokenService.createToken(new TokenDto(saveUser1.getEmail()));

            ResultActions resultActions = mvc.perform(get("/v1/technicalStack").contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token));

            // then
            resultActions
                    .andDo(print())
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("실패 : 비로그인 유저")
        public void fail2() throws Exception {
            // when
            ResultActions resultActions = mvc.perform(get("/v1/technicalStack").contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("기술스택 추가")
    class technicalStackRegister {
        @Test
        @DisplayName("성공 : 이미지가 null일 경우")
        public void success() throws Exception {
            // given
            // 어드민 세팅
            User adminUser1 = User.builder()
                    .name("userName1")
                    .sex("M")
                    .email("wkemrm12@naver.com")
                    .password("testPassword")
                    .github("testGithub")
                    .selfIntroduction("testSelfIntroduction")
                    .block(false)
                    .blockReason(null)
                    .permission(Role.ROLE_ADMIN)
                    .oauthCategory(OAuth.NORMAL)
                    .email_auth(false)
                    .imageNo(0L)
                    .position(null)
                    .build();
            User saveAdminUser1 = userRepository.save(adminUser1);

            // when
            TechnicalStackRegisterRequestDto technicalStackRegisterDto = new TechnicalStackRegisterRequestDto("technicalStackName");
            MockMultipartFile image = new MockMultipartFile("image", "image.jpeg", MediaType.IMAGE_JPEG_VALUE, "".getBytes(StandardCharsets.UTF_8));
            MockMultipartFile data = new MockMultipartFile("data", "", MediaType.APPLICATION_JSON_VALUE, new ObjectMapper().writeValueAsString(technicalStackRegisterDto).getBytes());

            String token = jwtTokenService.createToken(new TokenDto(saveAdminUser1.getEmail()));

            ResultActions resultActions = mvc.perform(multipart("/v1/technicalStack")
                    .file(image)
                    .file(data)
                    .with(requestProcessor -> {
                        requestProcessor.setMethod("POST");
                        return requestProcessor;
                    })
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
            );

            // then
            resultActions
                    .andDo(print())
                    .andExpect(jsonPath("$.data").value(true))
                    .andExpect(status().isOk());

            List<TechnicalStack> technicalStackList = technicalStackRepository.findAll();

            assertEquals(technicalStackList.get(0).getName(), technicalStackRegisterDto.getTechnicalStackName());
            assertEquals(technicalStackList.get(0).getImageNo(), null);
        }

        @Test
        @DisplayName("실패 : 비로그인 유저")
        public void fail1() throws Exception {
            // when
            TechnicalStackRegisterRequestDto technicalStackRegisterDto = new TechnicalStackRegisterRequestDto("technicalStackName");
            MockMultipartFile image = new MockMultipartFile("image", "image.jpeg", MediaType.IMAGE_JPEG_VALUE, "".getBytes(StandardCharsets.UTF_8));
            MockMultipartFile data = new MockMultipartFile("data", "", MediaType.APPLICATION_JSON_VALUE, new ObjectMapper().writeValueAsString(technicalStackRegisterDto).getBytes());

            ResultActions resultActions = mvc.perform(multipart("/v1/technicalStack")
                    .file(image)
                    .file(data)
                    .with(requestProcessor -> {
                        requestProcessor.setMethod("POST");
                        return requestProcessor;
                    })
                    .contentType(MediaType.MULTIPART_FORM_DATA)
            );

            // then
            resultActions
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("실패 : 어드민이 아닌 경우")
        public void fail2() throws Exception {
            // given
            // 유저 세팅
            User user1 = User.builder()
                    .name("userName1")
                    .sex("M")
                    .email("wkemrm12@naver.com")
                    .password("testPassword")
                    .github("testGithub")
                    .selfIntroduction("testSelfIntroduction")
                    .block(false)
                    .blockReason(null)
                    .permission(Role.ROLE_USER)
                    .oauthCategory(OAuth.NORMAL)
                    .email_auth(false)
                    .imageNo(0L)
                    .position(null)
                    .build();
            User saveUser1 = userRepository.save(user1);

            // when
            TechnicalStackRegisterRequestDto technicalStackRegisterDto = new TechnicalStackRegisterRequestDto("technicalStackName");
            MockMultipartFile image = new MockMultipartFile("image", "image.jpeg", MediaType.IMAGE_JPEG_VALUE, "".getBytes(StandardCharsets.UTF_8));
            MockMultipartFile data = new MockMultipartFile("data", "", MediaType.APPLICATION_JSON_VALUE, new ObjectMapper().writeValueAsString(technicalStackRegisterDto).getBytes());

            String token = jwtTokenService.createToken(new TokenDto(saveUser1.getEmail()));

            ResultActions resultActions = mvc.perform(multipart("/v1/technicalStack")
                    .file(image)
                    .file(data)
                    .with(requestProcessor -> {
                        requestProcessor.setMethod("POST");
                        return requestProcessor;
                    })
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
            );

            // then
            resultActions
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("실패 : 기술 스택 이름이 null일 경우")
        public void fail3() throws Exception {
            // given
            // 어드민 세팅
            User adminUser1 = User.builder()
                    .name("userName1")
                    .sex("M")
                    .email("wkemrm12@naver.com")
                    .password("testPassword")
                    .github("testGithub")
                    .selfIntroduction("testSelfIntroduction")
                    .block(false)
                    .blockReason(null)
                    .permission(Role.ROLE_ADMIN)
                    .oauthCategory(OAuth.NORMAL)
                    .email_auth(false)
                    .imageNo(0L)
                    .position(null)
                    .build();
            User saveAdminUser1 = userRepository.save(adminUser1);

            // when
            TechnicalStackRegisterRequestDto technicalStackRegisterDto = new TechnicalStackRegisterRequestDto(null);
            MockMultipartFile image = new MockMultipartFile("image", "image.jpeg", MediaType.IMAGE_JPEG_VALUE, "".getBytes(StandardCharsets.UTF_8));
            MockMultipartFile data = new MockMultipartFile("data", "", MediaType.APPLICATION_JSON_VALUE, new ObjectMapper().writeValueAsString(technicalStackRegisterDto).getBytes());

            String token = jwtTokenService.createToken(new TokenDto(saveAdminUser1.getEmail()));

            ResultActions resultActions = mvc.perform(multipart("/v1/technicalStack")
                    .file(image)
                    .file(data)
                    .with(requestProcessor -> {
                        requestProcessor.setMethod("POST");
                        return requestProcessor;
                    })
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
            );

            // then
            resultActions
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }
}