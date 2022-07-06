package com.matching.project.dto.user;

import com.matching.project.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserSimpleInfoDto {
    private Long no;
    private String profile;
    private String name;
    private String email;

    static public UserSimpleInfoDto toUserSimpleInfoDto(final User user) {
        return UserSimpleInfoDto.builder()
                .no(user.getNo())
                .name(user.getName())
                .email(user.getEmail())
                .profile(null)
                .build();
    }
}
