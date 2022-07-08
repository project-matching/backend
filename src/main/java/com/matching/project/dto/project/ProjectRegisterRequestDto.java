package com.matching.project.dto.project;

import com.matching.project.entity.Project;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ProjectRegisterRequestDto {
    @NotBlank
    private String name;

    private String profile;

    @NotBlank
    private LocalDateTime createDate;

    @NotBlank
    private LocalDate startDate;

    @NotBlank
    private LocalDate endDate;

    @NotNull
    private String introduction;

    @NotBlank
    private Integer maxPeople;


    private List<ProjectPositionDto> projectPosition = new ArrayList<>();
}
