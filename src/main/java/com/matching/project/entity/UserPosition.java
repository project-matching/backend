package com.matching.project.entity;

import javax.persistence.*;

@Entity
public class UserPosition {
    @Id @GeneratedValue
    private Long no;

    @Column(length = 20, nullable = false)
    private String name;
}
