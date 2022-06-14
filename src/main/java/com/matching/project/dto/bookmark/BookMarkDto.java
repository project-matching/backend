package com.matching.project.dto.bookmark;

import lombok.Getter;

@Getter
public class BookMarkDto {
    private String profile;
    private String projectName;
    private boolean state;
    private int viewCount;
    private int commentCount;
    private int maxPeople;
    private int currentPeople;
    private String creator;
}
