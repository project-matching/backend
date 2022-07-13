package com.matching.project.dto.project;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectPositionUpdateDto {
    private Long no;
    private String name;
    private UserUpdateDto userUpdateDto;
}
