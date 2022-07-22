package com.matching.project.dto.project;

import com.matching.project.dto.projectposition.ProjectPositionRegisterDto;
import com.matching.project.entity.Project;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRegisterRequestDto {
    @NotBlank
    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate endDate;

    @NotBlank
    private String introduction;

    @Valid
    @NotNull
    @Size(min = 0)
    private List<ProjectPositionRegisterDto> projectPositionRegisterDtoList;

    @NotNull
    @Size(min = 0)
    private List<Long> projectTechnicalStackList;
}
