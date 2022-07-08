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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technical_stack_no")
    private TechnicalStack technicalStack;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_no")
    private Project project;

    @Builder
    public ProjectTechnicalStack(Long no, String name, ProjectPosition projectPosition) {
        this.no = no;
        this.name = name;
        this.projectPosition = projectPosition;
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
