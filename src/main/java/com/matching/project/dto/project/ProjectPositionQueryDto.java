package com.matching.project.dto.project;

import lombok.Data;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProjectPositionQueryDto {
    private Long no;
    private boolean state;
    private List<ProjectTechnicalStackQueryDto> projectTechnicalStackList = new ArrayList<>();

}
