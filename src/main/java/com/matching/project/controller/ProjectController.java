package com.matching.project.controller;

import com.matching.project.dto.ResponseDto;
import com.matching.project.dto.project.*;
import com.matching.project.service.ProjectService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    @ApiOperation(value = "비로그인 : 모집중인 프로젝트 목록 조회")
    public ResponseEntity noneLoginProjectRecruitingList(@PageableDefault(size = 5, sort = "no", direction = Sort.Direction.DESC) Pageable pageable) throws Exception{
        List<NoneLoginProjectSimpleDto> projectSimpleDtoList = projectService.NoneLoginProjectRecruitingList(pageable);
        return ResponseEntity.ok(new ResponseDto<>(null, projectSimpleDtoList));
    }

    @GetMapping("/login/recruitment")
    @ApiOperation(value = "로그인 : 모집중인 프로젝트 목록 조회")
    public ResponseEntity LoginProjectRecruitingList(@PageableDefault(size = 5, sort = "no", direction = Sort.Direction.DESC) Pageable pageable) throws Exception{
        List<LoginProjectSimpleDto> projectSimpleDtoList = projectService.LoginProjectRecruitingList(pageable);
        return ResponseEntity.ok(new ResponseDto<>(null, projectSimpleDtoList));
    }

    @GetMapping("/recruitment/complete")
    @ApiOperation(value = "비로그인 : 모집 완료된 프로젝트 목록 조회")
    public ResponseEntity projectRecruitingCompleteList() {
        List<LoginProjectSimpleDto> projectDtoList = new ArrayList<>();

        return new ResponseEntity(projectDtoList, HttpStatus.OK);
    }


    @GetMapping("/{projectNo}")
    @ApiOperation(value = "프로젝트 상세 조회")
    public ResponseEntity projectInfo(@PathVariable Long projectNo) {
        ProjectDto projectDto = new ProjectDto();
        return ResponseEntity.ok(new ResponseDto<>(null, projectDto));
    }

    @PostMapping("/search")
    @ApiOperation(value = "프로젝트 검색")
    public ResponseEntity projectSearch(ProjectSearchRequestDto projectSearchRequestDto) {
        List<NoneLoginProjectSimpleDto> projectDtoList = new ArrayList<>();

        return new ResponseEntity(projectDtoList, HttpStatus.OK);
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
