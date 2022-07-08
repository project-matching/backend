package com.matching.project.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Position {
    @Id @GeneratedValue
    private Long no;

    @Column(length = 20, nullable = false)
    private String name;
}
