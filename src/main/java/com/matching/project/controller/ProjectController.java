package com.matching.project.controller;

import com.matching.project.dto.ResponseDto;
import com.matching.project.dto.enumerate.ProjectFilter;
import com.matching.project.dto.project.*;
import com.matching.project.dto.user.UserBlockResponseDto;
import com.matching.project.entity.User;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/project")
@RequiredArgsConstructor
public class ProjectController {
    @Autowired
    private final ProjectService projectService;

    @GetMapping("/create")
    @ApiOperation(value = "프로젝트 등록 페이지 (수정 완료)")
    public ResponseEntity<ResponseDto<ProjectRegisterFormResponseDto>> projectRegisterForm() throws Exception{

        return ResponseEntity.ok(new ResponseDto<ProjectRegisterFormResponseDto>(null, projectService.findProjectRegisterForm()));
    }

    @PostMapping
    @ApiOperation(value = "프로젝트 등록 (수정 완료)")
    public ResponseEntity<ResponseDto<Long>> projectRegister(@Valid @RequestBody ProjectRegisterRequestDto projectRegisterRequestDto) throws Exception{
        return ResponseEntity.ok(new ResponseDto<>(null, projectService.projectRegister(projectRegisterRequestDto)));
    }

    @GetMapping("/recruitment")
    @ApiOperation(value = "모집중인 프로젝트 목록 조회")
    public ResponseEntity<ResponseDto<Page<ProjectSimpleDto>>> projectRecruitingList(@PageableDefault(size = 5, sort = "createDate", direction = Sort.Direction.DESC) Pageable pageable, String searchContent) throws Exception {
        Page<ProjectSimpleDto> projectSimpleDtoList = projectService.findProjectList(true, false, new ProjectSearchRequestDto(ProjectFilter.PROJECT_NAME_AND_CONTENT, searchContent), pageable);
        return ResponseEntity.ok(new ResponseDto<>(null, projectSimpleDtoList));
    }

    @GetMapping("/recruitment/complete")
    @ApiOperation(value = "모집 완료된 프로젝트 목록 조회")
    public ResponseEntity<ResponseDto<Page<ProjectSimpleDto>>> projectRecruitingCompleteList(@PageableDefault(size = 5, sort = "createDate", direction = Sort.Direction.DESC) Pageable pageable, String searchContent) throws Exception {
        Page<ProjectSimpleDto> projectSimpleDtoList = projectService.findProjectList(false, false, new ProjectSearchRequestDto(ProjectFilter.PROJECT_NAME_AND_CONTENT, searchContent), pageable);
        return ResponseEntity.ok(new ResponseDto<>(null, projectSimpleDtoList));
    }

    @GetMapping("/create/self")
    @ApiOperation(value = "내가 만든 프로젝트 목록 조회")
    public ResponseEntity<ResponseDto<Page<ProjectSimpleDto>>> projectCreateSelfList(@PageableDefault(size = 5, sort = "createDate", direction = Sort.Direction.DESC) Pageable pageable) throws Exception {
        Page<ProjectSimpleDto> projectSimpleDtoList = projectService.findUserProjectList(false, pageable);
        return ResponseEntity.ok(new ResponseDto<>(null, projectSimpleDtoList));
    }

    @GetMapping("/participate")
    @ApiOperation(value = "참여중인 프로젝트 목록 조회")
    public ResponseEntity<ResponseDto<Page<ProjectSimpleDto>>> projectParticipateList(@PageableDefault(size = 5, sort = "createDate", direction = Sort.Direction.DESC) Pageable pageable) throws Exception {
        Page<ProjectSimpleDto> projectSimpleDtoList = projectService.findParticipateProjectList(false, pageable);
        return ResponseEntity.ok(new ResponseDto<>(null, projectSimpleDtoList));
    }

    @GetMapping("/application")
    @ApiOperation(value = "신청중인 프로젝트 목록 조회")
    public ResponseEntity<ResponseDto<Page<ProjectSimpleDto>>> projectApplicationList(@PageableDefault(size = 5, sort = "createDate", direction = Sort.Direction.DESC) Pageable pageable) throws Exception {
        Page<ProjectSimpleDto> projectSimpleDtoList = projectService.findParticipateRequestProjectList(false, pageable);
        return ResponseEntity.ok(new ResponseDto<>(null, projectSimpleDtoList));
    }

