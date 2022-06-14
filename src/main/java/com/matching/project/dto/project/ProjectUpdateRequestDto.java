package com.matching.project.dto.project;

import java.time.LocalDateTime;
import java.util.List;

public class ProjectUpdateRequestDto {
    private String name;
    private String profile;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String introduction;
    private Integer maxPeople;
    private List<ProjectPositionDto> projectPosition;
}
