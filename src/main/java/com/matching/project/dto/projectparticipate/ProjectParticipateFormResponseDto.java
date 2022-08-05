package com.matching.project.dto.projectparticipate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectParticipateFormResponseDto {
    private Long projectParticipateNo;
    private String userName;
    private String positionName;
    private List<String> technicalStackList = new ArrayList<>();
    private String motive;

    public ProjectParticipateFormResponseDto(Long projectParticipateNo, String userName, String positionName, String motive) {
        this.projectParticipateNo = projectParticipateNo;
        this.userName = userName;
        this.positionName = positionName;
        this.motive = motive;
    }
}
