package com.matching.project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Image extends BaseTimeEntity{
    @Id @GeneratedValue
    private Long no;

    @Column(length = 255, nullable = false)
    private String logicalName;

    @Column(length = 255, nullable = false)
    private String physicalName;

    @Column(length = 255, nullable = false)
    private String url;
}
