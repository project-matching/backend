package com.matching.project.controller;

import com.matching.project.dto.ResponseDto;
import com.matching.project.service.ProjectPositionService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/v1/projectposition")
@RequiredArgsConstructor
public class ProjectPositionController {
    private final ProjectPositionService projectPositionService;

    @DeleteMapping("/{projectPositionNo}/withdrawal")
    @ApiOperation(value = "프로젝트 탈퇴")
    @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "string", paramType = "header")
    public ResponseEntity<ResponseDto<Long>> projectPositionWithdraw(@PathVariable Long projectPositionNo) throws Exception {
        return ResponseEntity.ok(new ResponseDto<Long>(null, projectPositionService.projectPositionWithdraw(projectPositionNo)));
    }

    @DeleteMapping("/{projectPositionNo}/expulsion")
    @ApiOperation(value = "프로젝트 추방")
    @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "string", paramType = "header")
    public ResponseEntity<ResponseDto<Boolean>> projectPositionExpulsion(@PathVariable Long projectPositionNo, @RequestBody @NotBlank String reason) throws Exception {
        return ResponseEntity.ok(new ResponseDto<Boolean>(null, projectPositionService.projectPositionExpulsion(projectPositionNo, reason)));
    }
}
