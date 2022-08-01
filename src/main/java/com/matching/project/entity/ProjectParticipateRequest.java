package com.matching.project.entity;

import javax.persistence.*;

@Entity
public class ProjectParticipateRequest extends BaseTimeEntity{
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    private User userNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_position_no")
    private ProjectPosition projectPosition;

    @Column(length = 255)
    private String motive;

    @Column(length = 255)
    private String github;
}
