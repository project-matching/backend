package com.matching.project.dto.project;

import com.matching.project.dto.enumerate.Filter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectSearchDto {
    // 검색 필터
    private Filter filter;
    // 검색 내용
    private String content;
}