    @GetMapping("/{projectNo}")
    @ApiOperation(value = "프로젝트 상세 조회 (수정 완료)")
    public ResponseEntity<ResponseDto<ProjectDto>> projectInfo(@PathVariable Long projectNo) throws Exception{
        ProjectDto projectDetail = projectService.getProjectDetail(projectNo);

        return ResponseEntity.ok(new ResponseDto<>(null, projectDetail));
    }

    @GetMapping("/{projectNo}/update")
    @ApiOperation(value = "프로젝트 수정 페이지 조회 (수정 완료)")
    public ResponseEntity<ResponseDto<ProjectUpdateFormResponseDto>> projectUpdateForm(@PathVariable Long projectNo) {
        return ResponseEntity.ok(new ResponseDto<>(null, new ProjectUpdateFormResponseDto()));
    }
    
    @PatchMapping("/{projectNo}")
    @ApiOperation(value = "프로젝트 수정")
    public ResponseEntity<ResponseDto<Long>> projectUpdate(@PathVariable Long projectNo, ProjectUpdateRequestDto projectUpdateRequestDto) {
        return ResponseEntity.ok(new ResponseDto<>(null, 1L));
    }

    @DeleteMapping("/{projectNo}")
    @ApiOperation(value = "프로젝트 삭제")
    public ResponseEntity<ResponseDto<Boolean>> projectDelete(@PathVariable Long projectNo) {
        return ResponseEntity.ok(new ResponseDto<>(null, true));
    }

    @GetMapping("/block/{projectNo}")
    @ApiOperation(value = "(관리자) 프로젝트 차단 (수정 완료)")
    public ResponseEntity<ResponseDto<Boolean>> projectBlock(@PathVariable Long projectNo, @RequestBody String reason) {

        return ResponseEntity.ok(new ResponseDto<>(null, null));
    }

    @GetMapping("/unblock/{projectNo}")
    @ApiOperation(value = "(관리자) 프로젝트 차단 해제 (수정 완료)")
    public ResponseEntity<ResponseDto<Boolean>> projectUnBlock(@PathVariable Long projectNo) {
        return ResponseEntity.ok(new ResponseDto<>(null, null));
    }

    @GetMapping("/recruitment/admin")
    @ApiOperation(value = "(관리자) 모집중인 프로젝트 목록 조회 (수정 완료)")
    public ResponseEntity<ResponseDto<Page<AdminProjectSimpleDto>>> adminProjectRecruitingList(@PageableDefault(size = 5, sort = "createDate", direction = Sort.Direction.DESC) Pageable pageable, String searchContent) throws Exception {
        Page<ProjectSimpleDto> projectSimpleDtoList = projectService.findProjectList(true, false, null, pageable);
        return ResponseEntity.ok(new ResponseDto<>(null, null));
    }

    @GetMapping("/recruitment/complete/admin")
    @ApiOperation(value = "(관리자) 모집 완료된 프로젝트 목록 조회 (수정 완료)")
    public ResponseEntity<ResponseDto<Page<AdminProjectSimpleDto>>> adminProjectRecruitingCompleteList(@PageableDefault(size = 5, sort = "createDate", direction = Sort.Direction.DESC) Pageable pageable, String searchContent) throws Exception {
        Page<ProjectSimpleDto> projectSimpleDtoList = projectService.findProjectList(false, false, null, pageable);
        return ResponseEntity.ok(new ResponseDto<>(null, null));
    }

    @GetMapping("/{projectNo}/admin")
    @ApiOperation(value = "(관리자) 프로젝트 상세 조회 (수정 완료)")
    public ResponseEntity<ResponseDto<AdminProjectDto>> adminProjectInfo(@PathVariable Long projectNo) throws Exception{

        return ResponseEntity.ok(new ResponseDto<>(null, null));
    }
}
