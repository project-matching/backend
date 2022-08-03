package com.matching.project.service;

import com.matching.project.dto.technicalstack.TechnicalStackRegisterFormResponseDto;
import com.matching.project.dto.technicalstack.TechnicalStackUpdateRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TechnicalStackService {
    public List<TechnicalStackRegisterFormResponseDto> findTechnicalStackRegisterForm() throws Exception;
    public boolean technicalStackRegister(String technicalStackName, MultipartFile image) throws Exception;
    public boolean technicalStackUpdate(TechnicalStackUpdateRequestDto technicalStackUpdateRequestDto, MultipartFile image) throws Exception;
}
