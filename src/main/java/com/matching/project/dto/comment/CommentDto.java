package com.matching.project.dto.comment;

import com.matching.project.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.joda.time.LocalDate;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
public class CommentDto {
    private Long commentNo;
    private Long userNo;
    private String registrant;
    private String content;
    private LocalDateTime createDate;

    public static CommentDto toCommentDto(final Comment comment) {
        return CommentDto.builder()
                .commentNo(comment.getNo())
                .userNo(comment.getUser().getNo())
                .registrant(comment.getUser().getName())
                .content(comment.getContent())
                .createDate(comment.getCreatedDate())
                .build();
    }
}
