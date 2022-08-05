package com.matching.project.dto.technicalstack;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TechnicalStackRegisterFormResponseDto {
    private Long technicalStackNo;
    private String technicalStackName;
    private String image;
}
