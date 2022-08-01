package com.matching.project.entity;

import lombok.*;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
public class Position extends BaseTimeEntity {
    @Id @GeneratedValue
    private Long no;

    @Column(length = 20, nullable = false)
    private String name;

    public void updatePositionName(String updatePositionName) {
        this.name = updatePositionName;
    }
}
