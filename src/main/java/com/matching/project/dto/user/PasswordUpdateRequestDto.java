package com.matching.project.dto.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Builder
@Data
public class PasswordUpdateRequestDto {

    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;
}
