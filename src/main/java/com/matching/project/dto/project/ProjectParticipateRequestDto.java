package com.matching.project.dto.project;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectParticipateRequestDto {
    @NotNull
    private Long projectPositionNo;
    private List<String> technicalStackList;
    private String gitHub;
    private String motive;
}
