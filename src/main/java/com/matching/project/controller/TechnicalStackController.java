package com.matching.project.controller;

import com.matching.project.dto.ResponseDto;
import com.matching.project.dto.position.PositionRegisterFormResponseDto;
import com.matching.project.dto.project.TechnicalStackRegisterFormDto;
import com.matching.project.dto.technicalstack.TechnicalStackRegisterFormResponseDto;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/technicalStack")
@RequiredArgsConstructor
public class TechnicalStackController {
    @GetMapping
    @ApiOperation(value = "기술스택 추가 form (수정 완료)")
    public ResponseEntity<ResponseDto<List<TechnicalStackRegisterFormResponseDto>>> technicalStackRegisterForm() {
        return ResponseEntity.ok(new ResponseDto<>(null, null));
    }

    @PostMapping
    @ApiOperation(value = "기술스택 추가 (수정 완료)")
    public ResponseEntity<ResponseDto<Boolean>> technicalStackRegister(String technicalStackName, String image) {
        return ResponseEntity.ok(new ResponseDto<>(null, null));
    }

    @PutMapping("/{technicalStackNo}")
    @ApiOperation(value = "기술스택 수정 (수정 완료)")
    public ResponseEntity<ResponseDto<Boolean>> technicalStackUpdate(Long technicalStackNo, String technicalStackName, String image) {
        return ResponseEntity.ok(new ResponseDto<>(null, null));
    }
}
