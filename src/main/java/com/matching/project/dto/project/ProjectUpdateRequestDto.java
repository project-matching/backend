package com.matching.project.dto.project;

import com.matching.project.dto.projectposition.ProjectPositionAddDto;
import com.matching.project.dto.projectposition.ProjectPositionDeleteDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectUpdateRequestDto {
    @NotNull
    private String name;

    private List<ProjectPositionAddDto> projectPositionAddDtoList;

    private List<ProjectPositionDeleteDto> projectPositionDeleteDtoList;

    @NotNull
    private String startDate;

    @NotNull
    private String endDate;

    private List<Long> projectTechnicalStackNoList;

    @NotNull
    private String introduction;
}
