package com.matching.project.dto.common;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class NormalLoginRequestDto {
    private String email;
    private String password;
}
