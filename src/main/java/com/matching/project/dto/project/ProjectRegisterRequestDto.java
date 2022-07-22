package com.matching.project.dto.project;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Lob;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ProjectRegisterRequestDto {
    private String name;
    private String profile;
    private LocalDateTime createDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String introduction;
    private Integer maxPeople;
    private List<ProjectPositionDto> projectPosition;
}
