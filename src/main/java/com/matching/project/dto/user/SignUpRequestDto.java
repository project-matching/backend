package com.matching.project.dto.user;

import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Position;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.entity.User;
import com.matching.project.entity.UserPosition;
import com.matching.project.entity.UserTechnicalStack;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class SignUpRequestDto {
    private String name;
    private char sex;
    private String email;
    private String password;
    private Position position;
    private List<String> technicalStackList;
    private String github;
    private String selfIntroduction;

    public static User toUserEntity(final SignUpRequestDto signUpRequestDto, final UserPosition userPosition) {
        return User.builder()
                .name(signUpRequestDto.name)
                .sex(signUpRequestDto.sex)
                .email(signUpRequestDto.email)
                .password(signUpRequestDto.password)
                .github(signUpRequestDto.github)
                .selfIntroduction(signUpRequestDto.selfIntroduction)
                .permission(Role.USER)
                .oauthCategory(OAuth.NORMAL)
                .block(false)
                .userPosition(userPosition)
                .build();

    }

    public static UserPosition toPositionEntity(final Position position) {
        return UserPosition.builder()
                .name(position.toString()) // enum 이여야 하는거 아닌가?
                .build();

    }

    public static List<UserTechnicalStack> toTechStackListEntity(final List<String> technicalStackList, final UserPosition userposition) {
        List<UserTechnicalStack> technicalStackEntityList = new ArrayList<>();
        for (String stackName : technicalStackList)
        {
            technicalStackEntityList.add(UserTechnicalStack.builder()
                    .name(stackName)
                    .userPosition(userposition)
                    .build()
            );
        }
        return technicalStackEntityList;
    }


}
