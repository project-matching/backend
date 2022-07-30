package com.matching.project.entity;

import lombok.Builder;

import javax.persistence.*;

@Entity
@Builder
public class ProjectParticipateRequest {
    @Id @GeneratedValue
    private Long no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_position_no")
    private ProjectPosition projectPosition;

    @Column(length = 255)
    private String motive;

    @Column(length = 255)
    private String github;
}
