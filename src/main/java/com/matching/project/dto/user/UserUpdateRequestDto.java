package com.matching.project.dto.user;

import com.matching.project.entity.UserTechnicalStack;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserUpdateRequestDto {
    private String profile;
    private String name;
    private String sex;
    private String originPassword;
    private String newPassword;
    private String position;
    private List<String> technicalStackList;
    private String github;
    private String selfIntroduction;

//    public static UserPosition toPositionEntity(final Position position) {
//        return UserPosition.builder()
//                .name(position.toString())
//                .build();
//
//    }
//    public static List<UserTechnicalStack> toTechStackListEntity(final List<String> technicalStackList, final UserPosition userposition) {
//        List<UserTechnicalStack> technicalStackEntityList = new ArrayList<>();
//        for (String stackName : technicalStackList)
//        {
//            technicalStackEntityList.add(UserTechnicalStack.builder()
//                    .name(stackName)
//                    .userPosition(userposition)
//                    .build()
//            );
//        }
//        return technicalStackEntityList;
//    }
}
