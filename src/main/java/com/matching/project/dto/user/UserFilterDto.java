package com.matching.project.dto.user;

import com.matching.project.dto.enumerate.UserFilter;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserFilterDto {
    private UserFilter userFilter;
    private String content;

    public void setDefaultContent(String content) {
        this.content = content;
    }

    public void setDefaultFilter(UserFilter userFilter) {
        this.userFilter = userFilter;
    }
}
