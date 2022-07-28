package com.matching.project.service;

import com.matching.project.dto.comment.CommentDto;
import com.matching.project.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface CommentService {
    List<CommentDto> commentList(Pageable pageable, Long projectNo);
    Comment commentRegister(Long projectNo, String content);
    Comment commentUpdate(Long commentNo, String content);
    void commentDelete(Long commentNo);
}
