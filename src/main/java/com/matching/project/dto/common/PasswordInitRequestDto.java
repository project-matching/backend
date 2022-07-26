package com.matching.project.dto.common;

import com.matching.project.dto.enumerate.EmailAuthPurpose;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class PasswordInitRequestDto {
    @NotBlank
    String email;

    @NotBlank
    String password;

    @NotBlank
    String authToken;
}
