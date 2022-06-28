package com.matching.project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class ProjectTechnicalStack {
    @Id
    @GeneratedValue
    private Long no;

    @Column(length = 20, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_position_no")
    private ProjectPosition projectPosition;
    @Builder
    public ProjectTechnicalStack(Long no, String name) {
        this.no = no;
        this.name = name;
    }

    public void setProjectPosition(ProjectPosition projectPosition) {
        this.projectPosition = projectPosition;
        projectPosition.getProjectTechnicalStack().add(this);
    }

    public static ProjectTechnicalStack of(String technicalStack) {
        return ProjectTechnicalStack.builder()
                .name(technicalStack)
                .build();
    }
}
