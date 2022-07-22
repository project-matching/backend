package com.matching.project.dto.project;

import com.matching.project.dto.enumerate.Position;
import lombok.Getter;

import java.util.List;

@Getter
public class ProjectPositionDto {
    private Position position;
    private List<String> technicalStack;
}
