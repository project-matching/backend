package com.matching.project.dto.user;

import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.entity.Position;
import com.matching.project.entity.User;
import com.matching.project.entity.UserTechnicalStack;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SignUpRequestDto {
    @NotBlank
    private String name;

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    public void encodePassword(String encodePassword) {
        this.password = encodePassword;
    }

    public static User toUserEntity(final SignUpRequestDto signUpRequestDto) {
        User user = User.builder()
                .name(signUpRequestDto.name)
                .sex("N")
                .email(signUpRequestDto.email)
                .password(signUpRequestDto.password)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .block(false)
                .withdrawal(false)
                .email_auth(false)
                .build();
        return user;
    }
}
