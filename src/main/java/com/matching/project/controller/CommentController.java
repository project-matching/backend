package com.matching.project.controller;

import com.matching.project.dto.comment.CommentDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/comment")
public class CommentController {
    @PostMapping("/{projectNo}")
    public ResponseEntity<String> commentRegister(@PathVariable Long projectNo, CommentDto commentDto) {
        return new ResponseEntity("댓글 등록 완료", HttpStatus.OK);
    }

    @GetMapping("/{projectNo}")
    public ResponseEntity<List<CommentDto>> commentList(@PathVariable Long projectNo) {
        List<CommentDto> commentDtos = new ArrayList<>();
        return new ResponseEntity<>(commentDtos, HttpStatus.OK);
    }

    @PatchMapping("/{commentNo}")
    public ResponseEntity<String> commentUpdate(@PathVariable Long commentNo, CommentDto commentDto) {
        return new ResponseEntity("댓글 수정 완료", HttpStatus.OK);
    }

    @DeleteMapping("/{commentNo}")
    public ResponseEntity<String> commentDelete(@PathVariable Long commentNo) {
        return new ResponseEntity("댓글 삭제 완료", HttpStatus.OK);
    }
}
