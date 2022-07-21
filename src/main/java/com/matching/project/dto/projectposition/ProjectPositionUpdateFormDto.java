package com.matching.project.dto.projectposition;

import com.matching.project.dto.user.ProjectUpdateFormUserDto;
import lombok.Data;

@Data
public class ProjectPositionUpdateFormDto {
    private Long projectPositionNo;
    private Long positionNo;
    private Long projectPositionName;
    private ProjectUpdateFormUserDto projectUpdateFormUserDto;
}
