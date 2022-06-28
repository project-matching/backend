package com.matching.project.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.matching.project.dto.project.ProjectRegisterRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Project {
    @Id @GeneratedValue
    private Long no;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime createDate;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

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

    @OneToMany(mappedBy = "project")
    private List<ProjectPosition> projectPosition = new ArrayList<>();

    @Builder
    public Project(Long no, String name, LocalDateTime createDate, LocalDate startDate, LocalDate endDate, boolean state, String introduction, Integer maxPeople, boolean delete, String deleteReason, Image image, Integer count) {
        this.no = no;
        this.name = name;
        this.createDate = createDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.state = state;
        this.introduction = introduction;
        this.maxPeople = maxPeople;
        this.delete = delete;
        this.deleteReason = deleteReason;
        this.image = image;
        this.count = count;
    }

    public static Project of(ProjectRegisterRequestDto projectRegisterRequestDto) {
        return Project.builder()
                .name(projectRegisterRequestDto.getName())
                .createDate(projectRegisterRequestDto.getCreateDate())
                .startDate(projectRegisterRequestDto.getStartDate())
                .endDate(projectRegisterRequestDto.getEndDate())
                .state(true)
                .introduction(projectRegisterRequestDto.getIntroduction())
                .maxPeople(projectRegisterRequestDto.getMaxPeople())
                .delete(false)
                .deleteReason(null)
                .count(0)
                .image(null)
                .build();
    }
}
