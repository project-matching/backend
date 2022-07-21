package com.matching.project.dto.project;

import com.matching.project.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ProjectPositionDetailDto {
    private Long projectPositionNo;
    private String positionName;
    private UserDto userDto;
}
