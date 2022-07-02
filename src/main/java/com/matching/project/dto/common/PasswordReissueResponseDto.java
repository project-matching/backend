package com.matching.project.dto.common;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class PasswordReissueResponseDto {
    String email;
    String password;
}
