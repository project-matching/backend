package com.matching.project.dto.user;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserSimpleInfoDto {
    private Long no;
    private String profile;
    private String name;
    private boolean creator;
}
