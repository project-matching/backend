package com.matching.project.controller;

import com.matching.project.dto.ResponseDto;
import com.matching.project.dto.comment.CommentDto;
import com.matching.project.entity.Comment;
import com.matching.project.service.CommentService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{projectNo}")
    @ApiOperation(value = "댓글 등록")
    public ResponseEntity<ResponseDto<Boolean>> commentRegister(@PathVariable Long projectNo, String content) {
        Comment comment = commentService.commentRegister(projectNo, content);
        return ResponseEntity.ok(new ResponseDto(null, true));
    }

    @GetMapping("/{projectNo}")
    @ApiOperation(value = "댓글 조회")
    public ResponseEntity<ResponseDto<List<CommentDto>>> commentList(@PageableDefault(size = 5) Pageable pageable, @PathVariable Long projectNo) {
        List<CommentDto> commentDtos = commentService.commentList(pageable, projectNo);
        return ResponseEntity.ok(new ResponseDto(null, commentDtos));
    }

    @PatchMapping("/{commentNo}")
    @ApiOperation(value = "댓글 수정")
    public ResponseEntity<ResponseDto<Boolean>> commentUpdate(@PathVariable Long commentNo, String content) {
        Comment comment = commentService.commentUpdate(commentNo, content);
        return ResponseEntity.ok(new ResponseDto(null, true));
    }

    @DeleteMapping("/{commentNo}")
    @ApiOperation(value = "댓글 삭제")
    public ResponseEntity<ResponseDto<Boolean>> commentDelete(@PathVariable Long commentNo) {
        commentService.commentDelete(commentNo);
        return ResponseEntity.ok(new ResponseDto(null, true));
    }
}
