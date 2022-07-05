package com.matching.project.dto.project;

import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProjectPositionQueryDto {
    public ProjectPositionQueryDto(Long no, String name, boolean state) {
        this.no = no;
        this.name = name;
        this.state = state;
    }

    private Long no;
    private String name;
    private boolean state;
    private List<ProjectTechnicalStackQueryDto> projectTechnicalStackList = new ArrayList<>();
}
