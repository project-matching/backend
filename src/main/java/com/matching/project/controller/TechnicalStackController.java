package com.matching.project.controller;

import com.matching.project.dto.ResponseDto;
import com.matching.project.dto.technicalstack.TechnicalStackRegisterRequestDto;
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
import java.util.List;

@RestController
@RequestMapping("/v1/technicalStack")
@RequiredArgsConstructor
public class TechnicalStackController {
    private final TechnicalStackService technicalStackService;

    @GetMapping
    @ApiOperation(value = "기술스택 리스트 조회")
    public ResponseEntity<ResponseDto<List<TechnicalStackRegisterFormResponseDto>>> technicalStackRegisterForm() throws Exception{
        return ResponseEntity.ok(new ResponseDto<>(null, technicalStackService.findTechnicalStackRegisterForm()));
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation(value = "기술스택 추가", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDto<Boolean>> technicalStackRegister(@Valid TechnicalStackRegisterRequestDto technicalStackRegisterDto,
                                                                       @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {
        return ResponseEntity.ok(new ResponseDto<>(null, technicalStackService.technicalStackRegister(technicalStackRegisterDto.getTechnicalStackName(), image)));
    }

    @PutMapping(value = "/{technicalStackNo}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation(value = "기술스택 수정 (수정 완료)")
    public ResponseEntity<ResponseDto<Boolean>> technicalStackUpdate(@PathVariable Long technicalStackNo, @RequestPart("data") @Valid TechnicalStackUpdateRequestDto technicalStackUpdateRequestDto, @RequestPart(value = "image", required = false) MultipartFile image) throws Exception{
        return ResponseEntity.ok(new ResponseDto<>(null, technicalStackService.technicalStackUpdate(technicalStackNo, technicalStackUpdateRequestDto, image)));
    }
}
