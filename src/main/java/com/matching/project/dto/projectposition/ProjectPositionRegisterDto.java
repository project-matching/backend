package com.matching.project.dto.projectposition;

import com.matching.project.dto.user.ProjectRegisterUserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPositionRegisterDto {
    @NotNull
    private Long positionNo;

    private ProjectRegisterUserDto projectRegisterUserDto;
}
