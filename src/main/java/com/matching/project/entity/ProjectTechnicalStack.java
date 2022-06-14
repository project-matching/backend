package com.matching.project.entity;

import javax.persistence.*;

@Entity
public class ProjectTechnicalStack {
    @Id
    @GeneratedValue
    private Long no;

    @Column(length = 20, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_position_no")
    private ProjectPosition projectPosition;
}
