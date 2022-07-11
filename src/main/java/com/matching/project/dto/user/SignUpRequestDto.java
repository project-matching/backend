package com.matching.project.dto.user;

import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.entity.Position;
import com.matching.project.entity.User;
import com.matching.project.entity.UserTechnicalStack;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SignUpRequestDto {
    private String name;
    private String sex;
    private String email;
    private String password;
    private String position;
    private List<String> technicalStackList;
    private String github;
    private String selfIntroduction;

    public void setEncodePassword(String encodePassword) {
        this.password = encodePassword;
    }

    public static User toUserEntity(final SignUpRequestDto signUpRequestDto, Position position) {
        char sex = 0;
        if (!"".equals(signUpRequestDto.getSex()) && signUpRequestDto.getSex() != null)
            sex = signUpRequestDto.getSex().charAt(0);
        User user = User.builder()
                .name(signUpRequestDto.name)
                .sex(sex)
                .email(signUpRequestDto.email)
                .password(signUpRequestDto.password)
                .github(signUpRequestDto.github)
                .selfIntroduction(signUpRequestDto.selfIntroduction)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .block(false)
                .position(position)
                .email_auth(true) // 임시
                .build();
        return user;
    }
}
