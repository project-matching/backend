package com.matching.project.entity;

import com.matching.project.dto.enumerate.Position;
import com.matching.project.dto.project.ProjectPositionDto;
import com.matching.project.dto.project.ProjectRegisterRequestDto;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class ProjectPosition {
    @Id
    @GeneratedValue
    private Long no;

    @Column(length = 20, nullable = false)
    private String name;

    private boolean state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_no")
    private Project project;

    @OneToMany(mappedBy = "projectPosition")
    private List<ProjectTechnicalStack> projectTechnicalStack = new ArrayList<>();

    @Builder
    public ProjectPosition(Long no, String name, boolean state, Project project) {
        this.no = no;
        this.name = name;
        this.state = state;
        this.project = project;
    }

    public void setProject(Project project) {
        this.project = project;
        project.getProjectPosition().add(this);
    }

    public static ProjectPosition of(ProjectPositionDto projectPositionDto) {
        return ProjectPosition.builder()
                .name(projectPositionDto.getPosition().toString())
                .state(false)
                .build();
    }
}
