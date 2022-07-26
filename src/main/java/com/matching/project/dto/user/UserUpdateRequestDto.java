package com.matching.project.dto.user;

import com.matching.project.entity.UserTechnicalStack;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserUpdateRequestDto {

    private String image;

    @NotBlank
    private String name;

    @NotBlank
    @Length(min = 1, max = 1)
    @Pattern(regexp = "[mMwWsSnN]")
    private String sex;

    private String position;
    private List<String> technicalStackList;
    private String github;
    private String selfIntroduction;
}
