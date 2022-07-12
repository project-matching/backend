package com.matching.project.dto.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ProjectSimplePositionDto {
    private Long projectNo;
    private String positionName;
    private String image;
    private boolean state;

    public ProjectSimplePositionDto(Long projectNo, String positionName, boolean state) {
        this.projectNo = projectNo;
        this.positionName = positionName;
        this.state = state;
    }
}
