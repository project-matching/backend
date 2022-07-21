package com.matching.project.dto.projectparticipate;

import lombok.Data;

import java.util.List;
@Data
public class ProjectParticipateFormResponseDto {
    private Long projectParticipateNo;
    private String userName;
    private String positionName;
    private List<String> technicalStackList;
    private String motive;
}
