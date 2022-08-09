package com.matching.project.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.matching.project.dto.project.ProjectRegisterRequestDto;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Project extends BaseTimeEntity{
    @Id @GeneratedValue
    private Long no;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 50, nullable = false)
    private String createUserName;

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
    private Integer currentPeople;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer viewCount;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer commentCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    private User user;

    @OneToMany(mappedBy = "project")
    private List<ProjectPosition> projectPositionList = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<ProjectTechnicalStack> projectTechnicalStackList = new ArrayList<>();

    @Builder
    public Project(Long no, String name, String createUserName, LocalDate startDate, LocalDate endDate, boolean state, String introduction, Integer maxPeople, Integer currentPeople, Integer viewCount, Integer commentCount, User user) {
        this.no = no;
        this.name = name;
        this.createUserName = createUserName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.state = state;
        this.introduction = introduction;
        this.maxPeople = maxPeople;
        this.currentPeople = currentPeople;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
        this.user = user;
    }

    public static Project of(ProjectRegisterRequestDto projectRegisterRequestDto, User user) {
        return Project.builder()
                .name(projectRegisterRequestDto.getName())
                .createUserName(user.getName())
                .startDate(projectRegisterRequestDto.getStartDate())
                .endDate(projectRegisterRequestDto.getEndDate())
                .state(true)
                .introduction(projectRegisterRequestDto.getIntroduction())
                .maxPeople(projectRegisterRequestDto.getProjectPositionRegisterDtoList().size())
                .currentPeople(1)
                .viewCount(0)
                .commentCount(0)
                .user(user)
                .build();
    }
    
    // 프로젝트 업데이트 메소드
    public void updateProject(String name, LocalDate startDate, LocalDate endDate, String introduction) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.introduction = introduction;
    }
}
