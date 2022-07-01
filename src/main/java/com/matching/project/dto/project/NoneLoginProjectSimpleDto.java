package com.matching.project.dto.project;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class NoneLoginProjectSimpleDto {
    private Long no;
    private String name;
    private String profile;
    private Integer maxPeople;
    private Integer currentPeople;
    private Integer viewCount;
    private Integer commentCount;
    private String register;
}
