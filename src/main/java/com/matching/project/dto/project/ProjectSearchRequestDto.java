package com.matching.project.dto.project;

import com.matching.project.dto.enumerate.ProjectSearchFilter;
import lombok.Getter;

@Getter
public class ProjectSearchRequestDto {
    private ProjectSearchFilter projectSearchFilter;
    private String content;
}
