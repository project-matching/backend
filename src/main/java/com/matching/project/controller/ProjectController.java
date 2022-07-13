package com.matching.project.controller;

import com.matching.project.dto.ResponseDto;
import com.matching.project.dto.project.*;
import com.matching.project.service.ProjectService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/project")
@RequiredArgsConstructor
public class ProjectController {
    @Autowired
    private final ProjectService projectService;

    @PostMapping
    @ApiOperation(value = "프로젝트 등록")
    public ResponseEntity projectRegister(@RequestBody ProjectRegisterRequestDto projectRegisterRequestDto) throws Exception{
        ProjectRegisterResponseDto projectRegisterResponseDto = projectService.projectRegister(projectRegisterRequestDto);

        return ResponseEntity.ok(new ResponseDto<>(null, projectRegisterResponseDto));
    }

    @GetMapping("/recruitment")
    @ApiOperation(value = "모집중인 프로젝트 목록 조회")
    public ResponseEntity projectRecruitingList(@PageableDefault(size = 5, sort = "createDate", direction = Sort.Direction.DESC) Pageable pageable) throws Exception {
        Page<ProjectSimpleDto> projectSimpleDtoList = projectService.findProjectList(true, false, null, pageable);
        return ResponseEntity.ok(new ResponseDto<>(null, projectSimpleDtoList));
    }

    @GetMapping("/recruitment/complete")
    @ApiOperation(value = "모집 완료된 프로젝트 목록 조회")
    public ResponseEntity projectRecruitingCompleteList(@PageableDefault(size = 5, sort = "createDate", direction = Sort.Direction.DESC) Pageable pageable) throws Exception {
        Page<ProjectSimpleDto> projectSimpleDtoList = projectService.findProjectList(false, false, null, pageable);
        return ResponseEntity.ok(new ResponseDto<>(null, projectSimpleDtoList));
    }


    @GetMapping("/{projectNo}")
    @ApiOperation(value = "프로젝트 상세 조회")
    public ResponseEntity projectInfo(@PathVariable Long projectNo) throws Exception{
        ProjectDto projectDetail = projectService.getProjectDetail(projectNo);

        return ResponseEntity.ok(new ResponseDto<>(null, projectDetail));
    }

    @PostMapping("/recruitment/search")
    @ApiOperation(value = "모집중인 프로젝트 검색")
    public ResponseEntity projectRecruitingSearch(@RequestBody ProjectSearchRequestDto projectSearchRequestDto, @PageableDefault(size = 5, sort = "createDate", direction = Sort.Direction.DESC) Pageable pageable) throws Exception{
        Page<ProjectSimpleDto> projectSimpleDtoList = projectService.findProjectList(true, false, projectSearchRequestDto, pageable);

        return ResponseEntity.ok(new ResponseDto<>(null, projectSimpleDtoList));
    }

    @PostMapping("/recruitment/complete/search")
    @ApiOperation(value = "모집 완료된 프로젝트 검색")
    public ResponseEntity projectRecruitingCompleteSearch(@RequestBody ProjectSearchRequestDto projectSearchRequestDto, @PageableDefault(size = 5, sort = "createDate", direction = Sort.Direction.DESC) Pageable pageable) throws Exception{
        Page<ProjectSimpleDto> projectSimpleDtoList = projectService.findProjectList(false, false, projectSearchRequestDto, pageable);

        return ResponseEntity.ok(new ResponseDto<>(null, projectSimpleDtoList));
    }

    @PatchMapping("/{projectNo}")
    @ApiOperation(value = "프로젝트 수정")
    public ResponseEntity projectUpdate(ProjectUpdateRequestDto projectUpdateRequestDto) {
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/{projectNo}")
    @ApiOperation(value = "프로젝트 삭제")
    public ResponseEntity projectDelete(@PathVariable Long projectNo) {
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/participate")
    @ApiOperation(value = "프로젝트 참가 신청")
    public ResponseEntity projectParticipateRequest(ProjectParticipateRequestDto projectParticipateRequestDto) {

        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/participate/{projectNo}")
    @ApiOperation(value = "프로젝트 탈퇴")
    public ResponseEntity projectParticipateWithdraw(@PathVariable Long projectNo) {
        return new ResponseEntity(HttpStatus.OK);
    }
    
    // 프로젝트 참가 허가
    @PostMapping("/participate/permit")
    @ApiOperation(value = "프로젝트 참가 허가")
    public ResponseEntity projectParticipatePermit(ProjectParticipatePermitRequestDto projectParticipatePermitRequestDto) {
        return new ResponseEntity(HttpStatus.OK);
    }

    // 프로젝트 참가 거부
    @PostMapping("/participate/refusal")
    @ApiOperation(value = "프로젝트 참가 거부")
    public ResponseEntity projectParticipatePermit(ProjectParticipateRefusalRequestDto projectParticipatePermitRequestDto) {
        return new ResponseEntity(HttpStatus.OK);
    }
}
