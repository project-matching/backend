package com.matching.project.dto.user;

import lombok.Data;

import java.util.List;
@Data
public class UserProfileDto {
    private String image;
    private String name;
    private String sex;
    private String email;
    private String position;
    private List<String> technicalStackList;
    private String github;
    private String selfIntroduction;
}
