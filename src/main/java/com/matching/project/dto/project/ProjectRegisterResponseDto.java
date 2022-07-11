package com.matching.project.dto.project;

import com.matching.project.entity.Project;
import com.matching.project.entity.ProjectPosition;
import com.matching.project.entity.ProjectTechnicalStack;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProjectRegisterResponseDto {
    private Long no;
    private String name;
    private String createUser;
    private String profile;
    private LocalDateTime createDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean state;
    private String introduction;
    private Integer maxPeople;
    private Integer currentPeople;
    private Integer viewCount;
    private Integer commentCount;

    private List<ProjectPositionDto> projectPositionDtoList = new ArrayList<>();

    private List<String> projectTechnicalStack = new ArrayList<>();

    @Builder
    public ProjectRegisterResponseDto(Long no, String name, String createUser, String profile, LocalDateTime createDate, LocalDate startDate, LocalDate endDate, boolean state, String introduction, Integer maxPeople, Integer currentPeople, Integer viewCount, Integer commentCount) {
        this.no = no;
        this.name = name;
        this.createUser = createUser;
        this.profile = profile;
        this.createDate = createDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.state = state;
        this.introduction = introduction;
        this.maxPeople = maxPeople;
        this.currentPeople = currentPeople;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
    }
}
