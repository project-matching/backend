package com.matching.project.dto.common;

import lombok.*;

import javax.validation.constraints.NotNull;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class NormalLoginRequestDto {
    @NotNull
    private String email;

    @NotNull
    private String password;
}
