package com.matching.project.dto.user;

import com.matching.project.dto.enumerate.UserSearchFilter;
import lombok.Getter;

@Getter
public class UserSearchRequestDto {
    private UserSearchFilter searchFilter;
    private String content;
}
