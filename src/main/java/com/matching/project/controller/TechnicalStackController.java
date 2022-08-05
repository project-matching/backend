package com.matching.project.controller;

import com.matching.project.dto.ResponseDto;
import com.matching.project.dto.position.PositionRegisterFormResponseDto;
import com.matching.project.dto.project.TechnicalStackRegisterFormDto;
import com.matching.project.dto.technicalstack.TechnicalStackRegisterFormResponseDto;
import com.matching.project.dto.technicalstack.TechnicalStackUpdateRequestDto;
import com.matching.project.service.TechnicalStackService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/v1/technicalStack")
@RequiredArgsConstructor
public class TechnicalStackController {
    private final TechnicalStackService technicalStackService;

    @GetMapping
    @ApiOperation(value = "기술스택 추가 form")
    public ResponseEntity<ResponseDto<List<TechnicalStackRegisterFormResponseDto>>> technicalStackRegisterForm() throws Exception{
        return ResponseEntity.ok(new ResponseDto<>(null, technicalStackService.findTechnicalStackRegisterForm()));
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation(value = "기술스택 추가")
    public ResponseEntity<ResponseDto<Boolean>> technicalStackRegister(@RequestPart @NotNull String technicalStackName, @RequestPart MultipartFile image) throws Exception {
        return ResponseEntity.ok(new ResponseDto<>(null, technicalStackService.technicalStackRegister(technicalStackName, image)));
    }

    @PutMapping(value = "/{technicalStackNo}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation(value = "기술스택 수정 (수정 완료)")
    public ResponseEntity<ResponseDto<Boolean>> technicalStackUpdate(@RequestPart @Valid TechnicalStackUpdateRequestDto technicalStackUpdateRequestDto, @RequestPart MultipartFile image) throws Exception{
        return ResponseEntity.ok(new ResponseDto<>(null, technicalStackService.technicalStackUpdate(technicalStackUpdateRequestDto, image)));
    }
}
