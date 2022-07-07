package com.matching.project.dto.project;

import com.matching.project.dto.enumerate.Position;
import lombok.Data;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProjectPositionQueryDto {
    private Long no;
    private Position position;
    private boolean state;
    private List<ProjectTechnicalStackQueryDto> projectTechnicalStackList = new ArrayList<>();

    public ProjectPositionQueryDto(Long no, Position position, boolean state) {
        this.no = no;
        this.position = position;
        this.state = state;
    }

    public static ProjectPositionDto toProjectPositionDto(ProjectPositionQueryDto projectPositionQueryDto) {
        List<String> technicalStackList = projectPositionQueryDto.getProjectTechnicalStackList().stream()
                .map(projectTechnicalStackQueryDto -> projectTechnicalStackQueryDto.getName())
                .collect(Collectors.toList());

        return new ProjectPositionDto(projectPositionQueryDto.getPosition(), technicalStackList);
    }
}
