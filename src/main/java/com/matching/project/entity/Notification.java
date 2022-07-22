package com.matching.project.entity;

import com.matching.project.dto.enumerate.Type;

import javax.persistence.*;

@Entity
public class Notification {
    @Id @GeneratedValue
    private Long no;

    @Column(length = 255, nullable = false)
    private String content;

    @Column(length = 100, nullable = false)
    private Long sender;

    @Column(length = 100, nullable = false)
    private Long receiver;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Type type;

    @Column(nullable = true)
    private Long projectParticipateNo;

    @Column(nullable = false)
    private boolean read;
}
