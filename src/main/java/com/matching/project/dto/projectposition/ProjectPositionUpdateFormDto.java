package com.matching.project.dto.projectposition;

import com.matching.project.dto.user.ProjectUpdateFormUserDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectPositionUpdateFormDto {
    private Long projectPositionNo;
    private Long positionNo;
    private String projectPositionName;
    private ProjectUpdateFormUserDto projectUpdateFormUserDto;
}
