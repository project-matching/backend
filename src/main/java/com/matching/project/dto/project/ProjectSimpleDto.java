package com.matching.project.dto.project;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ProjectSimpleDto {
    private Long no;
    private String name;
    private String profile;
    private boolean bookmark;
    private Integer maxPeople;
    private Integer currentPeople;
    private Integer viewCount;
    private Integer commentCount;
    private String register;
}
