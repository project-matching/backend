package com.matching.project.controller;

import com.matching.project.dto.ResponseDto;
import com.matching.project.dto.position.PositionRegisterFormResponseDto;
import com.matching.project.dto.project.PositionRegisterFormDto;
import com.matching.project.dto.project.ProjectRegisterFormResponseDto;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/position")
@RequiredArgsConstructor
public class PositionController {
    @GetMapping
    @ApiOperation(value = "포지션 추가 form (수정 완료)")
    public ResponseEntity<ResponseDto<List<PositionRegisterFormResponseDto>>> positionRegisterForm() {
        return ResponseEntity.ok(new ResponseDto<>(null, null));
    }

    @PostMapping
    @ApiOperation(value = "포지션 추가 (수정 완료)")
    public ResponseEntity<ResponseDto<Boolean>> positionRegister(String positionName) {
        return ResponseEntity.ok(new ResponseDto<>(null, null));
    }

    @PutMapping("/{positionNo}")
    @ApiOperation(value = "포지션 수정 (수정 완료)")
    public ResponseEntity<ResponseDto<Boolean>> positionUpdate(Long positionNo, String positionName) {
        return ResponseEntity.ok(new ResponseDto<>(null, null));
    }
}
