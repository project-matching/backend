package com.matching.project.controller;

import com.matching.project.dto.ResponseDto;
import com.matching.project.dto.SliceDto;
import com.matching.project.dto.enumerate.ProjectFilter;
import com.matching.project.dto.project.*;
import com.matching.project.dto.user.UserBlockResponseDto;
import com.matching.project.entity.User;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import com.matching.project.service.ProjectService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/v1/project")
@RequiredArgsConstructor
public class ProjectController {
    @Autowired
    private final ProjectService projectService;

    @GetMapping("/create")
    @ApiOperation(value = "프로젝트 등록 페이지")
    @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "string", paramType = "header")
    public ResponseEntity<ResponseDto<ProjectRegisterFormResponseDto>> projectRegisterForm() throws Exception{

        return ResponseEntity.ok(new ResponseDto<ProjectRegisterFormResponseDto>(null, projectService.findProjectRegisterForm()));
    }

    @PostMapping
    @ApiOperation(value = "프로젝트 등록")
    @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "string", paramType = "header")
    public ResponseEntity<ResponseDto<Long>> projectRegister(@Valid @RequestBody ProjectRegisterRequestDto projectRegisterRequestDto) throws Exception{
        return ResponseEntity.ok(new ResponseDto<>(null, projectService.projectRegister(projectRegisterRequestDto)));
    }

    @GetMapping("/recruitment")
    @ApiOperation(value = "모집중인 프로젝트 목록 조회")
    @ApiImplicitParam(name = "Authorization", value = "Authorization", required = false, dataType = "string", paramType = "header")
    public ResponseEntity<ResponseDto<SliceDto<ProjectSimpleDto>>> projectRecruitingList(@PageableDefault(size = 5, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable, @RequestParam(value = "projectNo", required = false) Long projectNo, @RequestParam(value = "searchContent", required = false) String searchContent) throws Exception {
        log.debug("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        SliceDto<ProjectSimpleDto> projectSimpleDtoList = projectService.findProjectList(projectNo,true, new ProjectSearchRequestDto(ProjectFilter.PROJECT_NAME_AND_CONTENT, searchContent), pageable);
        return ResponseEntity.ok(new ResponseDto<>(null, projectSimpleDtoList));
    }

    @GetMapping("/recruitment/complete")
    @ApiOperation(value = "모집 완료된 프로젝트 목록 조회")
    @ApiImplicitParam(name = "Authorization", value = "Authorization", required = false, dataType = "string", paramType = "header")
    public ResponseEntity<ResponseDto<SliceDto<ProjectSimpleDto>>> projectRecruitingCompleteList(@PageableDefault(size = 5, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable, @RequestParam(value = "projectNo", required = false) Long projectNo, @RequestParam(value = "searchContent", required = false) String searchContent) throws Exception {
        SliceDto<ProjectSimpleDto> projectSimpleDtoList = projectService.findProjectList(projectNo, false, new ProjectSearchRequestDto(ProjectFilter.PROJECT_NAME_AND_CONTENT, searchContent), pageable);
        return ResponseEntity.ok(new ResponseDto<>(null, projectSimpleDtoList));
    }

    @GetMapping("/create/self")
    @ApiOperation(value = "내가 만든 프로젝트 목록 조회")
    @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "string", paramType = "header")
    public ResponseEntity<ResponseDto<SliceDto<ProjectSimpleDto>>> projectCreateSelfList(@PageableDefault(size = 5, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable, @RequestParam(value = "projectNo", required = false) Long projectNo) throws Exception {
        SliceDto<ProjectSimpleDto> projectSimpleDtoList = projectService.findUserProjectList(projectNo, pageable);
        return ResponseEntity.ok(new ResponseDto<>(null, projectSimpleDtoList));
    }

    @GetMapping("/participate")
    @ApiOperation(value = "참여중인 프로젝트 목록 조회")
    @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "string", paramType = "header")
    public ResponseEntity<ResponseDto<SliceDto<ProjectSimpleDto>>> projectParticipateList(@PageableDefault(size = 5, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable, @RequestParam(value = "projectNo", required = false) Long projectNo) throws Exception {
        SliceDto<ProjectSimpleDto> projectSimpleDtoList = projectService.findParticipateProjectList(projectNo, pageable);
        return ResponseEntity.ok(new ResponseDto<>(null, projectSimpleDtoList));
    }

    @GetMapping("/application")
    @ApiOperation(value = "신청중인 프로젝트 목록 조회")
    @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "string", paramType = "header")
    public ResponseEntity<ResponseDto<SliceDto<ProjectSimpleDto>>> projectApplicationList(@PageableDefault(size = 5, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable, @RequestParam(value = "projectNo", required = false) Long projectNo) throws Exception {
        SliceDto<ProjectSimpleDto> projectSimpleDtoList = projectService.findParticipateRequestProjectList(projectNo, pageable);
        return ResponseEntity.ok(new ResponseDto<>(null, projectSimpleDtoList));
    }

    @GetMapping("/{projectNo}")
    @ApiOperation(value = "프로젝트 상세 조회")
    @ApiImplicitParam(name = "Authorization", value = "Authorization", required = false, dataType = "string", paramType = "header")
    public ResponseEntity<ResponseDto<ProjectDto>> projectInfo(@PathVariable Long projectNo) throws Exception{

        return ResponseEntity.ok(new ResponseDto<>(null, projectService.getProjectDetail(projectNo)));
    }

    @GetMapping("/{projectNo}/update")
    @ApiOperation(value = "프로젝트 수정 페이지 조회")
    @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "string", paramType = "header")
    public ResponseEntity<ResponseDto<ProjectUpdateFormResponseDto>> projectUpdateForm(@PathVariable Long projectNo) throws Exception{
        ProjectUpdateFormResponseDto projectUpdateFormResponseDto = projectService.getProjectUpdateForm(projectNo);
        return ResponseEntity.ok(new ResponseDto<>(null, projectUpdateFormResponseDto));
    }
    
    @PatchMapping("/{projectNo}")
    @ApiOperation(value = "프로젝트 수정")
    @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "string", paramType = "header")
    public ResponseEntity<ResponseDto<Long>> projectUpdate(@PathVariable Long projectNo, @Valid @RequestBody ProjectUpdateRequestDto projectUpdateRequestDto) throws Exception {

        return ResponseEntity.ok(new ResponseDto<>(null, projectService.projectUpdate(projectNo, projectUpdateRequestDto)));
    }

    @DeleteMapping("/{projectNo}")
    @ApiOperation(value = "프로젝트 삭제")
    @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "string", paramType = "header")
    public ResponseEntity<ResponseDto<Boolean>> projectDelete(@PathVariable Long projectNo) throws Exception {
        return ResponseEntity.ok(new ResponseDto<>(null, projectService.projectDelete(projectNo)));
    }
}
