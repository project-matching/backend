package com.matching.project.service;

import com.matching.project.dto.technicalstack.TechnicalStackRegisterFormResponseDto;
import com.matching.project.dto.technicalstack.TechnicalStackUpdateRequestDto;
import com.matching.project.entity.Image;
import com.matching.project.entity.TechnicalStack;
import com.matching.project.repository.ImageRepository;
import com.matching.project.repository.TechnicalStackRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TechnicalStackServiceImplTest {
    @Mock
    private TechnicalStackRepository technicalStackRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private TechnicalStackServiceImpl technicalStackService;

    @Test
    public void 기술스택_등록_폼_조회() {
        // given
        // 기술스택 세팅
        List<TechnicalStack> technicalStackList = new ArrayList<>();
        TechnicalStack technicalStack1 = TechnicalStack.builder()
                .no(1L)
                .name("testTechnicalStack1")
                .imageNo(1L)
                .build();
        TechnicalStack technicalStack2 = TechnicalStack.builder()
                .no(2L)
                .name("testTechnicalStack2")
                .imageNo(2L)
                .build();
        TechnicalStack technicalStack3 = TechnicalStack.builder()
                .no(3L)
                .name("testTechnicalStack3")
                .imageNo(3L)
                .build();
        technicalStackList.add(technicalStack1);
        technicalStackList.add(technicalStack2);
        technicalStackList.add(technicalStack3);
        
        // 이미지 세팅
        List<Image> imageList = new ArrayList<>();
        Image image1 = Image.builder()
                .no(1L)
                .logicalName("testLogicalName1")
                .physicalName("testPhysicalName1")
                .url("testUrl1")
                .build();
        Image image2 = Image.builder()
                .no(2L)
                .logicalName("testLogicalName2")
                .physicalName("testPhysicalName2")
                .url("testUrl2")
                .build();
        Image image3 = Image.builder()
                .no(3L)
                .logicalName("testLogicalName3")
                .physicalName("testPhysicalName3")
                .url("testUrl3")
                .build();
        imageList.add(image1);
        imageList.add(image2);
        imageList.add(image3);

        given(technicalStackRepository.findAll()).willReturn(technicalStackList);
        given(imageRepository.findByNoIn(any())).willReturn(imageList);

        // when
        List<TechnicalStackRegisterFormResponseDto> technicalStackRegisterFormResponseDtoList = null;
        try {
            technicalStackRegisterFormResponseDtoList = technicalStackService.findTechnicalStackRegisterForm();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // then
        verify(technicalStackRepository).findAll();
        verify(imageRepository).findByNoIn(any());

        assertEquals(technicalStackRegisterFormResponseDtoList.get(0).getTechnicalStackNo(), technicalStack1.getNo());
        assertEquals(technicalStackRegisterFormResponseDtoList.get(0).getTechnicalStackName(), technicalStack1.getName());
        assertEquals(technicalStackRegisterFormResponseDtoList.get(0).getImage(), image1.getPhysicalName());
        assertEquals(technicalStackRegisterFormResponseDtoList.get(1).getTechnicalStackNo(), technicalStack2.getNo());
        assertEquals(technicalStackRegisterFormResponseDtoList.get(1).getTechnicalStackName(), technicalStack2.getName());
        assertEquals(technicalStackRegisterFormResponseDtoList.get(1).getImage(), image2.getPhysicalName());
        assertEquals(technicalStackRegisterFormResponseDtoList.get(2).getTechnicalStackNo(), technicalStack3.getNo());
        assertEquals(technicalStackRegisterFormResponseDtoList.get(2).getTechnicalStackName(), technicalStack3.getName());
        assertEquals(technicalStackRegisterFormResponseDtoList.get(2).getImage(), image3.getPhysicalName());
    }
    
    @Test
    public void 기술스택_등록() throws Exception{
        // given
        // 기술스택 세팅
        TechnicalStack technicalStack1 = TechnicalStack.builder()
                .no(1L)
                .name("testTechnicalStack1")
                .imageNo(1L)
                .build();


        given(imageService.imageUpload(any(MultipartFile.class), any(Integer.class), any(Integer.class))).willReturn(1L);
        given(technicalStackRepository.save(any())).willReturn(technicalStack1);

        // when
        MockMultipartFile mockMultipartFile
                = new MockMultipartFile("file", "hello.txt", MediaType.MULTIPART_FORM_DATA_VALUE, "Hello, World".getBytes());

        boolean result = false;
        try {
            result = technicalStackService.technicalStackRegister("testTechnicalStackName", mockMultipartFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // then
        verify(imageService).imageUpload(any(MultipartFile.class), any(Integer.class), any(Integer.class));
        verify(technicalStackRepository).save(any());

        assertEquals(result, true);
    }

    @Test
    public void 기술스택_수정() throws Exception{
        // given
        // 기술스택 세팅
        TechnicalStack technicalStack1 = TechnicalStack.builder()
                .no(1L)
                .name("testTechnicalStack1")
                .imageNo(1L)
                .build();

        given(technicalStackRepository.findById(any())).willReturn(Optional.of(technicalStack1));
        given(imageService.imageUpload(any(MultipartFile.class), any(Integer.class), any(Integer.class))).willReturn(1L);

        // when
        TechnicalStackUpdateRequestDto technicalStackUpdateRequestDto = new TechnicalStackUpdateRequestDto(technicalStack1.getNo(), "testTechnicalStackNameUpdate");
        MockMultipartFile mockMultipartFile
                = new MockMultipartFile("file", "hello.txt", MediaType.MULTIPART_FORM_DATA_VALUE, "Hello, World".getBytes());

        boolean result = false;
        try {
            result = technicalStackService.technicalStackUpdate(technicalStackUpdateRequestDto, mockMultipartFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // then
        verify(technicalStackRepository).findById(any());
        verify(imageService).imageUpload(any(MultipartFile.class), any(Integer.class), any(Integer.class));

        assertEquals(result, true);
    }
}