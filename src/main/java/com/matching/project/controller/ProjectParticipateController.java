package com.matching.project.controller;

import com.matching.project.dto.ResponseDto;
import com.matching.project.dto.SliceDto;
import com.matching.project.dto.project.ProjectParticipatePermitRequestDto;
import com.matching.project.dto.project.ProjectParticipateRefusalRequestDto;
import com.matching.project.dto.project.ProjectParticipateRequestDto;
import com.matching.project.dto.projectparticipate.ProjectParticipateFormResponseDto;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import com.matching.project.service.ProjectParticipateRequestService;
import com.matching.project.service.ProjectService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/participate")
@RequiredArgsConstructor
public class ProjectParticipateController {
    private final ProjectParticipateRequestService projectParticipateRequestService;
    private final ProjectService projectService;

    @PostMapping
    @ApiOperation(value = "프로젝트 참가 신청")
    public ResponseEntity<ResponseDto<Boolean>> projectParticipateRequest(@Valid @RequestBody ProjectParticipateRequestDto projectParticipateRequestDto) throws Exception{
        return ResponseEntity.ok(new ResponseDto<Boolean>(null, projectParticipateRequestService.projectParticipateRequestRegister(projectParticipateRequestDto)));
    }

    @GetMapping("/{projectNo}")
    @ApiOperation(value = "프로젝트 신청 관리 페이지 (수정 완료)")
    public ResponseEntity<ResponseDto<SliceDto<ProjectParticipateFormResponseDto>>> projectParticipateManagementForm(@PathVariable Long projectNo, @PageableDefault(size = 5, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable, @RequestParam(value = "projectParticipateRequestNo", required = false) Long projectParticipateRequestNo) throws Exception{

        return ResponseEntity.ok(new ResponseDto<>(null, projectParticipateRequestService.findProjectParticipateManagementForm(projectNo, projectParticipateRequestNo, pageable)));
    }

    // 프로젝트 참가 허가
    @PostMapping("/{projectParticipateNo}/permit")
    @ApiOperation(value = "프로젝트 참가 신청 수락 (수정 완료)")
    public ResponseEntity<ResponseDto<Boolean>> projectParticipatePermit(@PathVariable Long projectParticipateNo) throws Exception {
        return ResponseEntity.ok(new ResponseDto(null, projectParticipateRequestService.permitProjectParticipate(projectParticipateNo)));
    }

    // 프로젝트 참가 거부
    @PostMapping("/{projectParticipateNo}/refusal")
    @ApiOperation(value = "프로젝트 참가 거부 (수정 완료)")
    public ResponseEntity<ResponseDto<Boolean>> projectParticipateRefusal(@PathVariable Long projectParticipateNo, String reason) throws Exception{
        return ResponseEntity.ok(new ResponseDto(null, projectParticipateRequestService.refusalProjectParticipate(projectParticipateNo, reason)));
    }
}
