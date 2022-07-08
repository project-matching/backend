package com.matching.project.entity;

import com.matching.project.dto.enumerate.Position;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserPosition {
    @Id @GeneratedValue
    private Long no;

    @Column(length = 20, nullable = false)
    private String name;

    public UserPosition updatePosition(final Position position) {
        this.name = position.toString();
        return this;
    }
}
