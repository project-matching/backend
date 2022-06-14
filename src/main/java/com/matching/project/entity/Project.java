package com.matching.project.entity;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Project {
    @Id @GeneratedValue
    private Long no;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime createDate;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private boolean state;

    @Lob
    private String introduction;

    @Column(nullable = false)
    private Integer maxPeople;

    @Column(nullable = false)
    private boolean delete;

    @Column(length = 255)
    private String deleteReason;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer count;

    @OneToOne
    @JoinColumn(name = "image_no")
    private Image image;
}
