package com.matching.project.dto.technicalstack;

import com.matching.project.entity.UserTechnicalStack;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TechnicalStackDto {
    private String image;
    private String name;
}
