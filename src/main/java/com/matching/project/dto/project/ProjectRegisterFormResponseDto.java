package com.matching.project.dto.project;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class ProjectRegisterFormResponseDto {
    private List<PositionRegisterFormDto> positionRegisterFormDtoList;
    private List<TechnicalStackRegisterFormDto> technicalStackRegisterFormDtoList;
}
