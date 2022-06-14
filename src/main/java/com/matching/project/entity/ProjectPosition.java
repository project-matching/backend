package com.matching.project.entity;

import javax.persistence.*;

@Entity
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
}
