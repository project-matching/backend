package com.matching.project.oauth;

import com.matching.project.entity.User;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
public class UserSessionDto implements Serializable {
    private String name;
    private String email;

    public UserSessionDto(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
    }
}
