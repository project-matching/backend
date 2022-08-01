package com.matching.project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipateRequestTechnicalStack extends BaseTimeEntity {
    @Id @GeneratedValue
    private Long no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technical_stack_no")
    private TechnicalStack technicalStack;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_participate_request_no")
    private ProjectParticipateRequest projectParticipateRequest;
}
