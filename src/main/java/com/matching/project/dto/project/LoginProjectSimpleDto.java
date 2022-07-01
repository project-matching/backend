package com.matching.project.dto.project;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginProjectSimpleDto {
    private Long no;
    private String name;
    private String profile;
    private Integer maxPeople;
    private Integer currentPeople;
    private Integer viewCount;
    private Integer commentCount;
    private String register;
    private boolean bookMark;
}

