package com.matching.project.dto.user;

import com.matching.project.dto.enumerate.Position;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class UserSimpleInfoDto {
    private Long no;
    private String profile;
    private String name;
    private Position projectPosition;
    private boolean creator;

    public UserSimpleInfoDto(Long no, String name, Position projectPosition, boolean creator) {
        this.no = no;
        this.name = name;
        this.projectPosition = projectPosition;
        this.creator = creator;
    }
}
