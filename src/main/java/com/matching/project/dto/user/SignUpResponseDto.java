package com.matching.project.dto.user;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SignUpResponseDto {
    private Long no;
    private String name;
    private String email;
}
