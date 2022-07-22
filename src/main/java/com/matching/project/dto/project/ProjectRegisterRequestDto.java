package com.matching.project.dto.project;

import com.matching.project.dto.projectposition.ProjectPositionRegisterDto;
import com.matching.project.entity.Project;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ProjectRegisterRequestDto {
    @NotBlank
    private String name;

    @NotBlank
    private LocalDate startDate;

    @NotBlank
    private LocalDate endDate;

    @NotNull
    private String introduction;

    @NotNull
    @Size(min = 0)
    private List<ProjectPositionRegisterDto> projectPositionRegisterDtoList;

    @NotNull
    @Size(min = 0)
    private List<Long> projectTechnicalStackList;
}
