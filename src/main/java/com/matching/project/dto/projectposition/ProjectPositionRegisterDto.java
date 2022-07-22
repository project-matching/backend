package com.matching.project.dto.projectposition;

import com.matching.project.dto.user.ProjectRegisterUserDto;
import lombok.Data;

@Data
public class ProjectPositionRegisterDto {
    private Long positionNo;
    private ProjectRegisterUserDto projectRegisterUserDto;
}
