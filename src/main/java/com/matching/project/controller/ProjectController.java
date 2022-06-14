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
    public ResponseEntity projectRegister(ProjectRegisterRequestDto projectRegisterRequestDto) {

        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/recruitment")
    public ResponseEntity<List<ProjectSimpleDto>> projectRecruitingList() {
        List<ProjectSimpleDto> projectDtoList = new ArrayList<>();

        return new ResponseEntity(projectDtoList, HttpStatus.OK);
    }

    @GetMapping("/recruitment/complete")
    public ResponseEntity projectRecruitingCompleteList() {
        List<ProjectSimpleDto> projectDtoList = new ArrayList<>();

        return new ResponseEntity(projectDtoList, HttpStatus.OK);
    }

    @GetMapping("/{projectNo}")
    public ResponseEntity projectInfo(@PathVariable Long projectNo) {
        return new ResponseEntity(new ProjectDto(), HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity projectSearch(ProjectSearchRequestDto projectSearchRequestDto) {
        List<ProjectSimpleDto> projectDtoList = new ArrayList<>();

        return new ResponseEntity(projectDtoList, HttpStatus.OK);
    }

    @PatchMapping("/{projectNo}")
    public ResponseEntity projectUpdate(ProjectUpdateRequestDto projectUpdateRequestDto) {
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/{projectNo}")
    public ResponseEntity projectDelete(@PathVariable Long projectNo) {
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/participate")
    public ResponseEntity projectParticipateRequest(ProjectParticipateRequestDto projectParticipateRequestDto) {

        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/participate/{projectNo}")
    public ResponseEntity projectParticipateWithdraw(@PathVariable Long projectNo) {
        return new ResponseEntity(HttpStatus.OK);
    }
}
