package com.matching.project.dto.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ProjectSimpleTechnicalStackDto {
    private Long projectNo;
    private String image;
    private String technicalStackName;
}
