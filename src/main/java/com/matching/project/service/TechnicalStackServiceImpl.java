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
        Map<Long, Image> imageMap = imageRepository.findByNoIn(technicalStackImageNoList).stream()
                .collect(Collectors.toMap(image -> image.getNo(), image -> image));

        // 이미지 매칭
        // todo 추후 어떤 이미지 값을 넘길지 정해야함 현재는 physical
        List<TechnicalStackRegisterFormResponseDto> technicalStackRegisterFormResponseDtoList = new ArrayList<>();
        for (TechnicalStack technicalStack : technicalStackList) {
            TechnicalStackRegisterFormResponseDto technicalStackRegisterFormResponseDto;
            if (imageMap.containsKey(technicalStack.getImageNo())) {
                technicalStackRegisterFormResponseDto = new TechnicalStackRegisterFormResponseDto(technicalStack.getNo(), technicalStack.getName(), imageMap.get(technicalStack.getImageNo()).getPhysicalName());
            } else {
                technicalStackRegisterFormResponseDto = new TechnicalStackRegisterFormResponseDto(technicalStack.getNo(), technicalStack.getName(), null);
            }
            technicalStackRegisterFormResponseDtoList.add(technicalStackRegisterFormResponseDto);
        }

        return technicalStackRegisterFormResponseDtoList;
    }

    @Override
    public boolean technicalStackRegister(String technicalStackName, MultipartFile image) throws Exception {
        // 이미지 업로드 및 DB 저장
        Long imageNo = imageService.imageUpload(image, 56, 56);
        
        // 기술스택 저장
        TechnicalStack technicalStack = TechnicalStack.builder()
                .name(technicalStackName)
                .imageNo(imageNo)
                .build();
        technicalStackRepository.save(technicalStack);

        return true;
    }

    @Override
    public boolean technicalStackUpdate(TechnicalStackUpdateRequestDto technicalStackUpdateRequestDto, MultipartFile image) throws Exception {
        // 기술 스택 조회
        TechnicalStack technicalStack = technicalStackRepository.findById(technicalStackUpdateRequestDto.getTechnicalStackNo()).orElseThrow(() -> new CustomException(ErrorCode.TECHNICAL_STACK_NOT_FOUND));

        // 이미지 삭제
        imageService.imageDelete(technicalStack.getImageNo());

        // 이미지 변경
        Long imageNo = imageService.imageUpload(image, 56, 56);
        
        // 기술스택 업데이트
        technicalStack.updateTechnicalStack(technicalStackUpdateRequestDto.getTechnicalStackName(), imageNo);
        
        return true;
    }
}
