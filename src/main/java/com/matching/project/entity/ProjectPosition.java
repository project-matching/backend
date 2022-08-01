package com.matching.project.entity;

import com.matching.project.dto.project.ProjectRegisterRequestDto;
import lombok.*;

import javax.persistence.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPosition extends BaseTimeEntity{
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

    public void setProject(Project project) {
        this.project = project;
        project.getProjectPositionList().add(this);
    }
}
