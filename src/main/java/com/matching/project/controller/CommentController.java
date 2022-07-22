package com.matching.project.controller;

import com.matching.project.dto.ResponseDto;
import com.matching.project.dto.comment.CommentDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
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
    @ApiOperation(value = "댓글 등록 (수정 완료)")
    public ResponseEntity<ResponseDto<Boolean>> commentRegister(@PathVariable Long projectNo, String content) {
        return ResponseEntity.ok(new ResponseDto(null, true));
    }

    @GetMapping("/{projectNo}")
    @ApiOperation(value = "댓글 조회 (수정 완료)")
    public ResponseEntity<ResponseDto<List<CommentDto>>> commentList(Pageable pageable, @PathVariable Long projectNo) {
        List<CommentDto> commentDtos = new ArrayList<>();
        return ResponseEntity.ok(new ResponseDto(null, commentDtos));
    }

    @PatchMapping("/{commentNo}")
    @ApiOperation(value = "댓글 수정 (수정 완료)")
    public ResponseEntity<ResponseDto<Boolean>> commentUpdate(@PathVariable Long commentNo, String content) {
        return ResponseEntity.ok(new ResponseDto(null, true));
    }

    @DeleteMapping("/{commentNo}")
    @ApiOperation(value = "댓글 삭제 (수정 완료)")
    public ResponseEntity<ResponseDto<Boolean>> commentDelete(@PathVariable Long commentNo) {
        return ResponseEntity.ok(new ResponseDto(null, true));
    }
}
