package com.matching.project.controller;

import com.matching.project.dto.bookmark.BookMarkDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/bookmark")
public class BookMarkController {

    @PostMapping("/{projectNo}")
    @ApiOperation(value = "즐겨찾기 추가")
    public ResponseEntity bookMarkRegister(@PathVariable Long projectNo) {
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping
    @ApiOperation(value = "즐겨찾기 목록 조회")
    public ResponseEntity<List<BookMarkDto>> bookMarkList() {
        List<BookMarkDto> bookMarkDtos = new ArrayList<>();
        return new ResponseEntity(bookMarkDtos, HttpStatus.OK);
    }

    @DeleteMapping("/{projectNo}")
    @ApiOperation(value = "즐겨찾기 삭제")
    public ResponseEntity bookMarkDelete(@PathVariable Long projectNo) {
        return new ResponseEntity(HttpStatus.OK);
    }
}
