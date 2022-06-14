package com.matching.project.entity;

import javax.persistence.*;

@Entity
public class Comment {
    @Id @GeneratedValue
    private Long no;

    @Column(length = 255, nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_no")
    private Project project;
}
