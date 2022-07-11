package com.matching.project.dto.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ProjectPositionDetailDto {
    private String positionName;
    private Long userNo;
    private String userName;
    private boolean state;
}
