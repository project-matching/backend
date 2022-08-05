package com.matching.project.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PasswordUpdateRequestDto {

    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;
}
