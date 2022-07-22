package com.matching.project.dto.project;

import lombok.Data;

import java.util.List;
@Data
public class AdminProjectSimpleDto {
    private Long projectNo;
    private String name;
    private Integer maxPeople;
    private Integer currentPeople;
    private Integer viewCount;
    private String register;
    private boolean block;
    private List<ProjectSimplePositionDto> projectSimplePositionDtoList;
    private List<ProjectSimpleTechnicalStackDto> projectSimpleTechnicalStackDtoList;
}
