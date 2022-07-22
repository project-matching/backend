package com.matching.project.dto.project;

import com.matching.project.dto.enumerate.Filter;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
@Data
public class ProjectSearchRequestDto {
    private Filter filter;
    private String content;
}
