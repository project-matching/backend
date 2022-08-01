package com.matching.project.entity;

import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserTechnicalStack extends BaseTimeEntity{
    @Id @GeneratedValue
    private Long no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technical_stack_no")
    private TechnicalStack technicalStack;
}
