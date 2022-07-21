package com.matching.project.dto.user;

import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserInfoResponseDto {
    private String name;
    private char sex;
    private String email;
    private String position;
    private List<String> technicalStackList;
    private String github;
    private String selfIntroduction;
    private String profile;
}
