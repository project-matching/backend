package com.matching.project.dto.user;

import com.matching.project.dto.enumerate.Position;
import lombok.Getter;

import java.util.List;

@Getter
public class UserUpdateRequestDto {
    private String profile;
    private String name;
    private char sex;
    private String password;
    private Position position;
    private List<String> technicalStackList;
    private String github;
    private String selfIntroduction;
}
