package com.matching.project.dto.user;

import com.matching.project.dto.enumerate.Filter;
import com.matching.project.dto.enumerate.UserFilter;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Builder
@Data
public class UserFilterDto {
    private UserFilter userFilter;
    private String content;
}
