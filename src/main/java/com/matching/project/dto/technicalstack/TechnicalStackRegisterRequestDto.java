package com.matching.project.dto.technicalstack;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TechnicalStackRegisterRequestDto {
    @NotBlank
    String technicalStackName;
}
