package com.matching.project.dto.projectparticipate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipateRequestTechnicalStackDto {
    private Long projectParticipateNo;
    private String technicalsTackName;
}
