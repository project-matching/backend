package com.matching.project.entity;

import com.matching.project.dto.enumerate.Position;
import com.matching.project.dto.project.ProjectPositionDto;
import com.matching.project.dto.project.ProjectRegisterRequestDto;
import lombok.*;

import javax.persistence.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class ProjectPosition {
    @Id
    @GeneratedValue
    private Long no;

    private boolean state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_no")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_no")
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    private User user;

    private boolean creator;

    @Builder
    public ProjectPosition(Long no, Position position, boolean state, Project project) {
        this.no = no;
        this.position = position;
        this.state = state;
        this.project = project;
    }

    public void setProject(Project project) {
        this.project = project;
        project.getProjectPosition().add(this);
    }

    public static ProjectPosition of(ProjectPositionDto projectPositionDto) {
        return ProjectPosition.builder()
                .position(projectPositionDto.getPosition())
                .state(false)
                .build();
    }
}
