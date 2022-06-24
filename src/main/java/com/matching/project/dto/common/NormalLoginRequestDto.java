package com.matching.project.dto.common;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NormalLoginRequestDto {
    private String email;
    private String password;
}
