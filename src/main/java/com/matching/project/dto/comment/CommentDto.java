package com.matching.project.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentDto {
    private Long no;
    private String registrant;
    private String content;
}
