package com.matching.project.dto.user;

import com.matching.project.dto.enumerate.UserFilter;
import lombok.Data;

@Data
public class UserFilterDto {
    private UserFilter userFilter;
    private String content;
}
