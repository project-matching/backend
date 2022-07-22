package com.matching.project.dto.project;

import com.matching.project.dto.enumerate.ProjectFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
@Data
@AllArgsConstructor
public class ProjectSearchRequestDto {
    private ProjectFilter ProjectFilter;
    private String content;
}
