package com.matching.project.dto.project;

import com.matching.project.entity.Project;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ProjectRegisterRequestDto {
    private String name;
    private String profile;
    private LocalDateTime createDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String introduction;
    private Integer maxPeople;
    private List<ProjectPositionDto> projectPosition = new ArrayList<>();
}
