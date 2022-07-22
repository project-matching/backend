package com.matching.project.dto.project;

import com.matching.project.dto.comment.CommentDto;
import com.matching.project.dto.user.UserSimpleInfoDto;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ProjectDto {
    private String name;
    private String profile;
    private LocalDateTime createDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean state;
    private String introduction;
    private Integer maxPeople;
    private boolean bookmark;
    private String register;
    private List<UserSimpleInfoDto> userSimpleInfoDtoList;
    private List<ProjectPositionDto> projectPosition;
    private List<CommentDto> commentDtoList;
}
