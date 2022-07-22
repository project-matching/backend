package com.matching.project.dto.project;

import com.matching.project.dto.projectposition.ProjectPositionUpdateFormDto;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Data
public class ProjectUpdateFormResponseDto {
    private Long projectNo;
    private String name;
    private boolean state;
    private LocalDate startDate;
    private LocalDate endDate;
    private String introduction;
    private List<PositionUpdateFormDto> positionUpdateFormDto;
    private List<ProjectPositionUpdateFormDto> projectPositionUpdateFormDtoList;
    private List<TechnicalStackUpdateFormDto> technicalStackUpdateFormDtoList;
    private List<String> projectTechnicalStack;
}
