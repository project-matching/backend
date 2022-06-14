package com.matching.project.controller;

import com.matching.project.dto.bookmark.BookMarkDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/bookmark")
public class BookMarkController {
    // 유저 no, 프로젝트 no
    @PostMapping("/{projectNo}")
    public ResponseEntity bookMarkRegister(@PathVariable Long projectNo) {
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<BookMarkDto>> bookMarkList() {
        List<BookMarkDto> bookMarkDtos = new ArrayList<>();
        return new ResponseEntity(bookMarkDtos, HttpStatus.OK);
    }

    @DeleteMapping("/{projectNo}")
    public ResponseEntity bookMarkDelete(@PathVariable Long projectNo) {
        return new ResponseEntity(HttpStatus.OK);
    }
}
