package com.matching.project.dto.project;

import com.matching.project.dto.comment.CommentDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
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
    private List<String> technicalStack = new ArrayList<>();
    private List<ProjectPositionDetailDto> projectPositionDetailDtoList = new ArrayList<>();

//    @Builder
//    public ProjectDto(String name, String profile, LocalDateTime createDate, LocalDate startDate, LocalDate endDate, boolean state, String introduction, Integer maxPeople, boolean bookmark, String register) {
//        this.name = name;
//        this.profile = profile;
//        this.createDate = createDate;
//        this.startDate = startDate;
//        this.endDate = endDate;
//        this.state = state;
//        this.introduction = introduction;
//        this.maxPeople = maxPeople;
//        this.bookmark = bookmark;
//        this.register = register;
//    }
}
