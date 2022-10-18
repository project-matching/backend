package com.matching.project.dto.project;

import com.matching.project.dto.comment.CommentDto;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    private Long projectNo;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean state;
    private String introduction;
    private Integer currentPeople;
    private Integer maxPeople;
    private boolean bookmark;
    private boolean applicationStatus;
    private boolean participateStatus;

    private List<String> technicalStackList = new ArrayList<>();
    private List<ProjectPositionDetailDto> projectPositionDetailDtoList = new ArrayList<>();

    @Builder
    public ProjectDto(Long projectNo, String name, LocalDate startDate, LocalDate endDate, boolean state, String introduction, Integer currentPeople, Integer maxPeople, boolean bookmark, boolean applicationStatus, boolean participateStatus) {
        this.projectNo = projectNo;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.state = state;
        this.introduction = introduction;
        this.currentPeople = currentPeople;
        this.maxPeople = maxPeople;
        this.bookmark = bookmark;
        this.applicationStatus = applicationStatus;
        this.participateStatus = participateStatus;
    }
}
