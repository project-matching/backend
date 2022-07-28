package com.matching.project.dto.position;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PositionRequestDto {
    @NotBlank
    String positionName;
}
