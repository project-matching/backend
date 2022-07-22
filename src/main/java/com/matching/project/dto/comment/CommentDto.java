package com.matching.project.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentDto {
    private Long commentNo;
    private Long userNo;
    private String registrant;
    private String content;
    private LocalDateTime createDate;
}
