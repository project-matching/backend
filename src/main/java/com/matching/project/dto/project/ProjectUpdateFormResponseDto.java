package com.matching.project.dto.project;

import com.matching.project.dto.projectposition.ProjectPositionUpdateFormDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Data
@NoArgsConstructor
public class ProjectUpdateFormResponseDto {
    private Long projectNo;
    private String name;
    private boolean state;
    private LocalDate startDate;
    private LocalDate endDate;
    private String introduction;
    private List<PositionUpdateFormDto> positionUpdateFormDtoList;
    private List<ProjectPositionUpdateFormDto> projectPositionUpdateFormDtoList;
    private List<TechnicalStackUpdateFormDto> technicalStackUpdateFormDtoList;
    private List<String> projectTechnicalStackList;

    @Builder
    public ProjectUpdateFormResponseDto(Long projectNo, String name, boolean state, LocalDate startDate, LocalDate endDate, String introduction) {
        this.projectNo = projectNo;
        this.name = name;
        this.state = state;
        this.startDate = startDate;
        this.endDate = endDate;
        this.introduction = introduction;
    }
}
