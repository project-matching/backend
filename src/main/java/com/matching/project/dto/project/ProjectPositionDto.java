package com.matching.project.dto.project;

import com.matching.project.dto.enumerate.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ProjectPositionDto {
    private Position position;
    private List<String> technicalStack = new ArrayList<>();
}
