package com.matching.project.entity;

import com.matching.project.dto.enumerate.Position;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class ProjectUser {
    @Id @GeneratedValue
    private Long no;

    private boolean creator;

    @Enumerated(EnumType.STRING)
    private Position projectPosition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_no", nullable = false)
    private Project project;
}
