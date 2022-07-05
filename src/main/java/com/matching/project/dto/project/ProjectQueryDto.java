package com.matching.project.dto.project;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ProjectQueryDto {
    private Long no;
    private String name;
    private String createUserName;
    private LocalDateTime createDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean state;
    private String introduction;
    private int maxPeople;
    private int currentPeople;
    private boolean delete;
    private String deleteReason;
    private int viewCount;
    private int commentCount;
    private List<ProjectPositionQueryDto> projectPositionList = new ArrayList<>();

    public ProjectQueryDto(Long no, String name, String createUserName, LocalDateTime createDate, LocalDate startDate, LocalDate endDate, boolean state, String introduction, int maxPeople, int currentPeople, boolean delete, String deleteReason, int viewCount, int commentCount) {
        this.no = no;
        this.name = name;
        this.createUserName = createUserName;
        this.createDate = createDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.state = state;
        this.introduction = introduction;
        this.maxPeople = maxPeople;
        this.currentPeople = currentPeople;
        this.delete = delete;
        this.deleteReason = deleteReason;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
    }
}
