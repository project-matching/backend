package com.matching.project.dto.project;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Data
public class AdminProjectDto {
    private Long projectNo;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean state;
    private String introduction;
    private Integer currentPeople;
    private Integer maxPeople;
    private boolean block;
    private List<String> technicalStack = new ArrayList<>();
    private List<ProjectPositionDetailDto> projectPositionDetailDtoList = new ArrayList<>();
}
