package com.matching.project.controller;

import com.matching.project.dto.ResponseDto;
import com.matching.project.dto.position.PositionRegisterFormResponseDto;
import com.matching.project.dto.position.PositionRequestDto;
import com.matching.project.dto.project.PositionRegisterFormDto;
import com.matching.project.dto.project.ProjectRegisterFormResponseDto;
import com.matching.project.entity.Position;
import com.matching.project.service.PositionService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/v1/position")
@RequiredArgsConstructor
public class PositionController {
    private final PositionService positionService;

    @GetMapping
    @ApiOperation(value = "포지션 추가 FORM (관리자)")
    public ResponseEntity<ResponseDto<List<PositionRegisterFormResponseDto>>> positionRegisterForm() {
        List<PositionRegisterFormResponseDto> positionRegisterFormResponseDtos = positionService.positionList();
        return ResponseEntity.ok(new ResponseDto<>(null, positionRegisterFormResponseDtos));
    }

    @PostMapping
    @ApiOperation(value = "포지션 추가 (관리자)")
    public ResponseEntity<ResponseDto<Boolean>> positionRegister(@RequestBody @Valid PositionRequestDto dto) {
        Position position = positionService.positionRegister(dto.getPositionName());
        return ResponseEntity.ok(new ResponseDto<>(null, true));
    }

    @PutMapping("/{positionNo}")
    @ApiOperation(value = "포지션 수정 (관리자)")
    public ResponseEntity<ResponseDto<Boolean>> positionUpdate(@PathVariable Long positionNo, @RequestBody @Valid PositionRequestDto dto) {
        Position position = positionService.positionUpdate(positionNo, dto.getPositionName());
        return ResponseEntity.ok(new ResponseDto<>(null, true));
    }
}
