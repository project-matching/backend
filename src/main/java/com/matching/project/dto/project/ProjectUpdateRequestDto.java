package com.matching.project.dto.project;

import com.matching.project.dto.projectposition.ProjectPositionAddDto;
import com.matching.project.dto.projectposition.ProjectPositionDeleteDto;

import java.time.LocalDateTime;
import java.util.List;

public class ProjectUpdateRequestDto {
    private String name;
    private List<ProjectPositionAddDto> projectPositionAddDtoList;
    private List<ProjectPositionDeleteDto> projectPositionDeleteDtoList;
    private String startDate;
    private String endDate;
    private List<Long> projectTechnicalStackNoList;
    private String introduction;
}
