package com.matching.project.controller;

import com.matching.project.dto.ResponseDto;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/projectposition")
@RequiredArgsConstructor
public class ProjectPositionController {
    @DeleteMapping("/{projectPositionNo}/withdrawal")
    @ApiOperation(value = "프로젝트 탈퇴 (수정 완료)")
    public ResponseEntity<ResponseDto<Long>> projectPositionWithdraw(@PathVariable Long projectPositionNo) {
        return ResponseEntity.ok(new ResponseDto<Long>(null, 1L));
    }

    @DeleteMapping("/{projectPositionNo}/expulsion")
    @ApiOperation(value = "프로젝트 추방 (수정 완료)")
    public ResponseEntity<ResponseDto<Boolean>> projectPositionExpulsion(@PathVariable Long projectPositionNo, String reason) {
        return ResponseEntity.ok(new ResponseDto<Boolean>(null, true));
    }
}
