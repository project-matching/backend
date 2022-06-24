package com.matching.project.entity;

import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserTechnicalStack {
    @Id @GeneratedValue
    private Long no;

    @Column(length = 20, nullable = false)
    private String name;

    //user_position_no
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_position_no")
    private UserPosition userPosition;
}
