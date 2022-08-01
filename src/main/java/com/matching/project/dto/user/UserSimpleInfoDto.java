package com.matching.project.dto.user;

import com.matching.project.entity.User;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserSimpleInfoDto {
    private Long userNo;
    private String image;
    private String name;
    private String email;
}
