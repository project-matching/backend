package com.matching.project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TechnicalStack extends BaseTimeEntity{
    @Id @GeneratedValue
    private Long no;

    @Column(length = 50, nullable = false)
    private String name;

    private Long imageNo;

    public void updateTechnicalStack(String name, Long imageNo) {
        this.name = name;
        this.imageNo = imageNo;
    }
}