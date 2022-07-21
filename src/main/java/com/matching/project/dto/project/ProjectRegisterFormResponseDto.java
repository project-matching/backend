package com.matching.project.dto.project;

import lombok.Data;

import java.util.List;
@Data
public class ProjectRegisterFormResponseDto {
    private List<PositionRegisterFormDto> positionRegisterFormDtoList;
    private List<TechnicalStackRegisterFormDto> technicalStackRegisterFormDtoList;
}
