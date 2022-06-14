package com.matching.project.entity;

import javax.persistence.*;

@Entity
public class ProjectUser {
    @Id @GeneratedValue
    private Long id;

    private boolean creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    private User userNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_no", nullable = false)
    private Project projectNo;
}
