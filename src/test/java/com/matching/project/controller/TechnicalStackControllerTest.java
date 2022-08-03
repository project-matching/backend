package com.matching.project.controller;

import com.matching.project.dto.common.TokenDto;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.entity.Image;
import com.matching.project.entity.TechnicalStack;
import com.matching.project.entity.User;
import com.matching.project.repository.ImageRepository;
import com.matching.project.repository.TechnicalStackRepository;
import com.matching.project.repository.UserRepository;
import com.matching.project.service.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Test
    public void 기술스택_등록_폼_조회_테스트() throws Exception {
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

        // then
        String token = jwtTokenService.createToken(new TokenDto(saveAdminUser1.getEmail()));

        mvc.perform(get("/v1/technicalStack").contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
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
    public void 기술스택_등록_폼_조회_인가_테스트() throws Exception {
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
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(false)
                .imageNo(0L)
                .position(null)
                .build();
        User saveAdminUser1 = userRepository.save(adminUser1);


        // then
        String token = jwtTokenService.createToken(new TokenDto(saveAdminUser1.getEmail()));

        mvc.perform(get("/v1/technicalStack").contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}