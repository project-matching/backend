package com.matching.project.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class Image {
    @Id @GeneratedValue
    private Long no;

    @Column(length = 255, nullable = false)
    private String logicalName;

    @Column(length = 255, nullable = false)
    private String physicalName;
}
