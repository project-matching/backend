package com.matching.project.dto.project;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectPositionDto {
    private String name;
    private boolean state;
}
