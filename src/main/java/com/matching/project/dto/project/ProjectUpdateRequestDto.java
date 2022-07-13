package com.matching.project.dto.project;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ProjectUpdateRequestDto {
    private String name;
    private String profile;
    private LocalDate startDate;
    private LocalDate endDate;
    private String introduction;
    private Integer maxPeople;
    private List<ProjectPositionUpdateDto> projectPositionDtoList = new ArrayList<>();
    private List<String> projectTechnicalStack = new ArrayList<>();

    @Builder
    public ProjectUpdateRequestDto(String name, String profile, LocalDate startDate, LocalDate endDate, String introduction, Integer maxPeople) {
        this.name = name;
        this.profile = profile;
        this.startDate = startDate;
        this.endDate = endDate;
        this.introduction = introduction;
        this.maxPeople = maxPeople;
    }
}
