package com.matching.project.entity;

import com.matching.project.dto.enumerate.Type;

import javax.persistence.*;

@Entity
public class Notification {
    @Id @GeneratedValue
    private Long no;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(length = 255, nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Type type;


    @Column(nullable = false)
    private boolean read;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    private User user;
}
