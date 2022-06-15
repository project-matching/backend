package com.matching.project.controller;

import com.matching.project.dto.project.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/project")
public class ProjectController {
    @PostMapping
    @ApiOperation(value = "프로젝트 등록")
    public ResponseEntity projectRegister(ProjectRegisterRequestDto projectRegisterRequestDto) {

        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/recruitment")
    @ApiOperation(value = "모집중인 프로젝트 목록 조회")
    public ResponseEntity<List<ProjectSimpleDto>> projectRecruitingList() {
        List<ProjectSimpleDto> projectDtoList = new ArrayList<>();

        return new ResponseEntity(projectDtoList, HttpStatus.OK);
    }

    @GetMapping("/recruitment/complete")
    @ApiOperation(value = "모집 완료된 프로젝트 목록 조회")
    public ResponseEntity projectRecruitingCompleteList() {
        List<ProjectSimpleDto> projectDtoList = new ArrayList<>();

        return new ResponseEntity(projectDtoList, HttpStatus.OK);
    }

    @GetMapping("/{projectNo}")
    @ApiOperation(value = "프로젝트 상세 조회")
    public ResponseEntity projectInfo(@PathVariable Long projectNo) {
        return new ResponseEntity(new ProjectDto(), HttpStatus.OK);
    }

    @PostMapping("/search")
    @ApiOperation(value = "프로젝트 검색")
    public ResponseEntity projectSearch(ProjectSearchRequestDto projectSearchRequestDto) {
        List<ProjectSimpleDto> projectDtoList = new ArrayList<>();

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
