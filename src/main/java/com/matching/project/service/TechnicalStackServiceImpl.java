package com.matching.project.service;

import com.matching.project.dto.technicalstack.TechnicalStackRegisterFormResponseDto;
import com.matching.project.dto.technicalstack.TechnicalStackUpdateRequestDto;
import com.matching.project.entity.Image;
import com.matching.project.entity.TechnicalStack;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import com.matching.project.repository.ImageRepository;
import com.matching.project.repository.TechnicalStackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TechnicalStackServiceImpl implements TechnicalStackService {
    private final TechnicalStackRepository technicalStackRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    
    // 기술스택 폼 조회
    @Override
    public List<TechnicalStackRegisterFormResponseDto> findTechnicalStackRegisterForm() throws Exception {
        // 모든 기술스택 조회
        List<TechnicalStack> technicalStackList = technicalStackRepository.findAll();
        
        // 기술스택과 연결된 이미지 조회
        List<Long> technicalStackImageNoList = technicalStackList.stream()
                .map(technicalStack -> technicalStack.getImageNo()).collect(Collectors.toList());
        Map<Long, Image> imageMap = imageRepository.findByNoIn(technicalStackImageNoList)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FIND_IMAGE_EXCEPTION))
                .stream()
                .collect(Collectors.toMap(image -> image.getNo(), image -> image));

        // 이미지 매칭
        List<TechnicalStackRegisterFormResponseDto> technicalStackRegisterFormResponseDtoList = new ArrayList<>();
        for (TechnicalStack technicalStack : technicalStackList) {
            TechnicalStackRegisterFormResponseDto technicalStackRegisterFormResponseDto;
            if (imageMap.containsKey(technicalStack.getImageNo())) {
                technicalStackRegisterFormResponseDto = new TechnicalStackRegisterFormResponseDto(technicalStack.getNo(), technicalStack.getName(), imageMap.get(technicalStack.getImageNo()).getUrl());
            } else {
                technicalStackRegisterFormResponseDto = new TechnicalStackRegisterFormResponseDto(technicalStack.getNo(), technicalStack.getName(), null);
            }
            technicalStackRegisterFormResponseDtoList.add(technicalStackRegisterFormResponseDto);
        }

        return technicalStackRegisterFormResponseDtoList;
    }

    @Override
    public boolean technicalStackRegister(String technicalStackName, MultipartFile image) throws Exception {
        TechnicalStack technicalStack = TechnicalStack.builder()
                .name(technicalStackName)
                .build();
        // 이미지 업로드 및 DB 저장
        if (image != null && !image.isEmpty()) {
            Long imageNo = imageService.imageUpload(image, 56, 56);

            technicalStack.changeImageNo(imageNo);
        }

        // 기술스택 저장
        technicalStackRepository.save(technicalStack);

        return true;
    }

    @Override
    public boolean technicalStackUpdate(Long technicalStackNo, TechnicalStackUpdateRequestDto technicalStackUpdateRequestDto, MultipartFile image) throws Exception {
        // 기술 스택 조회
        TechnicalStack technicalStack = technicalStackRepository.findById(technicalStackNo).orElseThrow(() -> new CustomException(ErrorCode.NOT_FIND_TECHNICAL_STACK_EXCEPTION));

        if (image != null && !image.isEmpty()) {
            // 이미지 삭제
            imageService.imageDelete(technicalStack.getImageNo());

            // 이미지 변경
            Long imageNo = imageService.imageUpload(image, 56, 56);

            // 기술스택 업데이트
            technicalStack.updateTechnicalStack(technicalStackUpdateRequestDto.getTechnicalStackName(), imageNo);
        } else {
            technicalStack.updateTechnicalStack(technicalStackUpdateRequestDto.getTechnicalStackName());
        }

        return true;
    }
}
