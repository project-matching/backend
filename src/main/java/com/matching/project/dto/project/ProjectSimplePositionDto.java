package com.matching.project.dto.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ProjectSimplePositionDto {
    private Long projectNo;
    private Long positionNo;
    private String positionName;
}
