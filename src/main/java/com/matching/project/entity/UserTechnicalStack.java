package com.matching.project.entity;

import javax.persistence.*;

@Entity
public class UserTechnicalStack {
    @Id @GeneratedValue
    private Long no;

    @Column(length = 20, nullable = false)
    private String name;

    //user_position_no
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_position_no")
    private UserPosition userPosition;
}
