package com.matching.project.dto.project;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProjectSimpleDto {
    private Long projectNo;
    private String name;
    private Integer maxPeople;
    private Integer currentPeople;
    private Integer viewCount;
    private String register;
    private boolean bookMark;
    private List<ProjectSimplePositionDto> projectSimplePositionDtoList;
    private List<ProjectSimpleTechnicalStackDto> projectSimpleTechnicalStackDtoList;

//    public ProjectSimpleDto(Long no, String name, Integer maxPeople, Integer currentPeople, Integer viewCount, Integer commentCount, String register) {
//        this.no = no;
//        this.name = name;
//        this.maxPeople = maxPeople;
//        this.currentPeople = currentPeople;
//        this.viewCount = viewCount;
//        this.commentCount = commentCount;
//        this.register = register;
//    }

//    @Builder
//    public ProjectSimpleDto(Long no, String name, Integer maxPeople, Integer currentPeople, Integer viewCount, Integer commentCount, String register, boolean bookMark) {
//        this.no = no;
//        this.name = name;
//        this.maxPeople = maxPeople;
//        this.currentPeople = currentPeople;
//        this.viewCount = viewCount;
//        this.commentCount = commentCount;
//        this.register = register;
//        this.bookMark = bookMark;
//    }
}

