package com.matching.project.controller;

import com.matching.project.dto.ResponseDto;
import com.matching.project.dto.SliceDto;
import com.matching.project.dto.bookmark.BookMarkDto;
import com.matching.project.dto.project.ProjectSimpleDto;
import com.matching.project.service.BookMarkService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/bookmark")
public class BookMarkController {
    private final BookMarkService bookMarkService;

    @PostMapping("/{projectNo}")
    @ApiOperation(value = "즐겨찾기 추가 (수정 완료)")
    public ResponseEntity<ResponseDto<Boolean>> bookMarkRegister(@PathVariable Long projectNo) throws Exception {
        return ResponseEntity.ok(new ResponseDto<Boolean>(null, bookMarkService.bookMarkRegister(projectNo)));
    }

    @GetMapping
    @ApiOperation(value = "즐겨찾기 프로젝트 목록 조회")
    public ResponseEntity<ResponseDto<SliceDto<ProjectSimpleDto>>> bookMarkList(@PageableDefault(size = 5, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable, @RequestParam(value = "projectNo", required = false) Long projectNo) throws Exception{
        return ResponseEntity.ok(new ResponseDto<>(null, bookMarkService.findBookMarkProject(projectNo, pageable)));
    }

    @DeleteMapping("/{projectNo}")
    @ApiOperation(value = "즐겨찾기 삭제 (수정 완료)")
    public ResponseEntity<ResponseDto<Boolean>> bookMarkDelete(@PathVariable Long projectNo) throws Exception {
        return ResponseEntity.ok(new ResponseDto<Boolean>(null, bookMarkService.bookMarkDelete(projectNo)));
    }
}
