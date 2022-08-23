package com.matching.project.dto.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TokenReissueRequestDto {
    @NotBlank
    String access;

    @NotBlank
    String refresh;
}
